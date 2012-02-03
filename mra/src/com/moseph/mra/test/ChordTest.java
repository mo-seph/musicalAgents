package com.moseph.mra.test;

import com.moseph.mra.agent.attribute.Chord;

import junit.framework.TestCase;

public class ChordTest extends TestCase {

	Chord cmaj = new Chord( "C");
	Chord cmaj2 = new Chord( "Cmajor");
	Chord cmaj3 = new Chord( "Cmaj");
	
	Chord cmin = new Chord( "Cm");
	Chord cmin2 = new Chord( "Cmin");
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testEquality()
	{
		
		assertEquals( cmaj, cmaj2 );
		assertEquals( cmaj, cmaj3 );
		assertEquals( cmin, cmin2 );
		assertNotSame( cmaj, cmin );
	}

}
