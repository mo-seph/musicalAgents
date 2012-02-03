package com.moseph.mra.visualise;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Vector;

import com.moseph.mra.Channel;
import com.moseph.mra.Span;
import com.moseph.mra.TemporalEvent;
import com.moseph.mra.visualise.ChannelVisualiser.EventDisplay;

public class SpanContainerVisualiser extends ChannelVisualiser {
	
	
	void dataFromObject()
	{
		if( data == null | !(data instanceof Channel)) return;
		Channel channel = (Channel)data;
		display = new SpanDisplay( length, channel.getEvents() );
		add( display );
	}
	
	class SpanDisplay extends EventDisplay
	{
		public SpanDisplay() { super(); }
		public SpanDisplay( double length, List<Span> spans )
		{
			super( length, new Vector<TemporalEvent>( spans ));
		}
		
		void renderTemporalEvent( Graphics g, TemporalEvent sp )
		{
			if( !( sp instanceof Span )){ super.renderTemporalEvent(g, sp); return; }
			Span s = (Span)sp;
			int startX = timeToPosition( s.getOnset() );
			int width = lengthToWidth( s.getDuration() );
			if( width < 5 ) width = 5;
			g.setColor( getEventFiller( s ) );
			g.fillRect( startX, RULERHEIGHT, width, getWindowHeight() );
			g.setColor( getEventBorder( s ) );
			g.drawRect( startX, RULERHEIGHT, width, getWindowHeight() );
			g.setColor( Color.BLACK );
			g.drawString( s.getContentString(), startX + 3, DISPLAYHEIGHT - 4 );
			
		}
	}

}
