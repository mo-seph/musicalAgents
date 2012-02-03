package com.moseph.mra.symbolic;

import java.util.*;

public abstract class PatternValue<V,Tr extends TreeValue<Tr, V>,T extends PatternValue<V,Tr,T>> extends Value<T,V>
{
	List<Tr> values;
	TreeLattice<V,Tr> valueLattice;
	int size;
	
	public PatternValue( int size, TreeLattice<V, Tr> lattice )
	{
		this.size = size;
		this.valueLattice = lattice;
		values = new ArrayList<Tr>(size);
		for( int i = 0; i < size; i++  )
			values.add( valueLattice.getAny() );
	}
	
	public String toString()
	{
		String ret = "{ ";
		for( Tr val : values )
			ret += val + " ";
		ret += "}";
		return ret;
	}
	
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	public Tr get( int i )
	{
		return values.get( i );
	}
	public void set( int i, Tr val )
	{
		values.set( i % size, val );
	}
	
	public void setAny( int i )
	{
		values.set( i % size, valueLattice.getAny() );
	}
	public void setIncompatible( int i )
	{
		values.set( i % size, valueLattice.getIncompatible() );
	}
	
	public void setValue( int i, String term )
	{
		values.set( i % size, valueLattice.getTerm( term ) );
	}
	
	public void setValues( List<Tr> vals )
	{
		for( int i = 0; i < values.size(); i++ ) 
			if( vals.get(i) != null ) values.set( i, vals.get( i ) );
			else values.set( i, valueLattice.getIncompatible() );
	}
	
	
	/**
	 * Tests if this pattern value is equal to another of the same type
	 * @param other
	 * @return
	 */
	public boolean equals( Object comp )
	{
		T other;
		try
		{
			other = (T)comp;
		} catch (ClassCastException e )
		{
			return false;
		}
		//could also just do toString and compare strings
		for( int i = 0; i < size; i++ )
		{
			if( ! values.get( i ).equals( other.get( i ) ))
			{
				return false;
			}
		}
		return true;
	}
	
	public int getSize()
	{
		return size;
	}

	abstract T createCopy();
	abstract T createCopy( List<Tr> values );
	abstract T createEmptyCopy();
}
