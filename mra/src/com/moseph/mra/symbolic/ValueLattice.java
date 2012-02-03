package com.moseph.mra.symbolic;

import java.util.List;

public interface ValueLattice<V extends Value>
{
	public Relationship getRelationship( V a, V b );
	public List<V> getPossibleValues( V value, Relationship relationship );
	public List<V> getPossibleValues( V value, Relationship relationship, int max );
	public List<V> getPossibleValues( V value1, Relationship relation1, V value2, Relationship relation2 );
	public List<V> getPossibleValues( V value1, Relationship relation1, V value2, Relationship relation2, int maximum );
	public V getAny();
	public V getIncompatible();
	public boolean isAny( V val );
	public boolean isIncompatible( V val );
}
