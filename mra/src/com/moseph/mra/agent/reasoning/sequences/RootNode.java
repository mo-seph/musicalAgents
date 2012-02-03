package com.moseph.mra.agent.reasoning.sequences;

import java.util.List;

public class RootNode<S> extends SymbolNode<S>
{
	public RootNode()
	{
		super();
		count = 0;
	}
	
	public String toString()
	{
		String str = "Tree: (" + count + " items)\n";
		for( SymbolNode s : leaves.values() )
		{
			str += s.toString();
		}
		return str;
	}

	void addSequence( List<S> seq )
	{
		count++;
		super.addSequence( seq );
	}
}
