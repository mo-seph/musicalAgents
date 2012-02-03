package com.moseph.mra.agent.analyser;


import com.moseph.mra.Fragment;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

import static java.lang.Math.*;

public abstract class DualNumericAnalyser<T extends DualNumericFeature> extends Analyser<T>
{
	double smoothing;
	GroupDualNumericFeature feature;
	
	public DualNumericAnalyser( Context context, double smoothing )
	{
		super( context );
		this.smoothing = smoothing;
		feature = (GroupDualNumericFeature)super.feature;
	}
	T analyseAnnotatedFragment( AnnotatedFragment f )
	{
		T scored = analyseFragment( f.getScored() );
		T played = analyseFragment( f.getPlayed() );
		played.setValue1( played.getValue1() - scored.getValue1() );
		played.setValue2( played.getValue2() - scored.getValue2() );
		return played;
	}
	//abstract T analyseFragment( Fragment f );
	
	public GroupDualNumericFeature getFeatureSet()
	{
		return (GroupDualNumericFeature)super.getFeatureSet();
	}
	public GroupDualNumericFeature createFeatureSet()
	{
		GroupDualNumericFeature gf = new GroupDualNumericFeature( new DualValuedAttribute(), me );
		gf.setIncludeSelf( analyseSelf );
		return gf;
	}
	
	T smooth( T old, T current )
	{
		if( old == null ) return current;
		current.setValue1( smooth( old.getValue1(), current.getValue1() ) );
		current.setValue2( smooth( old.getValue2(), current.getValue2() ) );
		return current;
	}
	
	double smooth( double oldV, double newV )
	{
		return ( smoothing * oldV ) + (1-smoothing) * newV ;
	}
	
	T getNullFeature()
	{
		return (T) new DualValuedAttribute( Double.NaN, Double.NaN );
	}
}
