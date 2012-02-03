package com.moseph.mra.agent;


import static java.util.logging.Level.*;

import java.util.*;

import javax.swing.*;

import jade.content.abs.AbsContentElement;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.moseph.mra.*;

import static com.moseph.mra.MRAConstants.*;
/**
A MusicalAgent is a basic shell for musical agent programming.

It provides the following functionality:
<ul>
<li>A behaviour which accepts score messages from a conductor</li>
<li>A callback for when new music has been recieve</li>
<li>Storage of the music recieved</li>
<li>An output buffer which should be filled with the generated output</li>
<li>A behaviour which immediately responds to a request for music with a fragment of the output buffer</li>
</ul>
@author David Murray-Rust
@version $Revision$, $Date$
*/
public class MusicalAgent extends MusicallyAwareAgent
{
	Score musicHeard = new Score();
	Score mostRecentMusic = new Score();
	Fragment outputBuffer = new Fragment();
	//Map<String,MusicianInformation> information = new HashMap<String,MusicianInformation>();
	Instrument instrument = new Instrument( Instrument.Patch.AcousticPiano );
	protected Context context;
	double nextFragmentEnd = 4.0;
	MusicianInformation me;
	boolean doLeadIn = true;
	boolean onBreak = false;
	double breakTime = 0.0;
	List<Note> hangingNotes;
	boolean stopping = false;
	boolean dieing = false;
	boolean rememberMusic = false;
	private Musician nonPlayingID;
	
	/*******************************************************************************
*                                                                              *
* Instead of constructor, setup routine                                        *
*                                                                              *
*******************************************************************************/

	protected void initialise() 
	{
		super.initialise();
		log.log( FINE, "MusicalAgent " + getName() + " setting up" );
		me = getMyInformation();
		context = new Context( me );
		//Put the arguments into the context just in case
		for( AgentArgument aa : arguments ) context.setAttribute( aa.arg, aa.val );
		System.out.println( "context: " + context.getAllAttsAsString() );
		addBehaviour( getRespondWithMusicBehaviour() );
		addBehaviour( getMusicReceivingBehaviour() );
		addBehaviour( getUpdateLocationBehaviour() );
		addBehaviour( getBreakBehaviour() );
		addBehaviour( getBumpBehaviour() );
		addBehaviour( getFallthroughBehaviour() );
		nonPlayingID = new Musician( getLocalName() );
		nonPlayingID.setY( -100000 );
	}

/*******************************************************************************
*                                                                              *
* Behaviours                                                                   *
*                                                                              *
*******************************************************************************/

	/**
	Gets a behavious which answers a request for music by immediately sending back
	the requested bars from the output buffer
	*/
	Behaviour getRespondWithMusicBehaviour()
	{
		return new BlockingReceiver( this, getMusicRequestTemplate() )
		{
			void receivedMessage( ACLMessage m )
			{
				sendOutput( m );
				constructNewOutput();
			}
		};
	}
	
	Behaviour getBreakBehaviour()
	{
		return new BlockingReceiver( this, getObjectTemplate( BREAK_TYPE  ))
		{
			void receivedMessage( ACLMessage message )
			{
				String content = message.getContent();
				if( content.equalsIgnoreCase( BREAK_START ))
				{
					System.out.println( "Starting break");
					//takeBreak();
					setBreak( true );
				}
				else if ( content.equalsIgnoreCase( BREAK_END ))
				{
					System.out.println( "Ending break");
					//endBreak();
					setBreak( false );
				}
				else
					System.err.println( "Bad break message: " + content );
			}
		};
	}

	Behaviour getMusicReceivingBehaviour()
	{
		return new BlockingReceiver( this, getScoreTemplate() )
		{
			void receivedMessage( ACLMessage m )
			{
				Score newMusic = getScoreFromMessage( m );
				double start = getStart( m );
				//double length = getLength( m );
				mostRecentMusic = newMusic;
				addMusic( newMusic, start );
				gotInput( newMusic, start, getLength( m ) );
			}
		};
	}
	
	Behaviour getUpdateLocationBehaviour()
	{
		return new BlockingReceiver( this, getLocationTemplate() )
		{
			void receivedMessage( ACLMessage m)
			{
				/*
				double[] location = (double[]) getObjectFromMessage( m );
				*/
				String[] parts = m.getContent().split( ":");
				try
				{
					double x = Double.parseDouble( parts[0] );
					double y = Double.parseDouble( parts[1] );
					double[] coords = proportionsToWorldCoord( x, y );
					//System.out.printf( "+++++++ Got new location! (%f, %f)->( %f, %f )\n", x, y, coords[0], coords[1]);
					id.setX( coords[0] );
					id.setY( coords[1] );
					if( parts.length > 2 )
					{
						String activity = parts[2];
						if( activity.equalsIgnoreCase( "active" ) ) setBreak( false );
						else if( activity.equalsIgnoreCase( "inactive")) setBreak( true );
						else if( activity.equalsIgnoreCase( "bump")) bump();
						else if( activity.matches( "[0-9.]*")) setBreak( false );
					}
				}
				catch( Exception e )
				{
					System.err.println( "Bad location message: '" + m.getContent() + "'" );
					e.printStackTrace();
				}
			}
		};
	}
	
	Behaviour getBumpBehaviour()
	{
		return new BlockingReceiver( this, getObjectTemplate( BUMP_TYPE ) )
		{
			void receivedMessage( ACLMessage message )
			{
				bump();
			}
		};
	}
	
