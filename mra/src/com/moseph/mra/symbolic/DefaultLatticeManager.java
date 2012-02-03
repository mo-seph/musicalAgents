package com.moseph.mra.symbolic;

import java.util.*;

import com.moseph.mra.acts.ActionExtractor;
import com.moseph.mra.agent.Context;
import com.moseph.mra.agent.analyser.*;

public class DefaultLatticeManager extends EmptyLatticeManager
{
	public DefaultLatticeManager( int patternSize )
	{
		super( patternSize );
		setUpLattices();
	}

	public void setUpLattices()
	{
		setLattice( DynamicAverageAnalyser.featureName, getDynamicsLatticeValue() );
		setLattice( DynamicChangeAnalyser.featureName, getDynamicChangeLatticeValue() );
		setLattice( DynamicPatternAnalyser.featureName, getAccentsLatticeValue() );
		
		setLattice( LengthAverageAnalyser.featureName, getAverageLengthLatticeValue() );
		setLattice( LengthChangeAnalyser.featureName, getLengthChangeLatticeValue() );
		setLattice( LengthPatternAnalyser.featureName, getLengthPatternLatticeValue() );
		
		setLattice( DisplacementAverageAnalyser.featureName, getAverageDisplacementLatticeValue() );
		setLattice( DisplacementChangeAnalyser.featureName, getDisplacementChangeLatticeValue() );
		setLattice( DisplacementPatternAnalyser.featureName, getDisplacementPatternLatticeValue() );
	}
	
	
	NumericTreeLattice getDynamicsLatticeValue()
	{
		NumericTreeValue root = new NumericTreeValue( "Root", 0.5 );
		NumericTreeLattice tree = new NumericTreeLattice( root );
		tree.addTerm( "mf", 0.55, root );
		tree.addTerm( "f", 0.65, "mf" );
		tree.addTerm( "ff", 0.75, "f" );
		tree.addTerm( "mp", 0.45, root );
		tree.addTerm( "p", 0.35, "mp" );
		tree.addTerm( "pp", 0.25, "p" );
		return tree;
	}

	NumericTreeLattice getDynamicChangeLatticeValue() { return getDefaultChangeLatticeValue(); }
	NumericTreeLattice getAccentsLatticeValue() { return getDefaultPatternLatticeValue(); }
	
	NumericTreeLattice getAverageLengthLatticeValue()
	{
		NumericTreeValue root = new NumericTreeValue( "Root", 0.75 );
		NumericTreeLattice tree = new NumericTreeLattice( root );
		tree.addTerm( "Legato", 1.00, root );
		tree.addTerm( "Long", 2.00, "Legato" );
		tree.addTerm( "Short", 0.60, root );
		tree.addTerm( "Stacc", 0.40, "Short" );
		return tree;
	}
	
	NumericTreeLattice getLengthChangeLatticeValue() { return getDefaultChangeLatticeValue(); }
	NumericTreeLattice getLengthPatternLatticeValue() { return getDefaultPatternLatticeValue(); }
	
	NumericTreeLattice getAverageDisplacementLatticeValue()
	{
		NumericTreeValue root = new NumericTreeValue( "Root", 0.0 );
		NumericTreeLattice tree = new NumericTreeLattice( root );
		tree.addTerm( "l", 0.01, root );
		tree.addTerm( "e", -0.01, root );
		tree.addTerm( "La", 0.05, "l" );
		tree.addTerm( "Ea", -0.05, "e" );
		tree.addTerm( "VL", 0.10, "La" );
		tree.addTerm( "VE", 0.10, "Ea" );
		return tree;
	}
	
	NumericTreeLattice getDisplacementChangeLatticeValue() { return getDefaultChangeLatticeValue(); }
	NumericTreeLattice getDisplacementPatternLatticeValue() 
	{ 
		NumericTreeValue root = new NumericTreeValue( "Root", 0.0 );
		NumericTreeLattice tree = new NumericTreeLattice( root );
		tree.addTerm( "La", 0.01, root );
		tree.addTerm( "Ea", -0.01, root );
		tree.addTerm( "VL", 0.02, "La" );
		tree.addTerm( "VE", 0.02, "Ea" );
		return tree;
	}
	
	NumericTreeLattice getDefaultPatternLatticeValue()
	{
		NumericTreeValue root = new NumericTreeValue( "Root", 0.0 );
		NumericTreeLattice tree = new NumericTreeLattice( root );
		tree.addTerm( "+", 0.15, root );
		tree.addTerm( "-", -0.15, root );
		tree.addTerm( "++", 0.30, "+" );
		tree.addTerm( "--", 0.30, "-" );
		//System.out.println( tree );
		return tree;
	}
	NumericTreeLattice getDefaultChangeLatticeValue()
	{
		NumericTreeValue root = new NumericTreeValue( "Root", 0.0 );
		NumericTreeLattice tree = new NumericTreeLattice( root );
		tree.addTerm( "+", 0.15, root );
		tree.addTerm( "-", -0.15, root );
		tree.addTerm( "++", 0.30, "+" );
		tree.addTerm( "--", 0.30, "-" );
		//System.out.println( tree );
		return tree;
	}
}
