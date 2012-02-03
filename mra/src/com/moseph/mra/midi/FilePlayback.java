package com.moseph.mra.midi;

import java.io.*;

import javax.sound.midi.*;

import com.moseph.mra.*;

public class FilePlayback
{
	IncrementalTrackToFragment ittf;
	String filename;
	int partNumber;
	
	public FilePlayback( String filename, int partNumber )
	{
		this.filename = filename;
		this.partNumber = partNumber;
		readInputFile();
	}
	
	public Fragment getNextChunk( double chunkSize )
	{
		return ittf.getFragment( chunkSize );
	}
	
	public double getPosition()
	{
		return ittf.currentPosition;
	}
	
	public boolean hasMoreEvents()
	{
		return ittf.hasMoreEvents();
	}

	void readInputFile()
	{
		File file = new File( filename );
		Sequence s = readMidiFile( file );
		if( s == null )
		{
			System.err.println( "Could not create sequence..." );
			return;
		}
		Track[] tracks = s.getTracks();
		if( tracks.length <= partNumber )
		{
			System.err.println( "Not enough tracks: got " + tracks.length + " needed " + ( partNumber + 1 ) );
			return;
		}
		SongInfo info = new SongInfo( s );
		Track track = tracks[ partNumber ];
		System.out.println( "************************************");
		System.out.println( "Got a seq with " + tracks.length + " tracks in, track with " + track.size() + " events in");
		System.out.println( "************************************");
		info.printSongInfo();
		ittf = new IncrementalTrackToFragment( track, info );
		System.out.println( "Done initialising");
	}
	
	Sequence readMidiFile( File midiFile )
	{
		try
		{
			return MidiSystem.getSequence( midiFile );
		}
		catch (InvalidMidiDataException e)
		{
			System.err.println( "Bad file written" + "(" + midiFile + ") "+ e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err.println( "IO Exception " + "( " + midiFile + ") " + e );
			e.printStackTrace();
		}
		return null;
	}
}
