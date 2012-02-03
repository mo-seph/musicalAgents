package com.moseph.mra.agent.attribute;

import java.io.Serializable;
import java.util.List;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class NamedAttribute extends MRAAttribute implements Serializable
{
	Enum value;
	
	public NamedAttribute()
	{
		value = getDefault();
	}
	
	public NamedAttribute( String p )
	{
		setValue( p );
	}
	
	public NamedAttribute( NamedAttribute p )
	{
		value =  p.value ;
	}
	
	public NamedAttribute( Enum a )
	{
		value = a;
	}
	
	public String getValue()
	{
		return value.name();
	}
	
	public void setParameters( List<String> params)
	{
		if( params.size() > 0 ) setValue( params.get( 0 ) );
	}
	
	public void setValue( Enum v ) { value = v; }
	public abstract void setValue( String s );
	public abstract Enum getDefault();
	
	@Override
	public abstract NamedAttribute clone();
	
	public boolean equals( Object op )
	{
		System.out.println( "Comparing: " + this + " to " + op  );
		if( ! ( op instanceof NamedAttribute ) )return false;
		NamedAttribute other = (NamedAttribute)op;
		return other.value == value;
	}

	@Override
	public int compareTo( Feature other )
	{
		if( ! ( other instanceof NamedAttribute ) ) return -1;
		NamedAttribute o = (NamedAttribute)other;
		return value.compareTo( o.value );
	}
	
	public String toString()
	{
		return getClass().getSimpleName() + ":" + getValue();
	}
	
	public double distance( Feature f )
	{
		if( !(f instanceof NamedAttribute )) return Double.NaN;
		NamedAttribute a = (NamedAttribute)f;
		if( getValue().equals( a.getValue() )) return 0;
		return 1.0;
	}
	
}
