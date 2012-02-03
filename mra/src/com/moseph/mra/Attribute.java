/**
 * 
 */
package com.moseph.mra;

/**
 * @author s0239182
 *
 */
public class Attribute
{
	String name;
	Object value;
	
	public Attribute( String name, Object val )
	{
		this.name = name;
		this.value = val;
	}
	
	public String toString()
	{
		return name + ": " + value;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public Object getValue()
	{
		return value;
	}
	
	public String stringValue()
	{
		return value.toString();
	}

	public void setValue( Object value )
	{
		this.value = value;
	}
}
