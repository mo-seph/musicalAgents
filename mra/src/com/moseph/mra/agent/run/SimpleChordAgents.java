package com.moseph.mra.agent.run;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import jade.Boot;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class SimpleChordAgents
{
	public static void main( String[] args ) throws Exception
	{
		runAgents( 10 );
	}
	
	public static void runAgents( int number ) throws Exception
	{
			Vector<String> agents = new Vector<String>();
			agents.add( attsToString("conductor","com.moseph.mra.agent.Conductor", new Object[0]) );
			for( int i = 0; i < number; i++ )
			{
				Object args[] = { new Integer( i), new Integer( number ) };
			
				agents.add( attsToString( "chord" + i, "com.moseph.mra.agent.SimpleChordAgent", args ) );
			}
			Boot.main( (String[]) agents.toArray( new String[number+1]) );
			BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
			in.readLine();
	}
	
	static String attsToString( String name, String c, Object[] args )
	{
		String ret = name + ":" + c + "(";
		for( Object o : args ) ret += o + " ";
		return ret + ")";
	}
}
