package com.moseph.mra.agent.analyser;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

/**
 * Analyses the average displacement of notes in the given Fragments
 * @author s0239182
 *
 */
public class DisplacementAverageAnalyser extends AverageAnalyser
{
	double quantisation;
	
	public static String featureName = "Average Displacement";
	public DisplacementAverageAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
		quantisation = context.getQuantisation();
	}
	double getNoteValue( Note played, Note scored )
	{
		double disp = getNoteDisplacement( played, scored );
		//System.out.println( "displacement for " + played + ": " + disp );
		return disp;
	}
	double getNoteValue( Note played )
	{
		double disp = getNoteDisplacement( played, quantisation );
		//System.out.println( "displacement for " + played + ": " + disp );
		return disp;
	}
	
}
