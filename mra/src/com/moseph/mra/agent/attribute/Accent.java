package com.moseph.mra.agent.attribute;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Accent extends NamedAttribute implements Serializable
{
	public static enum AccentValue
	{
		VERY_SOFT,
		SOFT,
		NONE,
		ACCENT,
		STRONG;
	}
	
	public Accent() { super(); }
	public Accent( String name ) { super( name ); }
	public Accent( Accent p ) { super( p ); }
	public Accent( Enum p ) { super( p ); }
	
	public Enum getDefault() { return AccentValue.NONE; }
	
	public void setValue( String name )
	{
		try
		{
			super.setValue( AccentValue.valueOf( name ) );
		}
		catch( Exception e )
		{
			super.setValue( AccentValue.NONE );
		}
	}
	
	public Accent clone()
	{
		return new Accent( this );
	}
}
