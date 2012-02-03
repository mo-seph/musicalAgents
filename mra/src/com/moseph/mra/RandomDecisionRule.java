package com.moseph.mra;

public class RandomDecisionRule extends SectionDecisionRule {

	public RandomDecisionRule( Section s )
	{
		super( s );
	}
	public Section getNextSection() {
		int size = section.getNumChildren();
		return section.getSection( (int)( Math.random() * size ));
	}

	public String getName() { return "Random"; }

}
