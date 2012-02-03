package com.moseph.mra.test;

import java.util.*;

import javax.sound.midi.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.midi.*;
import static com.moseph.mra.midi.MidiUtilities.*;
import static java.lang.Math.*;

import junit.framework.TestCase;

/**
 * This is a roundtrip test to test the incremental recording system, in a similar manner to that used in the real
 * application.
 * @author dave
 *
 */
public class RealtimeTrackToFragmentTest extends TestCase implements MetaEventListener
{

	BasicSequencerThread seq;
	SongInfo info;
	Fragment mainOutput;
	RealtimeTrackToFragment rtf;
	Sequencer sequencer;
	Sequence sequence;
	FragmentAdaptor adaptor;
	boolean testUniqueNoteEnds = false;
	boolean testNoHanging = false;
	Track track;
	double beatsToRun = 0.0;
	
	public void setUp()
	{
		info = new SongInfo( new TimeSignature( 4, 4 ), 120, 384 );
		mainOutput = new Fragment();
		setupSequencer();
	}
	
	public void testConstantNotes()
	{
		TimerTask[] tasks = { getNoteOnTask( adaptor, 60), getNoteOffTask( adaptor, 60) };
		long[][] timings = { { 0, 200 }, { 150, 200 } };
		setTestNoHanging( true );
		setTestUniqueNoteEnds( true );
		TaskGen g = new TaskGen( sequencer, tasks, timings );
		test( 3, g );
	}
	
	public void testRandomPitchOverlappingNotes()
	{
		TimerTask[] tasks = 
		{
			getRandomNoteTask( adaptor, sequencer ),
			getRandomNoteTask( adaptor, sequencer ),
			getRandomNoteTask( adaptor, sequencer ),
			getRandomNoteTask( adaptor, sequencer ) 
		};
		long[][] timings = { { 0, 200 }, { 25, 190 }, { 50, 100 }, { 70, 1000 } };
		TaskGen g = new TaskGen( sequencer, tasks, timings );
		test( 5, g );
	}
	
	void setupSequencer()
	{
		try
		{
			sequencer = MidiSystem.getSequencer();
			sequencer.setTempoInBPM( 120 );
			sequence = new Sequence( Sequence.PPQ, 384 );
			sequencer.setSequence( sequence );
			
			adaptor = new FragmentAdaptor( sequencer.getReceiver(), info, mainOutput, sequencer );
			track = sequence.createTrack();
			sequencer.recordEnable( track, -1 );
			sequencer.setTrackMute( 0, true );
			sequencer.open();
		}
		catch( Exception e )
		{
			System.out.println( "Could not setup sequencer!" + e );
			e.printStackTrace();
		}
	}
	
	void test( double time, TaskGen generator )
	{
		beatsToRun = time;
		double seconds = beatsToRun / 2 + 1.0;
		long sleepTime = (long)( seconds * 1000 );
		System.out.println( "Starting sequencer for " + time + " beats");
		MetaMessage schedMessage = new MetaMessage();
		try
		{
			schedMessage.setMessage( 127, new byte[] { 1 }, 1 );
		} catch (InvalidMidiDataException e)
		{
			System.out.println( "Couldn't make stop message: " + e );
		}
		track.add( new MidiEvent( schedMessage, info.getBeatTick( time ) ) );
		sequencer.startRecording();
		//sequencer.addMetaEventListener( this );
		System.out.println( "Sequencer started");
		generator.run();
			try
			{
				Thread.sleep( sleepTime );
			} catch (InterruptedException e)
			{
				System.out.println( "Couldn't sleep! " + e );
			}
		//waitForEnter();
		endOfRun();
		
	}
	
