package com.moseph.mra.test;

import java.util.Date;

import com.moseph.mra.Score;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import junit.framework.TestCase;

public class ScoreTest extends TestCase
{
	
	public void testSetLength()
	{
		Score s = new Score();
		s.forceLength( 5.0 );
		assertEquals( 5.0, s.getLength() );
	}
	
	//Just included to stop it barfing when the longrunning test below is taken out...
	public void testNothing()
	{
		
	}

	/*
	 * Test method for 'com.moseph.mra.Score.add(Score, double)'
	 */
	/*
	public void testLinearTimeAddition()
	{
		Score s = Score.getExampleScore();
		Score a = Score.getExampleScore();
		for( int i = 0; i < 10; i++ ) s.append( a );
		double early = timeNumAdditions( s, a, 1005 );
		timeNumAdditions( s, a, 1005 );
		timeNumAdditions( s, a, 1005 );
		timeNumAdditions( s, a, 1005 );
		timeNumAdditions( s, a, 1005 );
		timeNumAdditions( s, a, 1005 );
		timeNumAdditions( s, a, 1005 );
		timeNumAdditions( s, a, 1005 );
		for( int i = 0; i < 1000; i++ ) s.append( a );
		double late = timeNumAdditions( s, a, 1000 );
		System.out.printf( "Early: %f, Late: %f\n", early, late );
		System.out.println( s.getLength() );
		assertTrue( early > late );
	}
	*/
	
	long timeNumAdditions( Score target, Score appendage, int numTests )
	{
		Date before = new Date();
		for( int i = 0; i < numTests; i++ ) target.append( appendage );
		long time = new Date().getTime() - before.getTime();
		System.out.printf( "Length at end: %f, time: %f\n", target.getLength(), (double)time );
		return time;
	}

}
