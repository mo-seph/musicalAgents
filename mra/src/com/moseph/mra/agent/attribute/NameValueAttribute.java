package com.moseph.mra.agent.attribute;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public abstract class NameValueAttribute extends ValuedAttribute implements Serializable
{
	String name;
	
	public NameValueAttribute( double level )
	{
			setValue( nameForLevel( level ) );
	}
	
	public NameValueAttribute( String name )
	{
			setValue( name );
	}
	
	public NameValueAttribute( NameValueAttribute p )
	{
		this.name = p.name;
		this.value = p.value;
	}
	
	public void setLevel( double level )
	{
		setValue( nameForLevel( level ));
	}
	
	public void setValue( String level )
	{
		this.name = level;
		this.value = valueForName( name );
	}
	
	abstract double valueForName( String name );
	abstract String nameForLevel( double name );
	
	public void setParameters( List<String> params)
	{
		if( params.size() > 0 )
		{
			setValue( params.get( 0 ));
		}
	}
}
