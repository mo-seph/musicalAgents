package com.moseph.mra.test.symbolic;

import java.util.List;

import junit.framework.TestCase;

import com.moseph.mra.symbolic.*;
import static com.moseph.mra.symbolic.Relationship.*;
import static com.moseph.mra.MRAUtilities.*;

public class PatternLatticeTest extends TestCase
{
	NumericTreeValue root = new NumericTreeValue( "Root", 0.0 );
	TreeLattice<Double, NumericTreeValue> tree = new TreeLattice<Double, NumericTreeValue>( root );
	NumericTreeValue a1;
	NumericTreeValue a2;
	NumericTreeValue b1;
	NumericTreeValue b2;
	PatternLattice<Double, NumericTreeValue, NumericPatternValue> patternLattice;
	NumericPatternValue p1;
	NumericPatternValue p2;
	NumericPatternValue p3;
	NumericPatternValue p4;
	NumericPatternValue p5;
	NumericPatternValue p6;
	
	public void setUp()
	{
		a1 = tree.addTerm( "a1", 0.3, root );
		a2 = tree.addTerm( "a2", 0.7, "a1" );
		b1 = tree.addTerm( "b1", 0.3, root );
		b2 = tree.addTerm( "b2", 0.7, "b1" );
		
		p1 = new NumericPatternValue( 2, tree, a1, a2 );
		p2 = new NumericPatternValue( 2, tree );
		p3 = new NumericPatternValue( 2, tree, tree.getAny(), b1 );
		p4 = new NumericPatternValue( 2, tree, tree.getAny(), b2 );
		p5 = new NumericPatternValue( 2, tree, b1, b2 );
		p6 = new NumericPatternValue( 2, tree, a2, a2 );
		patternLattice = new PatternLattice<Double, NumericTreeValue, NumericPatternValue>( 2, tree, p2 );
		
	}
	
	public void testCombination()
	{
		NumericPatternValue p1c2 = patternLattice.meet( p1, p2 );
		assertEquals( p1, p1c2 );
		NumericPatternValue p1c3 = patternLattice.meet( p1, p3 );
		assertEquals( new NumericPatternValue(2, tree, a1, tree.getIncompatible() ), p1c3 );
		NumericPatternValue p3c4 = patternLattice.meet( p3, p4 );
		assertEquals( p3c4, p4 );
	}
	
	public void testBasicRelationships()
	{
		assertEquals( SAME, patternLattice.getRelationship( p1, p1 ) );
		assertEquals( SUBSUMES, patternLattice.getRelationship( p1, p2 ) );
		assertEquals( SUBSUMED, patternLattice.getRelationship( p2, p1 ) );
		assertEquals( SUBSUMED, patternLattice.getRelationship( p3, p4 ) );
		assertEquals( ALTER, patternLattice.getRelationship( p1, p4 ) );
		assertEquals( DISJOINT, patternLattice.getRelationship( p1, p5 ) );
		assertEquals( SUBSUMED, patternLattice.getRelationship( p1, p6 ) );
	}
	
	public void testGetPossibilities()
	{
		System.out.println( "__________\nTestBegins");
		NumericPatternValue pSub = new NumericPatternValue( 2, tree, a1, a1 );
		List<NumericPatternValue> subsumes = patternLattice.getPossibleValues( p1, SUBSUMES );
		System.out.println( collectionToString( subsumes ));
		
	}

}
