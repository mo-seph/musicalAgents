package com.moseph.mra.agent.attribute;

import com.moseph.mra.agent.Context;
import com.moseph.mra.symbolic.*;

public class PatternNumeriser implements Numeriser<PatternAttribute,NumericPatternValue>
{
	Context context;
	TreeLattice<Double, NumericTreeValue> lattice;
	
	public PatternNumeriser( Context context, TreeLattice<Double,NumericTreeValue> lattice )
	{
		this.context = context;
		this.lattice = lattice;
	}

	public PatternAttribute numerise( NumericPatternValue value )
	{
		int numBuckets = value.getSize();
		double quantisation = context.getQuantisation();
		double[] values = new double[numBuckets];
		for( int i = 0; i < numBuckets; i++ )
		{
			NumericTreeValue v = value.get( i );
			if( v != null ) values[i] = v.getValue();
			else values[i] = 0.0;
		}
		return new PatternAttribute( quantisation, values  );
	}
}
