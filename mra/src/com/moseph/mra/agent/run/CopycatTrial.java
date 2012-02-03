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

public class CopycatTrial extends AgentRunner
{
	static int NUM_AGENTS = 8;
	
	public static void main( String[] args ) throws Exception
	{
		Conductor.setScheduleRealtimeFragments( false );
			if( args.length > 1) NUM_AGENTS = Integer.parseInt( args[1] );
			
			//List<String> agents = getStartingAgentDefinitions( "examples/Copycat.mra",  true, false, "PlayMetronome=false, EchoRecording=true" );
			List<String> agents = getStartingAgentDefinitions( "examples/Copycat.mra",  true, false, "PlayMetronome=false, EchoRecording=false" );
			for( int i = 0; i < NUM_AGENTS; i++ )
				agents.add( getCopycatAgent() );
			//agents.add( getCopycatAgent( 8.0 ) );
			//agents.add( getCopycatAgent( 8.0 ) );
			runAgents(agents);
	}
	
	static String getCopycatAgent( double offset )
	{
		String agentArgs[] = { 
				"Instrument=" + instrumentNames[ (int)( Math.random() * instrumentNames.length - 1 ) + 1] ,
				"Filename=examples/Copycat.mra",
				"Path=" + "/main/s0",
				"x=" + ( ( random() * 2 - 1 ) * ROOM_X ),
				"y=" + ( ( random() * 2 - 1 ) * ROOM_Y ),
				"Offset=" + offset
				};
		return attsToString( "player" + getAgentNumber() + "-" + offset, "com.moseph.mra.agent.CopycatAgent", agentArgs );
	}
	
	static String getCopycatAgent() 
	{
		double offset = (double)( random() * 16 ) + 1;
		return getCopycatAgent( offset );
	}

}
