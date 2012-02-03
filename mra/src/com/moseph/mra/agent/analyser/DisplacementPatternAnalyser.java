package com.moseph.mra.agent.analyser;


import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

/**
 * Analyses the pattern of displacements from quantised values of the notes played
 * @author s0239182
 *
 */
public class DisplacementPatternAnalyser extends CurveNormalisedPatternAnalyser
{
	public static String featureName = "Displacements";
	public DisplacementPatternAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}
	
	String getRegressionAnalyserFeatureName()
	{
		return DisplacementRegressionAnalyser.featureName;
	}
	
	String getRegressionAnalyserName()
	{
		return "DisplacementRegression";
	}
	double getValueForNote( Note n )
	{
		return n.getOnset() - n.getQuantisedOnset();
	}


}
