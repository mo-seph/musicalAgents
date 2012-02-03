package com.moseph.mra.visualise;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

public class TemporalDisplay extends JPanel
{

	protected int labelOffset = 50;
	int bottomOffset = 20;
	int topOffset = 10;
	protected Dimension size;
	int xsize = 0;
	int ysize = 0;
	protected double max = 1.0;
	protected double min = 0.0;
	protected int yZero = 0;
	int xZero = 0;
	protected double yscale = 1.0;
	protected double xscale = 1.0;
	int border = 4;
	boolean forceShowZero = true;
	boolean keepBounds = true;
	boolean applyMinimums = true;
	double maxYmax = Double.MIN_VALUE;
	double minYmin = Double.MAX_VALUE;
	public List<DisplayableAttribute> displayAttrs = new Vector<DisplayableAttribute>();

	public void initialiseGraphics( Graphics g )
	{
		size = getSize();
		g.setColor( Color.LIGHT_GRAY );
		g.fillRect( 0, 0, size.width, size.height );
	}

	public void setupDrawing( Graphics g, double ymax, double ymin, double xmin, double xmax )
	{
		g.setColor( Color.DARK_GRAY );
		xsize = size.width - labelOffset - border;
		ysize = size.height - bottomOffset - topOffset - border;
		g.drawRect( labelOffset, topOffset, xsize, ysize );
		max = ceil( ymax);
		min = floor( ymin);
		if( forceShowZero ) min = min( min, 0.0 );
		if( forceShowZero ) max = max( max, 0.0 );
		if( applyMinimums )
		{
			max = max( max, maxYmax );
			min = min( min, minYmin );
			if( keepBounds )
			{
				maxYmax = max;
				minYmin = min;
			}
		}
		yZero = (int)( ysize * ( max / ( max - min )) ) + 1 + topOffset - border;
		xZero = (int)( xsize * ( xmin / ( xmax - xmin )) ) + 1 ;
		yscale = (double)ysize / ( max - min );
		xscale = xsize / ( xmax - xmin );
	}
	
	protected void drawXAxis( Graphics g )
	{
		g.setColor( Color.gray);
		g.drawLine( labelOffset, getYpos( 0.0 ), size.width - border, getYpos( 0.0 ) );
	}

	protected void drawYLabel( Graphics g, double value )
	{
		//System.out.println( "Drawing " + value + " at " + yval + " yscale: " + yscale + " yzero: " + yZero );
		g.drawString( value + "", 0, getYpos( value) );
	}
	
	protected int getYpos( double value )
	{
		int yval = (int)(yscale * value );
		yval = yZero - yval;// + border + border;
		return yval;
	}

	protected void drawXLabel( Graphics g, double value )
	{
		//int xval = (int)(xscale * value ) + xZero + labelOffset;
		//xval = xZero + xval +;
		//System.out.println( "Drawing " + value + " at " + xval + " xscale: " + xscale + " xzero: " + xZero );
		g.drawString( value + "", getXpos( value ), size.height );
	}
	
	protected void drawAttributes( Graphics g )
	{
		if( displayAttrs.size() == 0 ) return;
		g.setColor( Color.black );
		int step = 12;
		int offset = step;
		for( DisplayableAttribute da : displayAttrs )
		{
			g.drawString( da.name + ":" + da.value, xZero, offset );
		}
	}

	protected int getXpos( double value )
	{
		return (int)(xscale * value ) + xZero + labelOffset;
	}
	
	protected void drawXBeats( Graphics g, double start, double end )
	{
		drawXBeats( g, start, end, 1.0 );
	}
	protected void drawXBeats( Graphics g, double start, double end, double spacing )
	{
		for( double beat = start; beat < end; beat += spacing )
			drawXLabel( g, beat );
	}
	
	public static JComponent getExample()
	{
		return new JLabel( "Silly...");
	}

	public boolean isKeepBounds()
	{
		return keepBounds;
	}

	public void setKeepBounds( boolean keepBounds )
	{
		this.keepBounds = keepBounds;
	}
	
	public void forceMin( double min )
	{
		minYmin = min;
		applyMinimums = true;
	}
	public void forceMax( double max )
	{
		maxYmax = max;
		applyMinimums = true;
	}


	class  DisplayableAttribute
	{
		String name;
		String value;
	}
}
