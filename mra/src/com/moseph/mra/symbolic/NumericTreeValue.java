package com.moseph.mra.symbolic;

public class NumericTreeValue extends TreeValue<NumericTreeValue,Double>
{
	public NumericTreeValue( String name, double value )
	{
		super( name, value );
	}
	
	public NumericTreeValue getTerm( String name, Double value )
	{
		return new NumericTreeValue( name, value );
	}
	
	public NumericTreeValue getAny()
	{
		return new NumericTreeValue( "(*)", Double.NaN );
	}
	public NumericTreeValue getIncomp()
	{
		return new NumericTreeValue( "(/)", Double.NaN );
	}

}
