package com.moseph.mra.midi;

import javax.sound.midi.*;

public class TimestampStripper implements Receiver
{
	Receiver recv;
	
	public TimestampStripper( Receiver other )
	{
		this.recv = other;
	}
	
	public void close()
	{
		recv.close();
	}

	public void send( MidiMessage arg0, long arg1 )
	{
		recv.send( arg0, -1 );
	}

}
