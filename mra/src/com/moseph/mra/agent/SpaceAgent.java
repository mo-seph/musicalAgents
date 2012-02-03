package com.moseph.mra.agent;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import FIPA.AgentID;

import com.moseph.mra.*;
import com.moseph.mra.agent.analyser.GroupNumericFeature;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.agent.gui.SpaceAgentGui;
import com.moseph.mra.parser.MRAParser;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.*;
import jade.wrapper.*;
import static com.moseph.mra.agent.MusicallyAwareAgent.*;
import static com.moseph.mra.MRAConstants.*;
import static com.moseph.mra.agent.AgentUtilities.*;
import static jade.lang.acl.ACLMessage.*;
import static java.lang.Math.*;

import com.moseph.mra.blob.BlobParams;

public class SpaceAgent extends MusicallyAwareAgent
{
	transient protected SpaceAgentGui myGui; // Reference to the gui
	Piece piece;
	protected Ontology ontology = MRAOntology.getInstance();
   protected ContentManager manager = (ContentManager) getContentManager();
   // This agent "speaks" the SL language
   protected Codec codec = new SLCodec();
   // This agent "knows" the Music-Shop ontology
   Context context;
   String filename;
   AnalysisSystem analyser;
   double analysisLength = 2.0;
   static SpaceAgent instance = null;


	protected void initialise() 
	{
		//super.setup();
		//	Instanciate the gui
		instance = this;
		manager.registerLanguage(codec);
		manager.registerOntology(ontology);

		addBehaviour( getUpdateFromMusicBehaviour() );
		addBehaviour( getUpdatePathBehaviour() );
		addBehaviour( getQuittingBehaviour() );
		addBehaviour( getIgnoreMusicRequestBehaviour() );
		addBehaviour( getFallthroughBehaviour() );
		registerServices();
		context = new Context(null );
		Object[] args = getArguments();
		if( args.length > 0 )
		{
				filename = args[0] + "";
				piece = parseFile( filename );
				context.setPiece( piece );
		}
		else
		{
			System.err.println( "No arguments given");
		}
		myGui = new SpaceAgentGui(this, context );
		setInitialAgents( getArguments() );
		myGui.setVisible(true);
		initAnalysis();
		setQueueSize( 80 );
	}
	
	void initAnalysis()
	{
		ForceAnalysisSettings settings = new ForceAnalysisSettings();
		settings.setForceNotDistanceWeighted( true );
		settings.setForceNotRelative( true );
		settings.setForceNotIncludeSelf( true );
		analyser = new AnalysisSystem( context, analysisLength, settings );
		analyser.setSmoothing( 0.5 );
		analyser.addAnalyser( "Dynamics", 0.5 );
		analyser.addAnalyser( "Density", 0.5 );
	}
	
	Behaviour getUpdateFromMusicBehaviour()
	{
		return new BlockingReceiver( this, getScoreTemplate() )
		{
			void receivedMessage( ACLMessage m ) 
			{ 
				Score s = getScoreFromMessage( m );
				myGui.updateScore( s );
				analyser.analyse( s );
			}
		};
	}
	
