package com.moseph.mra.agent.attribute;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.*;



public class DualValuedAttribute extends MRAAttribute implements Serializable, DualNumericFeature
{
	
	double value1;
	double value2;
	
	public DualValuedAttribute()
	{
		this( 0.0, 0.0 );
	}
	
	public DualValuedAttribute( double value1, double value2 )
	{
		setValue1( value1 );
		setValue2( value2 );
	}
	
	public DualValuedAttribute( DualValuedAttribute p )
	{
		setValue1( p.value1 );
		setValue2( p.value2 );
	}
	
	public double getValue1() { return value1; }
	public double getValue2() { return value2; }
	
	public void setValue1( double value1 ) { this.value1 = value1; }
	public void setValue2( double value2 ) { this.value2 = value2; }
	
	public void setParameters( List<String> params)
	{
		if( params.size() > 0 ) setValue1( Double.parseDouble( params.get( 0 ) ) );
		if( params.size() > 1 ) setValue2( Double.parseDouble( params.get( 1 ) ) );
	}
	
	@Override
	public DualValuedAttribute clone()
	{
		return new DualValuedAttribute( value1, value2 );
	}
	
	/* (non-Javadoc)
	 * @see com.moseph.mra.attribute.NumericFeature#equals(com.moseph.mra.attribute.ValuedAttribute)
	 */
	public boolean equals( DualNumericFeature op )
	{
		if( op.getValue1() != value1 ) return false;
		if( op.getValue2() != value2 ) return false;
		return true;
	}

	@Override
	public int compareTo( Feature other )
	{
		if( ! ( other instanceof DualValuedAttribute ) ) return -1;
		DualValuedAttribute o = (DualValuedAttribute)other;
		int c1 = Double.compare( value1, o.value1 );
		if( c1 != 0 ) return c1;
		return Double.compare( value2,  o.value2 );
	}
	
	public String toString()
	{
		return "[" + value1 + "," + value2 + "]";
	}
	
	public double distance( Feature f )
	{
		if( !(f instanceof DualNumericFeature)) return Double.NaN;
		DualNumericFeature feature = (DualNumericFeature)f;
		return abs( getValue1() - feature.getValue1() ) + 
			abs( getValue2() - feature.getValue2() );
	}
	
	
	
}
