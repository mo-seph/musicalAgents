package com.moseph.mra.test;

import java.util.*;

import junit.framework.TestCase;

import com.moseph.mra.*;
import com.moseph.mra.agent.MusicianInformation;
import com.moseph.mra.agent.analyser.*;

import static java.lang.Math.*;

public class AnalysisWindowTest extends TestCase
{
	double quantisation = 4;
	MusicianInformation musA = new MusicianInformation( new Musician( "a"));
	MusicianInformation musB = new MusicianInformation( new Musician( "b"));
	AnnotatedScore score = new AnnotatedScore();
	double length = 20.0;
	double windowSize = 5.0;
	double step = 1.0;
	AnnotatedFragment a = getLargeRandomFragment( length, musA );
	AnnotatedFragment b = getLargeRandomFragment( length, musB );
	Fragment aPlayed = a.getPlayed();
	Fragment bPlayed = b.getPlayed();
	Fragment aScored = a.getScored();
	Fragment bScored = b.getScored();
	List<AnnotatedScore> segmented = new Vector<AnnotatedScore>();
	List<Score> segmentedNormal = new Vector<Score>();
	AnalysisWindow window = new AnalysisWindow( windowSize );
	
	
	public void setUp()
	{
		for( double beat = 0.0; beat < length; beat += step )
		{
			Fragment ap = aPlayed.copyChunk( beat, beat + step );
			Fragment as = aScored.copyChunk( beat, beat + step );
			Fragment bp = bPlayed.copyChunk( beat, beat + step );
			Fragment bs = bScored.copyChunk( beat, beat + step );
			AnnotatedFragment apa = new AnnotatedFragment( ap, as, quantisation );
			AnnotatedFragment bpa = new AnnotatedFragment( bp, bs, quantisation );
			AnnotatedScore annoScore = new AnnotatedScore();
			annoScore.addAnnotation( apa );
			annoScore.addAnnotation( bpa );
			annoScore.forceLength( step );
			segmented.add( annoScore );
			
			Score nonAnno = new Score();
			nonAnno.add( ap, 0.0 );
			nonAnno.add( bp, 0.0 );
			nonAnno.forceLength( step );
			segmentedNormal.add( nonAnno );
		}
		
	}
	
	public void testBasicWindowing()
	{
		double pos = 0.0;
		for( AnnotatedScore scoreChunk : segmented )
		{
			window.add( scoreChunk );
			pos += scoreChunk.getLength();
			double startPos = max( 0.0, pos - windowSize );
			Fragment aPlayTarget = aPlayed.copyChunk( startPos, pos );
			Fragment bPlayTarget = bPlayed.copyChunk( startPos, pos );
			Fragment aScoreTarget = aScored.copyChunk( startPos, pos );
			Fragment bScoreTarget = bScored.copyChunk( startPos, pos );
			AnnotatedScore windowScore = (AnnotatedScore)window.getCurrent();
			//System.out.println( windowScore );
			
			assertEquals( aPlayTarget, windowScore.getAnnotation( musA.getMusician() ).getPlayed() );
			assertEquals( aScoreTarget, windowScore.getAnnotation( musA.getMusician() ).getScored() );
			assertEquals( bPlayTarget, windowScore.getAnnotation( musB.getMusician() ).getPlayed() );
			assertEquals( bScoreTarget, windowScore.getAnnotation( musB.getMusician() ).getScored() );
			System.out.println( pos + " ok!");
		}
	}
	
	public void testNormalScore()
	{
		double pos = 0.0;
		for( Score scoreChunk : segmentedNormal )
		{
			window.add( scoreChunk );
			pos += scoreChunk.getLength();
			double startPos = max( 0.0, pos - windowSize );
			Fragment aPlayTarget = aPlayed.copyChunk( startPos, pos );
			Fragment bPlayTarget = bPlayed.copyChunk( startPos, pos );
			Score windowScore = window.getCurrent();
			
			assertEquals( aPlayTarget, windowScore.getFragmentForMusician( musA.getMusician() ) );
			assertEquals( bPlayTarget, windowScore.getFragmentForMusician( musB.getMusician() ) );
			System.out.println( pos + " ok!");
		}
	}
	
	public Fragment getLargeRanFragment( double length, MusicianInformation mus )
	{
		Fragment played = new Fragment( 10.0 );
		played.setMusician( mus.getMusician() );
		int oldPitch = 5;
		for( double beat = 0.5; beat < 9.0; beat += 0.5 )
		{
			int offset =(int)( random() * 20 ) + 5;
			int pitch = oldPitch + offset + 50; 
			oldPitch = offset;
			played.addNote( beat, pitch, 0.5, 0.5 );
		}
		return played;
	}

	public AnnotatedFragment getLargeRandomFragment( double length, MusicianInformation mus )
	{
		Fragment scored = new Fragment( 10.0 );
		scored.setMusician( mus.getMusician() );
		Fragment played = new Fragment( 10.0 );
		played.setMusician( mus.getMusician() );
		int oldPitch = 0;
		for( double beat = 0.5; beat < 9.0; beat += 0.5 )
		{
			int pitch =(int)( random() * 20 ) + 45;
			if( pitch == oldPitch ) pitch += (int)(random() * 5 );
			oldPitch = pitch;
			double playedTime = beat + (random() - 0.5) * 0.2;
			scored.addNote( beat, pitch, 0.5, 0.5 );
			played.addNote( playedTime, pitch, 0.5, 0.5 );
		}
		return new AnnotatedFragment( played, scored, quantisation );
	}
	
}
