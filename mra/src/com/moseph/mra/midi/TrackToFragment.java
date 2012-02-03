package com.moseph.mra.midi;

import java.text.DecimalFormat;
import java.util.*;
import static com.moseph.mra.midi.MidiUtilities.*;

import javax.sound.midi.*;

import com.moseph.mra.*;

/**
 * Reads information out of a Track, and returns it as Fragments
 * @author s0239182
 *
 */
public class TrackToFragment
{
		Map<Integer,Vector<Note>> openNotes = new HashMap<Integer,Vector<Note>>();
		Track track;
		SongInfo songInfo;
		int currentEvent = 0;
		static DecimalFormat df = new DecimalFormat( "00.000");
		Fragment fragment;

		public TrackToFragment( Track t, SongInfo s )
		{
			this( t, s, new Fragment() );
		}
		public TrackToFragment( Track t, SongInfo s, Fragment fragment )
		{
			this.track = t;
			this.songInfo = s;
			this.fragment = fragment;
		}

		public void addToFragment()
		{
			int numEvents = track.size();
			//openHangingNotes( fragment );
			System.out.println( "Adding to fragment from " + currentEvent + " to " + numEvents + " events" );
			while( currentEvent < numEvents )
			{
				try
				{
				MidiEvent e = track.get( currentEvent );
				//System.out.println( "Adding event at " + df.format( songInfo.getEventBeat( e ) ) + "<" + e.getTick() + ">: " + eventToString( e ) + "("+ currentEvent + ")" );
				MidiMessage m = e.getMessage();
				if( isNoteOnMessage( m ) )
				{
					addNoteEvent( e, fragment );
				}
				else if( isNoteOffMessage( m ) )
				{
					closeNotes( e );
				}
				currentEvent++;
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}
			System.out.println( "Done adding to fragment");
		}

		public boolean addNoteEvent( MidiEvent e, Fragment f )
		{
			double noteBeat = songInfo.getEventBeat( e );
			int noteNumber = ((ShortMessage)e.getMessage()).getData1();
			int noteVelocity = ((ShortMessage)e.getMessage()).getData2();
			double vel = (double)noteVelocity / 127;
			Note n = createNote( noteBeat, noteNumber, vel );
			n.setEvent( e );
			f.addNote( n );
			openNote( n );
			return true;
		}

		Note createNote( double noteBeat, int noteNumber, double vel )
		{
			return new Note( noteBeat, noteNumber, vel );
		}
		
		public void closeNotes( MidiEvent e )
		{
			ShortMessage sm = (ShortMessage)e.getMessage();
			//System.out.println( "Closing notes: " + sm.getData1() );
			int pitch = ((ShortMessage)e.getMessage()).getData1();
			Vector<Note> v = getNoteVector( sm.getData1() );
			for( Note n : v )
				if( n.getPitchNumber() == pitch )
				{
					closeNoteWithEvent( n, e );
					//System.out.println( "Closing note!");
				}
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

		void closeNoteWithEvent( Note n, MidiEvent e )
		{
			if( n.getStartEvent() != null )
			{
				double endTime = songInfo.getEventBeat( e );
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
		
		public Fragment getChunk( double start, double end )
		{
			System.out.println( "Getting chunk from " + start + " to " + end );
			return fragment.copyChunk( start, end );
		}
		
		public double beatLength()
		{
			return fragment.getLength();
		}

}
