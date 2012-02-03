package com.moseph.mra.agent.reasoning;

import java.util.*;
import static com.moseph.mra.MRAUtilities.*;

import com.moseph.mra.*;
import com.moseph.mra.acts.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.agent.reasoning.sequences.*;
import com.moseph.mra.symbolic.*;

/**
 * For now, this class only works relative to a single other person - it needs extension for multiparty
 * @author dave
 *
 */
public class SequenceReasoner extends ValueReasoner
{
	SequenceManager seqManager;
	SequenceCollection<ActRelation> sequences;
	//This is the relations which have been used *this turn only*
	Map<Aspect,ActRelation> currentRelations = new HashMap<Aspect, ActRelation>();
	public SequenceReasoner( Context c, StructuralDecider decider )
	{
		super( c, decider );
		seqManager = context.getSequenceManager();
		sequences = seqManager.getSequences();
	}
	
	Value getValueInResponseTo( Aspect aspect, MusicalAction actToRespondTo )
	{
		//Find the apropriate next symbol, and add it to the end of the sequence
		ActRelation newRelation = getActRelation( aspect, new ActRelation( actToRespondTo ));
		//Default relation is currently MU:SAME, forcing us to take the same value as the other
		//This causes a problem, as it will never be in the sequence DB
		//However, the sequence DB will always provide *some* answer (even if it is 0th order)
		//so it should never come up...
		if( newRelation == null ) newRelation = getDefaultRelation( aspect, actToRespondTo );
		currentRelations.put( aspect, newRelation );
		actionHistories.get( aspect ).add( newRelation );
		System.out.println( "Response is: " + newRelation );
		
		//Find the values which can be used with the current next symbol
		//Select a value
		Value v = getResponseValue( aspect, newRelation, actToRespondTo );
		System.out.println( "Value is: " + v );
		if( v == null )
		{
			v = actToRespondTo.getValue();
			System.out.println( "No value found, using opponents: " + v);
		}
		return v;
	}
	
	
	
	//Currently tries to become the same as the other player's output
	ActRelation getDefaultRelation( Aspect aspect, MusicalAction actToRespondTo )
	{
		return new ActRelation( Relationship.MU, Relationship.SAME );
	}
	
	ActRelation getActRelation( Aspect aspect, ActRelation stimulus )
	{
		if( ! actionHistories.containsKey( aspect )) actionHistories.put( aspect, new LinkedList<ActRelation>() );
		LinkedList<ActRelation> sequence = actionHistories.get( aspect );
		sequence.add( stimulus );
		if( sequence.size() < 4 ) return new ActRelation( Relationship.MU, Relationship.SAME );
		int startIndex = sequence.size() - sequences.getOrder();
		if( startIndex < 0 ) startIndex = 0;
		int endIndex = sequence.size();
		List<ActRelation> keySequence = sequence.subList( startIndex, endIndex );
		//System.out.println( "History is " + collectionToString( sequence  ) + " using " + startIndex + ".." + endIndex  );
		ActRelation newAction = sequences.getLikelyCompletion( keySequence );
		System.out.println( "Using key sequence: " + keySequence + "\n gives response: " + newAction );
		return newAction;
	}
	
	Value getResponseValue( Aspect aspect, ActRelation newRelation, MusicalAction actToRespondTo )
	{
		String feature = aspect.feature;
		//Value myValue = context.getMyCurrentValue( feature );
		System.err.println( "+++++++++CurrentValues: "+ collectionToString( currentValues ));
		Value myValue = currentValues.get( aspect );
		Value otherValue = actToRespondTo.getValue();
		ValueLattice lattice = latticeManager.getLattice( feature );
		if( aspect.isPattern ) lattice = latticeManager.getPatternLatticeFor( feature );
		if( myValue == null ) myValue = lattice.getAny();
		
		List<Value> possible = lattice.getPossibleValues( myValue, newRelation.getRSelf(), otherValue, newRelation.getROther(), 100 );
		if( possible.size() == 0 ) return null;
		//For now, select first one every time
		return possible.get( 0 );
	}
	
	public Map<Aspect, ActRelation> getCurrentRelations()
	{
		return currentRelations;
	}
	

}
