package com.moseph.mra.logic;

import java.util.List;

public class ConnectiveOr extends ConnectiveExpression
{

	public ConnectiveOr(ExpressionTerm...terms)
	{
		super(terms);
	}

	public ConnectiveOr( List<ExpressionTerm> terms )
	{
		super( terms );
	}

	@Override
	public boolean isTrue()
	{
		for( ExpressionTerm t : terms ) if( t.isTrue() ) { return true; }
		return false;
	}

	public String getConnectiveString()
	{
		return "OR";
	}
}
