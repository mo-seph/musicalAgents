package com.moseph.mra.blob;
import static java.lang.Math.*;

import java.awt.Color;

public class BlobParams implements Cloneable
{
	public double xpos;
	public double ypos;
	public double red;
	public double blue;
	public double green;
	public boolean collision = false;
	public boolean tempInactive = false;
	public boolean inactive = false;
	public double movement = 0.0;
	public int bumpIndex = 0;
	public int vitality;
	public int bumped = 0;
	public double smoothedMovement = 0.0;
	public static final int MAX_VITALITY = 1000;
	int messaged = 0;
	
	public BlobParams( double xpos, double ypos, double red, double green, double blue )
	{
		this.xpos = xpos;
		this.ypos = ypos;
		this.red = red;
		this.blue = blue;
		this.green = green;
		vitality = MAX_VITALITY;
	}
	
	public BlobParams( BlobParams other )
	{
		this.xpos = other.xpos;
		this.ypos = other.ypos;
		this.red = other.red;
		this.blue = other.blue;
		this.green = other.green;
		this.collision = other.collision;
		this.tempInactive = other.tempInactive;
		this.inactive =  other.inactive;
		this.vitality = other.vitality;
		this.messaged = other.messaged;
	}
	
	public BlobParams( String info )
	{
		String[] parts = info.split( ":");
		xpos = Double.parseDouble( parts[0] );
		ypos = Double.parseDouble( parts[1] );
		red = Double.parseDouble( parts[2] );
		green = Double.parseDouble( parts[3] );
		blue = Double.parseDouble( parts[4] );
	}
	
	public double getColorDistance( BlobParams other )
	{
		return sqrt( 
				( red - other.red ) * ( red - other.red ) +
				( green - other.green ) * ( green - other.green ) +
				( blue - other.blue ) * ( blue - other.blue ) +
				0
				);
	}
	
	public Color getAverageColor( BlobParams b )
	{
		return new Color( (float)(red + b.red)/2, (float)(green+b.green)/2, (float)(blue+b.blue)/2 );
	}
	public Color getColor()
	{
		return new Color( (float)red, (float)green, (float)blue );
	}
	
	public double getPhysicalDistance( BlobParams other )
	{
		double xdis = xpos - other.xpos;
		double ydis = ypos - other.ypos;
		//System.out.println( "Distance x:" + xdis + " y:" + ydis );
		return Math.sqrt( xdis * xdis + ydis * ydis );
	}
	
	public void averageWith( BlobParams other )
	{
		xpos = ( xpos + other.xpos ) / 2.0;
		ypos = ( ypos + other.ypos ) / 2.0;
		red = ( red + other.red ) / 2.0;
		blue = ( blue + other.blue ) / 2.0;
		green = ( green + other.green ) / 2.0;
		revitalise();
	}
	
	public void revitalise()
	{
		vitality = MAX_VITALITY;
		tempInactive = false;
		inactive = false;
	}
	
	public String toString()
	{
		return "x:" + xpos + ", y:" + ypos + ", r: " + red + ", b:" + blue + ", g:" + green;
	}
	
	public String stringify()
	{
		return xpos + ":" + ypos + ": " + red + ":" + green + ":" + blue;
	}
	
}
