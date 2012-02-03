package com.moseph.mra.agent.run;
import java.io.BufferedReader;
import static com.moseph.mra.agent.AgentUtilities.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import com.moseph.mra.agent.*;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import jade.Boot;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import static com.moseph.mra.MRAConstants.*;
import static java.lang.Math.*;

public class TuioInC extends AgentRunner
{
	static int NUM_AGENTS = 9;
	static String outputDir = "output/";
	static String outputFilename = "test.mid";
	static String runtime = "50";
	
	public static void main( String[] args ) throws Exception
	{
		//AgentDisplay.setShowDisplay( true );
			String filename = "examples/ECAIInC.mra";
			if( args.length > 0 ) filename = args[0];
			if( args.length > 1) NUM_AGENTS = Integer.parseInt( args[1] );
			
			List<String> agents = getStartingAgentDefinitions( filename,  true, false );//, 
			 //"OutputDir=" + outputDir, "OutputFilename=" + outputFilename, "RunTime=" + runtime );
			//for( int i = 0; i < NUM_AGENTS; i++ )
				//agents.add( getRandomAgent( filename ) );
			agents.add( attsToString( "TUIO", "com.moseph.mra.agent.TuioAgent" ));
				
			runAgents(agents);
	}
	
	static String getRandomAgent( String filename )
	{
		String agentArgs[] = { 
				"Filename=" + filename,
				"Path=" + "/main/s0",
				"Instrument=" + instrumentNames[ (int)( Math.random() * instrumentNames.length )] ,
				"x=" + ( ( random() * 2 - 1 ) * ROOM_X ),
				"y=" + ( ( random() * 2 - 1 ) * ROOM_Y ),
				"Reasoner=Straight"
				};
		return attsToString( "player" + getAgentNumber(), "com.moseph.mra.agent.ScoreAgent", agentArgs );
}

}
