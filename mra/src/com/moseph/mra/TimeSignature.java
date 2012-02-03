package com.moseph.mra;


/**
represents a musical time signature.
The beat type is given just as it would be on a musical score.
@author David Murray-Rust
@version $Revision$, $Date$
*/
public class TimeSignature 
{
	public int beats;
	public int type;

	public TimeSignature( int beats, int type )
	{
		this.beats = beats;
		this.type = type;
	}
	
	public TimeSignature( String def )
	{
		String[] parts = def.split( "[/,x]");
		if( parts.length > 1 )
		{
			beats = Integer.parseInt( parts[0]);
			type = Integer.parseInt( parts[0]);
		}
	}

	public String toString()
	{
		return beats + "/" + type;
	}

	public int getBeats()
	{
		return beats;
	}
	public int getType()
	{
		return type;
	}
}
