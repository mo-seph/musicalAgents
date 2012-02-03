package com.moseph.mra.agent;


import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REQUEST;
import static java.util.logging.Level.*;
import static com.moseph.mra.agent.AgentUtilities.*;
import static com.moseph.mra.MRAConstants.*;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.*;
import jade.core.behaviours.*;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.*;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;

import sun.security.action.GetBooleanAction;

import com.moseph.mra.*;
import com.moseph.mra.parser.MRAParser;

/**
adds a few utility methods to a basic {@link Agent}.
Knows about the transport of music between agents
@author David Murray-Rust
@version $Revision$, $Date$
*/
public class MusicallyAwareAgent extends Agent
{
	protected static Logger log = Logger.getLogger( "com.moseph.music.agent" );
	public static String SCORE_MESSAGE = "Score";
	public static String FRAGMENT_MESSAGE = "Fragment";
	public static String MUSIC_REQUEST = "Music";
	public static String TYPE_PARAM = "type";
	public static String START_PARAM = "start";
	public static String LENGTH_PARAM = "length";
	public static String MUSICIAN_SERVICE = "musician";
	public static String CONDUCTOR_SERVICE = "conductor";
	public static String PATH_TYPE = "path";
	public static String LOCATION_TYPE = "location";
	public static String KILL_TYPE = "die";
	public static String BREAK_TYPE = "break";
	public static String BUMP_TYPE = "bump";
	
	public static String QUITTING_TYPE = "quitting";
	public static String BREAK_START = "start break";
	public static String BREAK_END = "end break";
	
	private static boolean logInitialised = false;
	Musician id = new Musician( "Bob" );
	List<DFAgentDescription> musicianList;
	
	protected Ontology ontology = MRAOntology.getInstance();
   protected ContentManager manager = (ContentManager) getContentManager();
   // This agent "speaks" the SL language
   protected Codec codec = new SLCodec();
   // This agent "knows" the Music-Shop ontology
   protected List<AgentArgument> arguments = new Vector<AgentArgument>();
	protected String filename = null;
	Piece piece = null;
	
	String experiment = "Unknown";
	String subExperiment = "Unknown";
	JTabbedPane infoPanel = new JTabbedPane( JTabbedPane.LEFT );
	protected double countIn = 0.0;


	public MusicallyAwareAgent()
	{
		log = getLogger();
	}
	
	protected void setup()
	{
		super.setup();
		initialise();
		populateGUI();
	}
	
	protected void initialise() 
	{
		setQueueSize( 400 );
		id = new Musician( getLocalName() );
		manager.registerLanguage(codec);
		manager.registerOntology(ontology);
		addBehaviour( getDieBehaviour() );
		addBehaviour( getOtherQuittingBehaviour() );

		registerServices();	
		parseArguments();
		applyArguments();
		
	}
	
	void populateGUI()
	{
		if( ! AgentDisplay.isShowDisplay() ) return;
		addGuiComponents();
		AgentDisplay.addAgent( getAID(), infoPanel );
	}
	
	void addGuiComponents() {};
	

	
/*******************************************************************************
*                                                                              *
* Putting music into messages and taking it out again                          *
*                                                                              *
*******************************************************************************/

	/**
	Using serialised objects for laziness at the moment
	@param s
	@param message
	*/
	void putScoreIntoMessage( Score s, double start, double length, ACLMessage message )
	{
		try
		{
			log.log( FINE, getName() + " putting a score from " + start + " to " + ( start + length ) + "into message" );
			message.setContentObject( s );
			message.addUserDefinedParameter( TYPE_PARAM, SCORE_MESSAGE );
			message.addUserDefinedParameter( START_PARAM, start + "" );
			message.addUserDefinedParameter( LENGTH_PARAM, length + "" );
		}
		catch( IOException e )
		{
			message.setContent( "" );
			log.log( INFO, "Could not set message content to score:\n" + s + "\n" + e );
		}
	}

	/**
	Gets a {@link Score} object out of an ACLMessage
	@param m
	@return a score if the message contained one, a new score containing
	a {@link Fragment} if the message contained a Fragment, or an empty {@link Score} if the message
	contained neither
	*/
	public static Score getScoreFromMessage( ACLMessage m )
	{
		try
		{
			//double start = getStart( m );
			//double length = getLength( m );
			//log.log( FINE, getName() + " retrieving a score from " + start + " to " + ( start + length ) );
			Serializable s = m.getContentObject();
			if( s instanceof Score ) return (Score)s;
			if( s instanceof Fragment ) return new Score( (Fragment) s );
			log.log( INFO, "Been given a music message which is not a Score or a Fragment: " + m.getContent() );
			return new Score();
		}
		catch( Exception e )
		{
			log.log( INFO, "Problem desirializing object: " + e );
			return null;
		}
	}
	
