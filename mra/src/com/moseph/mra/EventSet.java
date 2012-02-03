package com.moseph.mra;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

public class EventSet<T extends TemporalEvent> extends TreeSet<T>
{
	List<TreeSet<T>> buckets = new Vector<TreeSet<T>>();
	int bucketSize = 2;
	
	public boolean add( T e )
	{
		if( super.add( e )) { addToBuckets( e ); return true; }
		return false;
	}
	
	void addToBuckets( T e )
	{
		int[] startEnd = getStartEndBuckets( e );
		growBucketsTo( startEnd[1] + 1);
		for( int i = startEnd[0]; i <= startEnd[1]; i++ )
		{
			TreeSet<T> bucket = buckets.get(i);
			if( bucket == null ) 
			{ 
				bucket = new TreeSet<T>(); 
				buckets.set( i, bucket );
			}
			bucket.add( e );
		}
	}
	
	public void growBucketsFor( double length )
	{
		growBucketsTo( (int)length/bucketSize );
	}
	
	void growBucketsTo( int size )
	{
		while( buckets.size() < size ) buckets.add(null);
	}
	
	int[] getStartEndBuckets( TemporalEvent e )
	{
		double[] range = e.getRange();
		//System.out.println( "Event added " + e + " start: " + range[0] + " end: " + range[1] );
		return getStartEndBuckets( range[0], range[1]);
	}
	
	int[] getStartEndBuckets( double start, double end )
	{
		int s = (int)start/bucketSize ;//- 1;
		if( s < 0 ) s = 0;
		return new int[] { s, (int)end/bucketSize };		
	}
	
	public List<T> getPotentiallyActive( double start, double end )
	{
		SortedSet<T> ret = new TreeSet<T>();
		int[] startEnd = getStartEndBuckets( start - 0.05 , end + 0.05 );
		for( int i = startEnd[0]; ( i <= startEnd[1] && i < buckets.size() ); i++  )
		{
			if( buckets.get(i) != null ) ret.addAll( buckets.get(i));
		}
		return new Vector<T>(ret);
	}
	
	
}
