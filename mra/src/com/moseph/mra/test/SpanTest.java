package com.moseph.mra.test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static com.moseph.mra.MRAUtilities.*;
import com.moseph.mra.Span;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SpanTest extends TestCase
{

	Span a;
	Span b;
	Span c;
	Span d;


	public void setUp()
	{
		a = new Span( 0.0, 0.5 );
		b = new Span( 0.0, 0.5 );
		c = new Span( 1.0, 1.5 );
		d = new Span( 0.0, 1.5 );
	}

	public void testEquality()
	{
		assertEquals( a, b );
		assertNotSame( a, c );
		assertNotSame( a, d );

	}


	public void testAddOffset()
	{
		a.addOffset( 1.0 );
		assertEquals( a, c );
	}

	public void testMerge()
	{
		Span first = new Span( 0.0, 0.5, false, true );
		Span second = new Span( 0.5, 1.0, true, false );
		assertTrue( "Can't merge spans", first.mergeIfPossible(second));
	}

	public static Test suite() 
	{ 
		return new TestSuite(SpanTest.class);
	}

}
