package com.moseph.mra.agent.analyser;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

/**
 * Analyses the average length of notes relative to the scored values
 * @author s0239182
 *
 */
public class LengthAverageAnalyser extends AverageAnalyser
{
	double quantisation;
	public static String featureName = "Average Length Proportion";
	public LengthAverageAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
		quantisation = context.getQuantisation();
	}
	
	double getNoteValue( Note played, Note scored )
	{
		return played.getDuration() / scored.getDuration();
	}


	double getNoteValue( Note played )
	{
		double val = played.getDuration() * quantisation;
		return val;
	}

	@Override
	public boolean skipOverhangingNotes()
	{
		return true;
	}
	
	
}
