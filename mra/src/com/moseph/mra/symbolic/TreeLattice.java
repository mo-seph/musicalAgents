package com.moseph.mra.symbolic;

import java.util.*;
import static com.moseph.mra.symbolic.Relationship.*;

/**
 * Represents a concept lattice having one root, and a tree structure (values may have several descendants)
 * It is a tree of nodes of type T, each holding a value of type V
 * @author dave
 *
 * @param <V> The type of value stored in the nodes
 * @param <T> Then node class
 */
public class TreeLattice<V,T extends TreeValue<T, V>> implements ValueLattice<T>
{
	T root;
	Map<String,T> terms;
	T any;
	T incompatible;
	
	public TreeLattice( T rootVal )
	{
		this.terms = new HashMap<String, T>();
		this.root = rootVal;
		//any = root.getAny();
		any = root;
		incompatible = root.getIncomp();
	}
	
	/**
	 * Adds a term with the given name and value, belonging to the parent with the given name
	 * Should do some exceptioning, but not going to!
	 * @param name
	 * @param value
	 * @param parentName
	 * @return
	 */
	public T addTerm( String name, V value, String parentName )
	{
		T created = createTerm( name, value );
		if( terms.containsKey( parentName ))
		{
			terms.get( parentName ).addChild( created );
			created.setParent( terms.get( parentName ) );
		}
		return created;
	}
	
	public T addTerm( String name, V value, T parent )
	{
		T created = createTerm( name, value );
		parent.addChild( created );
		created.setParent( parent );
		return created;
	}
	
	T createTerm( String name, V value )
	{
		T created = root.getTerm( name, value );
		terms.put( name, created );
		return created;
	}
	
	public List<T> getPossibleValues( T value, Relationship relationship )
	{
		return getPossibleValues( value, relationship, -1 );
	}

	/**
	 * Returns a limited number of possible values
	 * 
	 * NOTE: limiting not implemented yet!
	 */
	public List<T> getPossibleValues( T value, Relationship relationship, int max )
	{
		List<T> ret = new Vector<T>();
		switch ( relationship )
		{
			case SAME:
				ret.add( value );
			break;
			case SUBSUMED:
				ret.addAll( getAncestors( value ));
			break;
			case SUBSUMES:
				ret.addAll( getDescendants( value ));
			break;
			case ALTER:
				ret.addAll( getParentBranch( value ));
				ret.removeAll( getBranch( value ) );
			break;
			case DISJOINT:
				ret.addAll( getDisjointSet( value ) );
			break;
			default:
			break;
		}
		return ret;
	}

	public List<T> getPossibleValues( T value1, Relationship relation1, T value2, Relationship relation2 )
	{
		return getPossibleValues( value1, relation1, value2, relation2, -1 );
	}
	/**
	 * Returns the set of values which hold the correct relations to the two values
	 */
	public List<T> getPossibleValues( T value1, Relationship relation1, T value2, Relationship relation2, int max )
	{
		if( relation1 == MU ) return getPossibleValues( value2, relation2, max );
		if( relation2 == MU ) return getPossibleValues( value1, relation1, max );
		Set<T> ret = new HashSet<T>();
		ret.addAll( getPossibleValues( value1, relation1, max ));
		ret.retainAll( getPossibleValues( value2, relation2, max ) );
		return new Vector<T>( ret );
	}

	public Relationship getRelationship( T source, T other )
	{
		if( other == null ) return Relationship.DISJOINT;
		if( other.equals( source )) return Relationship.SAME;
		if( isAncestor( source, other )) return Relationship.SUBSUMES;
		if( isAncestor( other, source )) return Relationship.SUBSUMED;
		if( commonAncestor( source, other )) return Relationship.ALTER;
		return Relationship.DISJOINT;
	}
	
