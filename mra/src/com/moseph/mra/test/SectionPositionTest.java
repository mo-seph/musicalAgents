package com.moseph.mra.test;

import com.moseph.mra.*;
import com.moseph.mra.agent.SectionPosition;

import junit.framework.TestCase;

public class SectionPositionTest extends TestCase
{
	Piece p;
	Section main;
	Section a;
	Section b;
	Section c;
	Section d;
	Section e;
	
	protected void setUp() throws Exception
	{
		main = new Section( "main" );
		a = new Section( "a");
		b = new Section( "b");
		c = new Section( "c");
		d = new Section( "d");
		e = new Section( "e");
		p = new Piece( "Test");
		p.addChild( main );
		main.addChild( a );
		main.addChild( d );
		a.addChild( b );
		a.addChild( c );
		d.addChild( e );
	}

	/*
	 * Test method for 'com.moseph.mra.agent.SectionPosition.onLast()'
	 */
	public void testOnLast()
	{
		SectionPosition as = new SectionPosition( a );
		SectionPosition bs = new SectionPosition( b );
		SectionPosition cs = new SectionPosition( c );
		SectionPosition ds = new SectionPosition( d );
		SectionPosition es = new SectionPosition( e );
		SectionPosition mains = new SectionPosition( main );
		assertFalse( as.onLast() );
		as.position = 1;
		assertTrue( as.onLast() );
		assertTrue( bs.onLast() );
		assertTrue( cs.onLast() );
		assertFalse( mains.onLast() );
		assertTrue( ds.onLast() );
		assertTrue( es.onLast() );
		mains.position = 1;
		assertTrue( mains.onLast() );
	}
	
	public void testNumRepeats()
	{
		a.setNumRepeats( 3 );
		SectionPosition as = new SectionPosition( a );
		assertTrue( as.moreRepeats() );
		as.repeats++;
		assertTrue( as.moreRepeats() );
		as.repeats++;
		assertTrue( as.moreRepeats() );
		as.repeats++;
		assertFalse( as.moreRepeats() );
	}

	/*
	 * Test method for 'com.moseph.mra.agent.SectionPosition.equals(Object)'
	 */
	public void testEqualsObject()
	{

	}

}
