package com.moseph.mra.agent.run;

import jade.Boot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

public class AgentRunner
{
	static List<String> getStartingAgentDefinitions()
	{
		return getStartingAgentDefinitions( null );
	}

	public static List<String> getStartingAgentDefinitions( String filename )
	{
		return getStartingAgentDefinitions( filename, false );
	}
	
	public static List<String> getStartingAgentDefinitions( String filename, boolean showSpace )
	{ return getStartingAgentDefinitions( filename, showSpace, true ); }
	
	public static List<String> getStartingAgentDefinitions( String filename, boolean showSpace, boolean playLeadIn )
	{
		return getStartingAgentDefinitions( filename, showSpace, playLeadIn, new String[] {} );
	}
	public static List<String> getStartingAgentDefinitions( String filename, boolean showSpace, boolean playLeadIn, String... conductorArgs )
	{
		Vector<String> agents = new Vector<String>();
		if( filename != null )
		{
			String[] args = new String[2];
			if( conductorArgs != null )
			{
				args = new String[ conductorArgs.length + 2 ];
				for( int i = 0; i < conductorArgs.length; i++ ) args[ i+2 ] = conductorArgs[i];
			}
			args[0] = "Filename=" + filename;
			args[1] = "PlayLeadIn=" + playLeadIn;
			agents.add( attsToString("conductor","com.moseph.mra.agent.Conductor", args ) );
		}
		else
		{
			agents.add( attsToString("conductor","com.moseph.mra.agent.Conductor", conductorArgs ) );
		}
		
		if( showSpace ) 
			agents.add( getSpaceDefinition( filename ) );
		return agents;
	}
	
	static String getSpaceDefinition( String filename )
	{
		return attsToString( "space", "com.moseph.mra.agent.SpaceAgent", filename );
	}
	
	
	
	static String attsToString( String name, String c, String... args )
	{
		String ret = name + ":" + c + "(";
		if( args != null ) for( Object o : args ) ret += o + " ";
		System.out.println( "--->" + ret + ")\n" );
		return ret + ")";
	}
	
	static void runAgents( List<String> agents )
	{
		Boot.main( (String[]) agents.toArray( new String[agents.size()]) );
		//waitForEnter();
		//System.exit( 0 );
	}
	
	public static void waitForEnter()
	{
		BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
		try
		{
			in.readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}

}
