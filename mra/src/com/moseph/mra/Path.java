package com.moseph.mra;

import jade.content.Concept;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class Path implements Serializable, Concept
{
	List<PathComponent> components = new Vector<PathComponent>();
	
	public Path( String[] pathComponents )
	{
		for( String c : pathComponents )
		{
			components.add( new PathComponent( c ));
		}
	}
	
	public Path()
	{
	}
	
	public Path( String fullString )
	{
		this();
		setPathSpec( fullString );
	}
		
	public void setPathSpec( String fullString )
	{
		components.clear();
		String[] spec = fullString.split( "/");
		for( String c : spec )
		{
			if( c == null | c.length() < 1 ) continue;
			//System.out.println( "Adding component '" + c + "'");
			components.add( new PathComponent( c ));
		}
	}
	
	public Section getSection( Section top )
	{
		Section s = top;
		String pathSoFar = "";
		for( PathComponent comp : components )
		{
			//System.out.println( "Matching: " + comp);
			s = comp.apply( s );
			if( s == null ) 
			{ 
				//System.out.println( "Couldn't match path '" + comp + "' at '" + pathSoFar + "'");
				break;
			}
			pathSoFar += "/" + comp;
		}
		return s;
	}
	
	public List<Section> getSections( Section top )
	{
		Vector<Section> ret = new Vector<Section>();
		Section s = top;
		for( PathComponent comp : components )
		{
			s = comp.apply( s );
			if( s == null ) 
				break;
			ret.add( s );
		}
		return ret;
	}
	
	public int getIndex( Section top )
	{
		Section s = top;
		int index = 0;
		int cur = 0;
		for( PathComponent comp : components )
		{
			cur = comp.leavesBefore( s );
			if( cur == -1 ) return -1;
			index += cur;
			s = comp.apply( s );
		}
		return index;
	}
	
	public String toString()
	{
		String ret = "";
		for( PathComponent p : components )
		{
			ret += "/" + p;
		}
		return ret;
	}
	
	public String trimmedPath()
	{
		String r = toString();
		r = r.replaceFirst( "/main", "" );
		r = r.replaceFirst( "^/", "" );
		return r;
	}
	
	public String getPathSpec()
	{
		return toString();
	}
	
}
