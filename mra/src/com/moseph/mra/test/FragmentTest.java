package com.moseph.mra.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import com.moseph.mra.*;

import junit.framework.*;
import javax.sound.midi.*;
import org.w3c.dom.*;

import static com.moseph.mra.midi.MidiUtilities.*;

public class FragmentTest extends TestCase
{
	private Fragment a;
	private Fragment b;
	private Fragment c;

	public static void main(String args[]) 
	{ 
		junit.textui.TestRunner.run(suite());
	}

	public void setUp()
	{
		a = new Fragment();
		b = new Fragment();
		c = new Fragment();
		Note n = new Note( 0.0, 66, 0.9, 0.5 );
		a.addNote( n );
		b.addNote( n );
		Note n2 = new Note( 0.0, 68, 0.9, 0.5 );
		c.addNote( n2 );
	}


	public void testEquality()
	{
		assertEquals( "Equal fragments are shown to be not equal", a, b );
		//System.out.println( a + "\n" + c );
		//assertEquals( "Equal fragments are shown to be not equal", a, c );
		assertFalse( "Transposed fragments are shown as equal", a.equals( c ) );
	}

	public void testTranspose()
	{
		
		assertFalse( a.equals( c ) );
		a.transpose( 2 );
		assertEquals( a, c );
	}

	public void testXML()
	{
		Document doc = MRAUtilities.getMAXMLXMLDocument();
		Element elem = a.getXMLElement( doc );
		Fragment fromXML = new Fragment( elem );
		assertEquals( a, fromXML );
	}
	
	public void testMerging()
	{
		Fragment f = new Fragment();
		f.addNote( new Note( 0.0, 60, 0.7, 0.5, false, true ) );
		f.addNote( new Note( 0.5, 60, 0.7, 0.5, true, false ) );
		Fragment j = new Fragment();
		j.addNote( new Note( 0.0, 60, 0.7, 1.0, false, false ));
		assertEquals( j, f );
		
		
	}

	public void testChunking()
	{
		Fragment f = new Fragment( 4 );
		f.addNote( 0.0, 60, 0.7, 1.5 );
		f.addNote( 0.5, 62, 0.7, 2.0 );
		f.addNote( 1.5, 64, 0.7, 1.5 );
		System.err.println( f );
		
		Fragment f1a = f.copyChunk( 0.0, 1.0 );
		Fragment f1c = f1a.clone();
		Fragment f2a = f.copyChunk( 1.0, 2.0 );
		Fragment f2c = f2a.clone();
		Fragment f3a = f.copyChunk( 2.0, 3.0 );
		Fragment f3c = f3a.clone();

		Fragment f1b = new Fragment( 1.0 );
		f1b.addNote( new Note( 0.0, 60, 0.7, 1.0, false, true ) );
		f1b.addNote( new Note( 0.5, 62, 0.7, 0.5, false, true ) );

		Fragment f2b = new Fragment( 1.0 );
		f2b.addNote( new Note( 0.0, 60, 0.7, 0.5, true, false ) );
		f2b.addNote( new Note( 0.0, 62, 0.7, 1.0, true, true ) );
		f2b.addNote( new Note( 0.5, 64, 0.7, 0.5, false, true ) );

		Fragment f3b = new Fragment( 1.0 );
		f3b.addNote( new Note( 0.0, 62, 0.7, 0.5, true, false ) );
		f3b.addNote( new Note( 0.0, 64, 0.7, 1.0, true, false ) );

		//System.out.println( "Section 1:\n"+ f1a + f1b + f1c );
		//System.out.println( "Section 2:\n"+ f2a + f2b + f2c );
		//System.out.println( "Section 3:\n"+ f3a + f3b + f3c );

		assertTrue( "First chunk not equal to constructed version", f1a.equals( f1b ) );
		assertTrue( "Second chunk not equal to constructed version", f2a.equals( f2b ) );
		assertTrue( "Third chunk not equal to constructed version", f3a.equals( f3b ) );

		f1a.append( f2a );
		f1a.append( f3a );
		f1c.append( f2c );
		f1c.append( f3c );
		System.out.println( "Reconstructed:\n"+ f1a + "\n" + f );
		assertTrue( "Reconstructed version not equal to original", f1a.equals( f ) );
		assertTrue( "Copied Reconstructed version not equal to original", f1c.equals( f ) );

	}
	
