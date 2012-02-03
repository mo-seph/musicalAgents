package com.moseph.mra.agent.analyser;

import java.util.List;

import com.moseph.mra.Fragment;
import com.moseph.mra.agent.Context;
import com.moseph.mra.agent.attribute.NumericFeature;

public abstract class RegressionDependantAnalyser extends NumericAnalyser<NumericFeature>
{
	public static String featureName = "Length Proportion Change";
	double quantisation = 4.0;
	GroupDualNumericFeature regression;
	
	public RegressionDependantAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
		quantisation = context.getQuantisation();
	}
	public void init()
	{
		super.init();
		regression = (GroupDualNumericFeature)features.getOrCreate( getRegressionFeatureName() );
	}
	
	/**
	 * Regression dependant analysers don't care about annotations - they just work off the regression results
	 */
	NumericFeature analyseAnnotatedFragment( AnnotatedFragment f )
	{
		return analyseFragment( f.played );
	}
	
	boolean analyseAnnotations() { return true; }
	
	@Override
	public List<String> dependsOn()
	{
		List<String> dep = super.dependsOn();
		dep.add( getRegressionName() );
		return dep;
	}
	
	abstract String getRegressionName();
	abstract String getRegressionFeatureName();
	protected double regressionAverage( Fragment f )
	{
		double val1 = regression.getNumericValue1( musician( f ));
		double val2 = regression.getNumericValue2( musician( f ));
		return val1 + ( val2 / 2.0 ) * f.getLength();
	}

}
