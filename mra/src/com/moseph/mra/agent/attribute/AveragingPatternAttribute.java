package com.moseph.mra.agent.attribute;

import java.text.DecimalFormat;
import java.util.List;
import static java.lang.Math.*;
import static com.moseph.mra.Note.*;

public class AveragingPatternAttribute extends PatternAttribute
{
	int[] histogram;
	
	public AveragingPatternAttribute( double beats, double quantise )
	{
		super( beats, quantise );
		histogram = new int[values.length];
		for( int i = 0; i < values.length; i++ ) histogram[i] = 0;
	}
	
	public void addValue( double beat, NumericFeature f )
	{
		addValue( beat, f, 1 );
	}
	
	public void addValue( double beat, NumericFeature f, int number )
	{
		if( f == null ) return;
		int index = indexOf( beat );
		double newValue = f.getValue();
		
		if( histogram[index] > 0 )
			newValue = ( values[index].getValue() * histogram[index]  + f.getValue() * number ) / ( histogram[index] + number );
		f.setValue( newValue );
		if( Double.isNaN( newValue ))
		{	
			System.out.println( "Bad averaging... " + newValue + ", beat: " + beat + ", f: " + f + ", index: " + index + ", hist: " + histogram[index]);
			return;
		}
		setValue( beat, f );
		histogram[index]++;
	}
	

	public AveragingPatternAttribute clone()
	{
		try
		{
			AveragingPatternAttribute p = (AveragingPatternAttribute)super.clone();
			for( int i = 0; i < histogram.length; i++ ) p.histogram[i] = histogram[i];
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
		String ret = super.toString();
		DecimalFormat df = new DecimalFormat( "  00 ");
		ret += "\n[ ";
		for( int f : histogram )
			ret += df.format( f ) + " ";
		return ret + " ]";
	}
	

}
