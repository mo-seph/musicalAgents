package com.moseph.mra;

import java.util.List;

import com.moseph.mra.logic.ExpressionTerm;

public abstract class SectionDecisionRule 
{
	Section section;
	SectionDecisionRule( Section s )
	{
		setTargetSection(s);
	}
	
	public abstract Section getNextSection();
	public void reset() {};
	public void setTargetSection( Section s )
	{
		section = s;
	}
	
	public abstract String getName();
	
	public void setParameters( List<ExpressionTerm> params ) {};

}