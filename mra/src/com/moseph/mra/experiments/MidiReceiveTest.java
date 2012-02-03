package com.moseph.mra.experiments;

import java.util.*;

import javax.sound.midi.*;

import com.moseph.mra.midi.MidiUtilities;

public class MidiReceiveTest implements Receiver
{
	String name;
	
	public static void main( String[] args )
	{
		List<MidiReceiveTest> tests = new Vector<MidiReceiveTest>();
		for( MidiDevice dev : MidiUtilities.getInputDevices() )
		{
			try
			{
				dev.open();
				MidiReceiveTest t = new MidiReceiveTest( dev.getDeviceInfo().getName() );
				dev.getTransmitter().setReceiver( t );
				tests.add(  t );
			}
			catch( Exception e )
			{
				System.out.println( "Could not open device: " + e );
			}
		}
		MidiUtilities.waitForEnter();
		for( MidiDevice dev : MidiUtilities.getInputDevices() )
			dev.close();
	}
	
	public MidiReceiveTest( String name )
	{
		this.name = name;
	}

	//Do nothing
	public void close() { }

	public void send( MidiMessage message, long timeStamp )
	{
		String msg = MidiUtilities.messageToString( message );
		System.out.println( name + " got " + msg + " at " + timeStamp );
	}
	

}
