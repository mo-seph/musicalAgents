package com.moseph.mra.test;

import java.util.List;

import com.moseph.mra.EventSet;
import com.moseph.mra.Note;
import com.moseph.mra.TemporalEvent;

import junit.framework.TestCase;

public class EventSetTest extends TestCase
{
	EventSet e = new EventSet<TemporalEvent>();
	EventSet f = new EventSet<TemporalEvent>();
	TemporalEvent ev1 = new TemporalEvent( 1.0 );
	TemporalEvent ev2 = new TemporalEvent( 2.0 );
	TemporalEvent ev3 = new TemporalEvent( 3.0 );
	Note n1 = new Note(0.0, 64, 0.5, 1.5 );
	Note n2 = new Note(1.0, 64, 0.5, 1.5 );
	
	public void setUp()
	{
		e.add( ev1 );
		e.add( ev2 );
		e.add( ev3 );
		f.add(n1);
		f.add(n2);
	}

	public void testActive()
	{
		List<TemporalEvent> active1 = e.getPotentiallyActive( 0.0, 1.9 );
		List<TemporalEvent> active2 = e.getPotentiallyActive( 2.0, 4.0 );
		assertEquals( 1, active1.size() );
		assertEquals( ev1, active1.get(0));
		//As 2.0 is very near the bucket boundary, it will return the previous bucket as well
		assertEquals( 3, active2.size() );
		assertEquals( ev2, active2.get(1) );
		assertEquals( ev3, active2.get(2) );

		List<TemporalEvent> activef1 = f.getPotentiallyActive( 0.0, 2.0 );
		List<TemporalEvent> activef2 = f.getPotentiallyActive( 2.5, 4.0 );
		assertEquals( 2, activef1.size() );
		assertEquals( n1, activef1.get(0));
		assertEquals( n2, activef1.get(1));
		//As 2.0 is very near the bucket boundary, it will return the previous bucket as well
		assertEquals( 1, activef2.size() );
		assertEquals( n2, activef2.get(0));
	}
	
}
