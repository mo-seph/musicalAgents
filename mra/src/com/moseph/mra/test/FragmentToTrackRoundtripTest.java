package com.moseph.mra.test;

import javax.sound.midi.*;

import com.moseph.mra.*;
import com.moseph.mra.midi.*;

import junit.framework.TestCase;

public class FragmentToTrackRoundtripTest extends TestCase
{
	Fragment f;
	
	protected void setUp() throws Exception
	{
		f = new Fragment();
	}
	
	public void testIncrementalBasic()
	{
		for( double i = 0.0; i < 20.0; i++ )
			f.addNote( i, 20 + (int)i, 0.7, 0.7 );
		runIncrementalTest( f, 0.5 );
	}
	
	public void testOverlapping()
	{
		for( double i = 0.0; i < 20.0; i++ )
			f.addNote( i, 20 + (int)i, 0.7, 1.7 );
		runIncrementalTest( f, 0.5 );
	}
	
	public void testVeryOverlapping()
	{
		for( double i = 0.0; i < 20.0; i++ )
			f.addNote( i, 20 + (int)i, 0.7, 10.7 );
		runIncrementalTest( f, 0.5 );
	}
	
	public void testSamePitchOverlapping()
	{
		for( double i = 0.0; i < 20.0; i++ )
			f.addNote( i, 20, 0.7, 1.7 );
		runIncrementalTest( f, 0.5 );
	}
	
	public void testShortThenLong()
	{
		for( double i = 0.0; i < 10.0; i++ )
		{
			double length = 0.2;
			if( i > 3 && i < 7 ) length = 3.8;
			f.addNote( i, 20, 0.7, length );
		}
		runIncrementalTest( f, 0.5 );
		
	}
	
	
	void runIncrementalTest( Fragment f, double step )
	{
		System.out.println( "Starting with fragment: " + f );
		SongInfo info = new SongInfo( new TimeSignature( 4,4 ), 120, 384 );
		Sequence s = null;
		try
		{
			s = new Sequence( Sequence.PPQ, 384 );
		}
		catch( Exception e )
		{
			fail();
		}
		//Put the fragment into a track
		Track t = s.createTrack();
		FragmentToTrack out = new FragmentToTrack( t, info );
		
		for( double pos = 0.0; pos < f.getLength(); pos += step )
			out.addToTrack( f.copyChunk( pos, pos + step ), 0 , pos );
		
		//Now read out of the track, into a fragment
		Fragment underTest = new Fragment();
		TrackToFragment ttf = new TrackToFragment( t, info, underTest );
		ttf.addToFragment();
		underTest.recalculateLength();
		System.out.println( "Roundtripped Fragment: (" + underTest.getLength() + ")\n" + underTest );
		assertEquals( f, underTest );
		
	}
	
	public void testRoundtrip()
	{
		//Create a Fragment
		f = Fragment.getExampleB();
		SongInfo info = new SongInfo( new TimeSignature( 4,4 ), 120, 384 );
		Sequence s = null;
		try
		{
			s = new Sequence( Sequence.PPQ, 384 );
		}
		catch( Exception e )
		{
			fail();
		}
		//Put the fragment into a track
		Track t = s.createTrack();
		FragmentToTrack out = new FragmentToTrack( t, info );
		out.addToTrack( f,0,0 );
		
		//Now read out of the track, into a fragment
		Fragment underTest = new Fragment();
		IncrementalTrackToFragment in = new IncrementalTrackToFragment( t, info );
		for( int i = 1; i <= f.getLength(); i++ )
		{
			System.out.println( "++++ Checking beat " + i );
			underTest.append( in.getFragment( 1.0 ));
			System.out.println( "+ Test fragment " + underTest );
			assertEquals(  f.copyChunk( 0.0, i ), underTest );
		}
	}

}
