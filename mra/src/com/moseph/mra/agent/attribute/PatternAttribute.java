package com.moseph.mra.agent.attribute;

import java.text.DecimalFormat;
import java.util.*;
import static java.lang.Math.*;
import static com.moseph.mra.Note.*;
import static com.moseph.mra.MRAUtilities.*;

public class PatternAttribute implements Feature, Cloneable
{
	NumericFeature[] values;
	double beats;
	double quantise;
	int size;
	double min = 0;
	double max = 0;
	
	public PatternAttribute( double beats, double quantise )
	{
		init( beats, quantise );
	}
	
	public PatternAttribute( double quantise, double...inputValues )
	{
		double beats = (double)inputValues.length / quantise;
		init( beats, quantise );
		for( int i = 0; i < size; i++ )
		{
			double d = inputValues[i];
			if( ! Double.isNaN( d ) ) values[i] = new ValuedAttribute( inputValues[i] );
			//else System.out.println( "Got NaN!");
			//System.out.println( d + "))))))))))))))" + this );
		}
	}
	
	void init( double beats, double quantise )
	{
		this.beats = beats;
		this.quantise = quantise;
		size =(int)ceil( beats * quantise );
		
		values = new NumericFeature[size];
	}
	
	public void setValue( double beat, double value )
	{
		setValue( beat, new ValuedAttribute( value ));
	}
	public void setValue( double beat, NumericFeature f )
	{
		int index = indexOf( beat );
		if( index < 0 || index >= values.length ) return;
		values[indexOf(beat)] = f;
		if( f == null ) return;
		min = min( min, f.getValue() );
		max = max( max, f.getValue() );
	}
	
	
	
	public double getValue( double beat )
	{
		if( values[indexOf(beat)] != null ) return values[ indexOf(beat)].getValue();
		return 0.0;
	}
	
	public double getBucketValue( int bucket )
	{
		if( bucket >= 0 && bucket < values.length && values[bucket] != null ) return values[bucket].getValue();
		return Double.NaN;
	}
	
	public boolean hasValue( double beat )
	{
		if( values.length == 0 ) return false;
		return values[indexOf( beat )] != null;
	}
	public NumericFeature getFeature( double beat )
	{
		int index = indexOf(beat);
		if( index > values.length - 1 ) return null;
		return values[indexOf(beat)];
	}
	
	int indexOf( double beat )
	{
		if( size == 0 ) return 0;
		int r = (int)rint(beat * quantise) % size;
		if( r < 0 ) r += size;
		return r;
	}
	
	public double getBeats()
	{
		return beats;
	}

	public double getQuantise()
	{
		return quantise;
	}

	double beatForIndex( int index )
	{
		return quantise * index;
	}
	

	public int compareTo( Feature other )
	{
		return toString().compareTo( other.toString() );
	}

	public void setParameters( List<String> params )
	{
		//Do nothing...
	}
	
	public PatternAttribute clone()
	{
		try
		{
			PatternAttribute p = (PatternAttribute)super.clone();
			for( int i = 0; i < values.length; i++ ) 
				if( values[i] != null)
					p.values[i] = (NumericFeature)values[i].clone();
			return p;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return null;
	}
	

	public String toString()
	{
		DecimalFormat df = new DecimalFormat( " 0.00;-");
		String ret = "[ ";
		for( NumericFeature f : values )
		{
			if( f != null ) ret += df.format( f.getValue() ) + " ";
			else ret += "  xx  ";
			//if( f != null ) System.out.println( f.getValue() );
		}
		return ret + " ]";
	}
	
	public double distance( Feature f )
	{
		if( ! ( f instanceof PatternAttribute ) ) return Double.NaN;
		PatternAttribute p = (PatternAttribute)f;
		if( p.size > size ) return p.distance( this );
		double dist = 0.0;
		for( double i = 0.0; i < beats * quantise; i += 1.0/quantise )
			dist += ( getValue( i ) - p.getValue( i ) ) * ( getValue( i ) - p.getValue( i ) );
		return dist;
	}
	
	public boolean sloppyCompare( PatternAttribute p, double threshold )
	{
		if( size != p.size ) return false;
		for( int i = 0; i < size; i++ )
		{
			//If they're both null, that's fine
			if( values[i] == null && p.values[i] == null ) continue;
			//If one is null, then they can't be equal
			if( values[i] == null || p.values[i] == null ) return false;
			if( abs( values[i].getValue() - p.values[i].getValue() ) > threshold )  return false;
		}
		return true;
	}
	
	public void setValueToAverageOf( Collection<PatternAttribute> others )
	{
		if( others.size() < 1 ) return;
		PatternAttribute first = others.iterator().next();
		init( first.beats, first.quantise );
		AveragingPatternAttribute avg = new AveragingPatternAttribute( first.beats, first.quantise );
		//System.out.println( "Averaging Starts");
		for( PatternAttribute p : others )
		{
			//System.out.println( "Averaging with: " + p );
			for( double beat = 0; beat < beats; beat += 1/quantise )
				avg.addValue( beat, p.getFeature( beat ) );
		}
		//System.out.println( "Averaging Done:");
		//System.out.println( avg );
		
		for( double beat = 0; beat <= beats; beat += 1/quantise )
			setValue( beat, avg.getFeature( beat ) );
	}
	
	public int getNumBuckets()
	{
		return size;
	}
	
	public double[] getRange()
	{
		return new double[] { min, max };
	}
	
	public void scale( double factor )
	{
		for( NumericFeature n : values )
		{
			if( n != null )
				n.setValue( n.getValue() * factor );
		}
	}
	
	public void overwriteWith( PatternAttribute p, double smoothing )
	{
		for( int i = 0; i < values.length; i++ )
		{
			int pInd  = i % p.values.length;
			if( p.values[ pInd ] != null )
				if( values[i] != null )
					values[i].setValue( smooth( values[i].getValue(), p.values[pInd].getValue(), smoothing ) );
				else
					values[i] = p.values[pInd];
		}
	}
	
	public PatternAttribute getToLength( PatternAttribute other )
	{
		if( other.values.length <= values.length ) return this;
		PatternAttribute p = new PatternAttribute( other.beats, other.quantise );
		for( int i = 0; i < p.values.length; i++ )
		{
			int ind = i % values.length;
			if( values[ind] != null )
				p.values[i] = new ValuedAttribute( values[ind].getValue() );
		}
		return p;
	}
}