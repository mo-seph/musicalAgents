package com.moseph.mra.agent.analyser;

import com.moseph.mra.Fragment;
import com.moseph.mra.agent.Context;
import com.moseph.mra.agent.attribute.*;

public abstract class PatternAnalyser extends Analyser<PatternAttribute>
{
	double smoothing = 0.3;
	protected double quantisation;

	public PatternAnalyser( Context context, double smoothing )
	{
		super( context );
		this.smoothing = smoothing;
		this.quantisation = context.getQuantisation();
	}

	public GroupFeature createFeatureSet()
	{
		return new GroupPatternFeature( new PatternAttribute(1.0, quantisation ), me );
	}
}
