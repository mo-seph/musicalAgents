package com.moseph.mra.midi;

import java.util.*;

import javax.sound.midi.*;
import static com.moseph.mra.midi.MidiUtilities.*;

public class FakePianist implements Transmitter
{
	Receiver rcv = null;
	int pitch;
	
	public FakePianist()
	{
		TimerTask t = new TimerTask()
		{
			public void run()
			{
				setPitch( (int)( 12.0 * Math.random() ) * 2 + 60 );
				if( rcv != null )
					rcv.send( noteOn( getPitch(), 100 ), -1 );
			}
		};
		TimerTask t2 = new TimerTask()
		{
			public void run()
			{
				if( rcv != null )
					rcv.send( noteOff( getPitch(), 100 ), -1 );
			}
		};
		Timer time = new Timer();
		time.schedule( t, 1000, 200 );
		time.schedule( t2, 1150, 200 );
	}
	
	public void close()
	{
	}

	public Receiver getReceiver()
	{
		return rcv;
	}

	public void setReceiver( Receiver receiver )
	{
		rcv = receiver;
	}

	public int getPitch()
	{
		return pitch;
	}

	public void setPitch( int pitch )
	{
		this.pitch = pitch;
	}


}
