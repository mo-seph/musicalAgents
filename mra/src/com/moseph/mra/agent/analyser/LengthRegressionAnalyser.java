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
public class LengthRegressionAnalyser extends CurveAnalyser
{
	public static String featureName = "Length Proportion Regression";
	
	public LengthRegressionAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}
	
	double[] getNoteValue( Note played, Note scored )
	{
		if( played.getLongerThan() ) return null;
		return new double[] { played.getQuantisedOnset(),  played.getDuration() / scored.getDuration() };
	}

	double[] getNoteValue( Note played )
	{
		if( played.getLongerThan() ) return null;
		//System.out.println( "At " + played.getQuantisedOnset() + " duration " + played.getDuration() + " gives " + ( played.getDuration() * quantisation ) );
		return new double[] { played.getQuantisedOnset(), played.getDuration() * quantisation };
	}
	
	boolean analyseAnnotations() { return true; }

	public boolean skipOverhangingNotes() { return true; }
	

}
