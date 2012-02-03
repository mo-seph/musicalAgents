package com.moseph.mra;

import com.moseph.mra.logic.ExpressionTerm;

public class Decision extends Unit
{
	ExpressionTerm expression;
	Decision ( String name )
	{
		super( name );
	}
	
	public void setExpression( ExpressionTerm expression )
	{
		this.expression = expression;
	}
	
	public ExpressionTerm getExpression()
	{
		return expression;
	}
}
