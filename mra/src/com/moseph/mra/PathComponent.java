/**
 * 
 */
package com.moseph.mra;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathComponent implements Serializable
{
	static Pattern indexPattern = Pattern.compile( "(\\d+)" );
	static Pattern namePattern = Pattern.compile( "([^;]+)(;(\\d+))?" );
	String name = null;
	int index= -1;
	int occurence = 0;
	
	public PathComponent( String spec )
	{
		if( indexPattern.matcher(spec).matches())
		{
			index = Integer.parseInt( spec );
		}
		else
		{
			Matcher m = namePattern.matcher( spec );
			if( m.matches() )
			{
				this.name = m.group(1);
				if(m.group(3) != null )
				{
					occurence = Integer.parseInt( m.group(3));
				}
			}
			else
			{
				System.err.println( "Bad path spec: " + spec );
			}
		}
	}
	
	public PathComponent( int index )
	{
		this.index = index;
	}
	
	public PathComponent( String name, int index )
	{
		this.name = name;
		this.index = index;
	}
	
	public Section apply( Section parent )
	{
		if( parent == null ) return null;
		int seenIndex = 0;
		int seen = 0;
		for( Section child : parent.getChildren() )
		{
			if( matches( child, seenIndex++ ))
				if( seen++ >= occurence ) return child;
		}
		return null;
	}
	
	public int leavesBefore( Section parent )
	{
		if( parent == null ) return -1;
		int leaves = 0;
		int seen = 0;
		int seenIndex = 0;
		for( Section child: parent.getChildren() )
		{
			if( matches( child, seenIndex ++ ))
				if( seen++ >= occurence) return leaves;
			leaves += child.getNumLeaves();
		}
		return -1;
	}
	
	public boolean matches( Section child )
	{
		return matches( child, -1 );
	}
	
	public boolean matches( Section child, int curIndex )
	{
			if( name != null && child.getName().equalsIgnoreCase(name))
			{
				return true;
			}
			if( name == null )
			{
				if( index == curIndex ) return true;
			}
			return false;
	}
	
	public String toString()
	{
		if( name != null )
		{
			if( index < 0 ) return name;
			else return name + ";" + index;
		}
		if( index >= 0 ) return "" + index;
		return "nullpath";
	}
}