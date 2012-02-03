package com.moseph.mra.agent.analyser;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

/**
 * Analyses the pattern of changes from scored note lengths to played note lengths
 * @author s0239182
 *
 */
public class LengthPatternAnalyser extends CurveNormalisedPatternAnalyser
{
	public static String featureName = "Length Proportion Pattern";
	
	public LengthPatternAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}
	
	double getAnnotatedValueForNote( Note played, Note scored )
	{
		if( played.getLongerThan() ) return Double.NaN;
		return  played.getDuration() / scored.getDuration();
	}

	@Override
	double getValueForNote( Note n )
	{
		if( n.getLongerThan() ) return Double.NaN;
		return n.getDuration() * quantisation;
	}

	@Override
	String getRegressionAnalyserFeatureName()
	{
		return LengthRegressionAnalyser.featureName;
	}

	@Override
	String getRegressionAnalyserName()
	{
		return "LengthRegression";
	}
	
	boolean analyseAnnotations()
	{
		return true;
	}

	public boolean skipOverhangingNotes() { return true; }
}
