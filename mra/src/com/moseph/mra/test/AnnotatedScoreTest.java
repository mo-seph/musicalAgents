package com.moseph.mra.test;

import junit.framework.TestCase;

import com.moseph.mra.*;
import com.moseph.mra.agent.MusicianInformation;
import com.moseph.mra.agent.analyser.*;

import static java.lang.Math.*;

public class AnnotatedScoreTest extends TestCase
{
	double quantisation = 4;
	MusicianInformation musA = new MusicianInformation( new Musician( "a"));
	MusicianInformation musB = new MusicianInformation( new Musician( "b"));
	MusicianInformation musC = new MusicianInformation( new Musician( "c"));
	AnnotatedScore score = new AnnotatedScore();
	AnnotatedFragment a = getLargeRandomFragment( musA );
	AnnotatedFragment b = getLargeRandomFragment( musB );
	AnnotatedFragment c = getLargeRandomFragment( musC );
	double offset = 5.0;
	
	public void setUp()
	{
		score.addAnnotation( a );
		score.addAnnotation( b );
		score.addAnnotation( c, offset );
	}
	
	public void testScore()
	{
		Fragment aPlayed = score.getAnnotation( musA.getMusician() ).getPlayed();
		Fragment bPlayed = score.getAnnotation( musB.getMusician() ).getPlayed();
		Fragment cPlayed = score.getAnnotation( musC.getMusician() ).getPlayed();
		Fragment aScored = score.getAnnotation( musA.getMusician() ).getScored();
		Fragment bScored = score.getAnnotation( musB.getMusician() ).getScored();
		Fragment cScored = score.getAnnotation( musC.getMusician() ).getScored();
		
		Fragment cPlayedShifted = cPlayed.clone();
		cPlayedShifted.clear();
		cPlayedShifted.addFragment( cPlayed, offset );
		
		Fragment cScoredShifted = cScored.clone();
		cScoredShifted.clear();
		cScoredShifted.addFragment( cScored, offset );
		
		assertEquals( a.getPlayed(), aPlayed );
		assertEquals( a.getScored(), aScored );
		assertNotSame( b.getPlayed(), aPlayed );
		assertEquals( b.getPlayed(), bPlayed );
		assertEquals( b.getScored(), bScored );
		assertNotSame( c.getPlayed(), cPlayed );
		assertNotSame( c.getScored(), cScored );
		assertNotSame( c.getPlayed(), cPlayedShifted );
		assertNotSame( c.getScored(), cScoredShifted );
	}
	
	public void testAppending()
	{
		AnnotatedFragment a2 = getLargeRandomFragment( musA );
		AnnotatedFragment b2 = getLargeRandomFragment( musB );
		AnnotatedScore score2 = new AnnotatedScore();
		score2.addAnnotation( a2 );
		score2.addAnnotation( b2 );
		Fragment aPlayed2 = a.getPlayed().clone();
		Fragment bPlayed2 = b.getPlayed().clone();
		aPlayed2.addFragment( a2.getPlayed(), 10.0 );
		bPlayed2.addFragment( b2.getPlayed(), 10.0 );
		Fragment aScored2 = a.getScored().clone();
		Fragment bScored2 = b.getScored().clone();
		aScored2.addFragment( a2.getScored(), 10.0 );
		bScored2.addFragment( b2.getScored(), 10.0 );
		
		System.out.println( "Adding to previous score!");
		score.addAnnotatedScore( score2, 10.0 );
		assertEquals( aPlayed2, score.getAnnotation( musA.getMusician() ).getPlayed() );
		assertEquals( bPlayed2, score.getAnnotation( musB.getMusician() ).getPlayed() );
		assertEquals( aScored2, score.getAnnotation( musA.getMusician() ).getScored() );
		assertEquals( bScored2, score.getAnnotation( musB.getMusician() ).getScored() );
		System.out.println(  score.getAnnotation( musB.getMusician() ).getScored() );
	}
	
	public AnnotatedFragment getLargeRandomFragment( MusicianInformation mus )
	{
		Fragment scored = new Fragment( 10.0 );
		scored.setMusician( mus.getMusician() );
		Fragment played = new Fragment( 10.0 );
		played.setMusician( mus.getMusician() );
		int oldPitch = 5;
		for( double beat = 0.5; beat < 9.0; beat += 0.5 )
		{
			int offset =(int)( random() * 20 ) + 5;
			int pitch = oldPitch + offset + 50; 
			oldPitch = offset;
			double playedTime = beat + (random() - 0.5) * 0.2;
			scored.addNote( beat, pitch, 0.5, 0.5 );
			played.addNote( playedTime, pitch, 0.5, 0.5 );
		}
		return new AnnotatedFragment( played, scored, quantisation );
	}
}
