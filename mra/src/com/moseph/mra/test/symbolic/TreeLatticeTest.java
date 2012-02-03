package com.moseph.mra.test.symbolic;

import java.util.*;

import junit.framework.TestCase;

import com.moseph.mra.symbolic.*;

public class TreeLatticeTest extends TestCase
{
	NumericTreeValue root = new NumericTreeValue( "Root", 0.0 );
	TreeLattice<Double, NumericTreeValue> tree = new TreeLattice<Double, NumericTreeValue>( root );
	NumericTreeValue a1;
	NumericTreeValue a2;
	NumericTreeValue a3;
	NumericTreeValue b1;
	NumericTreeValue b2;
	NumericTreeValue b3;
	NumericTreeValue b2b;
	NumericTreeValue b3b;
	Set<NumericTreeValue> a1Desc = new HashSet<NumericTreeValue>();
	Set<NumericTreeValue> b1Desc = new HashSet<NumericTreeValue>();
	Set<NumericTreeValue> b1Branch = new HashSet<NumericTreeValue>();
	Set<NumericTreeValue> a1Set = new HashSet<NumericTreeValue>();
	Set<NumericTreeValue> a1Ancestors = new HashSet<NumericTreeValue>();
	
	public void setUp()
	{
		a1 = tree.addTerm( "a1", 0.3, root );
		a2 = tree.addTerm( "a2", 0.7, "a1" );
		a3 = tree.addTerm( "a3", 0.9, "a2" );
		b1 = tree.addTerm( "b1", 0.3, root );
		b2 = tree.addTerm( "b2", 0.7, "b1" );
		b3 = tree.addTerm( "b3", 0.9, "b2" );
		b2b = tree.addTerm( "b2b", 0.7, "b1" );
		b3b = tree.addTerm( "b3b", 0.7, "b2b" );
		
		a1Desc.add( a2 );
		a1Desc.add( a3 );
		
		b1Desc.add( b2 );
		b1Desc.add( b3 );
		b1Desc.add( b2b );
		b1Desc.add( b3b );
		
		a1Set.add( a1 );
		a1Ancestors.addAll( tree.getAncestors( a1 ));
		
		b1Branch.addAll( b1Desc );
		b1Branch.add( b1 );
	}
	
	public void testBasicOperation()
	{
		//System.out.println( tree );
	}
	
	public void testAncestry()
	{
		assertTrue( tree.isAncestor( a2, a1 ));
		assertTrue( tree.isAncestor( a3, a1 ));
		assertTrue( tree.isAncestor( b3, b1 ));
		assertTrue( tree.isAncestor( b3b, b1 ));
		assertFalse( tree.isAncestor( b3b, a1 ));
	}
	
	public void testDescendants()
	{
		Set<NumericTreeValue> descA = new HashSet<NumericTreeValue>();
		descA.addAll( tree.getDescendants( a1 ) );
		assertTrue( equalSets( a1Desc, descA ) );
		
		Set<NumericTreeValue> gotB = new HashSet<NumericTreeValue>();
		gotB.addAll( tree.getDescendants( b1 ) );
		assertTrue( equalSets( b1Desc, gotB ) );
	}
	
	public void testCalculateRelationship()
	{
		assertEquals( Relationship.SAME, tree.getRelationship( a1, a1 ));
		assertEquals( Relationship.SUBSUMED, tree.getRelationship( a1, a2 ));
		assertEquals( Relationship.SUBSUMES, tree.getRelationship( a2, a1 ));
		assertEquals( Relationship.DISJOINT, tree.getRelationship( a1, b1 ));
		assertEquals( Relationship.ALTER, tree.getRelationship( b3, b3b ));
	}
	
	public void testGetPossibilities()
	{
		Set<NumericTreeValue> subsumesA1 = new HashSet<NumericTreeValue>();
		subsumesA1.addAll( tree.getPossibleValues( a1, Relationship.SUBSUMES ) );
		assertTrue( equalSets( a1Desc, subsumesA1 ));
		
		Set<NumericTreeValue> subsumedA1 = new HashSet<NumericTreeValue>();
		subsumedA1.addAll( tree.getPossibleValues( a1, Relationship.SUBSUMED ) );
		assertTrue( equalSets( a1Ancestors, subsumedA1 ));
		
		Set<NumericTreeValue> alterA1 = new HashSet<NumericTreeValue>();
		alterA1.addAll( tree.getPossibleValues( a1, Relationship.ALTER ) );
		assertTrue( equalSets( new HashSet<NumericTreeValue>(), alterA1  ));
		
		Set<NumericTreeValue> disjointA1 = new HashSet<NumericTreeValue>();
		disjointA1.addAll( tree.getPossibleValues( a1, Relationship.DISJOINT ) );
		assertTrue( equalSets( b1Branch, disjointA1 ));
		
		Set<NumericTreeValue> sameA1 = new HashSet<NumericTreeValue>();
		sameA1.addAll( tree.getPossibleValues( a1, Relationship.SAME ) );
		assertTrue( equalSets( a1Set, sameA1 ));
		
		Set<NumericTreeValue> alterB3 = new HashSet<NumericTreeValue>();
		alterB3.addAll( tree.getPossibleValues( b3, Relationship.ALTER ) );
		Set<NumericTreeValue> expected = new HashSet<NumericTreeValue>();
		expected.add( b3b );
		expected.add( b2b );
		assertTrue( equalSets( expected, alterB3  ));
	}
	
	public void testGetBranch()
	{
		List<NumericTreeValue> expected = 
			new Vector<NumericTreeValue>( Arrays.asList( new NumericTreeValue[] { root, a1, a2, a3 } ));
		List<NumericTreeValue> got = tree.getBranch( a2 );
		assertTrue( equalLists( expected, got ));
	}
	
	public void testGetTopAncestor()
	{
		assertEquals( a1, tree.getTopAncestor( a1 ));
		assertEquals( a1, tree.getTopAncestor( a2 ));
		assertEquals( a1, tree.getTopAncestor( a3 ));
		assertEquals( b1, tree.getTopAncestor( b3 ));
		assertEquals( b1, tree.getTopAncestor( b3b ));
	}
	
	void printCollection( Collection<NumericTreeValue> list )
	{
		System.out.print( "{ ");
		for( NumericTreeValue nt : list ) System.out.print( nt + " " );
		System.out.print( "}\n");
	}
	
	boolean equalLists( List<NumericTreeValue> a, List<NumericTreeValue> b )
	{
		if( a.size() != b.size() )
		{
			System.out.print( "Expected: " ); printCollection( a );
			System.out.print( "Got: " ); printCollection( b );
			System.out.println( "Different size!");
			return false;
		}
		for( int i = 0; i < a.size(); i++ )
		{
			if( ! a.get(i).equals( b.get( i ) ))
			{
				System.out.print( "Expected: " ); printCollection( a );
				System.out.print( "Got: " ); printCollection( b );
				System.out.println( "Mismatch: " + a.get( i ) + ":" + b.get(i) );
				return false;
			}
		}
		return true;
	}
	boolean equalSets( Set<NumericTreeValue> a, Set<NumericTreeValue> b )
	{
		if( a.size() != b.size() )
		{
			printCollection( a );
			printCollection( b );
			System.out.println( "Different size!");
			return false;
		}
		for( NumericTreeValue ca : a )
		{
			if( ! b.contains( ca ))
			{
				printCollection( a );
				printCollection( b );
				System.out.println( "Mismatch: " + ca );
				return false;
			}
		}
		return true;
	}

}
