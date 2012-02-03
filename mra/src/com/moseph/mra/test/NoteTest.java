package com.moseph.mra.test;

import com.moseph.mra.*;

import junit.framework.*;
import javax.sound.midi.*;
import org.w3c.dom.*;

import static com.moseph.mra.midi.MidiUtilities.*;

public class NoteTest extends TestCase
{
	Note a;
	Note b;
	Note c;
	Note d;
	Note e;
	Note f;

	public static void main(String args[]) 
	{ 
		junit.textui.TestRunner.run(suite());
	}

	public void setUp()
	{
		a = new Note( 0.0, 66, 0.9, 0.5 );
		b = new Note( 0.0, 66, 0.9, 0.5 );
		c = new Note( 0.0, 68, 0.9, 0.5 );
		d = new Note( 1.0, 66, 0.9, 0.5 );
		e = new Note( 0.0, 66, 0.9, 1.5 );
		f = new Note( 0.0, 66, 0.2, 0.5 );
	}

	public void testEquality()
	{
		assertEquals( "a=b", a, b );
		assertNotSame( "a != c", a, c );
		assertNotSame( "a != d", a, d );
		assertNotSame( "a != e", a, e );
		assertNotSame( "a != f", a, f );
	}
	
	public void testCopy()
	{
		Note n = a.clone();
		assertEquals( "Copy", a, n );
		assertEquals( d, a.copyAddOffset( 1.0 ));
	}

	public void testTranspose()
	{
		a.transpose( 2 );
		assertEquals( a, c );
	}

	public void testAddOffset()
	{
		a.addOffset( 1.0 );
		assertEquals( a, d );
	}

	public void testXML()
	{
		Note a = new Note( 0.0, 66, 0.5, 0.9, true, true );
		Document d = MRAUtilities.getMAXMLXMLDocument();
		Element e = a.getXMLElement( d );
		Note b = new Note( e );
		assertEquals( a, b );
	}

	public static Test suite() 
	{ 
		return new TestSuite(NoteTest.class);
	}
}

