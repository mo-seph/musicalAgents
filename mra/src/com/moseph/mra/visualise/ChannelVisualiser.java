package com.moseph.mra.visualise;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.moseph.mra.Channel;
import com.moseph.mra.Span;
import com.moseph.mra.TemporalEvent;

import static com.moseph.mra.MRAUtilities.*;

public class ChannelVisualiser extends UnitVisualiser
{
	double length = 10.0;
	EventDisplay display;

	public ChannelVisualiser()
	{
		super();
	}
	
	
	public ChannelVisualiser( double length )
	{
		this();
		this.length = length;
	}
	
	public void setLength( double length )
	{
		//System.out.println( "ChannelVisualiser setting length to " + length );
		this.length =length;
		if( display != null ) display.setLength( length );
		revalidate();
	}
	
	void dataFromObject()
	{
		if( data == null | !(data instanceof Channel)) return;
		Channel channel = (Channel)data;
		display = new EventDisplay( length, channel.getEvents() );
		add( display );
		/*
		Box b = new Box( BoxLayout.X_AXIS );
		if( channel.getAttributes().size() > 0 ) b.add( getListPanel( channel.getAttributes(), "Attributes"));
		JComponent p = getListPanel( channel.getUnits(), null, BoxLayout.X_AXIS);
		p.setBackground( Color.red );
		p.setOpaque( true );
		b.add( p );
		add( b );
		*/
	}
	
	public void fitToWindow()
	{
		display.fitToWindow();
	}
	
	public void setMinimumSize( Dimension d )
	{
		if( display != null ) display.setMinimumSize( d );
		super.setMinimumSize( d );
	}
	public void setPreferredSize( Dimension d )
	{
		if( display != null ) display.setPreferredSize( d );
		super.setPreferredSize( d );
	}
	
	class EventDisplay extends JPanel
	{
		public static final int DISPLAYHEIGHT = 60;
		public static final int RULERHEIGHT = 20;
		public static final int EVENTSIZE = 10;
		double length = 10.0;
		double zoom = 100.0;
		final Color DEFAULT_BACKGROUND = Color.LIGHT_GRAY;
		final Color DEFAULT_BORDER = Color.DARK_GRAY;
		List<TemporalEvent> events = new Vector<TemporalEvent>();
		
		double xscale;
		
		public EventDisplay() 
		{ 
			super(); 
			setAlignmentX(Component.LEFT_ALIGNMENT ); 
		}
		
		
		public EventDisplay( double length, List<TemporalEvent> evt )
		{
			this();
			this.length = length;
			setContent( evt );
		}
		
		public void setLength( double length )
		{
			this.length = length;
		}
		
		public void fitToWindow()
		{
			zoom =  (double)getWidth() / length;
		}
		
		public void setContent( List<TemporalEvent> events )
		{
			this.events = events;
		}
		public void paint( Graphics g )
		{		
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			drawRuler( g );

			g.setColor( Color.BLACK );
			g.drawRect( timeToPosition(0.0), RULERHEIGHT, lengthToWidth( length ), getWindowHeight() );
			for( TemporalEvent event : events )
			{
				renderTemporalEvent( g, event );
			}
			
		}
		
		void drawRuler( Graphics g )
		{
			double tickTiming = 10.0;
			int TICKHEIGHT = RULERHEIGHT / 2;
			int MINITICKHEIGHT = TICKHEIGHT / 2;
			int miniTickTiming = lengthToWidth( tickTiming / 4 );
			for( double tick = 0.0; tick <= length; tick += tickTiming )
			{
				int pos = timeToPosition( tick );
				g.drawLine( pos, TICKHEIGHT, pos, TICKHEIGHT * 2 );
				g.drawString( formatBeat( tick ), pos, TICKHEIGHT );
				pos += miniTickTiming;
				g.drawLine( pos, TICKHEIGHT + MINITICKHEIGHT, pos, TICKHEIGHT * 2 );
				pos += miniTickTiming;
				g.drawLine( pos, TICKHEIGHT + MINITICKHEIGHT / 2, pos, TICKHEIGHT * 2 );
				pos += miniTickTiming;
				g.drawLine( pos, TICKHEIGHT + MINITICKHEIGHT, pos, TICKHEIGHT * 2 );
				
			}
		}
		
		void renderTemporalEvent( Graphics g, TemporalEvent s )
		{
			int startX = timeToPosition( s.getOnset() ) - EVENTSIZE;
			g.setColor( getEventFiller( s ) );
			g.fillRect( startX, RULERHEIGHT, 2 * EVENTSIZE, getWindowHeight() );
			g.setColor( getEventBorder( s ) );
			g.drawRect( startX, RULERHEIGHT, 2 * EVENTSIZE, getWindowHeight() );
			g.setColor( Color.BLACK );
			g.drawString( s.getContentString(), startX + 3, DISPLAYHEIGHT - 4 );
			
		}
		
		Color getEventFiller( TemporalEvent e )
		{
			return DEFAULT_BACKGROUND;
		}
		
		Color getEventBorder( TemporalEvent e )
		{
			return DEFAULT_BORDER;
		}
		
		int timeToPosition( double time )
		{
			return lengthToWidth( time ) + EVENTSIZE;
		}
		
		int lengthToWidth( double time )
		{
			return (int)( time * zoom);
		}
		
		public Dimension getMaximumSize() { return getPreferredSize(); }
		public Dimension getMinimumSize() { return getPreferredSize(); }
		public Dimension getPreferredSize()
		{
			return new Dimension( getDisplayWidth(), getDisplayHeight() );
		}
		
		int getWindowHeight()
		{
			return DISPLAYHEIGHT;
		}
		
		int getWindowWidth()
		{
			return (int)( length * zoom);
		}
		int getDisplayWidth()
		{
			return getWindowWidth() + 5 * EVENTSIZE;
		}
		
		int getDisplayHeight()
		{
			return getWindowHeight() + 2 + RULERHEIGHT;
		}
	}


}
