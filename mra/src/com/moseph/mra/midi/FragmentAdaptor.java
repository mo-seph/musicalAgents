package com.moseph.mra.midi;

import javax.sound.midi.*;
import javax.swing.JTable.PrintMode;

import com.moseph.mra.*;
import com.moseph.mra.agent.BasicSequencerThread;

public class FragmentAdaptor implements Receiver
{
	Receiver recv;
	Sequencer sequencer = null;
	RealtimeTrackToFragment rtf;
	Fragment fragment;
	boolean echoOutput = false;
	int channel = -1;
	Track track = null;
	
	public FragmentAdaptor( Receiver other, SongInfo songInfo, Fragment fragment )
	{
		this.recv = other;
		this.fragment = fragment;
		rtf = new RealtimeTrackToFragment( songInfo, fragment );
	}
	
	public FragmentAdaptor( Receiver other, SongInfo songInfo, Fragment fragment, Sequencer seq )
	{
		this( other, songInfo, fragment );
		this.sequencer = seq;
	}
	
	public void close()
	{
		if( recv != null ) recv.close();
	}
	
	public void setReceiver( Receiver r )
	{
		this.recv = r;
	}

	public void send( MidiMessage arg0, long arg1 )
	{
		if( channel >= 0 && arg0 instanceof ShortMessage )
			if( ((ShortMessage)arg0).getChannel() != channel ) return;
		if( sequencer == null ) sequencer = BasicSequencerThread.getSequencerDevice();
		if( sequencer == null )
		{
			System.out.println( "No sequencer found");
			return;
		}
		long stamp = sequencer.getTickPosition();
		//System.out.println( "TS: " + arg1 );
		if( recv == null && echoOutput )
		{
			recv = BasicSequencerThread.getOutput();
			if( recv == null ) System.out.println( "Sequencer has no output!");
		}
		if( recv != null )
		{	
			recv.send( arg0, -1 );
		}
		MidiEvent e = new MidiEvent( arg0, stamp );
		//System.out.println( "[" + stamp + "]" );
		//MidiUtilities.printMessage( e, 0 );
		rtf.addEvent( e );
		if( track != null ) track.add( e );
	}

	public Fragment getFragment()
	{
		return fragment;
	}

	public boolean isEchoOutput()
	{
		return echoOutput;
	}

	public void setEchoOutput( boolean echoOutput )
	{
		this.echoOutput = echoOutput;
	}

	public int getChannel()
	{
		return channel;
	}

	public void setChannel( int channel )
	{
		this.channel = channel;
	}

	public Track getTrack()
	{
		return track;
	}

	public void setTrack( Track track )
	{
		this.track = track;
	}

}
