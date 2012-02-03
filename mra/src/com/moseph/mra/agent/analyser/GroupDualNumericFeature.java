package com.moseph.mra.agent.analyser;

import com.moseph.mra.agent.MusicianInformation;
import com.moseph.mra.agent.attribute.*;

import static java.lang.Math.*;

public class GroupDualNumericFeature<T extends DualNumericFeature> extends GroupFeature<T>
{
	double average1 = Double.NaN;
	double average2 = Double.NaN;
	double total1;
	double total2;
	double nonWeightedTotal1 = 0.0;
	double nonWeightedTotal2 = 0.0;
	double nonWeightedAverage1 = 0.0;
	double nonWeightedAverage2 = 0.0;
	boolean silent = true;
	
	public GroupDualNumericFeature( T instance, MusicianInformation me )
	{
		super( instance, me );
	}
	
	public double getAverageValue1() { return average1; }
	public double getAverageValue2() { return average2; }
	
	public T getAverage()
	{
		T ret = (T)instance.clone();
		ret.setValue1( average1 );
		ret.setValue2( average2 );
		return ret;
	}
	
	public void updateTotals()
	{
		total1 = 0.0;
		total2 = 0.0;
		nonWeightedTotal1 = 0.0;
		nonWeightedTotal2 = 0.0;
		int num = 0;
		if( !silent ) System.out.println( "start analysis");
		//else System.out.println( "Silent analysis");
		for( MusicianInformation m : features.keySet() )
		{
			if( me == null || ! m.equals(me) || includeSelf )
			{
				if( features.get( m ) == null ) continue;
				String myName = "unknown";
				String theirName = m.getName();
				double myValue1 = 0.0;
				double myValue2 = 0.0;
				double dist = 0.0;
				if( features.containsKey( me ) )
				{
					myValue1 = features.get( me ).getValue1();
					myValue2 = features.get( me ).getValue2();
				}
				if( me != null )
				{
					myName = me.getName();
					dist = sqrt( me.getSquaredDistance( m ) );
				}
				double distWeight = getDistanceWeight( m );
				double rawValue1 = features.get( m ).getValue1();
				double rawValue2 = features.get( m ).getValue2();
				double usedValue1 = rawValue1;
				double usedValue2 = rawValue2;
				if( rawValue1 != Double.MAX_VALUE && rawValue1 != Double.MIN_VALUE )
				{
					if( isRelative() ) usedValue1 -= myValue1;
					double weightedValue = distWeight * usedValue1;
					total1 += weightedValue;
					nonWeightedTotal1 += usedValue1;
				}
				if( rawValue2 != Double.MAX_VALUE && rawValue2 != Double.MIN_VALUE )
				{
					if( isRelative() ) usedValue2 -= myValue2;
					double weightedValue = distWeight * usedValue2;
					total2 += weightedValue;
					nonWeightedTotal2 += usedValue2;
				}
				
				//if( !silent ) System.out.printf( "(" + myName + ")-> " + theirName + ";raw: %f, used: %f, weight: %f (%f), weighted: %f\n", rawValue, usedValue, distWeight, dist, weightedValue );
				num++;
			}	
		}
		if( !silent ) System.out.println( "end analysis");
		if( num == 0 ) 
		{
			//System.out.println( "Noone here - avg = 0.0; " + features.size() );
			average1 = 0.0;
			average2 = 0.0;
			nonWeightedAverage1 = 0.0;
			nonWeightedAverage2 = 0.0;
		}
		else 
		{
			//System.out.printf( "Total: %f, Num: %f\n", total, (double)num );
			average1 = total1 / (double)num;
			average2 = total2 / (double)num;
			nonWeightedAverage1 = nonWeightedTotal1 / (double)num;
			nonWeightedAverage2 = nonWeightedTotal2 / (double)num;
		}
	}
	
	public double getNonWeightedAverage1() { return nonWeightedAverage1; }
	public double getNonWeightedAverage2() { return nonWeightedAverage2; }
	
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
	
	public double getNumericValue2( MusicianInformation m )
	{
		T val = getValue( m );
		if( val != null ) return val.getValue2();
		return 0.0;
	}
	public double getNumericValue1( MusicianInformation m )
	{
		T val = getValue( m );
		if( val != null ) return val.getValue1();
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

}
