package com.moseph.mra.test;

import com.moseph.mra.*;

import junit.framework.TestCase;

public class CurveContainerTest extends TestCase
{
	CurveContainer c = new CurveContainer( 1.0 );
	CurveContainer d = new CurveContainer( 1.0 );
	CurveContainer e = new CurveContainer( 1.0 );
	CurveContainer f = new CurveContainer( 1.0 );
	CurveContainer g = new CurveContainer( 1.0 );
	Curve linear = new Curve( 0.0, 1.0, 0.0, 1.0 );
	Curve halfXPlus1 = new Curve( 0.0, 1.0, 1.0, 1.5 );
	Curve linearOffset = new Curve( 1.0, 2.0, 0.0, 1.0 );
	
	public void setUp()
	{
		c.addEvent( linear );
		d.addEvent( halfXPlus1 );
		e.addEvent( linear );
		e.addEvent( halfXPlus1 );
		f.addEvent( linearOffset );
		g.addEvent( linear );
		g.addEvent( linear.clone() );
	}
	
	public void testBasic()
	{
		assertEquals( 0.0, c.sample( 0.0 ));
		assertEquals( 0.5, c.sample( 0.5 ));
		assertEquals( 1.0, c.sample( 1.0 ));
		
		assertEquals( 1.0, d.sample( 0.0 ));
		assertEquals( 1.25, d.sample( 0.5 ));
		assertEquals( 1.5, d.sample( 1.0 ));
	}
	
	public void testAdditive()
	{
		assertEquals( 1.0, e.sample( 0.0 ));
		assertEquals( 1.75, e.sample( 0.5 ));
		assertEquals( 2.5, e.sample( 1.0 ));
		
		assertEquals( 0.0, g.sample( 0.0 ));
		assertEquals( 1.0, g.sample( 0.5 ));
		assertEquals( 2.0, g.sample( 1.0 ));
	}
	
	public void testRanges()
	{
		assertEquals( 0.0, f.sample( 0.0 ));
		assertEquals( 0.0, f.sample( 0.5 ));
		assertEquals( 0.0, f.sample( 1.0 ));
		assertEquals( 0.5, f.sample( 1.5 ));
		assertEquals( 1.0, f.sample( 2.0 ));
		assertEquals( 0.0, f.sample( 2.5 ));
	}
}