	/**
	 * Respond to a "bump" - a general UI poke of some sort
	 *
	 */
	void bump()
	{
		
	}
	
/*******************************************************************************
*                                                                              *
* Utility Functions                                                            *
*                                                                              *
*******************************************************************************/
	
	public void setBreak( boolean brk )
	{
		if( brk && ! onBreak ) takeBreak();
		else if( !brk && onBreak ) endBreak();
	}
	
	public void takeBreak()
	{
		stopping = true;
		onBreak = true;
		breakTime = nextFragmentEnd;
	}
	
	public void endBreak()
	{
		onBreak = false;
	}
	
	public void preDie()
	{
		stopping = true;
		dieing = true;
	}
	
	public String[] getServiceNames()
	{
		return new String[] { MUSICIAN_SERVICE };
	}

	public List<MessageTemplate> getKnownMessages()
	{
		List<MessageTemplate> sup = super.getKnownMessages();
		sup.add( getScoreTemplate() );
		sup.add( getMusicRequestTemplate() );
		sup.add( getPathTemplate() );
		sup.add( getLocationTemplate() );
		sup.add( getObjectTemplate( BREAK_TYPE  ) );
		sup.add( getObjectTemplate( BUMP_TYPE ) );
		return sup;
	}
	
	public void otherQuitting( AID a )
	{
		context.retireMusician( a );
	}


	
	void addMusic( Score s, double position )
	{
		log.log( FINE, getName() + " got: " + s );
		if( rememberMusic ) musicHeard.add( s, position );
		for( Fragment f : s.fragments() )
		{
			String name = f.getPartIndex().getMusician().getName();
			MusicianInformation inf = context.getMusicianInformation( name );
			inf.setMusician( f.getPartIndex().getMusician() );
			inf.setInstrument( f.getPartIndex().getInstrument() );
		}
	}

	/**
	 * Returns the next chunk of the output buffer - shouldn't be overriden except in exceptional
	 * circumstances!
	 * @param start
	 * @param end
	 * @return
	 */
	Fragment getOutput( double start, double end )
	{
		//System.out.println( "Output buffer:" + outputBuffer );
		 Fragment output = outputBuffer.copyChunk( start, end );
		//TMP: Hack to not send any music for performance testing
		//Fragment output = new Fragment();
		//Fragment output = new Fragment(new Note(0.0, 64, 0.5, 0.5 ), new Note(0.0, 67, 0.5, 0.5 ), new Note(0.0, 71, 0.5, 0.5 ), new Note(0.0, 74, 0.5, 0.5 ) );
		
		//System.out.println( "Sending: " + output );
		return output;
	}

	void sendOutput( ACLMessage m )
	{
		try
		{
			ACLMessage reply = m.createReply();

			//Figure out what output they want
			double start = getStart( m );
			double length = getLength( m );
			musicRequest( start, start+ length );
			//log.log( FINE, getName() + " sending music from " + start + " to " + ( start + length ) );
			//log.log( FINE, "Output buffer: " + outputBuffer );
			//log.log( FINE, "Output: " + getOutput( start, start+length ) );
			

			Fragment op = getOutput( start, start+length );
			//System.out.println( "^^^^^" + getName() + " sending " + op );
			op.setMusician( id );
			if( stopping || dieing )
			{
				stopping = false;
				op = getClosingFragment( hangingNotes );
				op.setMusician( nonPlayingID );
			}
			op.setInstrument( instrument );
			hangingNotes = op.getHangingNotes();
			//System.out.println( getName() + "sending " + op.getEvents().size() + " notes" );
			//System.out.println( ">>>Sending : " + op );
			putFragmentIntoMessage( op, start, length, reply );
			nextFragmentEnd = start+length+length;
			//System.out.println( getName() + " sending music from " + start + " to " + ( start + length ) );
			//System.out.println( "Reply content: " + reply.getContentObject() );
			if( dieing ) die();
			send( reply );
		}
		catch( Exception e )
		{
			System.out.println( "Could not send output - " + e );
			e.printStackTrace();
		}
	}
	
	/**
	 * Callback - do something useful if necessary...
	 * @param start
	 * @param end
	 */
	void musicRequest( double start, double end ) {}

	/**
	Callback: Override to do something useful with the bar...
	 * @param length TODO
	 * @param bar
	*/
	void gotInput( Score newMusic, double start, double length )
	{
		log.log( FINE, getName() + " got music from " + start + " to " + ( start + newMusic.getLength() ) );
		context.updateLocationSensitiveTotals();
	}
	
	void constructNewOutput()
	{
	}
	
	void applyArgument( AgentArgument a )
	{
		if( a.arg.equalsIgnoreCase( "Instrument"))
			instrument = new Instrument( a.val );
		else super.applyArgument( a ); 
	}
	
	MusicianInformation getMyInformation()
	{
		MusicianInformation m = new MusicianInformation( id );
		m.setAid( getAID() );
		m.setInstrument( instrument );
		return m;
	}

	protected void sendPathMessage( Path p )
	{
		ACLMessage m = getMessageToAllMusicians();
		m.addUserDefinedParameter( TYPE_PARAM, PATH_TYPE );
		Play play = new Play();
		play.setPath( p );
		jade.content.onto.basic.Action act = new jade.content.onto.basic.Action( getAID(), play );
		try
		{
			//m.setContentObject( p );
			m.setLanguage( codec.getName()  );
			m.setOntology( ontology.getName()  );
			manager.fillContent( m, (AbsContentElement) ontology.fromObject( act));
		}
		catch (Exception e)
		{
			System.err.println( "Could not serialise path: " + e );
			e.printStackTrace();
		}
		send( m );
	}
}

