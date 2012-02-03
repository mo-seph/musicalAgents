package com.moseph.mra.test;


import static com.moseph.mra.test.TestUtilities.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;

import junit.framework.TestCase;

public class ScoreFollowerTest extends TestCase
{
	
	Piece p;
	Context context = new Context( null );
	Musician o = new Musician( "other");
	Musician b = new Musician( "bob");
	MusicianInformation other = new MusicianInformation( o );
	MusicianInformation bob = new MusicianInformation( b );
	ScoreFollower follow = new ScoreFollower( context );
	Fragment f = new Fragment();
	Fragment leftHand = new Fragment( "LeftHand");
	double quantisation = 4.0;
	
	public void setUp()
	{
		p = new Piece( "test");
		p.addAttribute( new Attribute( "Quantisation", "4.0"));
		context.setPiece( p );
		Section a = new Section( "a");
		f.addNote( 0.0, 60, 0.7, 1.0 );
		f.addNote( 1.0, 60, 0.7, 1.0 );
		a.addChannel( f );
		leftHand.addNote( 0.5, 70, 0.7, 1.0 );
		a.addChannel( leftHand );
		Section main = new Section( "main");
		p.addChild( main );
		main.addChild( a );
		follow = new ScoreFollower( context );
	}
	
	//Some of these setMusician, setName calls are just to make equals() behave
	public void testFollowing()
	{
		follow.setPart( bob, "LeftHand");
		Fragment played = f.clone();
		f.setMusician( o );
		played.setMusician( o );
		Score playedScore = new Score();
		playedScore.add( played );
		
		Fragment bobPlayed = leftHand.clone();
		bobPlayed.setMusician( b );
		bobPlayed.setName( "Notes");
		playedScore.add( bobPlayed );
		
		AnnotatedScore annotatedScore = follow.annotateScores( playedScore );
		System.out.println( annotatedScore );
		AnnotatedFragment annotated = annotatedScore.getAnnotation( o );
		
		assertEquals( f, annotated.getPlayed() );
		assertEquals( bobPlayed, annotatedScore.getAnnotation( "bob").getPlayed() );
	}
	
	
}