	void endOfRun()
	{
		System.out.println( "Sequencer end: " + sequencer.getTickLength() );
		
		Fragment recorded = new Fragment();
		TrackToFragment ttf = new TrackToFragment( track, info, recorded );
		ttf.addToFragment();
		recorded = recorded.copyChunk( 0.0, beatsToRun );
		
		mainOutput = mainOutput.copyChunk( 0.0, beatsToRun );
		System.out.println( "Finished Run!" );
		
		if( testUniqueNoteEnds )
		{
			HashSet ends = new HashSet<Double>();
			for( Note n : recorded.getNotes() ) ends.add( n.getEndTime() );
			assertTrue( "Not all note ends are unique in piecewise!", ends.size() == recorded.getNotes().size() );
			ends.clear();
			for( Note n : mainOutput.getNotes() ) ends.add( n.getEndTime() );
			assertTrue( "Not all note ends are unique in main! (Indicates some have been left hanging!", ends.size() == mainOutput.getNotes().size() );
		}
		if( testNoHanging )
		{
			for( Note n : recorded.getNotes() )
				assertFalse( "Piecewise output has hanging notes", n.getLongerThan() );
			for( Note n : mainOutput.getNotes() )
				assertFalse( "Main output has hanging notes", n.getLongerThan() );
		}
		
		
		//mainOutput = trackToFragment.getChunk( 0.0, trackToFragment.gotTill() );
		System.out.println( "Main output: " + mainOutput );
		System.out.println( "From Track: " + recorded );
		assertEquals( recorded, mainOutput );
		System.out.println( "Test OK!");
	}
	
	
	public void meta( MetaMessage meta )
	{
		System.out.println( "Meta!");
		sequencer.stop();
		endOfRun();
	}

	
	class TaskGen implements Runnable
	{
		TimerTask[] tasks;
		final Sequencer seq;
		long[][] onsets;
		public TaskGen( Sequencer seq, TimerTask[] tasks, long[][] onsets )
		{
			this.seq = seq;
			this.tasks = tasks;
			this.onsets = onsets;
		}
		
		public void run()
		{
			Timer t = new Timer();
			for( int i = 0; i < tasks.length; i++ )
			{
				t.schedule( tasks[i], onsets[i][0], onsets[i][1] );
			}
		}
	}
	
	
	TimerTask getRandomNoteTask( Receiver recv, final Sequencer sequencer )
	{
		return new NoteTask( recv )
		{
			int pitch;
			boolean on;
			ShortMessage getMessage() 
			{ 
				if( on )
				{
					on = false;
					//System.out.println( "Note on " + info.getTickBeat( sequencer.getTickPosition() ) );
					return noteOff( 0, pitch, 100 ); 
				}
				else
				{
					on = true;
					//System.out.println( "Note on " + info.getTickBeat( sequencer.getTickPosition() ) );
					pitch = (int)(random() * 24 ) + 60;
					return noteOn( 0, pitch, 100 ); 
				}
			}
		};
	}
	
	TimerTask getNoteOnTask( Receiver recv, final int pitch)
	{
		return new NoteTask( recv )
		{
			ShortMessage getMessage() 
			{
				System.out.println( "Note on " + info.getTickBeat( sequencer.getTickPosition() ) );
				return noteOn( 0, pitch, 100 ); 
			}
		};
	}
	TimerTask getNoteOffTask( Receiver recv, final int pitch)
	{
		return new NoteTask( recv )
		{
			ShortMessage getMessage() 
			{ 
				//System.out.println( "Note off " + info.getTickBeat( sequencer.getTickPosition() ) );
				return noteOff( 0, pitch, 100 ); 
			}
		};
	}
	
	abstract class NoteTask extends TimerTask
	{
		Receiver rec = null;
		boolean OK = true;
		
		public NoteTask( Receiver recv )
		{
			rec = recv;
			if( rec == null ) System.out.println( "Null receiver given!");
		}
		
		abstract ShortMessage getMessage();
		public void run()
		{
			try
			{
				ShortMessage msg = getMessage();
				rec.send( msg, -1 );
				//System.out.println( "Sent message: " + msg + " at " + info.getTickBeat( sequencer.getTickPosition() ) );
			}
			catch( Exception e )
			{
				System.err.println( "Couldn't send message!" + e );
			}
		}
	}

	public boolean isTestNoHanging()
	{
		return testNoHanging;
	}

	public void setTestNoHanging( boolean testNoHanging )
	{
		this.testNoHanging = testNoHanging;
	}

	public boolean isTestUniqueNoteEnds()
	{
		return testUniqueNoteEnds;
	}

	public void setTestUniqueNoteEnds( boolean testUniqueNoteEnds )
	{
		this.testUniqueNoteEnds = testUniqueNoteEnds;
	}
	
	
	
}
