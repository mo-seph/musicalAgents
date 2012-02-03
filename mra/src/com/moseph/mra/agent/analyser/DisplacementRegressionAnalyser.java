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
public class DisplacementRegressionAnalyser extends CurveAnalyser
{
	public static String featureName = "Displacement Regression";
	public DisplacementRegressionAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}
	
	boolean analyseAnnotations() { return true; }

	double[] getNoteValue( Note played, Note scored )
	{
		//System.out.printf( "Onset: %f, quantised: %f, diff: %f\n", played.getOnset(), played.getQuantisedOnset(), played.getOnset() - played.getQuantisedOnset() );
		return new double[] { played.getQuantisedOnset(), played.getOnset() - scored.getOnset() };
	}

	double[] getNoteValue( Note played )
	{
		played.calculateQuantisation( quantisation );
		//System.out.printf( "Onset: %f, quantised: %f, diff: %f\n", played.getOnset(), played.getQuantisedOnset(), played.getOnset() - played.getQuantisedOnset() );
		return new double[] { played.getQuantisedOnset(), played.getOnset() - played.getQuantisedOnset() };
	}

}
