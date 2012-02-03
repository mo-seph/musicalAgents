package com.moseph.mra;

import java.util.List;
import java.util.Vector;

public class Piece extends Section
{
	List<Action> actions;
	List<Behaviour> behaviours;
	List<Decision> decisions;
	List<Section> sections;
	
	public Piece( String name )
	{
		super( name );
		actions = new Vector<Action>();
		behaviours = new Vector<Behaviour>();
		decisions = new Vector<Decision>();
		sections = new Vector<Section>();
	}

	public List<Action> getActions()
	{
		return actions;
	}
	
	public void addAction( Action a )
	{
		actions.add( a );
	}

	public List<Decision> getDecisions()
	{
		return decisions;
	}

	public void addDecision( Decision d )
	{
		decisions.add( d );
	}
	
	public List<Behaviour> getBehaviours()
	{
		return behaviours;
	}

	public void addBehaviour( Behaviour d )
	{
		behaviours.add( d );
	}

	public List<Section> getSections()
	{
		return children;
	}
	
	String getMRADeclaration() { return "Piece "; }
	String getExtraMRAData() { return "BPM: " + getBPM() + "\n"; }
}
