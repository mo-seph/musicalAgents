package com.moseph.mra.agent.analyser;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

import static java.lang.Math.*;

public class DynamicVarianceAnalyser extends NumericAnalyser
{
	public static String featureName = "Dynamic Range";

	public DynamicVarianceAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}
	
	NumericFeature analyseFragment( Fragment f )
	{
		double top = 0.0;
		double bottom = 0.0;
		
		for( Note n : f.getNotes() )
		{
			top = max( top, n.getVelocity() );
			bottom = min( bottom, n.getVelocity() );
		}
		return new ValuedAttribute( top - bottom );
	}

}
