package com.moseph.mra.test;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.StructuralPosition;
import com.moseph.mra.agent.SectionPosition;

import junit.framework.TestCase;

public class StructuralPositionTest extends TestCase
{

	Piece p;
	Section main;
	Section a;
	Section b;
	Section c;
	Section d;
	Section e;
	StructuralPosition pos;
	
	public void setUp()
	{
		main = new Section( "main" );
		a = new Section( "a");
		b = new Section( "b");
		c = new Section( "c");
		d = new Section( "d");
		e = new Section( "e");
		p = new Piece( "Test");
		p.addChild( main );
		pos = new StructuralPosition( p );
	}
	/*
	 * Test method for 'com.moseph.mra.agent.StructuralPosition.getFirstSection()'
	 */
	public void testGetFirstSection()
	{
		a.addChild( b );
		main.addChild( a );
		assertEquals( b, pos.getFirstSection() );
		b.addChild( c );
		assertEquals( c, pos.getFirstSection() );
	}
	
	public void testReset()
	{
		standardSetup();
		pos.reset();
		List<SectionPosition> first = pos.getCurrentPosition();
		List<SectionPosition> exp = new Vector<SectionPosition>();
		exp.add( new SectionPosition( main ) );
		exp.add( new SectionPosition( a ) );
		exp.add( new SectionPosition( b ) );
		for( int i = 0; i < first.size(); i++ )
		{
			assertEquals( first.get( i ), exp.get( i ));
		}
	}
	
	public void testLogicalOrder()
	{
		standardSetup();
		pos.reset();
		assertEquals( b, pos.getCurrentSection() );
		pos.nextLogicalSection();
		assertEquals( c, pos.getCurrentSection() );
		pos.nextLogicalSection();
		assertEquals( e, pos.getCurrentSection() );
	}
	
	public void testRepeats()
	{
		standardSetup();
		Section f = new Section( "f");
		a.addChild( f );
		a.setNumRepeats( 1 );
		b.setNumRepeats( 1 );
		pos.reset();
		System.out.println( main + ">>>>> \n\n\n\n");
		assertEquals( b, pos.getCurrentSection() );
		System.out.println( pos );
		pos.nextLogicalSection();
		System.out.println( pos );
		assertEquals( b, pos.getCurrentSection() );
		pos.nextLogicalSection();
		System.out.println( pos );
		assertEquals( f, pos.getCurrentSection() );
		pos.nextLogicalSection();
		System.out.println( pos );
		assertEquals( b, pos.getCurrentSection() );
		pos.nextLogicalSection();
		System.out.println( pos );
		assertEquals( b, pos.getCurrentSection() );
		pos.nextLogicalSection();
		System.out.println( pos );
		assertEquals( f, pos.getCurrentSection() );
		pos.nextLogicalSection();
		System.out.println( pos );
		assertEquals( c, pos.getCurrentSection() );
		
	}
	
	public void testPathSetting()
	{
		standardSetup();
		pos.setPath( new Path( "/main/c"));
		assertEquals( c, pos.getCurrentSection() );
		pos.nextLogicalSection();
		assertEquals( e, pos.getCurrentSection() );
		pos.setPath( new Path( "/main/a/b"));
		assertEquals( b, pos.getCurrentSection() );
		
	}
	
	/**
	 * main
	 * / | \
	 * a c d
	 * b   e
	 *
	 */
	void standardSetup()
	{
		a.addChild( b );
		main.addChild( a );
		main.addChild( c );
		main.addChild( d );
		d.addChild( e );
	}
	

}
