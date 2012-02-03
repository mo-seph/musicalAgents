package com.moseph.mra.test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.moseph.mra.Span;
import com.moseph.mra.SpanContainer;
import com.moseph.mra.MRAUtilities;
import static com.moseph.mra.MRAUtilities.log;
import com.moseph.mra.Span;
import com.moseph.mra.agent.attribute.ValuedAttribute;

import static java.util.logging.Level.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SpanContainerTest extends TestCase
{
	private SpanContainer a;
	private SpanContainer b;
	private SpanContainer c;

	public static void main(String args[]) 
	{ 
		junit.textui.TestRunner.run(suite());
	}

	public void setUp()
	{
		a = new SpanContainer<Span>();
		b = new SpanContainer<Span>();
		c = new SpanContainer<Span>();
		Span n = new Span( 0.0, 1.0 );
		a.addEvent( n );
		b.addEvent( n );
		Span n2 = new Span( 0.0, 0.5 );
		c.addEvent( n2 );
	}


	public void testEquality()
	{
		assertEquals( "Equal SpanContainers are equal", a, b );
		assertFalse( "Span containers where the spans differ in timing are not equal", a.equals( c ) );
	}

	public void testChunking()
	{
		SpanContainer<Span> f = new SpanContainer<Span>( 4 );
		f.addEvent( new Span( new ValuedAttribute( 0.5 ), 0.0, 1.5 )  );
		f.addEvent( new Span( new ValuedAttribute( 0.2 ), 0.5, 2.5 ) );
		f.addEvent( new Span( new ValuedAttribute( 0.9 ), 1.5,  3.0 ) );

		SpanContainer f1a = f.copyChunk( 0.0, 1.0 );
		SpanContainer f1c = f1a.clone();
		SpanContainer f2a = f.copyChunk( 1.0, 2.0 );
		SpanContainer f2c = f2a.clone();
		SpanContainer f3a = f.copyChunk( 2.0, 3.0 );
		SpanContainer f3c = f3a.clone();

		SpanContainer f1b = new SpanContainer( 1.0 );
		f1b.addEvent( new Span( new ValuedAttribute( 0.5 ), 0.0, 1.0, false, true ), false );
		f1b.addEvent( new Span( new ValuedAttribute( 0.2 ), 0.5, 1.0, false, true ), false );

		SpanContainer f2b = new SpanContainer( 1.0 );
		f2b.addEvent( new Span( new ValuedAttribute( 0.5 ), 0.0, 0.5, true, false ), false );
		f2b.addEvent( new Span( new ValuedAttribute( 0.2 ), 0.0, 1.0, true, true ), false );
		f2b.addEvent( new Span( new ValuedAttribute( 0.9 ), 0.5, 1.0, false, true ), false );

		SpanContainer f3b = new SpanContainer( 1.0 );
		f3b.addEvent( new Span( new ValuedAttribute( 0.2 ), 0.0, 0.5, true, false ), false );
		f3b.addEvent( new Span( new ValuedAttribute( 0.9 ), 0.0, 1.0, true, false ), false );


		//System.err.println( f1a + "\n" + f1b );
		System.out.println( "///////////\n\n////////////////");
		assertEquals( f1a, f1b );
		//System.err.println( f2a + "\n" + f2b );
		assertEquals( f2a, f2b );
		//System.err.println( f3a + "\n" + f3b );
		assertEquals( f3a, f3b );

		System.out.println( "Appending\n" + f2a + "\n to \n " + f1a + "\n\n\n");
		f1a.append( f2a );
		System.out.println( f1a );
		//System.out.println( "Done...");
		System.out.println( "Appending\n" + f3a + "\n to \n " + f1a + "\n\n\n");
		//f1a.append( f3a );
		//System.out.println( f1a );


		f1a.append( f3a );
		f1c.append( f2c );
		f1c.append( f3c );
		assertEquals( f, f1a );

		assertEquals( f, f1c  );
		assertEquals( f1a, f1c );

		//assertTrue( false );
	}
	
	public void testReplace()
	{
		//cmpStart, cmpEnd
		//-1, -1
		SpanContainer<Span> test = new SpanContainer<Span>( new Span( 1.0, 2.0 ));
		System.out.println( test );
		System.out.println( test );
		test.replaceSpan( new Span( 0.5, 1.5 ));
		SpanContainer<Span> expected = new SpanContainer<Span>( new Span( 0.5,1.5), new Span( 1.5,2.0));
		System.out.println( test + "\n" + expected );
		assertTrue( "D1", test.equals( expected ) );
		
		//-1, 0
		test = new SpanContainer<Span>( new Span( 1.0, 2.0 ));
		test.replaceSpan( new Span( 0.5, 2.0 ));
		expected = new SpanContainer<Span>( new Span( 0.5,2.0));
		assertTrue( "C1", test.equals( expected ) );
		//-1, 1
		test = new SpanContainer<Span>( new Span( 1.0, 2.0 ));
		test.replaceSpan( new Span( 0.5, 2.5 ));
		expected = new SpanContainer<Span>( new Span( 0.5,2.5));
		assertTrue( "C2", test.equals( expected ) );
		//0, -1
		test = new SpanContainer<Span>( new Span( 1.0, 2.0 ));
		test.replaceSpan( new Span( 1.0, 1.5 ));
		expected = new SpanContainer<Span>( new Span( 1.0,1.5), new Span( 1.5,2.0));
		assertTrue( "D2", test.equals( expected ) );
		//0, 0
		test = new SpanContainer<Span>( new Span( 1.0, 2.0 ));
		test.replaceSpan( new Span( 1.0, 2.0 ));
		expected = new SpanContainer<Span>( new Span( 1.0,2.0));
		assertTrue( "C3", test.equals( expected ) );
		//0, 1
		test = new SpanContainer<Span>( new Span( 1.0, 2.0 ));
		test.replaceSpan( new Span( 1.0, 2.5 ));
		expected = new SpanContainer<Span>( new Span( 1.0,2.5));
		assertTrue( "C4", test.equals( expected ) );
		//1, -1
		test = new SpanContainer<Span>( new Span( 1.0, 2.0 ));
		test.replaceSpan( new Span( 1.25, 1.75 ));
		expected = new SpanContainer<Span>( new Span( 1.0, 1.25 ), new Span( 1.25,1.75), new Span( 1.75,2.0));
		assertTrue( "B", test.equals( expected ) );
		//1, 0
		test = new SpanContainer<Span>( new Span( 1.0, 2.0 ));
		test.replaceSpan( new Span( 1.5, 2.0 ));
		expected = new SpanContainer<Span>( new Span( 1.0,1.5), new Span( 1.5,2.0));
		assertTrue( "A1", test.equals( expected ) );
		//1, 1
		test = new SpanContainer<Span>( new Span( 1.0, 2.0 ));
		test.replaceSpan( new Span( 1.5, 2.5 ));
		expected = new SpanContainer<Span>( new Span( 1.0,1.5), new Span( 1.5,2.5));
		assertTrue( "A2", test.equals( expected ) );
		
	}
	
	SpanContainer getReplaceTestSpanContainer()
	{
		SpanContainer<Span> t = new SpanContainer<Span>();
		t.addEvent( new Span( 1.0, 2.0 ));
		return t;
	}
	
	public void testLengths()
	{
		SpanContainer a = new SpanContainer( 2.0 );
		a.addEvent( new Span( 0.3, 2.3 ));
		assertEquals( 2.3, a.getLength() );
		SpanContainer b = a.copyChunk( 0.0, 1.7 );
		assertEquals( 1.7, b.getLength() );
	}

	public static Test suite() 
	{ 
		return new TestSuite(SpanContainerTest.class);
	}
}
