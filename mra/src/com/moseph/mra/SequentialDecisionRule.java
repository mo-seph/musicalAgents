package com.moseph.mra;

public class SequentialDecisionRule extends SectionDecisionRule {
	int counter;
	
	public SequentialDecisionRule( Section s )
	{
		super( s );
	}
	
	/* (non-Javadoc)
	 * @see com.moseph.mra.SectionDecisionRule#getNextSection()
	 */
	public Section getNextSection()
	{
		Section s = section.getSection( counter );
		counter++;
		return s;
	}
	public void reset()
	{
		counter = 0;
	}

	public String getName() { return "Sequential"; }
}
