package com.moseph.mra.test.symbolic;

import java.util.*;

import junit.framework.TestCase;

import com.moseph.mra.symbolic.*;

public class NumericSymboliserTest extends TestCase
{
	NumericTreeValue root = new NumericTreeValue( "Root", 0.0 );
	TreeLattice<Double, NumericTreeValue> tree = new TreeLattice<Double, NumericTreeValue>( root );
	NumericTreeValue a1;
	NumericTreeValue a2;
	NumericTreeValue a3;
	NumericTreeValue b1;
	NumericTreeValue b2;
	NumericTreeValue b3;
	NumericSymboliser symboliser = new NumericSymboliser( tree );
	
	public void setUp()
	{
		a1 = tree.addTerm( "a1", 0.7, root );
		a2 = tree.addTerm( "a2", 0.8, "a1" );
		a3 = tree.addTerm( "a3", 0.9, "a2" );
		b1 = tree.addTerm( "b1", 0.3, root );
		b2 = tree.addTerm( "b2", 0.2, "b1" );
		b3 = tree.addTerm( "b3", 0.1, "b2" );
		
	}
	
	public void testBasicOperation()
	{
		assertEquals( root, symboliser.symbolise( 0.5 ) );
		assertEquals( a1, symboliser.symbolise( 0.75 ) );
		assertEquals( a2, symboliser.symbolise( 0.85 ) );
		//assertEquals( a3, symboliser.symbolise( 0.95 ) );
		assertEquals( b1, symboliser.symbolise( 0.25 ) );
		assertEquals( b2, symboliser.symbolise( 0.15 ) );
		assertEquals( b3, symboliser.symbolise( 0.05 ) );
		
	}
	

}
