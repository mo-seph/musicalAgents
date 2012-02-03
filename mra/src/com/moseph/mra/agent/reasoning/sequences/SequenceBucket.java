package com.moseph.mra.agent.reasoning.sequences;

import java.io.Serializable;
import static com.moseph.mra.MRAUtilities.*;
import java.util.*;

import com.moseph.mra.agent.reasoning.sequences.BucketNode.CompletionValues;

public class SequenceBucket<S> implements Serializable
{
	int order;
	Map<String,BucketNode<S>> sequences;
	public SequenceBucket( int order )
	{
		this.order = order;
		sequences = new HashMap<String, BucketNode<S>>();
	}
	
	public S getWeightedCompletion( S[] seq ) { return getWeightedCompletion( Arrays.asList( seq ) ); }
	
	public S getWeightedCompletion( List<S> seq )
	{
		if( seq == null ) System.err.println( "Null list passed in!");
		BucketNode<S> comps = getCompletions( seq );
		if( comps == null ) return null;
		double rand = Math.random();
		double current = 0.0;
		for( S sym : comps.completions.keySet() )
		{
			System.out.println( "Current: "+ current + " Rand: " + rand + " this: " + 
				comps.completions.get( sym ).probability );
			current += comps.completions.get( sym ).probability;
			if( current >= rand ) return sym;
		}
		System.err.println( "Fallen through!");
		return null;
		
	}
	
	public BucketNode<S> getCompletions( S[] seq )
	{
		return getCompletions( Arrays.asList( seq ) );
	}
	public BucketNode<S> getCompletions( List<S> seq )
	{
		List<S> probe = probeSequence(  seq );
		//System.out.println( "Finding completions for " + seqString( probe ));
		
		if( probe != null )
		{
			//System.out.println( "Keys: " + collectionToString( sequences.keySet() ));
			return sequences.get( seqString( probe ) );
		}
		return null;
	}
	
	public double getSequenceCompletionProbability( S[] seq )
	{
		return getSequenceCompletionProbability( Arrays.asList( seq ) );
	}
	public double getSequenceCompletionProbability( List<S> seq )
	{
		BucketNode<S> node = getCompletions( sequenceBody( seq ) );
		//System.out.println( "Prob node: " + node );
		return node.getProbability( seq.get( seq.size() - 1 ) );
	}
	
	public void addSequence( S[] seq )
	{
		addSequence( Arrays.asList( seq ));
	}
	public void addSequence( List<S> seq )
	{
		if( validAddition( seq )) addSequence( sequenceBody( seq ), sequenceHead( seq ));
	}

	public void addSequence( List<S> seq, S completion )
	{
		String str = seqString( seq );
		System.out.println( "Adding sequence:::: " + str );
		if( sequences.containsKey( str )) sequences.get( str ).increment( completion );
		else sequences.put( str, new BucketNode<S>( completion ) );
	}
	
	public void setCompletionProbabilitiy( S[] seq, double prob )
	{
		setCompletionProbabilitiy( Arrays.asList( seq ), prob );
	}
	public void setCompletionProbabilitiy( List<S> seq, double prob )
	{
		addSequence( seq );
		String str = seqString( sequenceBody( seq ) );
		//System.out.println( "Probe sequence: " + str );
		BucketNode<S> bucket = sequences.get( str );
		if( bucket == null ) System.err.println( "No bucket found for " + str );
		CompletionValues cv = bucket.completions.get( sequenceHead( seq ));
		cv.probability = prob;
	}

	boolean validProbe( List<S> seq )
	{
		if( seq == null ) return false;
		if( seq.size() != order ) return false;
		return true;
	}
	
	boolean validAddition( List<S> seq )
	{
		if( seq.size() < order + 1 ) return false;
		return true;
	}
	
	S sequenceHead( List<S> in ) { return in.get( in.size() - 1 ); } 
	
	List<S> sequenceBody( List<S> in )
	{
		if( in.size() >= order + 1 ) return in.subList( in.size() - order - 1, in.size() - 1 );
		else return null;
	}
	
	List<S> probeSequence( List<S> in )
	{
		if( in.size() >= order ) return in.subList( in.size() - order, in.size() );
		else return null;
	}
	
	public static String seqString( Object[] seq ) { return seqString( Arrays.asList( seq )); }
	public static String seqString( List seq )
	{
		String str = "";
		if( seq == null ) return "{null!}";
		for( Object s : seq ) str += "{" + s + "}";
		return str;
	}
	
	public String toString()
	{
		String str = "Sequence Bucket, order " + order + " contains " + sequences.keySet().size() + " sequences\n";
		for( String key : sequences.keySet() )
			str += key + " := \t\t" + sequences.get( key ) + "\n";
		return str;
	}
	
	public void calculateProbabilities()
	{
		for( BucketNode<S> bn : sequences.values() )
			bn.calculateProbabilities();
	}
	
	public int getNumSequences()
	{
		return sequences.keySet().size();
	}

}
