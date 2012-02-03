package com.moseph.mra.logic;

import com.moseph.mra.NamedSymbol;

public class Term implements ExpressionTerm, NamedSymbol
{
	String name;
	double value;
	boolean isUsed;
	boolean isDefined;
	
	public Term( String name )
	{
		super();
		this.name = name;
		value = 0.0;
	}

	public Term( String name, double value )
	{
		this( name );
		this.value = value;
	}

	public boolean isUsed()
	{
		return isUsed;
	}

	public boolean isDefined()
	{
		return isDefined;
	}
	
	public void use() { isUsed = true; }
	public void define() { isDefined = true; }
	
	public String toString()
	{
		return name + ": " + value;
	}

	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see com.moseph.mra.ExpressionTerm#getValue()
	 */
	public double getValue()
	{
		return value;
	}

	public void setValue( double value )
	{
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see com.moseph.mra.ExpressionTerm#isTrue()
	 */
	public boolean isTrue()
	{
		return value > 0.001;
	}
}
