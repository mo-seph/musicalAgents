package com.moseph.mra.test;

import javax.sound.midi.*;

import com.moseph.mra.*;
import com.moseph.mra.midi.*;

import junit.framework.TestCase;

public class TrackToFragmentTest extends TestCase
{
	protected void setUp() throws Exception
	{
	}
	
	public void testExampleFragment()
	{
		//Create a Fragment
		Fragment f = Fragment.getExampleB();
		Fragment underTest = roundtrip( f );
		assertEquals( f, underTest );
	}
	
	public void testGeneratedFragment()
	{
		//Create a Fragment
		Fragment f = new Fragment();
		double offset = 1.0/5;
		for( double pos = 0.0; pos < 5.0; pos += offset )
		{
			f.addNote( pos, 60, 100.0 / 127.0, 0.7*offset );
		}
		Fragment underTest = roundtrip( f );
		assertEquals( f, underTest );
	}
	
	Fragment roundtrip( Fragment f )
	{
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
		TrackToFragment in = new TrackToFragment( t, info, underTest );
		in.addToFragment();
		return underTest;
		
	}

}
