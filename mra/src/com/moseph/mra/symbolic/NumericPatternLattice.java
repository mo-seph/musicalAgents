package com.moseph.mra.symbolic;

import java.util.List;

public class NumericPatternLattice extends PatternLattice<Double, NumericTreeValue, NumericPatternValue>
{

	public NumericPatternLattice( int size, NumericTreeLattice lattice, NumericPatternValue basic )
	{
		super( size, lattice, basic );
	}
	
	public NumericPatternLattice( int size, NumericTreeLattice lattice )
	{
		super( size, lattice, new NumericPatternValue( size, lattice ) );
	}
	
}
