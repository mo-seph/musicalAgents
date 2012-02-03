package com.moseph.mra.test.symbolic;

import junit.framework.TestCase;

import com.moseph.mra.symbolic.*;

public class PatternValueTest extends TestCase
{
	NumericTreeValue root = new NumericTreeValue( "Root", 0.0 );
	TreeLattice<Double, NumericTreeValue> tree = new TreeLattice<Double, NumericTreeValue>( root );
	NumericTreeValue a1;
	NumericTreeValue a2;
	NumericTreeValue b1;
	NumericTreeValue b2;
	
	public void testBasics()
	{
		a1 = tree.addTerm( "a1", 0.3, root );
		a2 = tree.addTerm( "a2", 0.7, "a1" );
		b1 = tree.addTerm( "b1", 0.3, root );
		b2 = tree.addTerm( "b2", 0.7, "b1" );
		
		NumericPatternValue p1 = new NumericPatternValue( 2, tree, a1, a2 );
		NumericPatternValue p2 = new NumericPatternValue( 2, tree );
		
		System.out.println( p1 );
		
	}

}
