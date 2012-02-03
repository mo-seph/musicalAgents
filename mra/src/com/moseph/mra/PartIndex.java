package com.moseph.mra;
//import com.moseph.music.*;
import java.io.Serializable;
import com.moseph.mra.Instrument;

/*******************************************************************************
*                                                                              *
* Public  class used for indexing the map of fragments                         *
*                                                                              *
*******************************************************************************/

public class PartIndex implements Serializable
{
	Musician m = new Musician( "unknown" );
	Instrument i = new Instrument( "AcousticPiano" );
	

	PartIndex( Musician m, Instrument i )
	{
		this.m = m;
		this.i = i;
	}
	
	public Instrument getInstrument()
	{
		return i;
	}
	
	public Musician getMusician()
	{
		return m;
	}

	public boolean equals( Object o )
	{
		if( !(o instanceof PartIndex ) ) return false;
		PartIndex p = (PartIndex) o;
		if( !m.equals( p.m ) ) return false;
		if( i == null && p.i != null ) return false;
		if( i != null && !i.equals( p.i ) ) return false;
		return true;
	}

	public String toString()
	{
		return m + ": " + i;
	}

	public int hashCode()
	{
		int hashCode = toString().hashCode();
		//System.out.println( "Getting has code for " + toString() + ": " + hashCode );
		return hashCode;
	}
}

