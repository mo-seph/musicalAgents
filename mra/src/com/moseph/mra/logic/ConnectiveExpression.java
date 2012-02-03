package com.moseph.mra.logic;

import java.util.List;
import java.util.Vector;

public abstract class ConnectiveExpression implements ExpressionTerm
{
	List<ExpressionTerm> terms = new Vector<ExpressionTerm>();
	
	public ConnectiveExpression( ExpressionTerm...terms)
	{
		super();
		for( ExpressionTerm t : terms )
			this.terms.add( t );
		// TODO Auto-generated constructor stub
	}
	
	public ConnectiveExpression( List<ExpressionTerm> terms )
	{
		this.terms.addAll( terms );
	}
	
	public void addTerm( ExpressionTerm t )
	{
		terms.add(t);
	}
	
	public abstract boolean isTrue();
	public double getValue()
	{
		return isTrue() ? 1.0 : 0.0;
	}
	
	public String getConnectiveString()
	{
		return "~";
	}
	
	public List<ExpressionTerm>getConnectives()
	{
		return terms;
	}

}
