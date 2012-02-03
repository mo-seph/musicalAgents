package com.moseph.mra.agent.run;

import jade.Boot;
import jade.core.Runtime;
import jade.wrapper.PlatformState;

import java.io.File;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.moseph.mra.agent.*;
import com.moseph.mra.midi.MidiPatchbay;

import static com.moseph.mra.midi.MidiUtilities.*;

/**
 * Runs experiments as directed by an experiment definition file. It needs to supply:
 * 
 * * For the conductor:
 * ** Fragment size
 * ** output file to record to
 * ** what score to play
 * ** how long to play for
 * ** Lead in?
 * ** Metronome?
 * 
 * * For each agent:
 * ** What score to play
 * ** How long to play
 * ** Reasoner to use
 * ** Window size to use
 * ** Fragment size?
 * ** Location and instrument
 * 
 * * For the human:
 * ** Part to play
 * 
 * 
 * @author dave
 *
 */
public class ExperimentRunner extends AgentRunner implements Runnable
{
	Document d;
	List<String> systemDefs;
	String experimentMain;
	String experimentSub;
	double runtime;
	static String prefix = "../../../scores/CantoOstinato/";
	static final String outputDir = "./";
	String scoreFile;
	String outputFilename;
	boolean showSpace = false;
	double countIn = 0.0;
	MidiPatchbay patchbay = MidiPatchbay.getInstance();
	
	public static void main( String[] args )
	{
		if( args.length == 0 ) die( "You must give an experiment file!" );
		if( args.length > 1 ) prefix = args[1];
		String filename = args[0];
		ExperimentRunner er = new ExperimentRunner( filename );
	}
	
	public void run()
	{
	}
	
	public ExperimentRunner( String filename )
	{
		
		try
		{
			File f = new File( filename );
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			d = parser.parse( f );
		}
		catch( Exception e )
		{
			die( "Could not create parser: " + e );
		}
		useDocument();
	}
	
	void useDocument()
	{
		Element root = d.getDocumentElement();
		setupDefaults( root );
		scoreFile = prefix + root.getAttribute( "prefix" ) + ".mra";
		System.out.println( ">>>> Loooking for score: " + scoreFile );
		experimentMain = root.getAttribute( "id" );
		experimentSub = root.getAttribute( "subid" );
		runtime = Double.parseDouble( root.getAttribute( "runTime" ) );
		showSpace = Boolean.parseBoolean( root.getAttribute( "showSpace" ) );
		countIn = Double.parseDouble( root.getAttribute( "countIn" ) );
		outputFilename = experimentMain + "-" + experimentSub + ".mid";
		
		String deviceName = root.getAttribute( "deviceName" );
		int deviceNumber = Integer.parseInt( root.getAttribute( "deviceNumber" ) );
		patchbay.setDefaultOutput( deviceName, deviceNumber );
		
		systemDefs = new Vector<String>();
		
		
		//Get information about humans
		List<String> humanDefs = new Vector<String>();
		NodeList humans = root.getElementsByTagName( "human" );
		for( int i = 0; i < humans.getLength(); i++ )
			humanDefs.add( getHumanDefs( (Element)humans.item( i ) ) );
		
		//Create the conductor
		String conductorDefs = getConductorDefs( (Element)root.getElementsByTagName( "conductor" ).item( 0 ), humanDefs );
		systemDefs.add(  conductorDefs );
		
		NodeList patches = root.getElementsByTagName( "patch" );
		createPatches( patches );
		
		//Defs for the other agents
		NodeList agents = root.getElementsByTagName( "agent" );
		for( int i = 0; i < agents.getLength(); i++ )
		{
			String agentDefs = getAgentDefs( (Element)agents.item( i ) );
			systemDefs.add(  agentDefs );
		}
		if( showSpace )
			systemDefs.add( getSpaceDefinition( scoreFile ));
		
		
		//Run the system
		//System.out.println( "System defs: " );
		//for( String s : systemDefs ) System.out.println( s );
		runAgents( systemDefs );
		//Runtime.instance().invokeOnTermination( this );
		//waitForEnter();
		//Runtime.instance().shutDown();
		/*
		while( Runtime.instance() != null )
		{
			try
			{
				Thread.sleep( 200 );
			} catch (InterruptedException e)
			{
				System.out.println( "Couldn't sleep: " + e );
				e.printStackTrace();
			}
		}
		*/
	}
	
