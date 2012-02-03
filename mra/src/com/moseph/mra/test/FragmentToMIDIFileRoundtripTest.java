package com.moseph.mra.test;

import java.io.*;

import javax.sound.midi.*;

import com.moseph.mra.*;
import com.moseph.mra.midi.*;

import junit.framework.TestCase;
import static com.moseph.mra.agent.SequencerThread.*;

public class FragmentToMIDIFileRoundtripTest extends TestCase
{
	Fragment f = Fragment.getExampleB();
	SongInfo info = new SongInfo( new TimeSignature( 4,4 ), 120, 384 );
	Sequence s = null;
	Track t = null;
		
	protected void setUp() throws Exception
	{
		try
		{
			s = new Sequence( Sequence.PPQ, 384 );
		}
		catch( Exception e )
		{
			fail();
		}
		t = s.createTrack();
	}
	
	void writeSequence( File midiFile )
	{
		try
		{
			MidiSystem.write( s, 1, midiFile );
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail( "Couldn't write MIDI file!");
		}
	}
	
	Sequence readFile( File midiFile )
	{
		try
		{
			return MidiSystem.getSequence( midiFile );
		}
		catch (InvalidMidiDataException e)
		{
			e.printStackTrace();
			fail( "Bad file written");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail( "IO Exception");
		}
		return null;
	}
	
	void testEqual( IncrementalTrackToFragment in, Fragment target )
	{
		Fragment readIn = new Fragment();
		for( int i = 1; i <= target.getLength(); i++ )
		{
			System.out.println( "++++ Checking beat " + i );
			readIn.append( in.getFragment( 1.0 ));
			System.out.println( "+ Test fragment " + readIn );
			assertEquals(  target.copyChunk( 0.0, i ), readIn );
		}
		
	}
	
	public void testRoundtrip()
	{
		FragmentToTrack out = new FragmentToTrack( t, info );
		out.addToTrack( f,0,0 );
		File midiFile = new File( "tmp/test.mid");
		writeSequence( midiFile );
		Track track = readFile( midiFile ).getTracks()[0];
		testEqual( new IncrementalTrackToFragment( track, info ), f );
	}
	
	public void testMetaRoundtrip()
	{
		FragmentToTrack out = new FragmentToTrack( t, info );
		//f.addNote( new Note( 4.0, 50, 1.0, 100.0, false, true ) );
		out.addToTrack( f,0,0 );
		
		try
		{
			MetaMessage reqMessage = new MetaMessage();
			reqMessage.setMessage( PROPRIETRY_MESSAGE, new byte[] { REQUEST_MUSIC_MESSAGE }, 1 );
			MetaMessage schedMessage = new MetaMessage();
			schedMessage.setMessage( PROPRIETRY_MESSAGE, new byte[] { FRAGMENT_DEADLINE_MESSAGE },1);
			t.add( new MidiEvent( reqMessage, 10 ) );
			t.add( new MidiEvent( schedMessage, 11 ) );
		}
		catch( Exception e )
		{
			fail( e + "" );
		}
		File midiFile = new File( "tmp/test.mid");
		writeSequence( midiFile );
		Track track = readFile( midiFile ).getTracks()[0];
		testEqual( new IncrementalTrackToFragment( track, info ), f );
	}
	
	/*
	public void testReadingFunnyFile()
	{
		Track track = readFile( new File( "output10.mid") ).getTracks()[0];
		Fragment saved =  new IncrementalTrackToFragment( track, info ).getFragment( 100  );
		System.out.println( saved );
	}
		MetaMessage reqMessage = new MetaMessage();
			reqMessage.setMessage( PROPRIETRY_MESSAGE, new byte[] { REQUEST_MUSIC_MESSAGE }, 1 );
			MetaMessage schedMessage = new MetaMessage();
			schedMessage.setMessage( PROPRIETRY_MESSAGE, new byte[] { FRAGMENT_DEADLINE_MESSAGE },
	 */

}
