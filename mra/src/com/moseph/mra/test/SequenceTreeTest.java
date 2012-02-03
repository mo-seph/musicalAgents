package com.moseph.mra.test;

import java.util.*;

import junit.framework.TestCase;

import com.moseph.mra.agent.reasoning.sequences.SequenceTree;
import static com.moseph.mra.MRAUtilities.*;

public class SequenceTreeTest extends TestCase
{
	
	public void testSequenceTreeBasics()
	{
		SequenceTree<String> tree = new SequenceTree<String>();
		String[] seq = { "A", "B", "A", "B" };
		tree.addSequence( Arrays.asList( new String[] { "A", "B", "C", "B" } ) );
		tree.addSequence( Arrays.asList( new String[] { "A", "C", "C", "B" } ) );
		tree.calculateProbabilities();
		System.out.println( tree );
	}
	
	public void testGenerateSingleSequence()
	{
		SequenceTree<String> tree = new SequenceTree<String>();
		String[] seq = { "A", "B", "C" };
		tree.addSequence( Arrays.asList( seq ) );
		tree.calculateProbabilities();
		System.out.println( tree );
		List<String> sequence = tree.generateSequence( 4 );
		System.out.println( "Sequence: " + tree.seqToString( sequence ));
		assertTrue( equalSequences( Arrays.asList( seq ), sequence ));
	}
	
	public void testSimpleSequences()
	{
		String[][] sequences = 
		{
				{ "A", "B", "C" },
				{ "C", "D" },
				{ "B", "B", "C" }
		};
		double[] probabilities = { 0.2, 0.1, 0.7 };
		roundtripAndTest( sequences, probabilities, 1000 );
	}
	
	public void testSequenceGenerationAndLearning()
	{
		String[][] sequences =
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
		double[] probabilities = 
		{ 
				0.03, 0.03, 0.03, 0.03, 0.03,
				0.22, 0.03, 0.03, 0.03, 0.03,
				0.03, 0.03, 0.03, 0.03, 0.03,
				0.03, 0.03, 0.03, 0.03, 0.03,
				0.03, 0.03, 0.03, 0.03, 0.03,
				0.03, 0.03
		};
		SequenceTree<String> original = new SequenceTree<String>();
		for( int i = 0; i < sequences.length; i++ )
			original.addSequence( Arrays.asList( sequences[i] ), (int)( probabilities[i] * (double) 10000 ) );
		//Update the probabilities
		original.calculateProbabilities();
		for( String[] seq : sequences )
		{
			double prob = original.getPriorProbability( Arrays.asList( seq ) );
			System.out.println( original.seqToString( Arrays.asList( seq ) ) + " := " + prob );
		}
		System.out.println( original );
	}
	
	SequenceTree roundtripSequences( String[][] sequences, double[] probabilities, int number )
	{
		SequenceTree<String> original = new SequenceTree<String>();
		SequenceTree<String> tested = new SequenceTree<String>();
		//Put the apropriate number of sequences in the original tree
		for( int i = 0; i < sequences.length; i++ )
			original.addSequence( Arrays.asList( sequences[i] ), (int)( probabilities[i] * (double) number ) );
		//Update the probabilities
		original.calculateProbabilities();
		System.out.println( "Origninal: " + original );
		//Generate the requested number of new sequences, and put them in the new tree
		for( int i = 0; i < number; i++ )
			tested.addSequence( original.generateSequence( -1 ) );
		tested.calculateProbabilities();
		return tested;
	}
	
	void roundtripAndTest( String[][] sequences, double[] probabilities, int number )
	{
		SequenceTree<String> roundtripped = roundtripSequences( sequences, probabilities, number );
		System.out.println( roundtripped );
		for( int i = 0; i < sequences.length; i++ )
		{
			assertTrue( fuzzyCompare( 
					probabilities[i],
					roundtripped.getPriorProbability( Arrays.asList( sequences[i] ) ),
					0.05 ));
		}
	}
	
	boolean equalSequences( List a, List b )
	{
		if( a.size() != b.size() ) return false;
		for( int i = 0; i < a.size(); i++ )
			assertEquals( a.get( i ), b.get( i ));
		return true;
	}

}
