package com.moseph.mra.symbolic;

public abstract class SingleValue<T extends SingleValue<T,V>,V> extends Value<T,V>
{

	protected V value;
	protected String name;

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	public String toString()
	{
		return name + " {" + value + "}";
	}

	public V getValue()
	{
		return value;
	}

	public boolean equals( T other )
	{
		return name.equals( other.name );
	}

	public abstract T getTerm( String name, V value );

}
