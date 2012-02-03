package com.moseph.mra.visualise;

import java.awt.*;

import javax.swing.*;

import com.moseph.mra.*;
import static java.lang.Math.*;

public class FragmentDisplay extends TemporalDisplay
{
	Fragment fragment;
	double forceWidth = 0.0;
	
	public static void main( String[] args )
	{
		JFrame f = new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.add( getExample() );
		f.setVisible( true );
	}
	
	public FragmentDisplay(){ super(); forceShowZero = false;};
	
	public FragmentDisplay( Fragment c )
	{
		this();
		this.fragment = c;
	}
	
	public void setFragment( Fragment c )
	{
		this.fragment = c;
		repaint();
	}
	
	public void paint( Graphics g )
	{
		initialiseGraphics( g );
		if( fragment == null ) return;
		double end = fragment.getLength();
		if( forceWidth > 0 )
		{
			//System.out.println( "(*(*&(*&(*& Width forced to : " + forceWidth );
			end = forceWidth;
		}
		double startVal = 0.0;
		double maxVal =  64.0;
		double minVal =  56.0;
		
		for( Note n : fragment.getNotes() )
		{
			maxVal = max( maxVal, (double)n.getPitchNumber() );
			minVal = min( minVal, (double)n.getPitchNumber() );
		}
		setupDrawing( g, maxVal, minVal, startVal, end );
		
		//Figure out the y height for zero, and the scaling
		
		//Figure out the width of bars
		drawYLabel( g, min );
		drawYLabel( g, 0.0 );
		drawYLabel( g, max );
		drawXLabel( g, 0.0 );
		drawXLabel( g, end );
		drawXBeats( g, 0.0, end, 10.0 );
		
		for( Note n : fragment.getNotes() )
		{
			int xstart = getXpos( n.getOnset() );
			int xend = getXpos( n.getEndTime() );
			int ystart = getYpos( (double )n.getPitchNumber() - 1 );
			int yend = getYpos( (double )n.getPitchNumber() );
			int width = xend - xstart;
			int height = ystart - yend;
			g.setColor( getNoteColor( n ) );
			g.fillRect( xstart, ystart, width, height );
			//System.out.println( "Drawing note: " + n );
			//System.out.println( "xs: " + xstart + ", ys" + ystart + ", h: " + height + ", w: " + width );
		}
		drawXAxis( g );
		
	}
	
	Color getNoteColor( Note n )
	{
		double vel = n.getVelocity();
		float r = (float)vel;
		float b = 1 - (float) vel;
		return new Color( r, 0.0f, b );
	}
	
	public static JComponent getExample()
	{
		FragmentDisplay f= new FragmentDisplay( Fragment.getExampleFragment() );
		System.out.println( Fragment.getExampleFragment() );
		f.setFragment( Fragment.getExampleFragment() );
		return f;
	}

	public double getForceWidth()
	{
		return forceWidth;
	}

	public void setForceWidth( double forceWidth )
	{
		this.forceWidth = forceWidth;
	}
}
