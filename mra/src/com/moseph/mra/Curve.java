package com.moseph.mra;

import com.moseph.mra.agent.attribute.ValuedAttribute;

public class Curve<O extends ValuedAttribute> extends Span<O>
{
	double startValue = 0.0;
	double endValue = 0.0;
	double slope = 0.0;
	
	public Curve() { super(); }
	public Curve( double onset, double endTime, double startValue, double endValue )
	{
		super( null, onset, endTime );
		this.startValue = startValue;
		this.endValue = endValue;
		if( getDuration() != 0.0 ) 
			this.slope = ( endValue - startValue ) /getDuration();
	}
	
	public double sample( double point )
	{
		if( point < onset || point > endTime ) return 0.0;
		return startValue + ( point - onset ) * slope;
	}
	
	public String getContentString()
	{
		return startValue + " --> " + endValue;
	}
	
	public int realCompare( Object o )
	{
		int sup = super.realCompare( o );
		if( sup != 0 ) return sup;
		Curve oth = (Curve) o;
		if( oth.startValue < startValue ) return -1;
		if( oth.startValue > startValue ) return 1;
		if( oth.endValue < endValue ) return -1;
		if( oth.endValue > endValue ) return 1;
		return 0;
	}
	
	public boolean fakeDifferences()
	{
		return true;
	}
	
	public Curve clone()
	{
		return (Curve)super.clone();
	}
}
