package com.moseph.mra.test.symbolic;

import java.util.*;

import junit.framework.TestCase;

import com.moseph.mra.agent.attribute.PatternAttribute;
import com.moseph.mra.symbolic.*;

public class PatternSymboliserTest extends TestCase
{
	NumericTreeValue root = new NumericTreeValue( "Root", 0.0 );
	TreeLattice<Double, NumericTreeValue> tree = new TreeLattice<Double, NumericTreeValue>( root );
	NumericTreeValue a1;
	NumericTreeValue a2;
	NumericTreeValue a3;
	NumericTreeValue b1;
	NumericTreeValue b2;
	NumericTreeValue b3;
	PatternSymboliser symboliser = new PatternSymboliser( tree );
	
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
		PatternAttribute a1 = new PatternAttribute( 1.0, new double[] { 0.5, 0.9, 0.7 } );
		System.out.println( a1 + "\n" + symboliser.symbolise( a1 ));
	}
	

}
