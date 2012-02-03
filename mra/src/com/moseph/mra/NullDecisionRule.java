package com.moseph.mra;

public class NullDecisionRule extends SectionDecisionRule {

	public NullDecisionRule( Section section )
	{
		super( section );
	}
	@Override
	public Section getNextSection() {
		return null;
	}

	@Override
	public String getName() {
		return "null";
	}

}
