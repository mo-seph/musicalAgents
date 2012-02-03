package com.moseph.mra.agent.analyser;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

/**
 * Calculates the linear regression slope for note lengths relative to scored lengths
 * @author s0239182
 *
 */
public class LengthChangeAnalyser extends RegressionDependantAnalyser
{
	public static String featureName = "Length Proportion Change";
	
	public LengthChangeAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}
	

	NumericFeature analyseFragment( Fragment f )
	{
		MusicianInformation m = musician( f );
		double val = 0;
		if( m != null ) val = regression.getNumericValue2( m );
		return new ValuedAttribute( val );
	}

	String getRegressionName()
	{
		return "LengthRegression";
	}
	
	String getRegressionFeatureName()
	{
		return LengthRegressionAnalyser.featureName;
	}
	

}
