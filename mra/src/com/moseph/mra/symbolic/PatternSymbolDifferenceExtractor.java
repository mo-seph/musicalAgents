package com.moseph.mra.symbolic;

import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;

public class PatternSymbolDifferenceExtractor extends 
	SymbolDifferenceExtractor<NumericPatternValue,GroupPatternFeature<PatternAttribute>,PatternAttribute>
{
	public PatternSymbolDifferenceExtractor( String name, GroupPatternFeature<PatternAttribute> feature, TreeLattice<Double, NumericTreeValue> lattice, int size )
	{
		super( name, feature );
		symboliser = new PatternSymboliser( lattice );
		this.lattice = 
			new PatternLattice<Double, NumericTreeValue, NumericPatternValue>( 2, lattice, new NumericPatternValue( size, lattice ) );
	}
}
