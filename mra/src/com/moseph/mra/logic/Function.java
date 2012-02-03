package com.moseph.mra.logic;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public abstract class Function implements ExpressionTerm
{
	List<ExpressionTerm> terms = new Vector<ExpressionTerm>();
	public Function( ExpressionTerm...terms )
	{
		setTerms( terms );
	}
	
	public void setTerms( ExpressionTerm...terms )
	{
		this.terms = Arrays.asList(terms);
	}
	public abstract double getValue();

	public abstract boolean isTrue();
	
	public String getName()	{ return "Unknown Function"; }

	public List<ExpressionTerm> getParameters()
	{
		return terms;
	}
}
