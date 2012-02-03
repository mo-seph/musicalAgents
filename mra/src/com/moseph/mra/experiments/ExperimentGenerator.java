package com.moseph.mra.experiments;

import jade.tools.logging.ontology.GetAllLoggers;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import com.sun.org.apache.xml.internal.serialize.*;


public class ExperimentGenerator
{
	static final String PVP = "PVP";
	static final String PVC = "PVC";
	static final String MIRROR = "MIRROR";
	static final String MIDI = "STRAIGHT";
	static final String RECORDING = "RECORD";
	static final String REASON = "REASON";
	static final int M_DEV = 0;
	static final int M_NUM = 1;
	static final int H_DEV = 2;
	static final int H_NUM = 3;
	static final String[] PC_OTHER_DEV = { "AMT", "1" };
	static final String[] MAC_OTHER_DEV = { "E-MU XMidi2X2  Midi In 2 ", "0" };
	static final String[] PC_PVP_DEVS = { "AMT", "0", "AMT", "0" };
	static final String[] PC_PVC_DEVS = { "SD", "0", "AMT", "0" };
	static final String RECORD_FILE_LH = "lh.mid";
	static final String RECORD_FILE_RH = "rh.mid";
	static final String LEFT_HAND = "LH";
	static final String RIGHT_HAND = "RH";
	//static final String[] MAC_PVP_DEVS = { "Port 4", "0", "Port 1", "0" };
	//The first dev here is unconnected, so no metronome get sent!
	static final String[] MAC_PVP_DEVS = { "asdlfkj", "0", "E-MU XMidi2X2  Midi In 1 ", "0" };
	//static final String[] MAC_PVC_DEVS = { "Port 1", "0", "Port 1", "0" };
	static final String[] MAC_PVC_DEVS = { "E-MU XMidi2X2 Midi Out 1 ", "0", "E-MU XMidi2X2  Midi In 1 ", "0" };
	
	static final String[][] MAC_PVC_PATCHES =
	{
		//{"Port 1", "0", "Port 1", "0" },
		{"E-MU XMidi2X2  Midi In 1 ", "0", "E-MU XMidi2X2 Midi Out 1 ", "0" },
	};
	static final String[][] MAC_PVP_PATCHES =
	{
		//{"Port 1", "0", "Port 1", "0" },
		{"E-MU XMidi2X2  Midi In 1 ", "0", "E-MU XMidi2X2 Midi Out 1 ", "0" },
		//{"Port 1", "0", "Port 2", "0" },
		{"E-MU XMidi2X2  Midi In 1 ", "0", "E-MU XMidi2X2 Midi Out 2 ", "0" },
		//{"Port 2", "0", "Port 1", "0" },
		{"E-MU XMidi2X2  Midi In 2 ", "0", "E-MU XMidi2X2 Midi Out 1 ", "0" },
	};
	static final String[][] PC_PVC_PATCHES =
	{
		{"AMT", "0", "SD", "0" },
		{"AMT", "1", "AMT", "0" },
		{"AMT", "0", "AMT", "1" },
	};
	static final String[][] PC_PVP_PATCHES =
	{
		{"AMT", "6", "SD", "0" },
		{"AMT", "6", "AMT", "1" },
		{"AMT", "0", "AMT", "1" },
		{"AMT", "0", "SD", "0" },
		{"AMT", "1", "SD", "0" }
	};
	static final String MAC = "MAC";
	static final String PC = "PC";
	
	/*
	 * End constants
	 */
	
	int playerIndex = 0;
	ExperimentSetup setup;
	Document document;
	Element experimentRoot;
	
	public static void main( String[] args )
	{
		if( args.length < 7  ) 
		{
			System.err.println( "Need arguments: filename condition side part prefix id subid");
			System.err.println( "Example: experiment.xml MIRROR PC LH 1-9 102312 3");
			System.exit(1);
		}
		String filename = args[0];
		String condition = args[1].toUpperCase();
		String side = args[2].toUpperCase();
		String part = args[3].toUpperCase();
		String prefix = args[4];
		String id = args[5];
		String subId = args[6];
		
		//ExperimentGenerator e = new ExperimentGenerator( "MIRROR", "PC", "LH", "1-9");
		System.out.println( "Running generator!");
		ExperimentGenerator e = new ExperimentGenerator( filename, condition, side, part, prefix, id, subId );
		e.generateExperiment();
		e.writeExperiment( filename );
	}
	