	public static Object getObjectFromMessage( ACLMessage m )
	{
		try
		{
			Serializable s = m.getContentObject();
			return s;
		}
		catch( Exception e )
		{
			log.log( INFO, "Problem deserializing message: " + e );
			return null;
		}
	}

	static double getLength( ACLMessage m )
	{
		return getDoubleParam( m, LENGTH_PARAM );
	}

	static double getStart( ACLMessage m )
	{
		return getDoubleParam( m, START_PARAM );
	}

	static double getDoubleParam( ACLMessage m, String param )
	{
		String s = m.getUserDefinedParameter( param );
		try
		{
			return Double.parseDouble( s );
		}
		catch( Exception e )
		{
			log.log( INFO, "Bad numeric string for " + param + ": " + s );
			return 0.0;
		}
	}
	
	public static Fragment getClosingFragment( List<Note> hanging )
	{
		Fragment f = new Fragment();
		for( Note n : hanging  )
			f.addNote( new Note( 0.0, n.getPitch(), 0.0, 0.0, true, false ) );
		return f;
	}
	
	
	
	public static double[] proportionsToWorldCoord( double x, double y )
	{
		if( x < 0 ) x = 0;
		if( x > 1 ) x = 1;
		if( y < 0 ) y = 0;
		if( y > 1 ) y = 1;
		return new double[] { ( x * 2 - 1 ) * ROOM_X, ( y * 2 - 1 ) * ROOM_Y };
	}

	void putFragmentIntoMessage( Fragment f, double start, double length, ACLMessage message )
	{
		try
		{
			log.log( FINE, getName() + " putting a fragment from " + start + " to " + ( start + length ) + "into message" );
			f.stripMidi();
			message.setPerformative( INFORM );
			message.setContentObject( f );
			message.addUserDefinedParameter( TYPE_PARAM, FRAGMENT_MESSAGE );
			message.addUserDefinedParameter( START_PARAM, start + "" );
			message.addUserDefinedParameter( LENGTH_PARAM, length + "" );
			log.log( FINE, "Which looks like: " + message.toString() );
		}
		catch( IOException e )
		{
			message.setContent( "" );
			log.log( INFO, "Could not set message content to fragment:\n" + f + "\n" + e );
		}
	}

	/**
	Gets a {@link Fragment} object out of an ACLMessage
	@param m
	@return a {@link Fragment} if the message contained a Fragment, an empty {@link Fragment} if not
	*/
	Fragment getFragmentFromMessage( ACLMessage m )
	{
		try
		{
			double start = getStart( m );
			double length = getLength( m );
			log.log( FINE, getName() + " retrieving a fragment from " + start + " to " + ( start + length ) );
			Serializable s = m.getContentObject();
			//System.out.println( ">>> Retrieved: " + s );
			if( s instanceof Fragment ) return (Fragment) s;
			log.log( INFO, "Been given a Fragment message which is not a Fragment: " + m.getContent() );
			return new Fragment();
		}
		catch( Exception e )
		{
			log.log( INFO, "Problem desirializing object: " + e );
			return null;
		}
	}

	public ACLMessage getMessageToAllMusicians()
	{
		List<DFAgentDescription> listeners = getMusicians();
		ACLMessage message = new ACLMessage( ACLMessage.INFORM );
		for( DFAgentDescription listener : listeners )
		{
			message.addReceiver( listener.getName() );
		}
		return message;
	}
	
	
	/**
	 * Stub - override
	 * @param aid
	 */
	public void otherQuitting( AID aid )
	{
	}
	
/*******************************************************************************
*                                                                              *
* Utility Stuff                                                                *
*                                                                              *
*******************************************************************************/

	protected static Logger getLogger()
	{
		if( ! logInitialised )
		{
			Handler h = new ConsoleHandler();
			//log.addHandler( h );
			h.setLevel( Level.ALL );
			log.setLevel( Level.INFO );
			logInitialised = true;
		}
		return log;
	}

	public List<DFAgentDescription> getMusicians()
	{
		//if( musicianList == null ) musicianList = getServiceProviders( MUSICIAN_SERVICE );
		//return musicianList;
		return getServiceProviders( MUSICIAN_SERVICE );
	}
	
	public AID getConductor()
	{
		List<DFAgentDescription> cons = getServiceProviders( CONDUCTOR_SERVICE );
		if( cons.size() > 0 ) return cons.get(0).getName();
		return null;
	}

