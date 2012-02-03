package com.moseph.mra.logic;

public class FunctionPlaceholder extends Function
{
	String name = "Unknown";
	
	public FunctionPlaceholder( String name, ExpressionTerm...terms)
	{
		super( terms );
		this.name = name;
	}
	
	@Override
	public double getValue()
	{
		return 0;
	}

	@Override
	public boolean isTrue()
	{
		return false;
	}
	public String getName()	{ return name; }

}
