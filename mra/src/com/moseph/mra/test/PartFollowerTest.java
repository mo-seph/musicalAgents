package com.moseph.mra.test;


import static com.moseph.mra.test.TestUtilities.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.AnnotatedFragment;

import junit.framework.TestCase;

public class PartFollowerTest extends TestCase
{
	Piece p;
	Context context = new Context( null );
	Musician o = new Musician( "other");
	MusicianInformation other = new MusicianInformation( o );
	PartFollower follow; 
	Fragment f = new Fragment();
	double quantisation = 4.0;
	
	public void setUp()
	{
		p = new Piece( "test");
		context.setPiece( p );
		Section a = new Section( "a");
		f.addNote( 0.0, 60, 0.7, 1.0 );
		f.addNote( 1.0, 60, 0.7, 1.0 );
		a.addChannel( f );
		Section main = new Section( "main");
		p.addChild( main );
		main.addChild( a );
		follow = new PartFollower( context, other, null );
	}
	
	public void testFollowing()
	{
		Fragment played = f.clone();
		follow.newMaterial( f );
		Fragment scored = follow.getNextChunk( f.getLength() );
		AnnotatedFragment af = new AnnotatedFragment(played, scored, quantisation );
		assertEquals( f, scored );
	}
	
}
