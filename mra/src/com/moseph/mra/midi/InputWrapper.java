package com.moseph.mra.midi;

import java.util.*;

import javax.sound.midi.*;

public class InputWrapper extends DeviceWrapper implements Transmitter, Receiver
{
	List<Receiver> receivers;
	Transmitter transmitter;
	public InputWrapper( MidiDevice dev, String name ) throws MidiUnavailableException
	{
		super( dev, name );
		System.out.println( "InputWrapper created for " + dev.getDeviceInfo().getName() );
		transmitter = dev.getTransmitter();
		transmitter.setReceiver( this );
		receivers = new ArrayList<Receiver>();
	}

	public Receiver getReceiver()
	{
		return null;
	}

	public void setReceiver( Receiver arg0 )
	{
		receivers.add( arg0 );
	}

	public void send( MidiMessage message, long timeStamp )
	{
		//System.out.println( "Sending from input " + dev.getDeviceInfo().getName() );
		for( Receiver r : receivers ) r.send( message, timeStamp );
	}

}
