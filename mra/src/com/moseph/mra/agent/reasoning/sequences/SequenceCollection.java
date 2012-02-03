package com.moseph.mra.agent.reasoning.sequences;

import java.io.*;
import java.util.*;

import com.moseph.mra.acts.ActRelation;

public class SequenceCollection<S> implements Serializable
{
	List<SequenceBucket<S>> buckets;
	int order;
	
	public SequenceCollection( int order )
	{
		buckets = new Vector<SequenceBucket<S>>();
		for( int i = 0; i <= order; i++ )
			buckets.add( new SequenceBucket<S>( i ));
		this.order = order;
	}
	
	public BucketNode<S>[] getCompletions( List<S> seq )
	{
		BucketNode<S>[] ret = new BucketNode[order + 1];
		for( int i = 0; i <= order; i++) ret[i] = buckets.get( i ).getCompletions( seq );
		return ret;
	}
	
	/**
	 * Returns randomly weighted completion from the longest sequence level which can supply one
	 * @param seq
	 * @return
	 */
	public S getLikelyCompletion( List<S> seq )
	{
		//System.err.println( toString() );
		for( int i = buckets.size()-1; i > 0; i-- )
		{
			//System.out.println( "Trying bucket " + i + " of " + buckets.size() );
			S possibility = buckets.get( i ).getWeightedCompletion( seq );
			if( possibility != null ) 
			{
				//System.out.println( "Found possibility! " + possibility );
				return possibility;
			}
		}
		return null;
	}
	
	public void addSequence( List<S> seq )
	{
		for( SequenceBucket<S> bucket : buckets )
			bucket.addSequence( seq );
	}
	
	public void addSequence( S...seq )
	{
		for( SequenceBucket<S> bucket : buckets )
			bucket.addSequence( seq );
	}
	
	public SequenceBucket<S> getBucket( int ord )
	{
		if( ord > order ) return null;
		return buckets.get( ord );
	}
	
	public void calculateProbabilities()
	{
		for( SequenceBucket<S> bucket : buckets ) bucket.calculateProbabilities();
	}
	
	public int getOrder()
	{
		return order;
	}
	
	public String toString()
	{
		String ret = "Sequence Collection, order " + order;
		for( int i = 1; i<= order; i++ )
		{
			ret += "\nOrder " + i + ":\n" + getBucket( i ) ;
		}
		return ret;
	}
	
	public boolean toFile( String filename ) 
	{
		try
		{
			File tmpFile = new File( filename );
			FileOutputStream fout = new FileOutputStream( tmpFile );
			ObjectOutputStream out = new ObjectOutputStream( fout );
			out.writeObject( this );
			out.close();
			fout.close();
		}
		catch( IOException e )
		{
			System.err.println( "Could not serialise sequence!");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static SequenceCollection<ActRelation> fromFile( String filename )
	{
		try
		{
			FileInputStream fin = new FileInputStream( new File( filename ) );
			ObjectInputStream in = new ObjectInputStream( fin );
			Object o = in.readObject();
			return (SequenceCollection<ActRelation>)o;
		}
		catch( Exception e )
		{
			System.err.println( "Could not create sequences from file:");
			e.printStackTrace();
			return null;
		}
	}

}
