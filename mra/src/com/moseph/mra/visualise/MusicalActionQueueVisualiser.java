package com.moseph.mra.visualise;

import java.awt.*;
import java.util.List;
import java.util.Vector;

import javax.swing.*;

import com.moseph.mra.*;
import com.moseph.mra.acts.*;
import com.moseph.mra.agent.MusicianInformation;
import com.moseph.mra.symbolic.NumericTreeValue;
import com.moseph.mra.visualise.ChannelVisualiser.EventDisplay;
import com.moseph.mra.visualise.SpanContainerVisualiser.SpanDisplay;
import static com.moseph.mra.symbolic.Relationship.*;

public class MusicalActionQueueVisualiser extends ChannelVisualiser {
	ActQueue actQueue = (ActQueue)data;
	
	public static void main( String[] args )
	{
		MusicianInformation mus = new MusicianInformation( new Musician("me"));
		ActQueue acts = new ActQueue( mus );
		//acts.addAction( new MusicalAction( new NumericTreeValue( "a", 1.0 ), SUBSUMES, SUBSUMED, 0.5 ) );
		//acts.addAction( new MusicalAction( new NumericTreeValue( "b", 1.0 ), SUBSUMES, SUBSUMED, 2.5 ) );
		//acts.addAction( new MusicalAction( new NumericTreeValue( "c", 1.0 ), SUBSUMES, SUBSUMED, 3.5 ) );
		//acts.addAction( new MusicalAction( new NumericTreeValue( "d", 1.0 ), SUBSUMES, SUBSUMED, 4.5 ) );
		//acts.addAction( new MusicalAction( new NumericTreeValue( "e", 1.0 ), SUBSUMES, SUBSUMED, 6.5 ) );
		acts.addAction( new MusicalAction( mus, new NumericTreeValue( "b", 1.0 ), SUBSUMES, SUBSUMED, 114.5 ) );
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		MusicalActionQueueVisualiser vis = new MusicalActionQueueVisualiser();
		vis.setData( acts );
		frame.add( vis );
		frame.setVisible( true );
		vis.update();
		vis.revalidate();
	}
	
	void dataFromObject()
	{
		setLayout( new BorderLayout() );
		if( data == null | !(data instanceof Channel)) return;
		actQueue = (ActQueue)data;
		display = new ActionQueueDisplay( length, actQueue.getEvents() );
		add( display, BorderLayout.CENTER );
		revalidate();
	}
	
	public void update()
	{
		display.setContent( new Vector<TemporalEvent>( actQueue.getEvents() ) );
		setLength( actQueue.getLength() );
	}
	
	public ActQueue getQueue()
	{
		return actQueue;
	}
	
	
	class ActionQueueDisplay extends EventDisplay
	{
		int eventHeight = 15;
		int yIndex = 0;
		Dimension forceDimension = null;
		
		public ActionQueueDisplay() { super(); }
		public ActionQueueDisplay( double length, List<MusicalAction> spans )
		{
			super( length, new Vector<TemporalEvent>( spans ));
		}
		
		/*
		int getWindowHeight()
		{
			return eventHeight * 5;
		}
		*/
		
		public void paint( Graphics g )
		{
			yIndex = 0;
			super.paint( g );
		}
		
		void renderTemporalEvent( Graphics g, TemporalEvent sp )
		{
			if( !( sp instanceof MusicalAction )){ super.renderTemporalEvent(g, sp); return; }
			MusicalAction s = (MusicalAction)sp;
			int startX = timeToPosition( s.getOnset() );
			int startY = getDisplayHeight() - yIndex * eventHeight - RULERHEIGHT;
			int width = 2;
			g.setColor( Color.red );
			g.fillRect( startX, startY - eventHeight, width, eventHeight );
			g.setColor( Color.black );
			g.drawString( s.getRSelf() + ":" + s.getROther() + " " + s.getShortContentString(), startX + 4, startY );
			yIndex = ( yIndex + 1 ) % 3;
		}
		
		//Aaargh, ugly, ugly hack!
		public Dimension getPreferredSize()
		{
			if( forceDimension != null ) return forceDimension;
			return super.getPreferredSize();
		}
		
		public void setPreferredSize( Dimension d )
		{
			forceDimension = d;
			super.setPreferredSize( d );
		}
	}

}
