package com.moseph.mra.visualise;

import java.awt.*;
import java.util.TimerTask;

import javax.swing.*;

import com.moseph.mra.CurveContainer;
import static java.lang.Math.*;

public class CurveDisplay extends TemporalDisplay
{
	CurveContainer curve;
	
	public static void main( String[] args )
	{
		JFrame f = new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.add( getExample() );
		f.setVisible( true );
	}
	
	public CurveDisplay(){};
	
	public CurveDisplay( CurveContainer c )
	{
		this.curve = c;
	}
	
	public void setCurve( CurveContainer c )
	{
		this.curve = c;
		repaint();
	}
	
	public void paint( Graphics g )
	{
		initialiseGraphics( g );
		if( curve == null ) return;
		double end = curve.getLength();
		double startVal = curve.sample( 0.0 );
		double endVal = curve.sample( end );
		double maxVal =  max( startVal, endVal );
		double minVal =  min( startVal, endVal );
		setupDrawing( g, maxVal, minVal, 0.0, end );
		
		//Figure out the y height for zero, and the scaling
		
		//Figure out the width of bars
		drawYLabel( g, min );
		drawYLabel( g, 0.0 );
		drawYLabel( g, max );
		drawYLabel( g, curve.sample( 0.0 ));
		drawYLabel( g, curve.sample( end ));
		drawXLabel( g, 0.0 );
		drawXLabel( g, end );
		drawXBeats( g, 0.0, end );
		
		double[] samplePoints = { 0.0, end };
		Polygon polygon = new Polygon();
		polygon.addPoint( getXpos( 0.0 ), getYpos( 0.0 ) );
		for( double sample : samplePoints )
			polygon.addPoint( getXpos( sample ), getYpos( curve.sample( sample ) ));
		polygon.addPoint( getXpos( end ), getYpos( 0.0 ) );
		g.setColor( Color.orange );
		g.fillPolygon( polygon );
		
		drawXAxis( g );
		
	}
	
	public static JComponent getExample()
	{
		Box b = new Box( BoxLayout.Y_AXIS );
		CurveContainer cc = new CurveContainer( 2.0, -1.0, 1.0 );
		b.add( new CurveDisplay( cc ));
		cc = new CurveContainer( 12.0, 0.0, 10.0 );
		b.add( new CurveDisplay( cc ));
		cc = new CurveContainer( 12.0, 3.0, 10.0 );
		b.add( new CurveDisplay( cc ));
		cc = new CurveContainer( 12.0, -50.0, -10.0 );
		b.add( new CurveDisplay( cc ));
		final CurveDisplay cd = new CurveDisplay( cc );
		TimerTask tt = new TimerTask()
		{
			public void run()
			{
				cd.setCurve( new CurveContainer( 12.0, ( random() - 0.5 ) * 10.0, ( random() - 0.5 ) * 10.0 ) );
			}
		};
		java.util.Timer timer = new java.util.Timer();
		timer.schedule( tt, 200, 200 );
		b.add( cd );
		return b;
	}

}
