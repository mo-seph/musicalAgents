package com.moseph.mra.symbolic;

import java.util.*;

public abstract class TreeValue<T extends TreeValue<T,V>, V> extends SingleValue<T,V>
{
	T parent;
	List<T> children = new Vector<T>();
	boolean root = true;
	boolean leaf = true;
	public TreeValue( String name, V value )
	{
		this.name = name;
		this.value = value;
	}
	
	public void addChild( T child )
	{
		children.add( child );
		leaf = false;
	}
	
	public void setParent( T parent )
	{
		this.parent = parent;
		root = false;
	}
	
	public SingleValue<T, V> getParent()
	{
		return parent;
	}

	public boolean isLeaf()
	{
		return leaf;
	}

	public boolean isRoot()
	{
		return root;
	}
	
	public List<T> getChildren()
	{
		return children;
	}
	
	public int getNumChildren()
	{
		return children.size();
	}
	
}
