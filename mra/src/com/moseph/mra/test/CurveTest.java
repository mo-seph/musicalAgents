package com.moseph.mra.test;

import com.moseph.mra.Curve;

import junit.framework.TestCase;

public class CurveTest extends TestCase
{
	Curve c;
	Curve d;
	
	public void setUp()
	{
		//Create a curve which goes from (0,0) to (1,1)
		c = new Curve( 0.0, 1.0, 0.0, 1.0 );
		
		//Create a curve which goes from (0,1) to (1,1.5)
		d = new Curve( 0.0, 1.0, 1.0, 1.5 );
	}
	
	public void testCurveSampling()
	{
		assertEquals( 0.0, c.sample( 0.0 ));
		assertEquals( 0.5, c.sample( 0.5 ));
		assertEquals( 1.0, c.sample( 1.0 ));
		
		assertEquals( 1.0, d.sample( 0.0 ));
		assertEquals( 1.25, d.sample( 0.5 ));
		assertEquals( 1.5, d.sample( 1.0 ));
	}
	
	public void testCurveOutOfBounds()
	{
		assertEquals( 0.0, c.sample( -1.0 ));
		assertEquals( 0.0, c.sample( 2.0 ));
	}
	
	//Curves are designed to be nonidentical, so they can be stored in a
	//SortedSet.
	public void testComparison()
	{
		c = new Curve( 0.0, 1.0, 1.0, 1.5 );
		Curve c1 = c.clone();
		System.out.println( c.compareTo(c1) + ""  );
		System.out.println( c.equals(c1) + ""  );
	}

}
