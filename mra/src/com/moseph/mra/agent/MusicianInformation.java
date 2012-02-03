package com.moseph.mra.agent;

import java.awt.Color;

import com.moseph.mra.Instrument;
import com.moseph.mra.Musician;
import com.moseph.mra.Path;

import jade.core.AID;

public class MusicianInformation
{
	String name;
	AID aid;
	Musician musician;
	Instrument instrument;
	Path path;
	boolean active = true;
	Color colour;
	
	public MusicianInformation() { setMusician( new Musician("unknown"));}
	public MusicianInformation( Musician musician )
	{
		this();
		setMusician( musician );
	}
	
	public AID getAid()
	{
		return aid;
	}
	public void setAid( AID aid )
	{
		this.aid = aid;
	}
	public Instrument getInstrument()
	{
		return instrument;
	}
	public void setInstrument( Instrument instrument )
	{
		this.instrument = instrument;
	}
	public Musician getMusician()
	{
		return musician;
	}
	public void setMusician( Musician musician )
	{
		this.musician = musician;
		setName( musician.getName() );
	}
	public String getName()
	{
		return name;
	}
	public void setName( String name )
	{
		this.name = name;
	}
	public Path getPath()
	{
		return path;
	}
	public void setPath( Path path )
	{
		this.path = path;
	}
	
	public String[] details()
	{
		String pathString = "?";
		if( path != null ) pathString = path.trimmedPath();
		return new String[]
		                  {
				name, //"Name: " + name,
				instrument + "", //"Instrument: " + instrument,
				pathString //"Path: " + pathString
		                  };
	}
	
	public int hashCode()
	{
		if( name == null ) return super.hashCode();
		return name.hashCode();
	}
	
	public double getSquaredDistance( MusicianInformation m )
	{
		return musician.getSquaredDistance( m.musician );
	}
	public boolean isActive()
	{
		return active;
	}
	public void setActive( boolean active )
	{
		this.active = active;
	}
	public Color getColour()
	{
		return colour;
	}
	public void setColour( Color colour )
	{
		this.colour = colour;
	}
	
	public String toString()
	{
		return name + " (" + instrument + ")";
	}
}
