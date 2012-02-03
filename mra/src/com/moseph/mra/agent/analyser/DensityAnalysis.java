package com.moseph.mra.agent.analyser;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

/**
 * Analyses the proportion of the bar which is played; will give values over 1.0
 * for polyphonic music...
 * @author s0239182
 *
 */
public class DensityAnalysis extends NumericAnalyser
{
	public static String featureName = "Density";

	public DensityAnalysis( Context context,  double smoothing )
	{
		super( context, smoothing );
	}
	@Override
	NumericFeature analyseFragment( Fragment f )
	{
		double totalTime = f.getLength();
		double playedTime = 0.0;
		
		for( Note n : f.getNotes() )
			playedTime += n.getDuration();
		if( totalTime <= 0 ) return new ValuedAttribute( 0 );
		return new ValuedAttribute( playedTime / totalTime );
	}
	NumericFeature analyseAnnotatedFragment( AnnotatedFragment f )
	{
		return analyseFragment( f.played );
	}

}
