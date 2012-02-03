package com.moseph.mra;

import com.moseph.mra.agent.attribute.MRAAttribute;

public class StringAttribute
{
	String name;
	
	public StringAttribute( String name )
	{
		this.name = name;
	}
	
	public String toString() { return name; }
	public StringAttribute clone()
	{
		return new StringAttribute( name );
	}
	
	public int compareTo( MRAAttribute other )
	{
		return name.compareTo( other.toString() );
	}
}
