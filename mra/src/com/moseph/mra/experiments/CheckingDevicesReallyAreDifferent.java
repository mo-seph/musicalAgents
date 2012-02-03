package com.moseph.mra.experiments;

import javax.sound.midi.*;

public class CheckingDevicesReallyAreDifferent
{

	public static void main( String[] args )
	{
		MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();
		for( int i = 0; i < devices.length; i++ )
			for( int j = i+1; j < devices.length; j++ )
			{
				try
				{
					MidiDevice dev1 = MidiSystem.getMidiDevice( devices[i] );
					MidiDevice dev2 = MidiSystem.getMidiDevice( devices[j] );
					if( dev1 == dev2 ) System.out.println( "Devices same! " + dev1.getDeviceInfo() + ", " + dev2.getDeviceInfo() );
				} catch (MidiUnavailableException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
	}
}
