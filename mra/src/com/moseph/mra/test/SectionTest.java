package com.moseph.mra.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.parser.MRAParser;
import static com.moseph.mra.test.TestUtilities.*;

import junit.framework.TestCase;

public class SectionTest extends TestCase {

	Section main  = new Section( "Main");
	Section a = new Section( "A");
	Section b = new Section( "B");
	Section c = new Section( "C");
	Section d = new Section( "D");
	Section e = new Section( "E");
	
	
	protected void setUp() throws Exception {
		super.setUp();
	
	}
	
	public void testOrdering()
	{
		Section verse  = new Section( "Verse");
		Section chorus  = new Section( "Chorus");
		verse.addChild( a );
		verse.addChild( b );
		verse.addChild( c );
		chorus.addChild( d );
		chorus.addChild( a );
		
		main.addChild( verse );
		main.addChild( chorus );
		main.addChild( verse );
		main.addChild( chorus );
		main.activate();

		Section current = main;
		String[] expected = { "A", "B", "C", "D", "A", "A", "B", "C", "D", "A" }; 
		for( String s : expected )
		{
			current = current.getNextSection();
			assertEquals( s, current.getName() );
		}
	}
	
	/*
	public void testInCOrdering()
	{
		FileInputStream file = null;
		try
		{
			file = new FileInputStream( "examples/InC.mra" );
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MRAParser parser = new MRAParser( file );
		Piece piece = parser.runParser();	
		Section current = piece.getNextSection();
		while( current != null )
		{
			current.activate();
			System.out.println( "Current section: " + current.getName() );
			current = current.getNextSection();
		}
		//assertTrue( false );
	}
	*/
	
	public void testRandomOrdering()
	{
		Section rand = new Section( "Random");
		rand.addChild( a );
		rand.addChild( b );
		rand.addChild( c );
		rand.addChild( d );
		rand.setDecisionRule( new RandomDecisionRule( rand ));
		rand.activate();
		Section current = rand;
		for( int i = 0; i < 10; i++ )
		{
			current = current.getNextSection();
			System.out.println( current.getName() );
		}
	}
	
	public void testPathSelection()
	{
		Section top = new Section( "top");
		Section v = new Section("verse");
		Section ch = new Section("chorus");
		v.addChild( a );
		v.addChild( a );
		v.addChild( b );
		v.addChild( a );
		v.addChild( b );
		ch.addChild( a  );
		ch.addChild( c  );
		ch.addChild( a  );
		ch.addChild( c  );
		top.addChild( v );
		top.addChild( ch );
		top.addChild( v );
		top.addChild( ch );
		
		Path p;
		//Simple name selection
		p = new Path( "/verse");
		assertEquals( v, p.getSection( top ));
		p = new Path( "verse");
		assertEquals( v, p.getSection( top ));
		p = new Path( "verse/A");
		assertEquals( a, p.getSection( top ));
		p = new Path( "verse/b");
		assertEquals( b, p.getSection( top ));
		
		//Index selection
		p = new Path( "1");
		assertEquals( ch, p.getSection( top ));
		p = new Path( "0/3");
		assertEquals( a, p.getSection( top ));
		
		//Mixed selection
		p = new Path( "verse/3");
		assertEquals( a, p.getSection( top ));
		p = new Path( "0/a");
		assertEquals( a, p.getSection( top ));
		
		//Name+occurence selection
		p = new Path( "verse;1");
		assertEquals( v, p.getSection( top ));
		p = new Path( "verse;2");
		assertNull( p.getSection( top ));
		
		//Path retrieval.
		Section s = top.getNextSection();
		assertEquals( a, s );
		assertEquals( a, s.getPath().getSection( top ));
		s = s.getNextSection();
		s = s.getNextSection();
		assertEquals( b, s.getPath().getSection( top ));
	}
	
	public void testOverallIndexing()
	{
		Section top = new Section( "top");
		Section v = new Section("verse");
		Section ch = new Section("chorus");
		v.addChild( a );
		v.addChild( a );
		v.addChild( b );
		v.addChild( a );
		v.addChild( b );
		ch.addChild( a  );
		ch.addChild( c  );
		ch.addChild( a  );
		ch.addChild( c  );
		top.addChild( v );
		top.addChild( ch );
		top.addChild( v );
		top.addChild( ch );
		
		assertEquals( 0, new Path( "verse/a").getIndex(top));
		assertEquals( 2, new Path( "verse/b").getIndex(top));
		assertEquals( 3, new Path( "verse/a;2").getIndex(top));
		assertEquals( 9, new Path( "verse;1/a").getIndex(top));
		
	}
	
