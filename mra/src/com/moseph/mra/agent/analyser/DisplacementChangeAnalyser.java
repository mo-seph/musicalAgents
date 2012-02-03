package com.moseph.mra.agent.analyser;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

/**
 * Calculates the slope of the line of best fit to the the displacement of 
 * each note in the given fragment
 * @author s0239182
 *
 */
public class DisplacementChangeAnalyser extends NumericAnalyser
{
	GroupDualNumericFeature regression;
	public static String featureName = "Displacement Change";
	public DisplacementChangeAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}
	
	public void init()
	{
		super.init();
		Object o = features.getOrCreate( DisplacementRegressionAnalyser.featureName );	
		regression = (GroupDualNumericFeature)features.getOrCreate( DisplacementRegressionAnalyser.featureName );
	}
	
	NumericFeature analyseAnnotatedFragment( AnnotatedFragment f )
	{
		return new ValuedAttribute(  regression.getNumericValue2( musician( f.played ) ) );
	}

	NumericFeature analyseFragment( Fragment f )
	{
		return new ValuedAttribute(  regression.getNumericValue2( musician( f ) ) );
	}

	
	boolean analyseAnnotations() { return true; }

	@Override
	public List<String> dependsOn()
	{
		List<String> deps = super.dependsOn();
		deps.add( "DisplacementRegression");
		return deps;
	}

}
