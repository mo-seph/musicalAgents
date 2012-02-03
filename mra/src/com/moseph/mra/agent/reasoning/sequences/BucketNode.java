package com.moseph.mra.agent.reasoning.sequences;

import java.io.Serializable;
import java.util.*;

public class BucketNode<S> implements Serializable
{
	Map<S, CompletionValues> completions;
	int count;
	boolean hasProbs = false;	
	public BucketNode()
	{
		count = 0;
		completions = new HashMap<S, CompletionValues>();
	}
	
	public BucketNode( S initial )
	{
		this();
		increment( initial );
	}
	
	public void increment( S symbol )
	{
		count++;
		if( completions.containsKey( symbol )) completions.get( symbol ).count++;
		else completions.put( symbol, new CompletionValues() );
	}
	
	public S getProbableCompletion()
	{
		if( ! hasProbs ) calculateProbabilities();
		double number = Math.random();
		double cumulative = 0.0;
		for( S cur : completions.keySet() )
		{
			CompletionValues cv = completions.get( cur );
			cumulative += cv.probability;
			if( cumulative >= number ) return cur;
		}
		return null;
	}
	
	void calculateProbabilities()
	{
		for( CompletionValues cv : completions.values() )
			cv.probability = (double)cv.count / (double)count;
		hasProbs = true;
	}
	
	public double getProbability( S symbol )
	{
		if( ! completions.containsKey( symbol )) return 0.0;
		else return completions.get( symbol ).getProbability();
	}

	public String toString()
	{
		String str = "";
		for( S key : completions.keySet() )
			str += "{" + key + ": " + completions.get( key ).probability + " (" + completions.get( key ).count + ") }";
		return str;
	}

	class CompletionValues implements Serializable
	{
		int count = 1;
		double probability = 0.0;
		
		public double getProbability() { return probability; }
	}

}