	public ExperimentGenerator( String filename, String condition, String side, String partname, String prefix, String id, String subId )
	{
		setup = new ExperimentSetup( condition, side, partname, prefix );
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try
		{
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e)
		{
			System.out.println( "For some reason Java can't make an XML document: " + e );
			e.printStackTrace();
		}
		document = builder.newDocument();
		experimentRoot = document.createElement( "experiment" );
		experimentRoot.setAttribute( "id", id );
		experimentRoot.setAttribute( "subid", subId );
		document.appendChild( experimentRoot );
	}
	
	public void writeExperiment( String filename )
	{
		OutputFormat format = new OutputFormat( document );
		format.setIndenting( true );
		format.setIndent( 2 );
		File f = new File( filename  );
		try
		{
			Writer output = new OutputStreamWriter( System.out );
			if( filename != null ) output = new FileWriter( f );
			XMLSerializer serializer = new XMLSerializer( output, format );
			serializer.serialize( document );
		} catch (IOException e)
		{
			System.out.println( "Coulddn't serialise document: " + e );
			e.printStackTrace();
		}
	}
	
	
	public void generateExperiment()
	{
		experimentRoot.appendChild( getConductor() );
		setupDefaultArguments();
		setupScoreParams();
		setupMainDevice();
		addHuman();
		addOther();
		addPatches( setup.patches );
	}
	
	public void addOther()
	{
		Element other = null;
		if( setup.condition.equals( PVP ))
			other = getPlayer( setup.partnerDevName, setup.partnerDevNumber, setup.otherPartname );
		else if( setup.condition.equals( MIDI ))
			other = getScoreAgent( setup.otherPartname, "Straight" );
		else if( setup.condition.equals( MIRROR ))
			other = getScoreAgent( setup.otherPartname, "Value" );
		else if( setup.condition.equals( REASON ))
			other = getScoreAgent( setup.otherPartname, "SEQUENCE", "SequenceFile=main.seq" );
		else if( setup.condition.equals( RECORDING ))
		{
			if( setup.otherPartname == LEFT_HAND ) 
				other = getAgent( "PlaybackAgent", setup.otherPartname, "SEQUENCE", "PlaybackFile="+RECORD_FILE_LH, "TrackNumber=1" );
			else
				other = getAgent( "PlaybackAgent", setup.otherPartname, "SEQUENCE", "PlaybackFile="+RECORD_FILE_RH, "TrackNumber=1" );
		}
		else System.err.println( "Unknown condition: "+ setup.condition );
		
		if( other != null ) experimentRoot.appendChild( other );
	}
	
	public void setupScoreParams()
	{
		String prefix = setup.prefix;
		experimentRoot.setAttribute( "prefix", prefix );
		experimentRoot.setAttribute( "runTime", getRunTime( prefix ) );
	}
	
	String getRunTime( String prefix )
	{
		if( prefix.equals( "1-9" )) return "136";
		if( prefix.equals( "69")) return "134";
		if( prefix.equals( "88" )) return "152";
		return "0";
	}
	
	void setupDefaultArguments()
	{
		experimentRoot.setAttribute( "fragSize", "1.0" );
		experimentRoot.setAttribute( "showSpace", "false" );
		experimentRoot.setAttribute( "countIn", "4.0" );
	}
	
	void setupMainDevice()
	{
		experimentRoot.setAttribute( "deviceName", setup.mainDevName );
		experimentRoot.setAttribute( "deviceNumber", setup.mainDevNumber );
	}
	
	void addHuman()
	{
		experimentRoot.appendChild( getPlayer( setup.humanDevName, setup.humanDevNumber, setup.partname ) );
	}
	
	public Element getConductor()
	{
		Element conductor = document.createElement( "conductor" );
		addArg( "OutputDir=experiments/output", conductor );
		return conductor;
	}
	
	class ExperimentSetup
	{
		int mainNumber;
		int subNumber;
		String prefix;
		String condition;
		String side;
		String[][] patches;
		String partname;
		String otherPartname;
		String mainDevName;
		String mainDevNumber;
		String humanDevName;
		String humanDevNumber;
		String[] partnerDevs;
		String partnerDevName;
		String partnerDevNumber;
		