	/**
	 * Returns true if other is an ancestor of source
	 * @param source
	 * @param other
	 * @return
	 */
	public boolean isAncestor( T source, T other )
	{
		T current = source.parent;
		while( current != null )
		{
			if( current.equals( other ) ) return true;
			current = current.parent;
		}
		return false;
	}
	
	public List<T> getAncestors( T  start )
	{
		List<T> ancestors = new Vector<T>();
		T current = start.parent;
		while( current != null )
		{
			ancestors.add( current );
			current = current.parent;
		}
		return ancestors;
	}
	
	public List<T> getDescendants( T start )
	{
		List<T> ret = new Vector<T>();
		List<T> queue = new Vector<T>();
		if( ! start.isLeaf() ) queue.addAll( start.getChildren() );
		while( queue.size() > 0 )
		{
			T current = queue.get( 0 );
			queue.remove( 0 );
			ret.add( current );
			if( ! current.isLeaf() ) queue.addAll( current.getChildren() );
		}
		return ret;
	}
	
	
	/**
	 * Finds the top-level ancestor (just below the root) and returns all of its descendants
	 * @param start
	 * @return
	 */
	public List<T> getParentBranch( T start )
	{
		T anc = getTopAncestor( start );
		return getDescendants( anc );
	}
	
	public T getTopAncestor( T start )
	{
		T current = start;
		while( current != null && current.parent != null && ! current.parent.equals( root ) )
		{
			System.out.println( "Current " + current + "\nParent: " + current.parent );
			current = current.parent;
		}
		return current;
	}
	
	
	/**
	 * Returns the ancestors of the node, and its descendants
	 * @param start
	 * @return
	 */
	public List<T> getBranch( T start )
	{
		List<T> ret = getDescendants( start );
		ret.add( start );
		ret.addAll( getAncestors( start ));
		Collections.reverse( ret );
		return ret;
	}
	
	public Set<T> getDisjointSet( T start )
	{
		Set<T> disj = new HashSet<T>( terms.values() );
		disj.removeAll( getParentBranch( start ) );
		disj.remove( start );
		return disj;
	}
	
	boolean commonAncestor( T a, T b )
	{
		Set<T> ancestorA = new HashSet<T>( getAncestors( a ) );
		for( Value<T, V> test : getAncestors( b )) 
			if ( !test.equals( root ) && ancestorA.contains( test )) return true;
		return false;
	}
	
	public Value<T, V> getRoot()
	{
		return root;
	}
	
	public String toString()
	{
		String ret = root.toString() + "\n";
		List<List<T>> queue = new Vector<List<T>>();
		//Wierd casting! Should just be able to use root.getChildren, but java thinks root is a Value, not a TreeValue
		queue.add( new Vector( ((TreeValue)root).getChildren() ));
		int level = 0;
		boolean addOne = false;
		while( queue.size() > 0 )
		{
			level = queue.size();
			if( addOne ) { level++; addOne = false; }
			int listIndex = queue.size() - 1;
			List<T> curList = queue.get( listIndex );
			if( curList.size() == 0 )
			{
				queue.remove( listIndex );
				continue;
			}
			
			for( int i = 0; i < level; i++ ) ret += "\t";
			T curNode = curList.get( 0 );
			ret += curNode;
			if( curNode.getNumChildren() > 0 ) queue.add( new Vector<T>( curNode.getChildren() ) );
			curList.remove( 0 );
			ret += "\n";
		}
		return ret;
	}
	
	void printLoL( List<List<T>> queue )
	{
		for( List<T> list : queue )
		{
			System.out.print( "{ ");
			for( T v : list ) System.out.print( " " + v );
			System.out.println( " }");
		}
	}
	
	public T getTerm( String name )
	{
		return terms.get( name );
	}
	public T getAny()
	{
		return any;
	}
	public T getIncompatible()
	{
		return incompatible;
	}
	
	public boolean isAny( T val )
	{
		return  any.equals( val );
	}
	public boolean isIncompatible( T val )
	{
		return  incompatible.equals( val );
	}
	

}