	/**
	@param serviceType the type of service required
	@return an array of agents providing the service
	*/
	public List<DFAgentDescription> getServiceProviders( String serviceType )
	{
		DFAgentDescription desc = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType( serviceType );
		desc.addServices( sd );
		DFAgentDescription[] possibleProviders = null;
		try
		{
			possibleProviders = DFService.search( this, desc );
		}
		catch( FIPAException e )
		{
		}
		return Arrays.asList( possibleProviders );
	}

	public List<MessageTemplate> getKnownMessages()
	{
		List<MessageTemplate> types = new Vector<MessageTemplate>();
		types.add( getObjectTemplate( KILL_TYPE ) );
		types.add( getObjectTemplate( QUITTING_TYPE ) );
		return types;
	}

	protected Behaviour getFallthroughBehaviour()
	{
		return getFallthroughBehaviour( getKnownMessages() );
	}
	
	protected Behaviour getOtherQuittingBehaviour()
	{
		return new BlockingReceiver( this, getObjectTemplate( QUITTING_TYPE ) )
		{
			public void receivedMessage( ACLMessage m )  
			{  
				otherQuitting( m.getSender() );
			}
		};
	}
	
	protected Behaviour getDieBehaviour()
	{
		return new BlockingReceiver( this, getObjectTemplate( KILL_TYPE ) )
		{
			public void receivedMessage( ACLMessage m ) 
			{ 
				preDie(); 
			}
		};
	}

	Behaviour getFallthroughBehaviour( List<MessageTemplate> knownMessages )
	{
		MessageTemplate noMatch = MessageTemplate.MatchAll();
		for( MessageTemplate m : knownMessages )
		{
			noMatch = MessageTemplate.and( noMatch, MessageTemplate.not( m ) );
		}
		return getLoggingBehaviour( "Unknown message", noMatch, false );
	}
	

	public Behaviour getLoggingBehaviour( final String message, MessageTemplate match, final boolean putBack )
	{
		return new BlockingReceiver( this, match )
		{
			void receivedMessage( ACLMessage m )
			{
				//log.log( INFO, id.getName() + " got message " + message + ": " + m.getContent() + " from " + m.getSender().getLocalName() );
				if( m.getPerformative() == ACLMessage.FAILURE ) 
				{
					System.out.println( id + " got message " + message + ": " + m.getContent() );
					log.log( INFO, id + " got message " + message + ": " + m.getContent() );
				}
				if( putBack ) putBack( m );
			}

		};
	}

	public static MessageTemplate getFragmentTemplate()
	{
		return new MessageTemplate( new MusicMatch( new String[] { FRAGMENT_MESSAGE } ) );
	}

	public static MessageTemplate getScoreTemplate()
	{
		return new MessageTemplate( new MusicMatch( new String[] { SCORE_MESSAGE } ) );
	}

	public static MessageTemplate getMusicTemplate()
	{
		return new MessageTemplate( new MusicMatch( new String[] { FRAGMENT_MESSAGE, SCORE_MESSAGE } ) );
	}
	
	public static MessageTemplate getDieTemplate()
	{
		return getObjectTemplate( KILL_TYPE );
	}
	
	public static MessageTemplate getLocationTemplate()
	{
		return getObjectTemplate( LOCATION_TYPE );
	}
	
	public static MessageTemplate getPathTemplate()
	{
		return getObjectTemplate( PATH_TYPE );
	}
	
	public static MessageTemplate getObjectTemplate( final String paramType )
	{
		return new MessageTemplate( new GeneralMatch()
		{
			public boolean match( ACLMessage m )
			{
				if( m == null ) return false;
				if( m.getPerformative() != INFORM ) return false;
				if( paramType.equals(m.getUserDefinedParameter( TYPE_PARAM) ) ) return true;
				return false;
				
			}
		} );
	}

	public static class MusicMatch implements MessageTemplate.MatchExpression
	{
		String[] categories;
		public MusicMatch( String[] cat )
		{
			categories = cat;
		}

		public boolean match( ACLMessage m )
		{
			if( m == null ) return false;
			if( m.getPerformative() != INFORM ) return false;
			boolean correctCategory = false;
			if( m.getUserDefinedParameter( TYPE_PARAM ) == null ) return false;
			for( String cat : categories )
			{
				if( m.getUserDefinedParameter( TYPE_PARAM ).equals( cat ) ) correctCategory = true;
			}
			if( correctCategory == false ) return false;
			if( m.getUserDefinedParameter( START_PARAM ) == null )
			{
				log.log( INFO, "Got a music message without a start time: " + m );
				return false;
			}
			return true;
		}
	}

