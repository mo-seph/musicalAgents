package com.moseph.mra.symbolic;

import com.moseph.mra.agent.analyser.GroupNumericFeature;
import com.moseph.mra.agent.attribute.NumericFeature;

public class NumericSymbolDifferenceExtractor extends SymbolDifferenceExtractor<NumericTreeValue,GroupNumericFeature<NumericFeature>,NumericFeature>
{
	public NumericSymbolDifferenceExtractor( String name, GroupNumericFeature<NumericFeature> feature, TreeLattice<Double, NumericTreeValue> lattice )
	{
		super( name, feature );
		symboliser = new NumericSymboliser( lattice );
		this.lattice = lattice;
	}
}