		public ExperimentSetup( String condition, String side, String partname, String prefix )
		{
			this.condition = condition;
			this.side = side;
			this.partname = partname;
			this.prefix = prefix;
			setPatches();
			setupDevices();
			setupParts();
		}
		
		
		void setupParts()
		{
			if( partname.equals( LEFT_HAND )) otherPartname = RIGHT_HAND;
			else otherPartname = LEFT_HAND;
		}
		
		void setupDevices()
		{
			String[] devs;
			 if( condition.equals( PVP ) )
				if( side.equals( PC ) )
				{
					devs = PC_PVP_DEVS;
					partnerDevs = PC_OTHER_DEV;
				}
				else
				{
					devs = MAC_PVP_DEVS;
					partnerDevs = MAC_OTHER_DEV;
				}
			else
				if( side.equals( PC ) )
					devs = PC_PVC_DEVS;
				else
					devs = MAC_PVC_DEVS;
			mainDevName = devs[ M_DEV ];
			mainDevNumber = devs[ M_NUM ];
			humanDevName = devs[ H_DEV ];
			humanDevNumber = devs[ H_NUM ];
			if( partnerDevs != null )
			{
				partnerDevName = partnerDevs[0];
				partnerDevNumber = partnerDevs[1];
			}
			
		}
		void setPatches()
		{
			if( condition.equals( PVP ) )
				if( side.equals( PC ) )
					patches = PC_PVP_PATCHES;
				else
					patches = MAC_PVP_PATCHES;
			else
				if( side.equals( PC  ) )
					patches = PC_PVC_PATCHES;
				else
					patches = MAC_PVC_PATCHES;
		}
	}
	
	void addPatches(  String[][] patches )
	{
		for( String[] patch : patches )
		{
			Element elem = getPatch( patch[0], patch[1], patch[2], patch[3] );
			experimentRoot.appendChild( elem );
		}
	}
	
	Element getPatch( String iName, String iNum, String oName, String oNum )
	{
		Element patch = document.createElement( "patch" );
		patch.setAttribute( "iName", iName );
		patch.setAttribute( "iNum", iNum );
		patch.setAttribute( "oName", oName );
		patch.setAttribute( "oNum", oNum );
		return patch;
	}
	
	Element getPlaybackAgent( String partName, String file )
	{
		Element agent = document.createElement( "agent" );
		agent.setAttribute( "name", "bob" );
		addArg( "x=0", agent );
		addArg( "y=10", agent );
		addArg( "Instrument=AcousticPiano", agent );
		addArg( "Partname=" + partName, agent );
		//addArg( "Reasoner=" + reasoner, agent );
		return agent;
	}
	
	Element getAgent( String agentClass, String partName, String reasoner, String...strings  )
	{
		Element agent = document.createElement( "agent" );
		agent.setAttribute( "name", "bob" );
		agent.setAttribute( "class", agentClass );
		addArg( "x=0", agent );
		addArg( "y=0", agent );
		addArg( "Instrument=AcousticPiano", agent );
		addArg( "Partname=" + partName, agent );
		if( strings != null && strings.length > 0 )
		{
			for( String s : strings ) addArg( s, agent );
		}
		return agent;
		
	}
	
	Element getScoreAgent( String partName, String reasoner, String...strings  )
	{
		Element agent = getAgent( "ScoreAgent", partName, reasoner, strings );
		agent.setAttribute( "name", "bob" );
		addArg( "Reasoner=" + reasoner, agent );
		return agent;
	}
	
	Element getPlayer( String devName, String devNum, String partName )
	{
		Element player = document.createElement( "human" );
		player.setAttribute( "name", "Human" + playerIndex++ );
		player.setAttribute( "deviceName", devName );
		player.setAttribute( "deviceNumber", devNum );
		player.setAttribute( "channel", "-1" );
		addArg( "x=0", player );
		addArg( "y=10", player );
		addArg( "Instrument=AcousticPiano", player );
		addArg( "Partname=" + partName, player );
		return player;
	}
	

	void addArg( String arg, Element target )
	{
		Element argElem = document.createElement( "arg" );
		argElem.setTextContent( arg );
		target.appendChild( argElem );
	}
	
	
}
