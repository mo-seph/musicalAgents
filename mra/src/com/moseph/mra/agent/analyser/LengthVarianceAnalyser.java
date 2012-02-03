package com.moseph.mra.agent.analyser;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

import static java.lang.Math.*;

/**
 * Calculates the variability of note lengths relative to scored lengths
 * @author s0239182
 *
 */
public class LengthVarianceAnalyser extends NumericAnalyser
{
	public static String featureName = "Length Proportion Range";

	public LengthVarianceAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}
	
	NumericFeature analyseFragment( Fragment f )
	{
		return new ValuedAttribute( 0.0 );
	}
	
	NumericFeature analyseAnnotatedFragment( AnnotatedFragment  anno )
	{
		Fragment f = anno.getPlayed();
		Map<Note,Note> scored = anno.getMapToScoredNotes();
		double top = 0.0;
		double bottom = 0.0;
		
		for( Note n : f.getNotes() )
		{
			double scoredLength = scored.get( n ).getDuration();
			double nProp = n.getDuration() / scoredLength;
			top = max( top, nProp );
			bottom = min( bottom, nProp );
		}
		return new ValuedAttribute( top - bottom );
	}

}
