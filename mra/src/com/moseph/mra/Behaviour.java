/**
 * 
 */
package com.moseph.mra;

import com.moseph.mra.logic.ExpressionTerm;

/**
 * @author s0239182
 *
 */
public class Behaviour extends Unit
{
	Action action;
	ExpressionTerm expression = null;
	Decision decision = null;
	
	public Behaviour( String name )
	{
		super( name );
	}
	
	Behaviour( String name, Action a, ExpressionTerm e )
	{
		this( name );
		this.action = a;
		this.expression = e;
	}
	
	Behaviour( String name, Action a, Decision d )
	{
		this( name );
		this.action = a;
		this.decision = d;
	}

	public Action getAction()
	{
		return action;
	}

	public ExpressionTerm getExpression()
	{
		if( decision != null ) return decision .getExpression();
		return expression;
	}

	public Decision getDecision()
	{
		return decision;
	}

	public void setDecision( Decision decision )
	{
		this.decision = decision;
		decision.use();
		this.expression = null;
	}

	public void setAction( Action action )
	{
		action.use();
		this.action = action;
	}

	public void setExpression( ExpressionTerm expression )
	{
		this.expression = expression;
		this.decision = null;
	}
}
