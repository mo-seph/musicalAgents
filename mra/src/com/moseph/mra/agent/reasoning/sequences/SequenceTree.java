package com.moseph.mra.agent.reasoning.sequences;

import java.util.*;

/*
 * Holds a tree of sequences, where each level is a symbol and a value (e.g. freq or prob)
 * for that symbol, and each symbol also links to all possible symbols in the next level
 */
public class SequenceTree<S>
{
	SymbolNode root;
	public SequenceTree()
	{
		root = new RootNode<S>();
	}
	
	public String toString()
	{
		return root.toString();
	}
	
	public void addSequence( List<S> seq )
	{
		root.addSequence( seq );
	}
	
	public void calculateProbabilities()
	{
		root.calculateProbabilities();
	}
	
	public List<S> generateSequence( int length )
	{
		Vector<S> sequence = new Vector<S>();
		SymbolNode<S> current = root.getRandomlyWeightedChild();
		//System.out.println( "Looking for sequence of length " + length );
		//System.out.println( "Starting with : " + current.symbol ); 
		while( current != null && ( length > 1 || length < 0 ) )
		{
			sequence.insertElementAt( current.symbol, 0 );
			current = current.getRandomlyWeightedChild();
		}
		return sequence;
	}
	
	public S getNextSymbol( List<S> seq, int order )
	{
		SymbolNode<S> current = root;
		boolean ok = true;
		while( ok )
		{
			
		}
		
		
		return current.getRandomlyWeightedChild().symbol;
	}
	
	public String seqToString( List<S> seq )
	{
		String str = "";
		for( S s : seq ) str += "{" + s + "}";
		return str;
	}
	
	public void addSequence( List<S> seq, int times )
	{
		for( int i = 0; i <= times; i++ ) addSequence( seq );
	}
	
	public SymbolNode getNode( List<S> sequence )
	{
		SymbolNode<S> current = root;
		for( int i = sequence.size() - 1; i >= 0; i-- )
		{
			current = current.getChild( sequence.get( i ) );
			if( current == null ) return null;
		}
		return current;
	}
	
	public double getPriorProbability( List<S> sequence )
	{
		double prob = 1.0;
		SymbolNode<S> current = root;
		for( int i = sequence.size() - 1; i >= 0; i-- )
		{
			current = current.getChild( sequence.get( i ) );
			if( current == null ) return 0.0;
			prob *= current.probability;
		}
		return prob;
	}
}
