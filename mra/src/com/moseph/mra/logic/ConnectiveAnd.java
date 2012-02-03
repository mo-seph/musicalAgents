package com.moseph.mra.logic;

import java.util.List;

public class ConnectiveAnd extends ConnectiveExpression
{

	public ConnectiveAnd( ExpressionTerm...terms)
	{
		super( terms );
	}

	public ConnectiveAnd( List<ExpressionTerm> terms )
	{
		super( terms );
	}

	@Override
	public boolean isTrue()
	{
		for( ExpressionTerm t : terms ) if( ! t.isTrue() ) { return false; }
		return true;
	}
	public String getConnectiveString()
	{
		return "AND";
	}
}
