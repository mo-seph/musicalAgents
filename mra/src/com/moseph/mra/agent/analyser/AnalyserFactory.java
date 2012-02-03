package com.moseph.mra.agent.analyser;

import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;

public class AnalyserFactory
{
	Context context;
	MusicianInformation me;
	FeatureSet featureSet;
	public static final double DEFAULT_SMOOTHING = 0.3;
	double smoothing = DEFAULT_SMOOTHING;
	
	public AnalyserFactory( Context context )
	{
		this.context = context;
		me = context.getMyID();
		featureSet = context.getFeatures();
	}
	
	public Analyser getAnalyser( String name )
	{
		if( name.equals( "Dynamics"))
			return getAnalyser( name, smoothing );
		return null;
	}
	
	public Analyser getAnalyser( String name, double smooth )
	{
		if( name.equals( "Dynamics")) return new DynamicAverageAnalyser( context, smooth );
		if( name.equals( "Density")) return new DensityAnalysis( context, smooth );
		return null;
	}
}
