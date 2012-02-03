package com.moseph.mra.symbolic;

import java.util.List;

import com.moseph.mra.agent.attribute.NumericFeature;

public class NumericSymboliser implements Symboliser<NumericFeature, NumericTreeValue>
{
	TreeLattice<Double,NumericTreeValue> lattice;
	
	public NumericSymboliser( TreeLattice<Double,NumericTreeValue> lattice )
	{
		this.lattice = lattice;
	}
	
	public NumericTreeValue symbolise( NumericFeature feature )
	{
		return symbolise( feature.getValue() );
	}
	
	public NumericTreeValue symbolise( double value )
	{
		List<NumericTreeValue> children = lattice.root.getChildren();
		NumericTreeValue min, max;
		if( Double.isNaN( value )) return lattice.root;
		if( children.get(0).value > children.get(1).value )
		{
			min = children.get( 1 );
			max = children.get( 0 );
		}
		else
		{
			min = children.get( 0 );
			max = children.get( 1 );
		}
		if( value < max.getValue() && value > min.getValue() ) return lattice.root;
		if( value < min.getValue() )
		{
			while( min.children.size() != 0 )
			{
				max = min;
				min = max.getChildren().get( 0 );
				if( value > min.getValue() ) return max;
			}
			return min;
		}
		else
		{
			while( max.children.size() != 0 )
			{
				min = max;
				max = max.getChildren().get( 0 );
				if( value < max.getValue() ) return min;
			}
			return max;
		}
	}
	

}
