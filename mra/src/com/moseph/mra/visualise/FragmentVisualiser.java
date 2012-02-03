package com.moseph.mra.visualise;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;
import java.util.Vector;

import com.moseph.mra.Channel;
import com.moseph.mra.Fragment;
import com.moseph.mra.Note;
import com.moseph.mra.Span;
import com.moseph.mra.TemporalEvent;
import com.moseph.mra.visualise.ChannelVisualiser.EventDisplay;
import com.moseph.mra.visualise.SpanContainerVisualiser.SpanDisplay;

public class FragmentVisualiser extends SpanContainerVisualiser {
	
	void dataFromObject()
	{
		if( data == null | !(data instanceof Channel)) return;
		Fragment channel = (Fragment)data;
		display = new FragmentDisplay( length, channel.getEvents() );
		add( display );
	}
	
	class FragmentDisplay extends EventDisplay
	{
		int noteHeight = 4;
		
		public FragmentDisplay() { super(); }
		public FragmentDisplay( double length, List<Note> spans )
		{
			super( length, new Vector<TemporalEvent>( spans ));
		}
		
		int pitchToHeight( int pitch )
		{
			return ( 128 - pitch ) * noteHeight + RULERHEIGHT;
		}
		
		int getWindowHeight()
		{
			return noteHeight * 128;
		}
		
		void renderTemporalEvent( Graphics g, TemporalEvent sp )
		{
			if( !( sp instanceof Note )){ super.renderTemporalEvent(g, sp); return; }
			Note s = (Note)sp;
			int startX = timeToPosition( s.getOnset() );
			int startY = pitchToHeight( s.getPitchNumber() );
			int width = lengthToWidth( s.getDuration() );
			g.setColor( getEventFiller( s ) );
			g.fillRect( startX, startY, width, noteHeight );
			g.setColor( getEventBorder( s ) );
			g.drawRect( startX, startY, width, noteHeight );			
		}
	}

}
