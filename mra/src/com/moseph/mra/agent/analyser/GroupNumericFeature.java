package com.moseph.mra.agent.analyser;

import com.moseph.mra.agent.MusicianInformation;
import com.moseph.mra.agent.attribute.*;

import static java.lang.Math.*;

public class GroupNumericFeature<T extends NumericFeature> extends GroupFeature<T>
{
	double average = Double.NaN;
	double total;
	boolean silent = true;
	int counted = 0;
	
	public GroupNumericFeature( T instance, MusicianInformation me )
	{
		super( instance, me );
	}
	
	public double getAverageValue()
	{
		return average;
	}
	
	public T getAverage()
	{
		T ret = (T)instance.clone();
		ret.setValue( average );
		return ret;
	}
	
	public void updateTotals()
	{
		total = 0.0;
		counted = 0;
		if( !silent ) System.out.println( "start analysis");
		//else System.out.println( "Silent analysis");
		for( MusicianInformation m : features.keySet() )
		{
			if( me == null || ! m.equals(me) || includeSelf )
			{
				double rawValue = features.get( m ).getValue();
				if( Double.isNaN( rawValue ))
				{
					System.err.println( "NO value for: " + m.getName() );
					continue;
				}
				//System.err.println( "value for: " + m.getName() + ":" + rawValue );
				//System.out.println( "Value : " + rawValue + " for " + m.getName() + "(" + getClass() + ")");
				String myName = "unknown";
				String theirName = m.getName();
				double myValue = 0.0;
				double dist = 0.0;
				if( features.containsKey( me ) ) myValue = features.get( me ).getValue();
				if( me != null )
				{
					myName = me.getName();
					dist = sqrt( me.getSquaredDistance( m ) );
				}
				double distWeight = getDistanceWeight( m );
				double usedValue = rawValue;
				if( rawValue == Double.MAX_VALUE || rawValue == Double.MIN_VALUE ) continue;
				if( isRelative() ) usedValue -= myValue;
				double weightedValue = distWeight * usedValue;
				total += weightedValue;
				if( !silent ) System.out.printf( "(" + myName + ")-> " + theirName + ";raw: %f, used: %f, weight: %f (%f), weighted: %f\n", rawValue, usedValue, distWeight, dist, weightedValue );
				counted++;
			}	
		}
		if( !silent ) System.out.println( "end analysis");
		if( counted == 0 ) 
		{
			//System.out.println( "Noone here - avg = 0.0; " + features.size() );
			//average = 0.0;
			average = Double.NaN;
		}
		else 
		{
			//System.out.printf( "Total: %f, Num: %f\n", total, (double)num );
			average = total / (double)counted;
		}
	}
	
	public double getNonWeightedAverage()
	{
		if( ! distanceWeight ) return average;
		distanceWeight = false;
		updateTotals();
		double avg = average;
		distanceWeight = true;
		updateTotals();
		return avg;
	}
	public double getDistanceWeight( MusicianInformation m )
	{
		if( m == null ) return 0.0;
		if( me == null ) return 1.0;
		if( m.equals( me )) return 1.0;
		//double distance = sqrt( me.getSquaredDistance( m ) );
		if( distanceWeight ) return getWeightForSquaredDistance( me.getSquaredDistance( m ) );
		return 1.0;
	}
	
	public static double getWeightForSquaredDistance( double d )
	{
		return 10.0 / ( d + 0.1 );
	}

	public void setValue( MusicianInformation m, T f  )
	{
		//System.out.println( "Set value for " + m + " to " + f );
		super.setValue( m, f );
	}
	
	public void setValue( MusicianInformation m, double d )
	{
		T newVal = (T)instance.clone();
		newVal.setValue( d );
		setValue( m, newVal );
	}
	
	public double getNumericValue( MusicianInformation m )
	{
		T val = getValue( m );
		if( val != null ) return val.getValue();
		return 0.0;
	}

	public boolean isSilent()
	{
		return silent;
	}

	public void setSilent( boolean silent )
	{
		this.silent = silent;
	}
	
	public int getCounted()
	{
		return counted;
	}

}