	String getHumanDefs( Element n )
	{
		NamedNodeMap atts = n.getAttributes();
		String name = atts.getNamedItem( "name" ).getTextContent();
		String devName = atts.getNamedItem( "deviceName" ).getTextContent();
		String devNum = atts.getNamedItem( "deviceNumber" ).getTextContent();
		String channel = atts.getNamedItem( "channel" ).getTextContent();
		String x = "0.0";
		String y = "0.0";
		String instrument = "AcousticPiano";
			
		
		RecordAgentDetails rad = new RecordAgentDetails( name, instrument, devName, devNum, channel, x, y  );
		System.err.println( "Record agent details: " + rad.toString() );
		return rad.toString();
	}
	
	String getAgentDefs( Element n )
	{
		NamedNodeMap atts = n.getAttributes();
		String name = atts.getNamedItem( "name" ).getTextContent();
		String agentclass = atts.getNamedItem( "class" ).getTextContent();
		List<String> args = getDefaultAgentArgs();
		args.addAll( getArgsFromElement( n ) );
		String ret = attsToString( name, "com.moseph.mra.agent." + agentclass,
			args.toArray( new String[args.size()] ) );
		System.out.println( "Agent def: " + ret );
		return ret;
	}
	
	String getConductorDefs( Element n, List<String> humans )
	{
		List<String> args = getArgsFromElement( n );
		args.addAll( getDefaultConductorArgs() );
		args.add( "OutputDir=" + outputDir );
		args.add( "OutputFilename=" + outputFilename );
		args.add( "RunTime=" + runtime );
		for( String human : humans )
			args.add( "RecordAgent=\"" + human  + "\"");
		String ret = attsToString( "conductor", "com.moseph.mra.agent.Conductor", 
			args.toArray( new String[args.size()] ) );
		System.out.println( "Conductor def: " + ret );
		return ret;
	}
	
	List<String> getDefaultAgentArgs()
	{
		List<String> args = getDefaultCommonArgs();
		args.add( "PatternLength=2.0");
		//args.add( "NoSlopeCompensation=true");
		return args;
	}
	
	List<String> getDefaultConductorArgs()
	{
		List<String> args = getDefaultCommonArgs();
		args.add( "PlayMetronome=true");
		args.add( "EchoRecording=false");
		args.add( "PatternLength=2.0");
		return args;
	}
	
	List<String> getDefaultCommonArgs()
	{
		List<String> args = new Vector<String>();
		args.add( "Filename=" + scoreFile );
		args.add( "CountIn=" + countIn );
		return args;
	}
	
	
	void setupDefaults( Element root )
	{
		AgentDisplay.setShowDisplay( true );
		Conductor.setStoreOutput( false );
		Conductor.setScheduleRealtimeFragments( false );
		Conductor.setNoDeviceRequest( true );
		if( root.getAttribute( "fragSize" ) != null )
			Conductor.setFragmentSize( Double.parseDouble( root.getAttribute( "fragSize" ) ) );
	}
	
	List<String> getArgsFromElement( Element n )
	{
		NodeList argList = n.getElementsByTagName( "arg" );
		List<String> args = new Vector<String>();
		for( int i = 0; i < argList.getLength(); i++ )
			args.add( argList.item( i ).getTextContent() );
		return args;
	}
	
	static void die( String message )
	{
		System.out.println( message );
		System.exit( 1 );
	}
	
	void createPatches( NodeList patches )
	{
		for( int i = 0; i < patches.getLength(); i++ )
		{
			Node n = patches.item( i );
			NamedNodeMap atts = n.getAttributes();
			
			String iName = atts.getNamedItem( "iName" ).getTextContent();
			String iNum = atts.getNamedItem( "iNum" ).getTextContent();
			String oName = atts.getNamedItem( "oName" ).getTextContent();
			String oNum = atts.getNamedItem( "oNum" ).getTextContent();
			patchbay.patch( iName, Integer.parseInt( iNum ), oName, Integer.parseInt( oNum ) );
		}
	}

}
