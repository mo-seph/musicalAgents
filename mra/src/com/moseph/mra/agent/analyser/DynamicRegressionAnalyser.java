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
public class DynamicRegressionAnalyser extends CurveAnalyser
{
	public static String featureName = "Dynamic Regression";

	public DynamicRegressionAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}
	
	double[] getNoteValue( Note played, Note scored )
	{
		return new double[]{ scored.getOnset(), played.getVelocity() };
	}

	double[] getNoteValue( Note played )
	{
		played.calculateQuantisation( quantisation );
		return new double[]{ played.getQuantisedOnset(), played.getVelocity() };
	}
	

}
