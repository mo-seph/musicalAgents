package com.moseph.mra.test;

import com.moseph.mra.TemporalEvent;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TemporalEventTest extends TestCase
{
	TemporalEvent a;
	TemporalEvent b;
	TemporalEvent c;
	TemporalEvent d;


	public void setUp()
	{
		a = new TemporalEvent( 0.0 );
		b = new TemporalEvent( 0.0 );
		c = new TemporalEvent( 1.0 );
	}

	public void testEquality()
	{
		assertEquals( a, b );
		assertNotSame( a, c );
	}


	public void testAddOffset()
	{
		a.addOffset( 1.0 );
		assertEquals( a, c );
	}


	public static Test suite() 
	{ 
		return new TestSuite(TemporalEventTest.class);
	}
}
