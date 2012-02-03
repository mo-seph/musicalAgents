package com.moseph.mra.agent.analyser;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

/**
 * Extracts the average dynamic of the played fragments
 * @author s0239182
 *
 */
public class DynamicAverageAnalyser extends AverageAnalyser
{

	public static String featureName = "Dynamics";
	
	public DynamicAverageAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}
	
	
	@Override
	double getNoteValue( Note played, Note scored )
	{
		return getNoteValue( played );
	}


	@Override
	double getNoteValue( Note played )
	{
		//return played.getDuration() * played.getVelocity();
		return played.getVelocity();
	}
}
