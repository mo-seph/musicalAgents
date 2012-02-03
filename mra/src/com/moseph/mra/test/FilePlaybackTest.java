package com.moseph.mra.test;

import jade.Boot;
import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.*;

import java.io.File;
import java.util.*;

import javax.sound.midi.*;

import sun.misc.FpUtils;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.run.AgentRunner;
import com.moseph.mra.midi.*;
import static com.moseph.mra.midi.MidiUtilities.*;
import static java.lang.Math.*;

import junit.framework.TestCase;

/**
 * This is a roundtrip test to test the incremental recording system, in a similar manner to that used in the real
 * application.
 * @author dave
 *
 */
public class FilePlaybackTest extends TestCase
{
	FilePlayback fpb = null;
	public void setUp()
	{
	}
	
	public void testSimpleOperation()
	{
		Fragment frag = new Fragment();
		for( double d = 0; d < 5; d += 1.0 )
			frag.addNote( d, 45, 0.7, 0.6 );
		runTest( frag, "tmp/test-a.mid", 0, 1.0 );
	}
	
	public void testBasicOperation()
	{
		runTest( new Fragment(), "tmp/test-a.mid", 0, 1.0 );
	}
	
	public void testOtherTrack()
	{
		Fragment frag = new Fragment();
		for( double d = 0; d < 5; d += 1.0 )
			frag.addNote( d, 45, 0.7, 0.6 );
		runTest( frag, "tmp/test-a.mid", 4, 1.0 );
	}
	
	//Fails about 1 in 10 - probably OK!
	public void testLongRandomSequence()
	{
		Fragment frag = new Fragment();
		double pos = 0.0;
		double dur,rest;
		while( pos < 500 )
		{
			dur = 0.1 + random() * 0.5;
			rest = 0.1 + random() * 0.5;
			frag.addNote( pos, 30 + (int)( random() * 50 ), 0.693, dur );
			pos += dur + rest;
		}
		runTest( frag, "tmp/test-a.mid", 4, 1.0 );
	}
	
	void runTest( Fragment frag, String filename, int trackNumber, double chunkSize )
	{
		try
		{
			File f = new File( filename );
			f.delete();
			SongInfo si = new SongInfo( new TimeSignature( 4,4 ), 60, 384 );
			Sequence seq = new Sequence( Sequence.PPQ, 384 );
			Track track = null;
			for( int i = 0; i <= trackNumber; i++ )
				track = seq.createTrack();
			FragmentToTrack ftt = new FragmentToTrack(track, si );
			ftt.addToTrack( frag, 0 );
			MidiSystem.write( seq, 1, new File( filename ) );
			
			fpb = new FilePlayback( filename, trackNumber );
			System.out.println( "Created FilePlayback");
		}
		catch( Exception e )
		{
			e.printStackTrace();
			fail( e.getMessage() );
		}
		
		Fragment returned = new Fragment();
		Fragment chunk;
		for( double pos = 0.0; pos < frag.getLength(); pos += chunkSize )
		{
			double playbackPos = fpb.getPosition();
			chunk = fpb.getNextChunk( chunkSize );
			//assertEquals( frag.copyChunk( pos, pos + chunkSize ), chunk );
			returned.addFragment( chunk, playbackPos );
			//returned.append( chunk );
		}
		System.out.println( "Got:\n" + returned + "\n\nExpected: \n" + frag );
		assertEquals( frag, returned );
	}
}