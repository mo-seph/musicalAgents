package com.moseph.mra.midi;

import java.util.*;
import static com.moseph.mra.midi.MidiUtilities.*;

import javax.sound.midi.*;

import com.moseph.mra.*;

/**
 * Reads information out of a Track, and returns it as Fragments
 * @author s0239182
 *
 */
public class IncrementalTrackToFragment
{
		Map<Integer,Vector<Note>> openNotes = new HashMap<Integer,Vector<Note>>();
		Track track;
		SongInfo songInfo;
		int lastEvent = 0;
		double currentPosition = 0.0;
		boolean hasMore = true;

		public IncrementalTrackToFragment( Track t, SongInfo s )
		{
			track = t;
			songInfo = s;
		}

		public Fragment getFragment( double length )
		{
			//System.out.println( "Track has " + track.size() + " events! asked for " + length + " beats" );
			Fragment f = new Fragment();
			int numEvents = track.size();
			long startTick = songInfo.getBeatTick( currentPosition );
			long endTick = songInfo.getBeatTick( currentPosition + length );
			long lastTick = track.ticks();
			//System.out.println( "Starting from " + startTick + " to " + endTick + " out of " + lastTick );
			if( startTick > lastTick ) hasMore = false;
			openHangingNotes( f );
			while( lastEvent < numEvents )
			{
				MidiEvent e = track.get( lastEvent );
				MidiMessage m = e.getMessage();
				if( e.getTick() >= endTick && ! ( e.getTick() == endTick  && isNoteOffMessage( m )) ) break;
				if( isNoteOnMessage( m ) )
					addNoteEvent( e, f, length, currentPosition );
				else if( isNoteOffMessage( m ) )
					closeNotes( e, currentPosition );
				lastEvent++;
			}
			currentPosition += length;
			//System.out.println( "Returning: " + f );
			return f;
		}
		
		public boolean hasMoreEvents()
		{
			return hasMore;
		}

		public boolean addNoteEvent( MidiEvent e, Fragment f, double length, double offset )
		{
			double noteBeat = songInfo.getEventBeat( e ) - offset;
			int noteNumber = ((ShortMessage)e.getMessage()).getData1();
			int noteVelocity = ((ShortMessage)e.getMessage()).getData2();
			double vel = (double)noteVelocity / 127;
			Note n = new Note( noteBeat, noteNumber, vel, length - noteBeat, false, true );
			n.setEvent( e );
			f.addNote( n );
			openNote( n );
			return true;
		}

		public void closeNotes( MidiEvent e, double offset )
		{
			ShortMessage sm = (ShortMessage)e.getMessage();
			//System.out.println( "Closing notes: " + sm.getData1() );
			Vector<Note> v = getNoteVector( sm.getData1() );
			for( Note n : v )
				closeNoteWithEvent( n, e, offset );
			v.clear();
		}

		public void openNote( Note n )
		{
			//System.out.println( "Opening note " + n.getPitchNumber() );
			getNoteVector( n.getPitchNumber() ).add( n );
		}
		
		Vector<Note> getNoteVector( int pitch )
		{
			if( openNotes.containsKey( pitch ) )
				return openNotes.get( pitch );
			Vector<Note> notes = new Vector<Note>();
			openNotes.put( pitch, notes );
			return notes;
		}

		void closeNoteWithEvent( Note n, MidiEvent e, double offset )
		{
			if( n.getStartEvent() != null )
			{
				double endTime = songInfo.getEventBeat( e ) - offset;
				double duration = ( endTime - n.getOnset() );
				n.close( duration );
			}
		}
		
		void truncateNotes( Fragment f, double length )
		{
			for( Vector<Note> notes : openNotes.values() )
				for( Note n : notes )
					n.setEndTime( length );
		}
		
		void openHangingNotes( Fragment f )
		{
			for( Vector<Note> notes : openNotes.values() )
				for( int i = 0; i < notes.size(); i++ )
				{
					Note n = notes.get( i );
					//System.out.println( "Opening hanging note " + n.getPitchNumber() );
					Note n2 = n.clone();
					n2.setOnset( 0.0 );
					n2.setStartsBefore( true );
					n2.setLongerThan( true );
					notes.set( i, n2 );
					f.addNote( n2 );
				}
			
		}
		
		public double gotTill()
		{
			return songInfo.getTickBeat( track.ticks() );
		}

		public double getCurrentPosition()
		{
			return currentPosition;
		}
		
		public Fragment getCompleteFragment()
		{
			return getFragment( gotTill() );
		}

}
