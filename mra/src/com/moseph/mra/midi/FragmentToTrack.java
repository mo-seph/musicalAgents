package com.moseph.mra.midi;

import javax.sound.midi.*;
import static com.moseph.mra.midi.MidiUtilities.*;

import com.moseph.mra.*;

public class FragmentToTrack
{
	Track track;
	SongInfo info;
	int channel;

	public FragmentToTrack( Track t, SongInfo s )
	{
		track = t;
		info = s;
	}
	
	public void addToTrack( Fragment fragment, int channel )
	{
		addToTrack( fragment, channel, 0.0 );
	}

	public void addToTrack( Fragment fragment, int channel, double offset )
	{
		//System.out.println( "Adding fragment: " + fragment );
		for( Note note : fragment.getNotes() )
		{
			addNote( note, channel, offset );
		}
	}

	void addNote( Note n, int channel, double offset )
	{
		long startTicks = info.getBeatTick( n.getOnset() + offset );
		//Old version, to make notes slightly shorter (EEeeurgh...)
		//long endTicks = info.getBeatTick( n.getEndTime() + offset - NOTE_OFF_FUZZ );
		long endTicks = info.getBeatTick( n.getEndTime() + offset );
		if( endTicks - startTicks < 2 ) return;
		//System.out.println( ">>>>>From " + startTicks + " to " +  endTicks );
		int pitch = n.getPitchNumber();
		int vel = (int)(n.getVelocity() * 127 );
		if( vel > 127 ) vel = 127;
		if( vel < 1 ) vel = 1;
		ShortMessage on = noteOn( channel, pitch, vel );
		ShortMessage off = noteOff( channel, pitch, vel );
		if( off == null ) System.out.println( "Bad NOTE: " + n );
		//log.log( INFO, "Scheduling note at tick " + startTicks );
		if( ! n.getStartsBefore() ) track.add( new MidiEvent( on, startTicks ) );
		if( ! n.getLongerThan() ) track.add( new MidiEvent( off, endTicks ) );
	}
	
	public void addEvent( MidiMessage msg, double beat )
	{
		track.add( new MidiEvent( msg, info.getBeatTick( beat ) ) );
	}

}