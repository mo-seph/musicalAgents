package com.moseph.mra.symbolic;

import java.util.*;

import com.moseph.mra.acts.ActionExtractor;
import com.moseph.mra.agent.Context;
import com.moseph.mra.agent.analyser.*;

public class EmptyLatticeManager implements LatticeManager
{
	Map<String,NumericTreeLattice> lattices =  new HashMap<String, NumericTreeLattice>();
	NumericTreeLattice defaultLattice = null;
	NumericTreeLattice defaultPatternLattice = null;
	Map<String,NumericPatternLattice> patternLattices = new HashMap<String, NumericPatternLattice>();
	int patternSize;
	
	public EmptyLatticeManager( int patternSize )
	{
		this.patternSize = patternSize;
	}

	public NumericTreeLattice getDefaultLattice()
	{
		return defaultLattice;
	}

	public NumericTreeLattice getDefaultPatternLattice()
	{
		return defaultPatternLattice;
	}

	public NumericTreeLattice getLattice( String name )
	{
		if( lattices.containsKey( name ))
		{
			return lattices.get( name );
		}
		System.err.println( "No lattice for " + name + ", using default: " + defaultLattice );
		return defaultLattice;
	}
	
	public NumericTreeLattice getPatternLattice( String name )
	{
		if( lattices.containsKey( name ))
		{
			return lattices.get( name );
		}
		System.err.println( "No lattice for " + name );
		if( defaultPatternLattice != null )
		{
			System.err.println( "Using default pattern lattice");
			return defaultPatternLattice;
		}
		
		System.err.println( "Fallback to default lattice");
		return defaultLattice;
	}
	
	public void setDefaultLattice( NumericTreeLattice lattice )
	{
		defaultLattice = lattice;
	}
	public void setDefaultPatternLattice( NumericTreeLattice lattice )
	{
		defaultPatternLattice = lattice;
	}
	
	public NumericPatternLattice getPatternLatticeFor( String latticeName )
	{
		return patternLattices.get( latticeName );
	}
	
	public void setLattice( String latticeName, NumericTreeLattice lattice )
	{
		System.err.println( "Setting lattice for " + latticeName + " to:\n" + lattice );
		lattices.put( latticeName, lattice );
		patternLattices.put(  latticeName, new NumericPatternLattice( patternSize, lattice ) );
	}


}
