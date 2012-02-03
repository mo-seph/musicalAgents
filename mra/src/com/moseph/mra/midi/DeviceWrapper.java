package com.moseph.mra.midi;

import javax.sound.midi.*;

public class DeviceWrapper
{
	protected MidiDevice dev;
	String name;

	public DeviceWrapper( MidiDevice dev, String name ) throws MidiUnavailableException
	{
		System.out.println( "Creating wrapper for " + dev.getDeviceInfo().getName() );
		this.dev = dev;
		dev.open();
		this.name = name;
	}
	
	public void close()
	{
		dev.close();
	}

	public MidiDevice.Info getDeviceInfo()
	{
		return dev.getDeviceInfo();
	}
	
	public String toString()
	{
		return name;
	}

}
