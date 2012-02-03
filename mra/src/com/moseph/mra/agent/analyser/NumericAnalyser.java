package com.moseph.mra.agent.analyser;


import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;


import static java.lang.Math.*;
import static com.moseph.mra.MRAUtilities.*;

public abstract class NumericAnalyser<T extends NumericFeature> extends Analyser<T>
{
	double smoothing;
	GroupNumericFeature feature;
	
	public NumericAnalyser( Context context, double smoothing )
	{
		super( context );
		this.smoothing = smoothing;
		feature = (GroupNumericFeature)super.feature;
	}
	T analyseAnnotatedFragment( AnnotatedFragment f )
	{
		T scored = analyseFragment( f.getScored() );
		T played = analyseFragment( f.getPlayed() );
		played.setValue( played.getValue() - scored.getValue() );
		return played;
	}
	//abstract T analyseFragment( Fragment f );
	
	public GroupNumericFeature getFeatureSet()
	{
		return (GroupNumericFeature)super.getFeatureSet();
	}
	public GroupNumericFeature createFeatureSet()
	{
		GroupNumericFeature gf = new GroupNumericFeature( getNullFeature(), me );
		gf.setIncludeSelf( analyseSelf );
		System.out.println( "Created feature for " + getClass().getSimpleName() + ": " + gf );
		return gf;
	}
	
	T smooth( T old, T current )
	{
		if( old == null ) return current;
		double oldV = old.getValue();
		double newV = current.getValue();
		current.setValue( MRAUtilities.smooth( oldV, newV, smoothing ) );
		return current;
	}
	
	T getNullFeature()
	{
		return (T)new ValuedAttribute( Double.NaN );
	}
}