package com.moseph.mra;
//import com.moseph.music.*;
import java.io.Serializable;

/**
A Musician is a unique representation of Musical Agent
@author David Murray-Rust
@version $Revision$, $Date$
*/
public class Musician implements Serializable
{
	String name = "Anonymous";
	double x = 0.0;
	double y = 0.0;
	double pull = 0.0;

	public double getPull()
	{
		return pull;
	}

	public void setPull( double pull )
	{
		this.pull = pull;
	}

	public Musician( String name )
	{
		this.name = name;
	}

	public String toString()
	{
		return name;
	}
	
	public String getName()
	{
		return name;
	}

	public boolean equals( Object o )
	{
		if( ! ( o instanceof Musician ) ) return false;
		Musician m = (Musician) o;
		return name.equals( m.name );
	}

	public double getX()
	{
		return x;
	}

	public void setX( double x )
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY( double y )
	{
		this.y = y;
	}
	
	public void setPosition( double x, double y )
	{
		this.x = x;
		this.y = y;
	}
	
	public double getSquaredDistance( Musician m )
	{
		if( m == null ) return Double.MAX_VALUE;
		return ( ( getX() - m.getX() ) *( getX() - m.getX() ) ) +	( ( getY() - m.getY() ) *( getY() - m.getY() ) );
	}
}

