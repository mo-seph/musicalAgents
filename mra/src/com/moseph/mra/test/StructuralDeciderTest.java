package com.moseph.mra.test;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;

import junit.framework.TestCase;

public class StructuralDeciderTest extends TestCase
{
	Context context = new Context( new MusicianInformation( new Musician( "kev") ) );
	Piece p;
	Section a;
	Section b;
	Section c;
	Section d;
	Section e;
	Section main;
	StructuralDecider decider;
	Fragment piano = new Fragment( "Piano" );
	Fragment drums = new Fragment( "Drums" );
	Fragment general = new Fragment( "Notes" );
	
	public void setUp()
	{
		context.setPartname( "Piano");
		p = new Piece( "testpiece" );
		context.setPiece( p );
		main = new Section( "main");
		p.addChild( main );
		a = new Section( "a");
		a.addChannel( piano );
		b = new Section( "b");
		c = new Section( "c");
		c.addChannel( drums );
		d = new Section( "d");
		d.addChannel( general );
		e = new Section( "e");
		e.addChannel( general );
		e.addChannel( piano );
		b.addChild( c );
		main.addChild( a );
		main.addChild( b );
		main.addChild( d );
		main.addChild( e );
		//p.activate();
		decider = new StructuralDecider( context );
		System.out.println( p );
	}
	
	public void testOrdering()
	{
		Section test = decider.getCurrentSection();
		assertEquals( test, a );
		decider.update();
		test = decider.getCurrentSection();
		assertEquals( test, c );
		decider.update();
		test = decider.getCurrentSection();
		assertEquals( test, d );
		decider.update();
		test = decider.getCurrentSection();
		assertEquals( test, e );
	}
	
	public void testFragments()
	{
		Fragment test = decider.getCurrentFragment();
		assertEquals( piano, test);
		decider.update();
		test = decider.getCurrentFragment();
		System.out.println( decider.getCurrentSection() );
		assertEquals( null, test );
		decider.update();
		test = decider.getCurrentFragment();
		assertEquals( general, test);
		decider.update();
		test = decider.getCurrentFragment();
		assertEquals( piano, test );
		decider.update();
		test = decider.getCurrentFragment();
		assertEquals( null, test );
	}
	
	public void testRepeats()
	{
		a.setNumRepeats( 8 );
		decider.update();
		assertEquals( a, decider.getCurrentSection() );
		decider.update();
		assertEquals( a, decider.getCurrentSection() );
		decider.update();
		assertEquals( a, decider.getCurrentSection() );
		decider.update();
		assertEquals( a, decider.getCurrentSection() );
		decider.update();
		assertEquals( a, decider.getCurrentSection() );
		decider.update();
		assertEquals( a, decider.getCurrentSection() );
		decider.update();
		assertEquals( a, decider.getCurrentSection() );
		decider.update();
		assertEquals( a, decider.getCurrentSection() );
		decider.update();
		assertEquals( c, decider.getCurrentSection() );
	}
}
