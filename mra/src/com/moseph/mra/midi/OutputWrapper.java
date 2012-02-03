package com.moseph.mra.midi;

import javax.sound.midi.*;

public class OutputWrapper extends DeviceWrapper implements Receiver
{
	Receiver receiver;
	
	public OutputWrapper( MidiDevice dev, String name ) throws MidiUnavailableException
	{
		super( dev, name );
		receiver = dev.getReceiver();
		System.out.println( "OutputWrapper created for " + dev.getDeviceInfo().getName() );
	}

	public void send( MidiMessage message, long timeStamp )
	{
		//System.out.println( "Sending to output " + dev.getDeviceInfo().getName() );
		receiver.send( message, timeStamp );
	}

}
