package com.moseph.mra.logic;

public class FunctionZero extends Function
{

	public FunctionZero( ExpressionTerm...terms)
	{
		super( terms );
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
	public String getName()	{ return "Zero"; }

}
