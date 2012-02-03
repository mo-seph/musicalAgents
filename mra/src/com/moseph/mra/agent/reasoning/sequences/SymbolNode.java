package com.moseph.mra.agent.reasoning.sequences;

import java.util.*;

public class SymbolNode<S>
{
	int count;
	S symbol;
	Map<String, SymbolNode<S>> leaves;
	SymbolNode parent = null;
	double probability = 0.0;
	
	SymbolNode() 
	{
		leaves = new HashMap<String, SymbolNode<S>>();
		
	}
	SymbolNode( S symbol, SymbolNode parent )
	{
		this();
		count = 1;
		this.symbol = symbol;
		this.parent = parent;
	}
	
	void increment() { count++; }
	
	void addSequence( List<S> seq )
	{
		//System.out.println( "Node " + symbol + " adding sequence length " + seq.size()  );
		SymbolNode child = null;
		if( seq.size() > 0 )
			child = addValue( seq.get( seq.size() - 1 ) );
		if( seq.size() > 1 )
			child.addSequence( seq.subList( 0, seq.size() - 1 ));
	}
	
	SymbolNode addValue( S item )
	{
		if( leaves.containsKey( item )) leaves.get( item ).increment();
		else leaves.put( item.toString(), new SymbolNode( item, this ));
		return leaves.get(  item );
	}
	
	public String toString()
	{
		String str = "{" + symbol.toString() + "}";
		SymbolNode p = parent;
		while( p != null )
		{
			str += "{" + p.symbol + "}";
			p = p.parent;
		}
		
		str += " := " + count + "  (" + probability + ")" + "\n";
		for( SymbolNode s : leaves.values() )
		{
			str += s.toString();
		}
		return str;
	}
	
	public void calculateProbabilities()
	{
		for( SymbolNode child : leaves.values() )
		{
			//System.out.println( "Count: " + count + " Child (" + child.symbol + ") count: " + child.count );
			child.probability = (double)child.count / (double)count;
			child.calculateProbabilities();
		}
	}
	
	public SymbolNode<S> getChild( String name )
	{
		return leaves.get( name );
	}
	public SymbolNode<S> getChild( S symbol )
	{
		return leaves.get( symbol.toString() );
	}
	public SymbolNode<S> getRandomlyWeightedChild()
	{
		double cumulative = 0.0;
		double random = Math.random();
		if( leaves.keySet().size() == 0 ) return null;
		for( SymbolNode child : leaves.values() )
		{
			cumulative += child.probability;
			//System.out.println( "Cumul: " + cumulative + ", Rand: " + random );
			if( cumulative > random ) return child;
		}
		return null;
	}
}
	