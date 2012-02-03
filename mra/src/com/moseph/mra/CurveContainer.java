package com.moseph.mra;

import com.moseph.mra.agent.attribute.ValuedAttribute;
import static java.lang.Math.*;

public class CurveContainer<T extends Curve> extends SpanContainer<T>
{
	public CurveContainer()
	{
		super();
	}
	
	public CurveContainer( String name )
	{
		super( name );
	}
	public CurveContainer( double length )
	{
		super( length );
	}
	
	/**
	 * Creates a CurveContainer with a single curve, having the given value
	 * for the entire length given. NOT GENERICS SAFE!
	 * @param length
	 * @param value
	 */
	public CurveContainer( double length, double value )
	{
		this( length );
		addEvent( (T)new Curve( 0.0, length, value, value ) );
	}
	
	/**
	 * Creates a CurveContainer with a single curve, sloping from 
	 * value1 to value2. NOT GENERICS SAFE!
	 * @param length
	 * @param value1
	 * @param value2
	 */
	public CurveContainer( double length, double value1, double value2 )
	{
		this( length );
		addEvent( (T)new Curve( 0.0, length, value1, value2 ) );
	}
	
	
	public CurveContainer( T...spans )
	{
		super( spans );
	}
	
	public boolean replaceOnly()
	{
		return false;
	}
	
	public void addEvent( T n )
	{
		super.addEvent( n, false );
	}
	
	public double sample( double time )
	{
		double output = 0.0;
		for( T n : events.getPotentiallyActive( time, time ) )
				output += n.sample( time );
		return output;
	}
	
	//Not sure about this - overriding add in SpanContainer so that
	//we always make a copy as we add; also ignoring replacing sections
	public void addEvent( T n, boolean checkMerge )
	{
		n.use();
		events.add( (T)n.clone() );
		if( n.getEndTime() > length )
			length = (double)n.getEndTime();
	}
	
	CurveContainer<T> getEmptyContainer( double length )
	{
		return new CurveContainer<T>( length );
	}
	
	public boolean sloppyCompare( CurveContainer o, double threshold )
	{
		if( abs( length - o.length ) > threshold ) return false;
		double startA = sample( 0.0 );
		double startB = o.sample( 0.0 );
		double endA = sample( length );
		double endB = o.sample( o.length );
		if( Double.isNaN( startA ) ^ Double.isNaN( startB )) return false;
		if( Double.isNaN( endA ) ^ Double.isNaN( endB )) return false;
		if( abs( startA - startB ) > threshold ) return false;
		if( abs( endA - endB ) > threshold ) return false;
		return true;
	}
	
}
