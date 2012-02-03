package com.moseph.mra.agent.attribute;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.*;



public class ValuedAttribute extends MRAAttribute implements Serializable, NumericFeature
{
	
	double value;
	
	public ValuedAttribute()
	{
		this( 0.0 );
	}
	
	public ValuedAttribute( double value )
	{
		setValue( value );
	}
	
	public ValuedAttribute( ValuedAttribute p )
	{
		setValue( p.value );
	}
	
	/* (non-Javadoc)
	 * @see com.moseph.mra.attribute.NumericFeature#getValue()
	 */
	public double getValue()
	{
		return value;
	}
	
	/* (non-Javadoc)
	 * @see com.moseph.mra.attribute.NumericFeature#setValue(double)
	 */
	public void setValue( double value )
	{
		this.value = value;
	}
	
	public void setParameters( List<String> params)
	{
		if( params.size() > 0 ) setValue( Double.parseDouble( params.get( 0 ) ) );
	}
	
	@Override
	public ValuedAttribute clone()
	{
		return new ValuedAttribute( value );
	}
	
	/* (non-Javadoc)
	 * @see com.moseph.mra.attribute.NumericFeature#equals(com.moseph.mra.attribute.ValuedAttribute)
	 */
	public boolean equals( NumericFeature op )
	{
		//System.out.println( "Comparing " + this + " to " + op );
		return op.getValue() == value;
	}
	
	public boolean equals( Object o )
	{
		if( o instanceof NumericFeature ) return equals( (NumericFeature) o );
		return super.equals( o );
	}

	@Override
	public int compareTo( Feature other )
	{
		if( ! ( other instanceof ValuedAttribute ) ) return -1;
		ValuedAttribute o = (ValuedAttribute)other;
		return Double.compare( value,  o.value );
	}
	
	public double distance( Feature f )
	{
		if( !(f instanceof NumericFeature)) return Double.NaN;
		NumericFeature feature = (NumericFeature)f;
		return abs( getValue() - feature.getValue() );
	}
	
	public String toString()
	{
		return "[" + value + "]";
	}
	
	
}
