package com.moseph.mra.test;

import java.util.*;

import junit.framework.TestCase;

import com.moseph.mra.agent.reasoning.sequences.*;

public class SequenceBucketTest extends TestCase
{
	
	public void testBasicOperation()
	{
		String[] seq = { "1", "2", "3", "4" };
		SequenceBucket<String> sb = new SequenceBucket<String>( 3 );
		sb.addSequence( seq );
		sb.addSequence( new String[] { "1", "2", "3", "5" } );
		sb.addSequence( new String[] { "1", "1", "3", "5" } );
		sb.calculateProbabilities();
		System.out.println( sb );
		BucketNode<String> completions = sb.getCompletions( new String[] { "1", "2", "3" } );
		assertEquals( 0.0, completions.getProbability( "A" ) );
		assertEquals( 0.5, completions.getProbability( "5" ) );
		assertEquals( "5", sb.getWeightedCompletion( new String[] { "1", "1", "3" } ));
	}
	
	public void testSimpleGrammar()
	{
		SequenceBucket<String> orig = new SequenceBucket<String>( 1 );
		SequenceBucket<String> target = new SequenceBucket<String>( 1 );
		String[][] seqs = 
		{
				{ "A", "B" },
				{ "B", "A" }
		};
		double[] probs = { 1.0, 1.0 };
		roundtripSequences( orig, target, seqs, probs, 100 );
	}
	
	public void testThreeStageGrammar()
	{
		SequenceBucket<String> orig = new SequenceBucket<String>( 2 );
		SequenceBucket<String> target = new SequenceBucket<String>( 2 );
		String[][] seqs =
		{
				{ "A", "A", "A" },
				{ "A", "A", "B" },
				{ "A", "A", "C" },
				{ "A", "B", "A" },
				{ "A", "B", "B" },
				{ "A", "B", "C" },
				{ "A", "C", "A" },
				{ "A", "C", "B" },
				{ "A", "C", "C" },
				{ "B", "A", "A" },
				{ "B", "A", "B" },
				{ "B", "A", "C" },
				{ "B", "B", "A" },
				{ "B", "B", "B" },
				{ "B", "B", "C" },
				{ "B", "C", "A" },
				{ "B", "C", "B" },
				{ "B", "C", "C" },
				{ "C", "A", "A" },
				{ "C", "A", "B" },
				{ "C", "A", "C" },
				{ "C", "B", "A" },
				{ "C", "B", "B" },
				{ "C", "B", "C" },
				{ "C", "C", "A" },
				{ "C", "C", "B" },
				{ "C", "C", "C" },
		};
		double[] probs = 
		{ 
				0.3, 0.3, 0.4, 
				0.3, 0.3, 0.4,
				0.3, 0.3, 0.4,
				0.3, 0.3, 0.4,
				0.3, 0.3, 0.4,
				0.3, 0.3, 0.4,
				0.3, 0.3, 0.4, 
				0.3, 0.3, 0.4, 
				0.3, 0.3, 0.4
		};
		roundtripSequences( orig, target, seqs, probs, 10000 );
	}
	
	public void roundtripSequences( SequenceBucket<String> orig, SequenceBucket<String> target, String[][] seqs, double[] probs, int number  )
	{
		//Put the probabilities into the original tree
		for( int i = 0; i < seqs.length; i++ )
			orig.setCompletionProbabilitiy( seqs[i], probs[i] );
		
		//Generate a sequence as a result
			//Start with a random one of the input sequences
		List<String> sequence = new Vector();
		sequence.addAll( Arrays.asList( seqs[ (int)(Math.random() * seqs.length ) ] ) );
			//Add symbols until we hit num
		while( sequence.size() < number )
			sequence.add( orig.getWeightedCompletion( sequence ));
		
		//Feed these sequences into the target tree
		for( int i = 1; i <= number; i++ )
			target.addSequence( sequence.subList( 0, i ) );
		target.calculateProbabilities();
		System.out.println( "Orig:\n\n---------------\n" + orig );
		System.out.println( "Roundtrip:\n\n---------------\n" + target );
		for( int i = 0; i < seqs.length; i++ )
		{
			System.out.println( "Getting probability for " + SequenceBucket.seqString( seqs[i] ) + " expecting " + probs[i]);
			assertEquals( probs[i], target.getSequenceCompletionProbability( seqs[i] ), 0.05 );
		}
	}
}
