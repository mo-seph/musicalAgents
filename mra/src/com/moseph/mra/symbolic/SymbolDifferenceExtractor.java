package com.moseph.mra.symbolic;

import java.util.*;

import com.moseph.mra.acts.MusicalAction;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;

public class SymbolDifferenceExtractor<V extends Value, G extends GroupFeature<F>,F extends Feature>
{
	G feature;
	Map<String,V> current = new HashMap<String, V>();
	Symboliser<F,V> symboliser;
	String name;
	HashMap<MusicianInformation, MusicalAction> currentActions;
	ValueLattice lattice;
	Context context;
	
	protected SymbolDifferenceExtractor( String name, G feature )
	{
		this.feature = feature;
		this.name = name;
	}
	public SymbolDifferenceExtractor( String name, G feature, Symboliser<F,V> symboliser )
	{
		this( name, feature );
		this.symboliser = symboliser;
	}
	
	/**
	 * Call after updating the feature. The return value is a hash with only the updated values in
	 * @return
	 */
	public Map<MusicianInformation,V> update()
	{
		Map<MusicianInformation,V> ret = new HashMap<MusicianInformation, V>();
		currentActions = new HashMap<MusicianInformation, MusicalAction>();
		//Java's not seeing the return type from getOtherMusicians as Set<MusicianInformation>
		for( Object i : feature.getMusicians() )
		{
			MusicianInformation inf = (MusicianInformation)i;
			String name = inf.getName();
			V newVal = symboliser.symbolise( (F)feature.getValue( inf ) );
			if( current.containsKey( name ) && newVal.equals( current.get( name )) )
			{
				//do nothing, it's the same
				//System.out.println( "Old:" + current.get( name ) + ", New: " + newVal );
			}
			else
			{
				//System.err.println( "NEW ACTION!\n************\nOld:" + current.get( name ) + ", New: " + newVal );
				valueChanged( inf, newVal, current.get( name ) );
				current.put( name, newVal );
				ret.put(  inf, newVal );
			}
		}
		return ret;
	}
	
	public Map<MusicianInformation,MusicalAction> getActions()
	{
		return currentActions;
	}
	
	/**
	 * Called when a value changes
	 * @param inf
	 * @param value
	 * @param oldValue
	 */
	void valueChanged( MusicianInformation inf, V value, V oldValue )
	{
		//This is slightly confusing... we are analysing someone else's stream
		//so ROthehr is between that stream and OUR values...
		//System.out.println( name + ": " + inf.getName() + "-> " + value );
		MusicalAction action = new MusicalAction( inf, value, lattice.getRelationship( value, oldValue ) );
		if( context != null && context.getMyCurrentValue( name ) != null )
		{
			action.setROther( lattice.getRelationship( value, context.getMyCurrentValue( name ) ) );
		}
		currentActions.put( inf, action );
	}
	public String getName()
	{
		return name;
	}
	public void setName( String name )
	{
		this.name = name;
	}
	public void setContext( Context context )
	{
		this.context = context;
	}
	public ValueLattice getLattice()
	{
		return lattice;
	}

}
