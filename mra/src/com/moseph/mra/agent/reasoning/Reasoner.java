package com.moseph.mra.agent.reasoning;

import com.moseph.mra.Fragment;
import com.moseph.mra.agent.AnalysisSystem;

public interface Reasoner
{
	public RenderPlan getNextPlan( double length );
	public RenderPlan getNextPlan( double length, Fragment notes );
	public void initNecessaryAnalysers( AnalysisSystem sys );
}