	public void setInitialAgents( Object[] agentDefs )
	{
		for( Object defO : agentDefs )
		{
			String def = defO.toString();
			try
			{
				String[] bits = def.split( ":", 2  );
				BlobParams bp = new BlobParams( bits[1] );
				MusicianInformation inf = context.getMusicianInformation( bits[0]);
				inf.setColour( new Color( (float)bp.red, (float)bp.green, (float) bp.blue ));
				double[] coords = proportionsToWorldCoord( bp.xpos, bp.ypos );
				inf.getMusician().setX( coords[0] );
				inf.getMusician().setY( coords[1] );
			}
			catch( Exception e )
			{
				System.err.println( "Funny blob params: " + def );
				//e.printStackTrace();
			}
		}
	}	
	Behaviour getUpdatePathBehaviour()
	{
		return new BlockingReceiver( this, getPathTemplate() )
		{
			void receivedMessage( ACLMessage msg )
			{
				Path path = null;
				try
				{
					jade.content.onto.basic.Action act = (jade.content.onto.basic.Action)ontology.toObject( manager.extractAbsContent(msg));
					Play p = (Play)act.getAction();
					path = p.getPath();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				if( path == null ) return;
				context.setPath( msg.getSender(), path );
				context.getMusicianInformation( msg.getSender() ).setPath( path );
				String name = msg.getSender().getLocalName();
				myGui.updatePath( msg.getSender(), name, path );
			}
		};
	}
	
	Behaviour getQuittingBehaviour()
	{
		return new BlockingReceiver( this, getObjectTemplate( QUITTING_TYPE ) )
		{
			void receivedMessage( ACLMessage m )
			{
				System.out.println( "Space removed agent" + m.getSender() );
				context.retireMusician( m.getSender() );
				//myGui.removeAgent( m.getSender() );
			}
		};
	}

	
	
	public void sendLocationUpdate( AID aid, double[] coords )
	{
		System.out.println( ">>>>> Sending location update to: " + aid );
		ACLMessage msg = getMsg( aid, LOCATION_TYPE );
		msg.setContent( coords[0] + ":" + coords[1]);
		send( msg );
	}
	
	public void killAgent( AID aid )
	{
		System.out.println( ">>>>> Sending quit request to: " + aid );
		send( getMsg( aid, KILL_TYPE ) );
	}
	
	public void bumpAgent( AID aid )
	{
		System.out.println( ">>>>> Sending bump to: " + aid );
		send( getMsg( aid, BUMP_TYPE ) );
	}
	
	public ACLMessage getMsg( AID aid, String type )
	{
		ACLMessage msg = new ACLMessage( INFORM );
		msg.addReceiver( aid );
		msg.addUserDefinedParameter( TYPE_PARAM, type );
		return msg;
	}
	
	public void setOnBreak( AID aid, boolean onBreak )
	{
		String breakStr = onBreak ? BREAK_START : BREAK_END;
		System.out.println( ">>>>> Sending break request " + breakStr + " to: " + aid );
		ACLMessage msg = new ACLMessage( INFORM );
		if( aid != null )
			msg.addReceiver( aid );
		else
		{
			msg = getMessageToAllMusicians();
			msg.setPerformative( INFORM);
		}	
		msg.addUserDefinedParameter( TYPE_PARAM, BREAK_TYPE);
		msg.setContent( breakStr );
		send( msg );
	}
	
	public void recallAll()
	{
		setOnBreak( null, false );
	}
	public void sendAllOnBreak()
	{
		setOnBreak( null, true );
	}
	
	public AID createNewAgent()
	{
		GroupNumericFeature pathFeature = (GroupNumericFeature)context.getFeature("Path");
		List<Feature> paths = pathFeature.getValues();
		Path pathToUse = piece.getFirstPath();
		if( paths.size() > 0 )
			pathToUse = ((PathFeature)paths.get( (int)( random() * paths.size() ))).getPath();
		return createNewAgent( pathToUse.toString() );
	}
	
	public AID createNewAgent( String pathToUse )
	{
		
		ContainerController controller = getContainerController();
		System.out.println( "Creating agent with path: " + pathToUse );
		String agentArgs[] = { 
				"Filename=" + filename,
				"Instrument=" + instrumentNames[ (int)( Math.random() * instrumentNames.length )] ,
				( pathToUse == piece.getFirstPath().toString() ? "" : "Path=" + pathToUse ),
				"NoLeadIn=true",
				"Reasoner=Straight",
				"x=" + ( ( random() * 2 - 1 ) * ROOM_X ),
				"y=" + ( ( random() * 2 - 1 ) * ROOM_Y )
				};
		try
		{
			AgentController agent = controller.createNewAgent( "created" + getAgentNumber(),
					"com.moseph.mra.agent.ScoreAgent", agentArgs );
			agent.start();
			System.out.println( "Created an agent called " + agent.getName() + " args: " + Arrays.asList( agentArgs ) );
			return new AID( agent.getName(), false );
			//agent.activate();
		}
		catch( Exception e )
		{
			System.err.println( "Could not create agent!" + e );
			return null;
		}
	}
	 
	public String[] getServiceNames()
	{
		return new String[] { MUSICIAN_SERVICE };
	}
	 
	public List<MessageTemplate> getKnownMessages()
	{
		List<MessageTemplate> sup = super.getKnownMessages();
		sup.add( getMusicRequestTemplate() );
		sup.add( getScoreTemplate() );
		sup.add( getPathTemplate() );
		return sup;
	}

	/**
	 * Ignores requests for music
	*/
	Behaviour getIgnoreMusicRequestBehaviour()
	{
		return new BlockingReceiver( this, getMusicRequestTemplate() )
			{ void receivedMessage( ACLMessage m ) { } };
	}
	
	public static SpaceAgent getInstance() { return instance; }
}
