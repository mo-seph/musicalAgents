package com.moseph.mra.symbolic;

import com.moseph.mra.agent.attribute.PatternAttribute;

public class PatternSymboliser implements Symboliser<PatternAttribute, NumericPatternValue>
{
	TreeLattice<Double,NumericTreeValue> lattice;
	NumericSymboliser symboliser;
	
	public PatternSymboliser( TreeLattice<Double, NumericTreeValue> lattice )
	{
		this.lattice = lattice;
		symboliser = new NumericSymboliser( lattice );
	}
	public NumericPatternValue symbolise( PatternAttribute feature )
	{
		NumericPatternValue ret = new NumericPatternValue( feature.getNumBuckets(), lattice );
		for( int i = 0; i < feature.getNumBuckets(); i++ )
		{
			//System.out.println( "i: " + i + " feat: " + feature.getBucketValue( i ));
			ret.set( i, symboliser.symbolise( feature.getBucketValue( i ) ) );
		}
		return ret;
	}

}
