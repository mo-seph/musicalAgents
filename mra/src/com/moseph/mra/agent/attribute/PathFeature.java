package com.moseph.mra.agent.attribute;

import java.util.List;

import com.moseph.mra.*;
import static java.lang.Math.*;

public class PathFeature implements Feature,NumericFeature,Cloneable
{
	Path path;
	Piece piece;
	
	public PathFeature( Path path, Piece piece )
	{
		this.path = path;
		this.piece = piece;
		if( piece == null ) new Exception( "No path!").printStackTrace();
	}
	
	public PathFeature( String path, Piece piece )
	{
		this.path = new Path( path );
		this.piece = piece;
		if( piece == null ) new Exception( "No piece!").printStackTrace();
	}
	
	public double getValue()
	{
		if( path == null ) return Double.MIN_VALUE;
		if( piece == null )
		{
			System.out.println( "No piece!!!");
			return Double.MIN_VALUE;
		}
		int ind = path.getIndex( piece );
		if( ind < 0 ) return Double.MIN_VALUE;
		return ind;
	}
	
	public void setValue( double d )
	{
		//TODO Figure out how to set path values later
	}
	
	public Path getPath()
	{
		return path;
	}
	
	public void setParameters( List<String> atts )
	{
		if( atts.size() > 0 ) path.setPathSpec( atts.get( 0 ));
	}
	
	public int compareTo( Feature o )
	{
		if( ! ( o instanceof PathFeature )) return toString().compareTo( o.toString() );
		PathFeature p = (PathFeature) o;
		return (int)(getValue() - p.getValue());
	}
	
	public boolean equals( NumericFeature v )
	{
		if( ! ( v instanceof PathFeature )) return false;
		return getValue() == v.getValue();
	}
	
	public PathFeature clone()
	{
		try
		{
			return (PathFeature)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public String toString()
	{
		return "pf: " + path;
	}
	
	public double distance( Feature f )
	{
		if( !( f instanceof PathFeature )) return Double.NaN;
		PathFeature pf = (PathFeature)f;
		return abs( getValue() - pf.getValue() );
	}
}
