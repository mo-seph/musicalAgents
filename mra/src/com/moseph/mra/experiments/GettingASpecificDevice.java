package com.moseph.mra.experiments;

import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;

import com.moseph.mra.agent.RecordAgentDetails;
import com.moseph.mra.agent.run.AgentRunner;
import com.moseph.mra.midi.TeeReceiver;

public class GettingASpecificDevice
{
	static String devName = "SD20";
	static int devNum = 0;
	
	public static void main( String[] args )
	{
		if( args.length > 0 ) devName = args[0];
		if( args.length > 1 ) devNum = Integer.parseInt( args[1] );
		//new GettingASpecificDevice( devName );
		//new GettingASpecificDevice(  "SD20" );
		openAllDevices();
		//new GettingASpecificDevice( "AMT8");
		new GettingASpecificDevice( "AMT8", 1 );
		AgentRunner.waitForEnter();
	}
	public GettingASpecificDevice( String name, int number )
	{
		getASpecificDevice( name, number, name + ":" + number + "\t");
	}
	public GettingASpecificDevice( String name )
	{
		int i = 0;
		while( getASpecificDevice( name, i, "++" + name + ":" + i + "\t" ) ) {i++;}
	}
	
	boolean getASpecificDevice( String name, int number, String prefix )
	{
		System.out.println( "Trying " + name + ":" + number );
		MidiDevice.Info info = RecordAgentDetails.getMidiDevice( name, number );
		if( info == null )
		{
			System.err.println( "No such device: " + name + ", " + number );
			return false;
		}
		TeeReceiver recv = new TeeReceiver( null, prefix );
		try
		{
			MidiDevice dev = MidiSystem.getMidiDevice( info );
			dev.open();
			dev.getTransmitter().setReceiver( recv );
			return true;
		} catch (MidiUnavailableException e)
		{
			System.out.println( "Couldn't make device " + name + ", " + number + ": " + e.getMessage() );
			//e.printStackTrace();
		}
		return false;
	}
	
	public static void openAllDevices()
	{
		for( Info i : MidiSystem.getMidiDeviceInfo() )
		{
			try
			{
				MidiSystem.getMidiDevice( i ).open();
			} catch (MidiUnavailableException e)
			{
				e.printStackTrace();
			}
		}
	}

}
