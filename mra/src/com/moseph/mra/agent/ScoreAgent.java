package com.moseph.mra.agent;


import static com.moseph.mra.MRAConstants.*;
import static com.moseph.mra.MRAUtilities.*;
import static com.moseph.mra.Examples.*;


import jade.content.lang.Codec.CodecException;
import jade.content.onto.*;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.*;
import static jade.lang.acl.ACLMessage.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import javax.swing.*;

import FIPA.AgentID;

import com.moseph.mra.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.reasoning.*;
import com.moseph.mra.agent.reasoning.sequences.*;
import com.moseph.mra.parser.MRAParser;
import com.moseph.mra.symbolic.*;

/**
A RandomNoteAgent is a basic test of the musical agent shell

every time it recieves another fragment of music, it creates a similar
length fragment in the output buffer consisting of random notes

@author David Murray-Rust
@version $Revision$, $Date$
*/
public class ScoreAgent extends MusicalAgent
{
	int index = 60;
	Section current = null;
	double gotMusicTill = countIn;
	//double grace = 0.5;
	double grace = 1.0;
	double initialGrace = 2.0;
	boolean loaded = false;
	double baseProbability = 0.02;
	double maxWeight = 1.0;
	double skipWeight = 0.0;
	String partname = "Notes";
	RenderingSystem renderer;
	Reasoner reasoner;
	StructuralDecider structuralDecider;
	OutputSubsystem output;
	String initialPath = null;
	public static final double breakThreshold = 3.0;
	ScoreFollower follower;
	AnalysisSystem analyser;
	boolean annotateScores = false;
	static boolean analysePlaying = true;
	
	String reasonerType = "Default";
	double analysisLength = 4.0;
	String sequenceFile = null;
	
	
	SequenceManager getSequenceManager()
	{
		if( sequenceFile != null )
			return new FileSequenceManager( sequenceFile );
		return new DefaultSequenceManager();
	}
	
	LatticeManager getLatticeManager()
	{
		return new DefaultLatticeManager( 5 );
	}
	
	protected void initialise()
	{
		super.initialise();
		doFileParse();
		context.setPiece( piece );
		
		context.setPartname( partname );
		
		context.setSequenceManager( getSequenceManager() );
		context.setLatticeManager( getLatticeManager() );
		
		analyser = new AnalysisSystem( context, analysisLength );
		analyser.setAnalyseSelf( false );
		//analyser.addAnalyser( "DisplacementAverage" );
		//analyser.addAnalyser( "DisplacementChange" );
		//analyser.addAnalyser( "DisplacementPattern" );
		//analyser.addAnalyser( "DynamicAverage" );
		//analyser.addAnalyser( "DynamicChange" );
		//analyser.addAnalyser( "DynamicPattern" );
		//analyser.addAnalyser( "LengthChange" );
		//analyser.addAnalyser( "LengthAverage" );
		//analyser.addAnalyser( "LengthPattern" );
		
		structuralDecider = new StructuralDecider( context );
		if( initialPath != null ) setPath( initialPath );
		renderer = new RenderingSystem();
		reasoner = getReasoner( context, structuralDecider );
		//countIn = Conductor.countIn;
		System.out.println( "Starting with count in: " + countIn );
		outputBuffer = new Fragment( countIn );
		output = new OutputSubsystem( outputBuffer, renderer, reasoner, countIn, grace );
		current = piece;
		renderer.setRenderExpressivity( true );
		if( doLeadIn ) output.fillBufferTill( countIn + initialGrace );
		//System.out.println( "Buffer @ start till: " + gotMusicTill + "\n" + outputBuffer );
		
		//System.out.println( "Initial output: " + outputBuffer );
		
		follower = new ScoreFollower(context);
		addBehaviour( getPathBehaviour() );
		sendPathMessage( structuralDecider.getCurrentSection().getPath() );

		//System.exit( 0);
	}
	
	void addGuiComponents()
	{
		if( analyser instanceof GuiComponent )
		{
			JComponent c = ((GuiComponent)analyser).getGUIComponent();
			c.setBorder( BorderFactory.createTitledBorder( "Analyser" ) );
			infoPanel.add( "Analysis", c );
		}
		if( reasoner instanceof GuiComponent )
		{
			JComponent c = ((GuiComponent)reasoner).getGUIComponent();
			c.setBorder( BorderFactory.createTitledBorder( "Reasoner" ) );
			infoPanel.add( "Reasoner", c );
		}
	}
	
