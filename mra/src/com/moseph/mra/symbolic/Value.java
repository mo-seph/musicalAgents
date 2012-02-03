package com.moseph.mra.symbolic;


/**
 * 
 * @author dave
 *
 * @param <T> the value class (parameter to allow generics to return correct subclass intstances)
 * @param <V> the type of value this value holds
 */
public abstract class Value<T extends Value<T,V>,V>
{
	public abstract int hashCode();
	public abstract T getAny();
	public abstract T getIncomp();
}
