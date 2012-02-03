package com.moseph.mra.test;

import junit.framework.TestCase;
import static com.moseph.mra.agent.attribute.Pitch.*;

public class PitchTest extends TestCase
{

	/*
	 * Test method for 'com.moseph.mra.MRAConstants.pitchFromString(String)'
	 */
	public void testPitchFromString()
	{
		assertEquals( 60, pitchFromString("C3"));
		assertEquals( 61, pitchFromString("C#3"));
		assertEquals( 59, pitchFromString("Cb3"));
		assertEquals( 61, pitchFromString("Db3"));
		assertEquals( 72, pitchFromString("C4"));
		assertEquals( pitchFromString("Cb3"), pitchFromString("B2"));
		assertEquals( INVALID_PITCH, pitchFromString( "a1236"));
		assertEquals( INVALID_PITCH, pitchFromString("234"));
		assertEquals( INVALID_PITCH, pitchFromString( "CC3#"));
		assertEquals( pitchFromString("C3"), pitchFromString("C"));
	}

}