	Reasoner getReasoner( Context context, StructuralDecider decider )
	{
		System.out.println( "+++++ Creating reasoner: REasoner type: " + reasonerType );
		if( reasonerType.equalsIgnoreCase( "AcousticMirror" ) )
			return new AcousticMirrorReasoner( context, structuralDecider );
		if( reasonerType.equalsIgnoreCase( "Straight" ) )
			return new StraightReasoner( context, structuralDecider );
		if( reasonerType.equalsIgnoreCase( "Value" ) )
			return new ValueReasoner( context, structuralDecider );
		if( reasonerType.equalsIgnoreCase( "Sequence" ) )
			return new SequenceReasoner( context, structuralDecider );
		else  return new DefaultReasoner( context, structuralDecider );
	}
	
	void gotInput( Score newMusic, double start, double length )
	{
		if( annotateScores ) newMusic = follower.annotateScores( newMusic );
		super.gotInput( newMusic, start, length );
		
		//Trigger analysis?
		if( analysePlaying )
		{
			//System.out.println( "Analysing playing");
			analyser.analyse( newMusic );
		}
		else System.out.println( "!!!! NOT ANALYSING PLAYING");
		//if( analyseMusicalActs )
		//{
		//}
	}
	
	void musicRequest( double start, double end )
	{
		//System.out.println( id.getName() + ": " + getPull() ) ;
		id.setPull( getPull() );
		output.musicRequest( start, end );
	}
	
	double getPull()
	{
		GroupFeature paths = context.getFeature( "Path");
		if( paths.getMusicians().size() < 2 )
		{
			//System.out.println( "Not enough musicians!");
			return 0.0;
		}
		return ((GroupNumericFeature) paths ).getAverageValue();
	}
	
	void constructNewOutput()
	{
		Section oldSection = structuralDecider.getCurrentSection();
		if( oldSection == null ) 
		{
			sendPathMessage( new Path( "null"));
			die();
		}
		//System.out.printf( "++ Calling renderer for %f to %f\n", gotMusicTill, nextFragmentEnd );
		output.fillBufferTill( nextFragmentEnd );
		Section newSection = structuralDecider.getCurrentSection();
		if( oldSection != null && ! oldSection.equals( newSection ) && newSection != null ) 
			sendPathMessage( newSection.getPath() );
	}
	
	
	
	public void takeBreak()
	{
		super.takeBreak();
		System.out.println( "Taking a break!");
		output.setTakingABreak( true );
		sendPathMessage( new Path( "break" ) );
	}
	
	public void endBreak()
	{
		super.endBreak();
		System.out.println( "Restarting! (" + nextFragmentEnd + ", " + breakTime + ")");
		output.setTakingABreak( false );
		if( nextFragmentEnd - breakTime  > breakThreshold ) structuralDecider.getApropriateSection();
		sendPathMessage( structuralDecider.getCurrentSection().getPath() );
	}
	
	void setPath( String path )
	{
		Path p = new Path( path );
		structuralDecider.setPath( p );
		sendPathMessage( p );
	}
	
	void bump()
	{
		System.out.println( "Bumped!");
		structuralDecider.forceUpdate();
	}
	
	Behaviour getPathBehaviour()
	{
		return new BlockingReceiver( this, getPathTemplate() )
		{
			void receivedMessage( ACLMessage m )
			{
				updatePath( m );
			}
		};
	}
	
	public void updatePath( ACLMessage msg )
	{
		Path path = null;
		try
		{
			//System.err.println( "__ " + msg.toString() );
			jade.content.onto.basic.Action act = (jade.content.onto.basic.Action)ontology.toObject( manager.extractAbsContent(msg));
			Play p = (Play)act.getAction();
			path = p.getPath();
			//((GroupNumericFeature)context.getFeature( "Path")).setSilent( false );
			context.setPath( msg.getSender(), path );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	
	}
	

	
	void applyArgument( AgentArgument a )
	{
		if( a.arg.equalsIgnoreCase( "Partname")) partname = a.val;
		if( a.arg.equalsIgnoreCase( "Path")) initialPath = a.val;
		if( a.arg.equalsIgnoreCase( "NoLeadIn")) doLeadIn = false;
		if( a.arg.equalsIgnoreCase( "Reasoner" )) reasonerType = a.val;
		if( a.arg.equalsIgnoreCase( "SequenceFile" )) sequenceFile = a.val;
		else super.applyArgument( a );
	}
	
	Fragment getOutput( double start, double end )
	{
		Fragment ret = output.getOutput( start, end );
		//System.out.println( id.getName() + " asked for music from " + start + " to " + end + ":\n" + ret );
		return ret;
	}
	
	public List<MessageTemplate> getKnownMessages()
	{
		List<MessageTemplate> sup = super.getKnownMessages();
		sup.add( getPathTemplate() );
		return sup;
	}

	public static void setAnalysePlaying( boolean analysePlaying )
	{
		ScoreAgent.analysePlaying = analysePlaying;
	}
}