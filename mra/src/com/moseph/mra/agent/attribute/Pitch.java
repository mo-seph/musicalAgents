package com.moseph.mra.agent.attribute;

import java.io.Serializable;
import static java.lang.Math.*;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Pitch extends MRAAttribute implements Serializable
{
	static Pattern pitchMatch = Pattern.compile( "([A-Z])(b|#)?(\\d)?");
	public static final int INVALID_PITCH = -1;

	
	int pitch;
	public static final String[] noteNames = { "C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B" };
	
	public Pitch( int pitch )
	{
		this.pitch = pitch;
	}
	
	public Pitch( String pitch )
	{
		setPitch( pitch );
	}
	
	public Pitch( Pitch p )
	{
		this.pitch = p.pitch;
	}
	
	public int getPitch()
	{
		return pitch;
	}
	
	public void setPitch( int pitch )
	{
		this.pitch = pitch;
	}
	
	void setPitch( String pitch )
	{
		this.pitch = pitchFromString( pitch );
	}
	
	public void setParameters( List<String> params)
	{
		if( params.size() > 0 ) setPitch( params.get( 0 ));
	}
	@Override
	public Pitch clone()
	{
		return new Pitch( pitch );
	}
	
	public boolean equals( Object o )
	{
		if( ! ( o instanceof Pitch ))	return false;
		Pitch op = (Pitch)o;
		return op.getPitch() == pitch;
	}

	@Override
	public int compareTo( Feature other )
	{
		if( !( other instanceof Pitch )) return toString().compareTo( other.toString() );
		Pitch o = (Pitch)other;
		if( o.pitch > pitch ) return -1;
		if( o.pitch < pitch ) return 1;
		return 0;
	}
	
	
	public static enum NoteNames
	{
		C ( 0 ),
		D ( 2 ),
		E ( 4 ),
		F ( 5 ),
		G ( 7 ),
		A ( 9 ),
		B ( 11 );
		public final int offset;
		NoteNames( int offset ) {this.offset = offset;}
		
		
	}
	
	public static int pitchFromString( String pitchString )
	{
		int pitch = INVALID_PITCH;
		try
		{
			if( pitchString.matches( "(\\d){1,3}"))
			{
				pitch = Integer.parseInt( pitchString);
				if( pitch > 128 ) pitch = INVALID_PITCH;
				return pitch;
			}
			Matcher m = pitchMatch.matcher( pitchString );
			if( m.matches() )
			{
				pitch = NoteNames.valueOf( m.group(1)).offset;
				String accidental = m.group( 2 );
				if( accidental != null )
				{
					if( accidental.equals( "#")) pitch++;
					if( accidental.equals( "b")) pitch--;
				}
				if( m.group(3) != null )
				{
					int octave = Integer.parseInt( m.group(3));
					pitch += ( octave + 2 ) * 12;
				}
				else
				{
					//Default to octave 3
					pitch += 60;
				}
			}
		}
		catch (Exception e)
		{
			log.log( Level.WARNING, "Bad Pitch string given: " + pitchString );
			e.printStackTrace();
		}
		return pitch;

	}
	
	public String getNoteName()
	{
		return Pitch.noteNames[ pitch % 12 ];
	}

	/**
	Returns the number corresponding to the MIDI numbering of octaves on which this note falls
	*/
	public int getOctave()
	{
		return ((int)pitch/12) - 2;
	}
	
	public void transpose( int amount )
	{
		pitch += amount;
	}
	
	public double distance( Feature f )
	{
		if( !( f instanceof Pitch )) return Double.NaN;
		Pitch p = (Pitch) f;
		return abs( getPitch() - p.getPitch() );
	}
}