	public static class GeneralMatch implements MessageTemplate.MatchExpression
	{
		public boolean match( ACLMessage m ) { return false; }
	}

	public static MessageTemplate getMusicRequestTemplate()
	{
		return new MessageTemplate( new GeneralMatch()
		{
			public boolean match( ACLMessage m )
			{
				if( m.getPerformative() != REQUEST ) return false;
				if( ! m.getContent().equals( MUSIC_REQUEST ) ) return false;
				if( m.getUserDefinedParameter( START_PARAM ) == null )
				{
					log.log( INFO, "Music request with no start time: " + m );
					return false;
				}
				if( m.getUserDefinedParameter( LENGTH_PARAM ) == null )
				{
					log.log( INFO, "Music request with no length: " + m );
					return false;
				}
				return true;

			}
		} );
	}

	public String[] getServiceNames()
	{
		return new String[0];
	}

	void registerServices()
	{
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		for( String service : getServiceNames() )
		{
			// Registration with the DF 
			ServiceDescription sd = new ServiceDescription();   
			sd.setType( service ); 
			sd.setName(getName());
			sd.setOwnership("mo-seph");
			dfd.addServices(sd);
		}
		try 
		{
			DFService.register( this, dfd );
		}
		catch (FIPAException e) 
		{
			log.log( INFO, getLocalName()+" registration with DF unsucceeded. Reason: "+e.getMessage());
			doDelete();
		}
	}
	
	void unregisterServices()
	{
		try 
		{
			DFService.deregister( this );
		}
		catch( Exception e )
		{
			System.err.println( id.getName() + " could not deregister: " + e );
		}
	}
	
	void die()
	{
		System.out.println( getName() + " quitting!");
		sendDieMessage();
		unregisterServices();
		doDelete();
	}
	
	void preDie()
	{
	}
	
	void sendDieMessage()
	{
		ACLMessage m = getMessageToAllMusicians();
		m.addUserDefinedParameter( TYPE_PARAM, QUITTING_TYPE );
		AID conductor = getConductor();
		if( conductor != null ) m.addReceiver( conductor );
		send( m );
	}
	
	void safeWait( int ms, String unexpectedWakeupMessage )
	{
		//try
		//{
			//sleep( (long)ms );
			blockingReceive( MessageTemplate.not( MessageTemplate.MatchAll() ), ms );
		//}
		//catch( InterruptedException e )
		//{
			//log.log( INFO, unexpectedWakeupMessage );
		//}
	}


	
	void doFileParse()
	{
		System.out.println( "Loading piece: " + filename );
		piece = parseFile( filename );
		if( piece == null )
		{
			System.err.println( id + " couldn't create piece... exiting");
			doDelete();
		}
	}
	
	void parseArguments()
	{
		arguments.clear();
		if( getArguments() == null ) return;
		for( Object o : getArguments() )
			if( o.toString().length() > 0 )
				arguments.add( new AgentArgument( o.toString() ));
	}
	
	void applyArguments()
	{
		for( AgentArgument a : arguments )
		{
			if( a == null || a.arg == null || a.val == null ) System.err.println( "Aaargh! null argument: " + a );
			else applyArgument( a );
		}
	}
	
	void applyArgument( AgentArgument a )
	{
		if( a.arg.equalsIgnoreCase( "X")) id.setX( Double.parseDouble( a.val ));
		else if( a.arg.equalsIgnoreCase( "Y")) id.setY( Double.parseDouble( a.val ));
		else if( a.arg.equalsIgnoreCase( "Filename"))
		{
			System.out.println( "___________ Got filename! " + a.val );
			filename = a.val;
		}
		else if( a.arg.equalsIgnoreCase( "CountIn" )) countIn = Double.parseDouble( a.val );
		else if( a.arg.equalsIgnoreCase( "Experiment")) experiment = a.val;
		else if( a.arg.equalsIgnoreCase( "SubExperiment")) subExperiment = a.val;
	}
	
	
	
	class AgentArgument
	{
		public String arg;
		public String val;
		public AgentArgument( String in )
		{
			String[] input = in.split( "\\s*=\\s*" );
			if( input.length > 1 )
			{
				arg = input[0];
				val = input[1];
				if( val == null || arg == null ) System.err.println( "Bad AgentArgument! " + in );
			}
		}
		
		public String toString()
		{
			return arg + ":-" + val;
		}
	}
	
}
