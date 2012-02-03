package com.moseph.mra.visualise;

import java.awt.*;
import java.util.TimerTask;

import javax.swing.*;
import static java.lang.Math.*;

import com.moseph.mra.agent.attribute.*;

public class PatternDisplay extends TemporalDisplay
{
	PatternAttribute pattern;
	double scaleFactor = 1.0;
	DisplayableAttribute curveCompensate = new DisplayableAttribute();
	
	public static void main( String[] args )
	{
		JFrame f = new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.add( getExample() );
		f.setVisible( true );
	}
	
	public PatternDisplay() 
	{
		curveCompensate.name = "CC";
		curveCompensate.value = "true";
		displayAttrs.add( curveCompensate );
	}
	
	public PatternDisplay( double scale ) 
	{
		this();
		this.scaleFactor = scale;
	}
	
	public PatternDisplay( PatternAttribute pattern )
	{
		this();
		this.pattern = pattern;
	}
	
	public void setPattern( PatternAttribute p )
	{
		this.pattern = p;
		invalidate();
		repaint();
	}
	
	
	@Override
	public void forceMax( double max )
	{
		super.forceMax( max * scaleFactor );
	}

	@Override
	public void forceMin( double min )
	{
		super.forceMin( min * scaleFactor );
	}

	public void paint( Graphics g )
	{
		initialiseGraphics( g );
		if( pattern == null ) return;
		double maxBeat = pattern.getBeats();
		double delta = 1.0 / pattern.getQuantise();
		double[] range = pattern.getRange();
		
		for( double beat = 0; beat < maxBeat; beat += delta )
		{
			range[1] = max( range[1], pattern.getValue( beat ));
			range[0] = min( range[0], pattern.getValue( beat ));
		}
		setupDrawing( g, range[1] * scaleFactor, range[0] * scaleFactor, 0.0, pattern.getBeats() );
		
		drawXAxis( g );
		
		//Figure out the width of bars
		int barSize = (int)(xscale / pattern.getQuantise() );
	
		int barWidth = barSize - 3;
		if( barWidth < 2 ) barWidth = 2;
		drawYLabel( g, min );
		drawYLabel( g, max );
		
		drawXBeats( g, 0.0, maxBeat );
		
		for( double beat = 0; beat < maxBeat; beat += delta )
		{
			g.setColor( Color.GRAY );
			int val = -(int)( pattern.getValue( beat ) * yscale * scaleFactor );
			if( val < 0 )
				g.fillRect( getXpos( beat ), yZero + val, barWidth, -val );
			else	
				g.fillRect( getXpos( beat ), yZero, barWidth, val );
			
			g.setColor( Color.RED );
		}
		//drawAttributes( g );
	}
	
	public static JComponent getExample()
	{
		Box b = new Box( BoxLayout.Y_AXIS);
		PatternAttribute p = new PatternAttribute( 2.0, 4.0 );
		for( double i = 0.0; i < 2.0; i += 0.25 )
			p.setValue( i, new ValuedAttribute( i - 1 ) );
		b.add( new PatternDisplay( p ) );
		
		p = new PatternAttribute( 2.0, 4.0 );
		for( double i = 0.0; i < 2.0; i += 0.25 )
			p.setValue( i, new ValuedAttribute( random() ) );
		b.add( new PatternDisplay( p ) );
		
		p = new PatternAttribute( 12.0, 4.0 );
		for( double i = 0.0; i < 12.0; i += 0.25 )
			p.setValue( i, new ValuedAttribute( ( random() - 0.5 ) * 2000 ) );
		b.add( new PatternDisplay( p ) );
		
		p = new PatternAttribute( 2.0, 4.0 );
		for( double i = 0.0; i < 12.0; i += 0.25 )
			p.setValue( i, new ValuedAttribute( ( random() - 0.8 ) * 0.3 ) );
		PatternDisplay pd = new PatternDisplay(p);
		//pd.forceMin( 0.0 );
		//pd.forceMax( 1.0 );
		b.add( pd );
		
		
		final PatternDisplay pr = new PatternDisplay( null );
		b.add(  pr );
		TimerTask t = new TimerTask()
		{
			public void run()
			{
				PatternAttribute pa = new PatternAttribute( 140.0, 4.0 );
				for( double i = 0.0; i < 140.0; i += 0.25 )
					pa.setValue( i, new ValuedAttribute( ( random() - 0.5 ) * 20 ) );
				pr.setPattern( pa );
			}
		};
		java.util.Timer timer = new java.util.Timer();
		timer.schedule( t, 200, 200 );
		return b;
	}
	
	public void setIsCurveCompensate( boolean is )
	{
		curveCompensate.value = is + "";
	}
	
	protected void drawYLabel( Graphics g, double value )
	{
		//System.out.println( "Drawing " + value + " at " + yval + " yscale: " + yscale + " yzero: " + yZero );
		g.drawString( ( value / scaleFactor ) + "", 0, getYpos( value) + 5 );
	}
}
