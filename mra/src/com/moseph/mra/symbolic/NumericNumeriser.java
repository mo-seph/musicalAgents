package com.moseph.mra.symbolic;

import com.moseph.mra.agent.attribute.*;

public class NumericNumeriser implements Numeriser<NumericFeature, NumericTreeValue>
{
	TreeLattice<Double, NumericTreeValue> lattice;
	
	public NumericNumeriser( TreeLattice<Double,NumericTreeValue> lattice  )
	{
		this.lattice = lattice;
	}
	
	public NumericFeature numerise( NumericTreeValue value )
	{
		//Do we need to do something special for the root?
		// if( value.isRoot() ) 
		return new ValuedAttribute( value.getValue() );
	}

}
