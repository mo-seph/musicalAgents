package com.moseph.mra.agent.run;
import java.io.BufferedReader;
import static com.moseph.mra.agent.AgentUtilities.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.*;

import javax.swing.JFrame;

import com.illposed.osc.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.gui.DummyOSCPanel;

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

public class OSCTest extends AgentRunner
{
	static int NUM_AGENTS = 1;
	static String filename = "examples/InC.mra";
	static int portIn = 15001;
	OSCPortIn input;
	List<String> agents = getStartingAgentDefinitions( filename,  false );
	List<String> agentParams = new Vector<String>();
	
	
	public static void main( String[] args ) throws Exception
	{
		
		@SuppressWarnings("unused") OSCTest oscTest = new OSCTest();
	}
	
	public OSCTest()
	{
		try
		{
			input = new OSCPortIn( portIn );
			OSCListener agentInfo = new OSCListener()
			{
				public void acceptMessage( Date arg0, OSCMessage arg1 )
				{
					acceptAgent( arg1 );
				}
			};
			OSCListener start = new OSCListener()
			{
				public void acceptMessage( Date arg0, OSCMessage arg1 )
				{
					startAgents();
				}
			};
			input.addListener( "/agentParam", agentInfo );
			input.addListener( "/start", start );
			input.startListening();
		}
		catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void startAgents()
	{
		System.out.println( " ++++ Starting agents!");
		ScoreAgent.setAnalysePlaying( false );
		Conductor.setFragmentSize( 2.0 );
		input.stopListening();
		input.close();
		agents.add( attsToString( "osc", "com.moseph.mra.agent.OSCAgent" ) );
		agentParams.add( 0, filename );
		String[] args = agentParams.toArray( new String[agentParams.size()]);
		agents.add( 0, attsToString( "space", "com.moseph.mra.agent.SpaceAgent", args ));
		runAgents(agents);
	}
	
	//Needs: name:x:y:r:g:b 
	void acceptAgent( OSCMessage msg )
	{
		String[] input = msg.getArguments()[0].toString().split( ":");
		System.out.println( " + Got agent: " + msg.getArguments()[0] );
		if( input.length < 6 ) System.err.println( "Bad agent params: " + msg.getArguments()[0]);
		else
		{
			String name = input[0];
			double x = Double.parseDouble( input[1]);
			double y = Double.parseDouble( input[2]);
			agents.add( getRandomAgent( name, x, y ));
		}
		agentParams.add( msg.getArguments()[0].toString() );
	}
	
	
	String getRandomAgent( String name, double x, double y )
	{
		String agentArgs[] = { 
				"Filename=" + filename,
				"Path=" + "/main/s0",
				"Instrument=" + instrumentNames[ (int)( Math.random() * instrumentNames.length )] ,
				"x=" + ( ( x * 2 - 1 ) * ROOM_X ),
				"y=" + ( ( y * 2 - 1 ) * ROOM_Y )
				};
		return attsToString( name, "com.moseph.mra.agent.ScoreAgent", agentArgs );
}

}
