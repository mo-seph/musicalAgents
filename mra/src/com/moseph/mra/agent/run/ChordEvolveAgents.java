package com.moseph.mra.agent.run;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import jade.Boot;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class ChordEvolveAgents extends AgentRunner
{
	public static void main( String[] args ) throws Exception
	{
		doAgents( 5 );
	}
	
	public static void doAgents( int number ) throws Exception
	{
		List<String> agents = getStartingAgentDefinitions();
		for( int i = 0; i < number; i++ )
		{
			String args[] = { i + "", "" + number };
			agents.add( attsToString( "random" + i, "com.moseph.mra.agent.ChordEvolveAgent", args ) );
		}
		runAgents( agents );
	}
	
}
