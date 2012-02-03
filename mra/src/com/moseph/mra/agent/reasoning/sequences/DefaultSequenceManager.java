package com.moseph.mra.agent.reasoning.sequences;

import com.moseph.mra.acts.ActRelation;

public class DefaultSequenceManager implements SequenceManager
{
	SequenceCollection s = new SequenceCollection<ActRelation>( 3  );
	public SequenceCollection<ActRelation> getSequences()
	{
		return s;
	}
	
}
