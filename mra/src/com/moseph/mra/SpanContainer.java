package com.moseph.mra;

import static java.util.logging.Level.*;
import static com.moseph.mra.MRAConstants.*;
import static com.moseph.mra.MRAUtilities.*;
import static java.lang.Math.*;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;

public class SpanContainer<T extends Span> extends Channel<T> implements Serializable
{
	private static final long serialVersionUID = 420L;
	private static final double EVENT_START_FUDGE_FACTOR = 10.0;

	public SpanContainer()
	{
		super();
	}
	
	public SpanContainer( String name )
	{
		super( name );
	}
	public SpanContainer( double length )
	{
		this();
		this.length = length;
	}
	
	public SpanContainer( T...spans )
	{
		super();
		for( T span : spans ) { addEvent( span ); }
	}
	/**
	@param f the fragment to combine with this one
	*/
	public void addSpanContainer( SpanContainer<T> f, double offset )
	{
		addSpans( f.getSpans(), offset );
	}

	/**
	Appends fragment f to the current fragment.
	
	@param f the fragment to combine with this one
	*/
	public void append( SpanContainer<T> f )
	{
		double newLength = length + f.length;
		addSpanContainer( f, length );
		length = newLength;
	}

	/*******************************************************************************
	*                                                                              *
	* Copying methods                                                              *
	*                                                                              *
	*******************************************************************************/
	public SpanContainer<T> copyChunk( double start, double end ) { return copyChunk( start, end, false ); }
	public SpanContainer<T> copyChunk( double start, double end, boolean allowStartsBefore )
	{
		//A bit inefficient for now
		SpanContainer<T> f = getEmptyContainer( end - start );
		//System.out.println( "copying " + start + " to " + end + " of \n" + this + "\n.\n");
		for( T n : events.getPotentiallyActive( start, end ) )
		//for( T n : events.getPotentiallyActive( 0.0, end+100 ) )
		{
			if( n.getEndTime() < start + SPLIT_THRESHOLD && n.isEnded() ) continue;
			//Notes are sorted, so the first one which is outside the range
			//is the end of the usable note list
			if( n.getOnset() > end - SPLIT_THRESHOLD ) break;
			
			T newSpan = (T)n.clone();
			//System.out.println( "Copying " + newSpan + " for " + start + " to " + end );
			if( newSpan.getOnset() < start - SPLIT_THRESHOLD && ! allowStartsBefore )
			{
				newSpan.setOnset( start );
				newSpan.setStartsBefore( true );
				//System.out.println( newSpan );
			}
			if( newSpan.getEndTime() > end + SPLIT_THRESHOLD || ! n.isEnded() )
			{
				newSpan.setEndTime( end );
				newSpan.setLongerThan( true );
				//System.out.println( newSpan );
			}
			newSpan.addOffset( 0.0 - start );
			//System.out.println( newSpan );
			//f.addEvent(newSpan, false );
			f.addEvent(newSpan, true );
		}
		return f;
	}
	
	SpanContainer<T> getEmptyContainer( double length )
	{
		return new SpanContainer<T>( length );
	}

	public SpanContainer clone()
	{
		return (SpanContainer)super.clone();
	}

	/*******************************************************************************
	*                                                                              *
	* Channel compatibility methods                                                              *
	*                                                                              *
	*******************************************************************************/

	public void merge( SpanContainer<T> c )
	{
		addSpanContainer( c, 0.0 );
	}

	/*******************************************************************************
	*                                                                              *
	* Utility methods                                                              *
	*                                                                              *
	*******************************************************************************/
	
	/**
	The main addNote method
	@param n a Note
	*/
	
	public void addEvent( T n ) { addEvent( n, true ); }
	public void addEvent( T n, boolean checkMerge )
	{
		n.use();
		if( replaceOnly() ) { replaceSpan( n ); return; }
		if( n.getStartsBefore() && checkMerge )
		{
			//log.log( FINE, "Looking for friend for:" + n );
			//System.out.println( "Looking for friend for:" + n );
			//Find a note which this is a continuation of n
			boolean merged = false;
			for( T existing : events.getPotentiallyActive( n.getOnset() - EVENT_START_FUDGE_FACTOR, n.getEndTime() ) )
			{
				//System.out.println( "Trying:" + existing );
				if( existing.mergeIfPossible( n ) )	
				{ 
					//System.out.println( "Success!:\n" + this );
					merged = true; 
					//break;
				}
				//else
				//{
					//System.out.println( "Failed: " + n );
				//}
			}
			//Fall through		
			if( ! merged )
			{
				//log.log( WARNING, "No friend found for " + n + " in " + this );
				//System.out.println( "No friend found for " + n + " in " + this );
				events.add( n );
			}
		}
		else
		{
			//log.warning( n.getClass() + "" );
			events.add( n );
		}
		//String s = "Span length: " + length + "\nadding " + n + " gives ";
		length = max( n.getEndTime(), length );
		//System.out.println( s + length );
	}
	
	public void replaceSpan( T n )
	{
		n.use();
			for( T current : events.getPotentiallyActive( n.getOnset(), n.getEndTime() ) )
			{
				if( current.getEndTime() < n.getOnset() ) continue;
				if( current.getOnset() > n.getEndTime() ) break;

				int cmpStart = fuzzyCmp( n.getOnset(), current.getOnset() );
				int cmpEnd = fuzzyCmp(  n.getEndTime(), current.getEndTime() );
				/*      cmpStart
				||    || -1 || 0 || 1 ||
				|| -1 ||  D || D || B ||
				||  0 ||  C || C || A ||
				||  1 ||  C || C || A ||

				A: Shorten current, so it ends when new starts
				B: Chop current in two, giving the portion before new, and the portion after
				C: Remove current
				D: Move start of current so it starts when new finishes

				 */
				if( cmpStart <= 0 && cmpEnd >= 0 ) //C 
					{ System.err.println( "Action C" ); events.remove( current ); }
				else if( cmpStart <= 0 && cmpEnd < 0 ) //D
					{ System.err.println( "Action D" ); current.setOnset( n.getEndTime() ); }
				else if( cmpStart > 0 && cmpEnd >= 0 ) //A
					{ System.err.println( "Action A" ); current.setEndTime( n.getOnset() ); }
				else
				{ System.err.println( "Action B" ); 
					events.add( (T)current.copySetStartTime( n.getEndTime()));
					current.setEndTime( n.getOnset() );					
				}
				events.add( n );
			}
	}
	
	public boolean replaceOnly()
	{
		return false;
	}
	
	public void addSpans( List<T> spans, double offset ) { addSpans( spans, offset, true ); }
	public void addSpans( List<T> spans, double offset, boolean checkMerge )
	{
		for( T newSpan : spans ) addEvent( (T)newSpan.copyAddOffset( offset ), checkMerge );
	}

	public List<T> getSpans()
	{
		return new Vector<T>( events );
	}

	

	/*******************************************************************************
	*                                                                              *
	* XMLisation and printing                                                      *
	*                                                                              *
	*******************************************************************************/
	public String toString()
	{
		String ret = "SpanContainer: " + name;
		ret += "\n";
		Iterator i = events.iterator();
		while( i.hasNext() )
		{
			ret += "\t" + i.next() + "\n";
		}
		return ret;
	}
	
}
