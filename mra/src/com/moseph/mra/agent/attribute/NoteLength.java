package com.moseph.mra.agent.attribute;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class NoteLength extends NamedAttribute implements Serializable
{
	public static enum NoteLengthValue
	{
		STACCATISSIMO,
		STACCATO,
		MARCATO,
		NORMAL,
		LEGATO,
		SLUR,
		HELD;
	}
	
	public NoteLength( String name )
	{
			super( name );
	}
	
	public NoteLength( NoteLength p )
	{
		super( p );
	}
	
	public Enum getDefault()
	{
		return NoteLengthValue.NORMAL;
	}
	
	public void setValue( String name )
	{
		try
		{
			super.setValue( NoteLengthValue.valueOf( name ) );
		}
		catch( Exception e )
		{
			System.err.println( "Unknown NoteLength: " + name );
			super.setValue( NoteLengthValue.NORMAL );
		}
	}
	
	public NoteLength clone()
	{
		return new NoteLength( this );
	}
}
