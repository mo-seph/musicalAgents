package com.moseph.mra.midi;

import javax.sound.midi.*;

public class TeeReceiver implements Receiver
{
	Receiver recv;
	String pre = ">>>";
	boolean strip = false;
	boolean mute = false;
	
	public TeeReceiver( Receiver other, String pre )
	{
		this( other );
		this.pre = pre;
	}
	public TeeReceiver( Receiver other )
	{
		this.recv = other;
	}
	
	public void close()
	{
		recv.close();
	}

	public void send( MidiMessage arg0, long arg1 )
	{
		System.out.println( pre + MidiUtilities.messageToString( arg0 ) + "(" + arg1 + ")");
		if( mute ) return;
		if( recv == null ) return;
		if( strip ) recv.send( arg0, -1 );
		else recv.send( arg0, arg1 );
	}

	public String getPre()
	{
		return pre;
	}

	public void setPre( String pre )
	{
		this.pre = pre;
	}
	public boolean isStrip()
	{
		return strip;
	}
	public void setStrip( boolean strip )
	{
		this.strip = strip;
	}
	public boolean isMute()
	{
		return mute;
	}
	public void setMute( boolean mute )
	{
		this.mute = mute;
	}

}
