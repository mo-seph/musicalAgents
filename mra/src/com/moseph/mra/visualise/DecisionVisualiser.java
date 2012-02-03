package com.moseph.mra.visualise;

import com.moseph.mra.Decision;

public class DecisionVisualiser extends UnitVisualiser
{
	//TODO DecisionVisualiser
	public DecisionVisualiser()
	{
		super();
	}

	public void dataFromObject()
	{
		add( getVisualiser( ((Decision)dat).getExpression() ) );
	}
}
