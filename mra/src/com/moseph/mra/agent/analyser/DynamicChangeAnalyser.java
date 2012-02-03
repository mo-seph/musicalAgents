package com.moseph.mra.agent.analyser;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

/**
 * Calculates a linear regression of the dynamic values of the notes in the given fragment
 * @author s0239182
 *
 */
public class DynamicChangeAnalyser extends NumericAnalyser
{
	public static String featureName = "Dynamic Change";
	GroupDualNumericFeature regression;

	public DynamicChangeAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}
	
	public void init()
	{
		super.init();
		regression = (GroupDualNumericFeature)features.getOrCreate( DynamicRegressionAnalyser.featureName );
	}
	
	NumericFeature analyseFragment( Fragment f )
	{
		return new ValuedAttribute( regression.getNumericValue2( musician( f ) ) );
	}

	@Override
	public List<String> dependsOn()
	{
		List<String> deps = super.dependsOn();
		deps.add( "DynamicRegression");
		return deps;
	}

}
