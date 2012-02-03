package com.moseph.mra.agent.analyser;

import java.util.Vector;

import com.moseph.mra.agent.MusicianInformation;
import com.moseph.mra.agent.attribute.*;

import static java.lang.Math.*;

public class GroupPatternFeature<T extends PatternAttribute> extends GroupFeature<T>
{
	boolean clean = false;
	T average;
	
	public GroupPatternFeature( T instance, MusicianInformation me )
	{
		super( instance, me );
	}
	
	public T getAverage()
	{
		if( clean && average != null ) return average;
		average = (T)instance.clone();
		average.setValueToAverageOf( new Vector<PatternAttribute>( getValuesToAverage()) );
		clean = true;
		return average;
	}
	
	public void updateTotals()
	{
		clean = false;
	}
	
	
}
