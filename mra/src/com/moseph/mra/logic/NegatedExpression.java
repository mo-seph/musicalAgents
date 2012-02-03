package com.moseph.mra.logic;


public class NegatedExpression implements ExpressionTerm
{
	ExpressionTerm term;
	
	public NegatedExpression( ExpressionTerm term)
	{
		this.term = term;
	}
	
	public boolean isTrue()
	{
		return  !( term.isTrue() );
	}
	public double getValue()
	{
		return isTrue() ? 1.0 : 0.0;
	}

}
