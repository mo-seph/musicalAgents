package com.moseph.mra.agent.analyser;

import java.util.List;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

/**
 * Analyses given fragments to extract the pattern of dynamic variation -
 * the pattern of accents used
 * @author s0239182
 *
 */
public class DynamicPatternAnalyser extends CurveNormalisedPatternAnalyser
{
	public static String featureName = "Accents";
	
	public DynamicPatternAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}
	
	String getRegressionAnalyserFeatureName()
	{
		return DynamicRegressionAnalyser.featureName;
	}

	String getRegressionAnalyserName()
	{
		return "DynamicRegression";
	}

	@Override
	double getValueForNote( Note n )
	{
		return n.getVelocity();
	}

	
	
	

}
