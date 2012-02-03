package com.moseph.mra.agent.analyser;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.Context;
import com.moseph.mra.agent.attribute.*;

public abstract class AverageAnalyser extends NumericAnalyser<NumericFeature>
{
	public AverageAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
	}

	NumericFeature analyseFragment( Fragment f )
	{
		double amount = 0;
		int numCounted = 0;
		for( Note n : f.getNotes() )
		{
			if( shouldSkip( n )) continue;
			double p = getNoteValue( n );
			if( Double.isNaN( p )) continue;
			amount += p;
			numCounted++;
		}
		//System.out.println( getClass().getSimpleName() + " Analysis: " + amount + "/" + numCounted );
		if( numCounted > 0 ) return new ValuedAttribute( amount / numCounted );
		return getNullFeature();
	}
	
	NumericFeature analyseAnnotatedFragment( AnnotatedFragment f )
	{
		double amount = 0;
		int numCounted = 0;
		Map<Note,Note> scored = f.getMapToScoredNotes();
		for( Note playedNote : f.played.getNotes() )
		{
			if( shouldSkip( playedNote )) continue;
			double p = getNoteValue( playedNote, scored.get( playedNote ) );
			if( Double.isNaN( p )) continue;
			amount += p;
			numCounted++;
		}
		if( numCounted > 0 ) return new ValuedAttribute( amount / numCounted );
		return getNullFeature();
	}
	
	abstract double getNoteValue( Note played );
	abstract double getNoteValue( Note played, Note scored );


}
