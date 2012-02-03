package com.moseph.mra.experiments;

import java.util.List;

import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;

public class DeviceNameTest
{
	public static void main( String[] args )
	{
		MidiDevice.Info[] devs = MidiSystem.getMidiDeviceInfo();
		for( Info dev : devs )
		{
			MidiDevice device = null;
			try
			{
				device = MidiSystem.getMidiDevice( dev );
			} catch (MidiUnavailableException e)
			{
				e.printStackTrace();
			}
			System.out.println( "____________");
			System.out.println( "Description:" + dev.getDescription() );
			System.out.println( "Name:" + dev.getName() );
			System.out.println( "Vendor:" + dev.getVendor() );
			System.out.println( "Version:" + dev.getVersion() );
			if( device != null ) System.out.println( "Receivers:" + device.getMaxReceivers() );
			if( device != null ) System.out.println( "Transmitters:" + device.getMaxTransmitters() );
		}
	}

}
