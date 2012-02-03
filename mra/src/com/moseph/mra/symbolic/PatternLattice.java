package com.moseph.mra.symbolic;

import java.util.*;

import com.moseph.mra.MRAUtilities;
import static com.moseph.mra.MRAUtilities.*;

import static com.moseph.mra.symbolic.Relationship.*;

public class PatternLattice<V,P extends TreeValue<P, V>,T extends PatternValue<V,P,T>> implements ValueLattice<T>
{
	TreeLattice<V,P> valueLattice;
	T basicValue;
	int size;
	T any;
	T incomp;
	
	public PatternLattice( int size, TreeLattice<V,P> valueLattice, T basicValue )
	{
		this.valueLattice = valueLattice;
		this.basicValue = basicValue;
		any = basicValue.getAny();
		incomp = basicValue.getIncomp();
	}
	
	public List<T> getPossibleValues( T value, Relationship relationship )
	{
		return getPossibleValues( value, relationship, -1 );
	}
	
	public List<T> getPossibleValues( T value, Relationship relationship, int max )
	{
		List<T> ret = new Vector<T>();
		if( relationship == SAME )
		{
			ret.add( value.createCopy() );
			return ret;
		}
		
		
		//For each element in the pattern, the domain is the set of values with
		//the specified relation
		List<List<P>> domains = new Vector<List<P>>();
		for( P current : value.values ) domains.add( valueLattice.getPossibleValues( current, relationship ) );
		//System.out.println( "Domains: " + collectionToString( domains ));
		
		//Construct the power list of this list
		List<List<P>> pow = new Vector<List<P>>();
		if( max < 0 )
		{
			pow = MRAUtilities.powerList( domains );
		}
		else
		{
			for( int i = 0; i < max; i++ )
			{
				List<P> a = new Vector<P>();
				for( List<P> cur : domains ) 
				{
					if( cur.size() > 0 ) a.add( cur.get( (int )(Math.random() * cur.size() ) ) );
					else a.add( valueLattice.incompatible );
				}
				pow.add( a );
			}
		}
		//System.out.println( "PowerList: " + collectionToString( pow ));
		for( List<P> retVal : pow ) ret.add( basicValue.createCopy( retVal ) );
		
		
		return ret;
	}

	public List<T> getPossibleValues( T value1, Relationship relation1, T value2, Relationship relation2 )
	{
		return getPossibleValues( value1, relation1, value2, relation2, -1 );
	}
	public List<T> getPossibleValues( T value1, Relationship relation1, T value2, Relationship relation2, int max )
	{
		if( relation1 == MU ) return getPossibleValues( value2, relation2, max );
		if( relation2 == MU ) return getPossibleValues( value1, relation1, max );
		Set<T> ret = new HashSet<T>();
		ret.addAll( getPossibleValues( value1, relation1, max ));
		ret.retainAll( getPossibleValues( value2, relation2, max ) );
		return new Vector<T>( ret );
	}


	public Relationship getRelationship( T a, T b )
	{
		if( b == null ) return DISJOINT;
		if( a.equals(b)) return SAME;
		T meet = meet( a, b );
		//System.out.println( "Comparing: \n" + a + " and \n" + b + " (meet is: \n" + meet);
		if( meet.equals( a )) return SUBSUMES;
		if( meet.equals( b )) return SUBSUMED;
		if( isIncompatible( meet )) return DISJOINT;
		return ALTER;
	}
	
	public T joint( T a, T b )
	{
		T combined = basicValue.createEmptyCopy();
		for( int i = 0; i < basicValue.size; i++ )
		{
			Relationship rel = valueLattice.getRelationship( a.get(i), b.get(i) );
			//Any values always JOIN to any
			if( valueLattice.isAny( a.get(i) ) || valueLattice.isAny( b.get(i) ) )
				combined.setAny( i );
			//Two incomps -> incomp
			else if ( valueLattice.isIncompatible( a.get( i ) ) && valueLattice.isIncompatible( b.get( i )))
				combined.setIncompatible( i );
			else if ( valueLattice.isIncompatible( a.get(i)))
				combined.set( i, b.get( i ));
			else if ( valueLattice.isIncompatible( b.get(i)))
				combined.set( i, a.get( i ));
			else if( rel.equals( Relationship.SUBSUMED ))
				combined.set( i, a.get( i ));
			else if( rel.equals( Relationship.SUBSUMES ))
				combined.set( i, b.get( i ));
			else 
				//This is probably wrong, but we're also probably not going to use it, so that's OK!
				combined.set( i, valueLattice.getAny() );
		}
		
		return combined;
	}

	public T meet( T a, T b )
	{
		T combined = basicValue.createEmptyCopy();
		for( int i = 0; i < basicValue.size; i++ )
		{
			Relationship rel = valueLattice.getRelationship( a.get(i), b.get(i) );
			if( a.get(i).equals(  b.get( i )  ))
				combined.set( i, a.get(i) );
			//Incompatible values always combine to incompatible
			else if( valueLattice.isIncompatible( a.get(i) ) || valueLattice.isIncompatible( b.get(i) ) )
				combined.setIncompatible( i );
			//Two anys -> any
			else if ( valueLattice.isAny( a.get( i ) ) && valueLattice.isAny( b.get( i )))
				combined.set( i, a.get(i) );
			else if ( valueLattice.isAny( a.get(i)))
				combined.set( i, b.get( i ));
			else if ( valueLattice.isAny( b.get(i)))
				combined.set( i, a.get( i ));
			else if( rel.equals( Relationship.SUBSUMES ))
				combined.set( i, a.get( i ));
			else if( rel.equals( Relationship.SUBSUMED ))
				combined.set( i, b.get( i ));
			else 
				combined.set( i, valueLattice.getIncompatible() );
		}
		
		return combined;
	}

	public T getAny()
	{
		return any;
	}

	public T getIncompatible()
	{
		return incomp;
	}

	public boolean isAny( T val )
	{
		for( P i : val.values ) if( ! valueLattice.isAny( i ) ) return false;
		return true;
	}

	public boolean isIncompatible( T val )
	{
		for( P i : val.values ) if( ! valueLattice.isIncompatible( i ) ) return false;
		return true;
	}
	

}
