package com.moseph.mra.agent.run;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import com.moseph.mra.agent.*;

import jade.Boot;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import static com.moseph.mra.MRAConstants.*;

public class CantoOstinato extends AgentRunner
{
	public static void main( String[] args ) throws Exception
	{
		//Conductor.setNoDeviceRequest( true );
		AgentDisplay.setShowDisplay( true );
			String filename = "examples/CantoOstinatoMain.mra";
			if( args.length > 0 ) filename = args[0];
			
			List<String> agents = getStartingAgentDefinitions( filename, false, true, "PlayMetronome=true,NoDeviceRequest=true" );
	
			String[] agentArgs = { "Filename=" + filename,
									"Instrument=AcousticPiano",
									"x=" + ( ROOM_X * -0.6 ),
									"y=0.0",
									"Partname=LH",
									"Path=/main/s2",
									"SequenceFile=sequences/main.seq",
									//"Reasoner=AcousticMirror"
									"Reasoner=Sequence"
									};
			agents.add( attsToString( "LH", "com.moseph.mra.agent.ScoreAgent", agentArgs ) );
			agentArgs[ 2 ] = "x=" + ( 0.6 * ROOM_X );
			agentArgs[ 4 ] = "Partname=RH";
			//agents.add( attsToString( "RH", "com.moseph.mra.agent.ScoreAgent", agentArgs ) );
			runAgents(agents);
	}
	

}
