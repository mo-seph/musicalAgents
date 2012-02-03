package com.moseph.mra.agent.analyser;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

import static java.lang.Math.*;

public class DisplacementVarianceAnalyser extends NumericAnalyser
{
	public static String featureName = "Displacement Range";

	public DisplacementVarianceAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}
	
	NumericFeature analyseFragment( Fragment f )
	{
		return new ValuedAttribute( 0.0 );
	}
	
	NumericFeature analyseAnnotatedFragment( Fragment f )
	{
		double top = 0.0;
		double bottom = 0.0;
		
		for( Note n : f.getNotes() )
		{
			top = max( top, n.getOnset() - n.getQuantisedOnset() );
			bottom = min( bottom, n.getOnset() - n.getQuantisedOnset() );
		}
		return new ValuedAttribute( top - bottom );
	}

}
