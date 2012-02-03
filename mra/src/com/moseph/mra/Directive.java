package com.moseph.mra;

public class Directive extends Unit
{
	Directive( String name )
	{
		super( name );
		System.out.println( "Creating Directive: " + name );
	}
}
