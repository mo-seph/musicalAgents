package com.moseph.mra.midi;

import javax.sound.midi.*;

import com.moseph.mra.*;
import static com.moseph.mra.midi.MidiUtilities.*;

public class RealtimeTrackToFragment extends TrackToFragment
{
	public RealtimeTrackToFragment( SongInfo s, Fragment fragment )
	{
		super( null, s, fragment );
	}
	
	public void addEvent( MidiEvent e )
	{
		if( isNoteOnMessage( e.getMessage() )) addNoteEvent( e, fragment );
		if( isNoteOffMessage( e.getMessage() )) closeNotes( e );
	}
	
	/*
	Note createNote( double noteBeat, int noteNumber, double vel )
	{
		return new Note( noteBeat, noteNumber, vel, 1000 );
	}
	*/
	
}
