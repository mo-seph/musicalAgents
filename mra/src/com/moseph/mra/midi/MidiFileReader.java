package com.moseph.mra.midi;
//import com.moseph.music.representation.*;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import com.moseph.mra.*;

//Test program to read a midi file...
//NOTE: pulses and ticks seem to be identical (PPQ is given in ticks per beat...)

/**
Utility class to read in a MIDI file and split it up into a MAXML representation.
At present, only files in PPQ format (as opposed to SMPTE) are
supported. Also Type 0 files (where all the data is in a single chunk)
are not supported. Pulses and Ticks are assumed to be identical.
@author David Murray-Rust
@version $Revision$, $Date$
*/
public class MidiFileReader
{
	Track[] tracks;
	Sequence sequence;
	SongInfo songInfo;
	MidiUtilities midiUtils = new MidiUtilities();
	int[] currentEvents;
	double timeScaling = 1.0;

	public static void main( String[] args )
	{
		try
		{
			String filename = "output/test.mid";
			if( args.length > 0 ) filename = args[ 0 ];
			MidiFileReader fileReader = new MidiFileReader();
			fileReader.readFile( filename );
			Hashtable<String,Track> tracks = fileReader.getInstrumentalTracks();
			for( String trackname : tracks.keySet() )
			{
				System.out.println( trackname );
			}
			List<Fragment> f= fileReader.getFragments();
			for( Fragment frag : f )
			{
				System.out.println( frag );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.err.println( e );
			System.exit( 1 );
		}
	}

	/**
	reads in the specified MIDI file and prepares to segment it
	@param filename
	@throws InvalidMidiDataException
	@throws IOException
	*/
	public void readFile( String filename ) throws InvalidMidiDataException, IOException
	{
		File myMidiFile = new File(filename);
		sequence = MidiSystem.getSequence(myMidiFile);
		tracks = sequence.getTracks();
		System.err.println( "Found " + tracks.length + " tracks");
		currentEvents = new int[ tracks.length ];
		songInfo = new SongInfo( sequence );
	}
	
	public void readFile( String filename, double timeScale ) throws InvalidMidiDataException, IOException
	{
		readFile( filename );
		songInfo.setTimeScale( timeScale );
	}
	


	public List<Fragment> getFragments()
	{
		List<Fragment> fragments = new Vector<Fragment>();
		for( Track t : getInstrumentalTracks().values() )
		{
			fragments.add( midiUtils.trackToFragment( t, songInfo ) );
		}
		return fragments;
	}

	/**
	Looks through the loaded file to see which tracks contain
	instrumental data. This is designed to weed out tracks which have
	been created just for a place to write in the sequencer, and
	tracks which only contain meta data (key/time signatures)
	*/
	public Hashtable<String,Track> getInstrumentalTracks()
	{
		Hashtable<String,Track> instrumentalTracks = new Hashtable<String,Track>();
		for( Track t : tracks )
		{
			String trackName = searchForTrackname( t );
			if( trackName != null && searchForNoteData( t ) )
			{
				instrumentalTracks.put( trackName, t );
			}
			else if( searchForNoteData( t ))
				instrumentalTracks.put(  "Unknown", t );
		}
		return instrumentalTracks;
	}

	/**
	Checks a track to see if it has note data in.
	Returns <code>true</code> as soon as any data is found (working on the principle
	that tracks without notes won't have much data so it'll fail fast)
	@param t
	*/
	public boolean searchForNoteData( Track t )
	{
		for( int i = 0; i < t.size(); i++ )
		{
			if( MidiUtilities.isNoteOnMessage( t.get( i ).getMessage() ) )
			{
				return true;
			}
		}
		return false;
	}

	/**
	Looks for the first {@link MetaMessage} indicating a track title
	and returns it's contents.
	@param t track to search
	*/
	public String searchForTrackname( Track t )
	{
		for( int i = 0; i < t.size(); i++ )
		{
			MidiEvent e = t.get( i );
			MidiMessage m = e.getMessage();
			if( MidiUtilities.isTrackName( m ) )
			{
				return MidiUtilities.getDataAsString( (MetaMessage)m );
			}
			if( e.getTick() > 0 )
			{
				return null;
			}
		}
		return null;
	}

	/*
	public class MidiFileConverter implements MAXMLConverter
	{
		public List<Fragment> fromFile( String filename )
		{
		}

		public void toFile( List<Fragment> fragments, String filename )
		{
		}
	}
	*/
}