	public void testAppendLengths()
	{
		Fragment p = new Fragment( 2.0 );
		Fragment q = new Fragment( 2.0 );
		assertEquals( "Blank fragments have correct length", 2.0, p.getLength() );
		p.append( q );
		assertEquals( "Concatenated blank fragments have correct length", 4.0, p.getLength() );
		p.append( p );
		assertEquals( "Appending to self works OK", 8.0, p.getLength() );
	}

	public static Test suite() 
	{ 
		return new TestSuite(FragmentTest.class);
	}
	
	public void testSerialization()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try
		{
			oos = new ObjectOutputStream( bos );
			oos.writeObject(a);
			byte[] data = bos.toByteArray();
			ByteArrayInputStream bis = new ByteArrayInputStream( data );
			ObjectInputStream ois = new ObjectInputStream( bis );
			Object o = ois.readObject();
			assertEquals( (Fragment)o, a );
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testAdditionTime()
	{
		/*
		Fragment f = Fragment.getExampleFragment();
		Fragment a = Fragment.getExampleFragment();
		double start = timeAdditions( f, a, 2050 );
		start = timeAdditions( f, a, 20050 );
		for( int i = 0; i < 10000; i++ ) f.append( a );
		double end = timeAdditions( f, a, 10000 );
		end = timeAdditions( f, a, 1000 );
		end = timeAdditions( f, a, 1000 );
		System.out.printf( "Start: %f, End: %f\n", start, end );
		assertTrue( end < start );
		*/
	}
	
	public double timeAdditions( Fragment target, Fragment appendage, int number )
	{
		Date before = new Date();
		for( int i = 0; i < number; i++ ) target.append( appendage );
		double time = new Date().getTime() - before.getTime();
		System.out.println( "Timed " + number + ": " + time );
		return time;
	}
	
	/*
	public void testAccessTime()
	{
		System.out.println( "Warmup: " + timeFragmentAtEnd( 10000, 0.25, 1000 ) );
		System.out.println( "Warmup: " + timeFragmentAtEnd( 10000, 0.25, 1000 ) );
		System.out.println( "Warmup: " + timeFragmentAtEnd( 10000, 0.25, 1000 ) );
		System.out.println( "Warmup: " + timeFragmentAtEnd( 10000, 0.25, 1000 ) );
		System.out.println( "Warmup: " + timeFragmentAtEnd( 10000, 0.25, 1000 ) );
		System.out.println( "Warmup: " + timeFragmentAtEnd( 10000, 0.25, 1000 ) );
		System.out.println( "Warmup: " + timeFragmentAtEnd( 10000, 0.25, 1000 ) );
		long big = timeFragmentAtEnd( 10000, 0.25, 1000 );
		long small = timeFragmentAtEnd( 100, 0.25, 1000 );
	

		assertTrue( big < small * 2 );
	}
	*/
	
	long timeFragmentAtEnd( double length, double spacing, int numTests )
	{
		Fragment f = new Fragment();
		for( double i = 0.0; i < length; i+=spacing ) f.addNote( new Note( i, 64, 0.8, 0.5 ));
		Date before = new Date();
		for( int i = 0; i < numTests; i++ ) f.copyChunk( length - 4.0, length );
		return new Date().getTime() - before.getTime();
	}
	
	public void testGracenotes()
	{
		Fragment f = new Fragment( 2 );
		f.addNote( new Gracenote( 1.0, 60, 0.5 ));
		Fragment fa = f.copyChunk( 0, 2 );
		assertEquals( f, fa );
		
		Fragment g = new Fragment( 1 );
		g.addNote( new Gracenote( 0.0, 60, 0.5 ));
		assertEquals( g, f.copyChunk( 1, 2) );
		
		Fragment main = new Fragment();
		main.addNote( new Gracenote( 0.0, 60, 0.5 ));
		main.addNote( new Note( 0.0, 64, 0.5, 1.0 ));
		main.addNote( new Gracenote( 1.0, 60, 0.5 ));
		main.addNote( new Note( 1.0, 64, 0.5, 1.0 ));
		
		Fragment tmp = main.copyChunk( 0.0, 1.0 );
		System.out.println( main + "" + tmp );
		tmp.append( main.copyChunk( 1.0, 2.0 ));
		System.out.println( main + "" + tmp );
		assertEquals( main, tmp );
	}
	
	
	public void testChunkLengths()
	{
		Fragment f = new Fragment( 1.0 );
		f.addNote( 0.0, 60, 0.5, 0.7 );
		assertEquals( "Fragment Length reported correctly", 1.0,  f.getLength() );
		assertEquals( "Chunk has correct length", 1.0,  f.copyChunk( 0.0, 1.0 ).getLength() );
		Fragment g = new Fragment();
		g.addNote( 0.0, 60, 0.5, 0.7 );
		assertEquals( "Fragment Length reported correctly", 0.7,  g.getLength() );
		assertEquals( "Chunk has correct length", 1.0,  g.copyChunk( 0.0, 1.0 ).getLength() );
		
	}
	
	public void testOrdering()
	{
		Fragment f = new Fragment();
		f.addNote( 0.7, 60, 0.5, 0.7 );
		f.addNote( 0.2, 63, 0.5, 1.9 );
		f.addNote( 0.3, 42, 0.5, 0.3 );
		f.addNote( 0.5, 42, 0.5, 0.9 );
		f.addNote( 0.6, 42, 0.5, 0.9 );
		System.out.println( f );
		List<Note> notes = f.getNotes();
		for( Note n : notes ) System.out.println( "n: " + n );
		for( int i = 0; i < notes.size(); i++ )
			for( int j = i+1; j< notes.size(); j++ )
			{
				int comp = notes.get( i ).compareTo( notes.get( j ) );
				assertTrue( comp < 0 );
				//Compare the other way round!
				int compB = notes.get( j ).compareTo( notes.get( i ) );
				assertTrue( compB > 0 );
			}
	}
	
	public void testClosingMultipleNotes()
	{
		Fragment f = new Fragment();
		Note n1 = new Note( 0.0, 60, 0.5, 1.0, false, true );
		Note n2 = new Note( 0.5, 60, 0.5, 0.5, false, true );
		Note n3 = new Note( 1.0, 60, 0.5, 0.5, true, false );
		f.addNote( n1 );
		f.addNote( n2 );
		f.addNote( n3 );
		System.out.println( f );
		for( Note n : f.getNotes() )
			assertFalse( "No notes left hanging", n.getLongerThan() );
	}
	
	public void testRemovingNotes()
	{
		Fragment f1 = new Fragment();
		Fragment f2 = new Fragment();
		Fragment fRes = new Fragment();
		
		Note n = new Note( 0.5, 66, 0.5, 0.5 );
		Note n2 = new Note( 0.0, 60, 0.5, 0.5 );
		Note n3 = new Note( 0.0, 60, 0.5, 0.5 );
		
		f1.addNote( n );
		f1.addNote( n2 );
		
		f2.addNote( n );
		f2.addNote( n2 );
		
		fRes.addNote( n );
		
		f1.remove( n2 );
		assertEquals( fRes, f1 );
		
		f2.remove( n3 );
		assertEquals( fRes, f2 );
	}
	
	public void testStripQuietNotes()
	{
		Fragment f1 = new Fragment(2.5);
		Fragment fExp = new Fragment(2.5);
		
		Note n1 = new Note( 0.0, 60, 0.6, 0.5 );
		Note n2 = new Note( 0.5, 61, 0.6, 0.5 );
		Note n3 = new Note( 1.0, 62, 0.006, 0.5 );
		Note n4 = new Note( 1.5, 63, 0.006, 0.5 );
		Note n5 = new Note( 2.0, 64, 0.6, 0.5 );
		
		f1.addNote( n1 );
		f1.addNote( n2 );
		f1.addNote( n3 );
		f1.addNote( n4 );
		f1.addNote( n5 );
		
		fExp.addNote( n1 );
		fExp.addNote( n2 );
		fExp.addNote( n5 );
		
		f1.stripQuietNotes( 0.1 );
		assertEquals( fExp, f1 );
	}
	
	public void testChunkingOverlaps()
	{
		Fragment orig = new Fragment();
		for( double i = 0.0; i < 10.0; i++ )
		{
			double length = 0.2;
			if( i > 3 && i < 7 ) length = 3.8;
			orig.addNote( i, 20, 0.7, length );
		}
		for( double step = 1.0; step < 2.0; step += 0.1 )
		{
			Fragment chunked = new Fragment();
			for( double d = 0.0; d < orig.getLength(); d += step )
				chunked.addFragment( orig.copyChunk( d, d+step ), d );
			System.out.println( "*** Running at step " + step + ":\n" + orig + "\n" + chunked ); 
			assertEquals( orig, chunked );
		}
		
	}
}


