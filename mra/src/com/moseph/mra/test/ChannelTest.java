package com.moseph.mra.test;

import com.moseph.mra.TemporalEvent;
import com.moseph.mra.Channel;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ChannelTest extends TestCase
{
	private Channel a;
	private Channel b;
	private Channel c;

	public static void main(String args[]) 
	{ 
		junit.textui.TestRunner.run(suite());
	}

	public void setUp()
	{
		a = new Channel<TemporalEvent>();
		b = new Channel<TemporalEvent>();
		c = new Channel<TemporalEvent>();
		TemporalEvent n = new TemporalEvent( 0.0 );
		a.addEvent( n );
		b.addEvent( n );
		TemporalEvent n2 = new TemporalEvent( 0.5 );
		c.addEvent( n2 );
	}


	public void testEquality()
	{
		assertEquals( "Equal EventContainers are equal", a, b );
		assertFalse( "TemporalEvent containers where the events differ in timing are not equal", a.equals( c ) );
	}

	public void testChunking()
	{
		Channel<TemporalEvent> f = new Channel<TemporalEvent>( 4 );
		f.addEvent( new TemporalEvent( 0.0 )  );
//		f.addEvent( new TemporalEvent( 0.25 )  );

		f.addEvent( new TemporalEvent( 0.5 ) );
		f.addEvent( new TemporalEvent( 1.5 ) );

		Channel f1a = f.copyChunk( 0.0, 1.0 );
		Channel f1c = f1a.clone();

		Channel f2a = f.copyChunk( 1.0, 2.0 );
		Channel f2c = f2a.clone();
		Channel f3a = f.copyChunk( 2.0, 3.0 );
		Channel f3c = f3a.clone();

		Channel f1b = new Channel( 1.0 );
		f1b.addEvent( new TemporalEvent( 0.0  ) );
		f1b.addEvent( new TemporalEvent( 0.5 ) );

		Channel f2b = new Channel( 1.0 );
		f2b.addEvent( new TemporalEvent( 0.5 ) );

		Channel f3b = new Channel( 1.0 );


		assertTrue( "First chunk not equal to constructed version", f1a.equals( f1b ) );
		assertTrue( "Second chunk not equal to constructed version", f2a.equals( f2b ) );
		assertTrue( "Third chunk not equal to constructed version", f3a.equals( f3b ) );

		f1a.append( f2a );
		f1a.append( f3a );
		f1c.append( f2c );
		f1c.append( f3c );
		assertTrue( "Reconstructed version not equal to original", f1a.equals( f ) );

		assertEquals( "Copied Reconstructed version not equal to original", f1c, f );

	}

	public void testAppend()
	{
		Channel c = new Channel( 2.0 );
		Channel c2 = new Channel( 2.0 );
		c.append( c );
		assertEquals( 4.0, c.getLength() );
		c.append( c2 );
		assertEquals( 6.0, c.getLength() );
	}
	public static Test suite() 
	{ 
		return new TestSuite(ChannelTest.class);
	}
}