	public void testLengths()
	{
		Section a = new Section( "Test");
		Fragment f = new Fragment();
		f.addNote( 8.0, 64, 0.0, 2.0 );
		a.addChannel( f );
		assertEquals( 10.0, a.getLength() );
		
		Section b = new Section("Test");
		b.setLength( 3.0 );
		b.addChannel( f );
		assertEquals( 10.0, b.getLength() );
		
		b = new Section("Test");
		b.setLength( 13.0 );
		b.addChannel( f );
		assertEquals( 13.0, b.getLength() );
		
		f.addNote( 18.0, 64, 0.0, 2.0 );
		assertEquals( 13.0, b.getLength() );
		b.recalculateLength();
		assertEquals( 20.0, b.getLength() );
	}
	
	public void testAttributes()
	{
		Attribute aa = new Attribute( "a", "a"); //by a - wins
		Attribute ab = new Attribute( "a", "b"); //by b - overriden by a
		Attribute ac = new Attribute( "a", "c"); //by c - overriden by b
		Attribute ad = new Attribute( "a", "d"); //by d - overriden by b
		Attribute ae = new Attribute( "a", "e"); //by e - overriden by d
		Attribute bc = new Attribute( "b", "c"); //by c - uncontested
		Attribute cb = new Attribute( "c", "b"); //by b - overriden by a
		Attribute ca = new Attribute( "c", "a"); //by a - wins
		Attribute db = new Attribute( "d", "b"); //by b - wins
		Attribute ee = new Attribute( "e", "e"); //by e - wins
		Attribute fe = new Attribute( "f", "e"); //by f - oberriden by d
		Attribute fd = new Attribute( "f", "d"); //by d - wins
		
		e.addChild( d );
		e.activate();
		c.addChild( b );
		c.activate();
		b.addChild( a );
		b.activate();
		a.setDerivedFrom( d);
		//  c       e
		//  |       |
		//  b       d
		//  |       |
		//  a .......
		
		a.addAttribute( aa );
		a.addAttribute( ca );
		b.addAttribute( ab );
		b.addAttribute( cb );
		b.addAttribute( db );
		c.addAttribute( ac );
		c.addAttribute( bc );
		d.addAttribute( ad );
		d.addAttribute( fd );
		e.addAttribute( ae );
		e.addAttribute( ee );
		e.addAttribute( fe );
		
		e.activate();
		c.activate();
		
		Map<String,Attribute> aAtts = a.getAttributeMap();
		for( String s : aAtts.keySet() ) System.out.println( s + ": " + aAtts.get( s ));
		System.out.println( ">>>>" + aAtts.get( "a"));
		assertEquals( "The value set in a wins", aa, aAtts.get( "a") );
		assertEquals( "The value set in c wins", bc, aAtts.get( "b"));
		//Unneccessary...
		assertEquals( "The value set in a wins", ca, aAtts.get( "c"));
		assertEquals( "The value set in b wins", db, aAtts.get( "d"));
		assertEquals( "The value set in e gets through", ee, aAtts.get( "e"));
		assertEquals( "The value set in d wins and gets through", fd, aAtts.get( "f"));
		//Checks are:
		//d's attributes carry through
		//b's attributes override d's
		//b's attributes carry through
		//a's attributes override b's
		//a's attributes carry through
		
	}
	
	public void testLeaves()
	{
		List<Section> leaves = getBasicPiece().getLeaves();
		String[] expected = { "b", "c", "e" };
		for( int i = 0; i < expected.length; i++ )
			assertEquals( expected[i], leaves.get( i ).getName() );
	}

	public void testLeafPaths()
	{
		List<Path> leaves = getBasicPiece().getLeafPaths();
		String[] expected = { "/main/a/b", "/main/a/c", "/main/d/e" };
		for( int i = 0; i < expected.length; i++ )
		{
			assertEquals( expected[i], leaves.get( i ).toString() );
		}
	}

}
