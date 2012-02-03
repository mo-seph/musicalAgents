package com.moseph.mra.test;

import jade.Boot;
import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.*;

import java.util.*;

import javax.sound.midi.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.run.AgentRunner;
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
public class RecordAgentTest extends TestCase
{

	BasicSequencerThread seq;
	SongInfo info;
	Fragment mainOutput;
	Fragment piecewiseOutput = new Fragment();
	List<Fragment> bits = new Vector<Fragment>();
	RealtimeTrackToFragment rtf;
	Sequencer sequencer;
	Sequence sequence;
	FragmentAdaptor adaptor;
	boolean testUniqueNoteEnds = false;
	boolean testNoHanging = false;
	Track track;
	double beatsToRun = 0.0;
	double currentPosition = 0.0;
	double fragmentSize = 1.0;
	double offset = 0.7;
	MetaMessage schedMessage;
	boolean noteMessages = false;
	boolean messageMessages = false;
	
	public void setUp()
	{
		Conductor.setStoreOutput( true );
		BasicSequencerThread.setScaleVelocity( false );
		//Create agent arguments which load the record test piece
		info = new SongInfo( new TimeSignature( 4,4 ), 120.0f, 384 );
		try
		{
			adaptor = new FragmentAdaptor( null, info, new Fragment() );
		} catch ( Exception e )
		{
			System.out.println( "Couldn't make sequencer for FragmentAdaptor: " + e );
			e.printStackTrace();
		}
		
	}
	
	public void atestConstantNotesB()
	{
		fragmentSize = 0.70;
		TimerTask[] tasks = { getNoteOnTask( adaptor, 60), getNoteOffTask( adaptor, 60) };
		long[][] timings = { { 0, 600 }, { 450, 600 } };
		TaskGen g = new TaskGen( sequencer, tasks, timings );
		test( 10, g );
	}
	
	public void testRandomPitchOverlappingNotes()
	{
		TimerTask[] tasks = 
		{
			getRandomNoteTask( adaptor, sequencer ),
			getRandomNoteTask( adaptor, sequencer ),
			getRandomNoteTask( adaptor, sequencer ),
			getRandomNoteTask( adaptor, sequencer ),
			getRandomNoteTask( adaptor, sequencer ),
			getRandomNoteTask( adaptor, sequencer ),
			getRandomNoteTask( adaptor, sequencer ) 
		};
		long[][] timings = { { 0, 200 }, { 25, 190 }, { 50, 100 }, { 70, 1000 }, { 30, 1500 }, {500,30}, {10, 126} };
		TaskGen g = new TaskGen( sequencer, tasks, timings );
		test( 18, g );
	}
		
	void setupSequencer()
	{
	}
	
	void test( double time, TaskGen generator )
	{
		beatsToRun = time;
		//Boot the system
		BasicSequencerThread.setConnected( false );
		List<String> agentDefs = AgentRunner.getStartingAgentDefinitions( "examples/recordTest.mra", false, false, new String[] { "NoDeviceRequest=true", "FragmentSize=" + fragmentSize } );
		Boot.main( (String[]) agentDefs.toArray( new String[agentDefs.size()]) );
		ContainerController controller = Runtime.instance().createAgentContainer( new ProfileImpl() );
		try
		{
			RecordAgentDetails recDetails = new RecordAgentDetails();
			recDetails.setName( "recordAgent" );
			recDetails.setInfo( info );
			while( ! BasicSequencerThread.isDoneInit() )
			{
				try
				{
					Thread.sleep( 100 );
				} catch (InterruptedException e)
				{
					System.out.println( "Couldn't sleep! " + e );
				}
						
			}
			sequencer = BasicSequencerThread.getSequencerDevice();
			sequence = sequencer.getSequence();
			track = sequence.createTrack();
			sequencer.recordEnable( track, -1 );
			sequencer.recordEnable( sequence.getTracks()[0], -1 );
			adaptor.setReceiver( sequencer.getReceiver() );
			recDetails.setAdaptor( adaptor );
			recDetails.setX( 0.0 );
			recDetails.setY( 0.0 );
			recDetails.setIgnoreNoInputDevice( true );
			AgentController a = controller.createNewAgent( "recordAgent", "com.moseph.mra.agent.RecordAgent", new Object[] { recDetails } );
			a.start();
		} catch( Exception e )
		{
			System.out.println( "Could not create Record Agent! " + e );
			e.printStackTrace();
		}
		
		//Create a record agent as part of the system, hooked up to our generators
		
		
		generator.run();
		
		try
		{
			long sleepTime = (long)( ( time / 2 + 1 ) * 1000 );
			Thread.sleep( sleepTime );
		} catch (InterruptedException e)
		{
			System.out.println( "Couldn't sleep! " + e );
		}
		endOfRun();
	}
	
	void endOfRun()
	{
		if( sequencer == null ) sequencer = BasicSequencerThread.getSequencerDevice();
		sequencer.stop();
		Score s = Conductor.getCurrentScore();
		double offset = RecordAgent.getPasteOffset();
		
		//Get the output that the agent produced
		if( s.getFragments().size() < 1 ) fail( "No output was found!" );
		Fragment agentOutput = s.getFragments().get(  0 ).copyChunk( offset, beatsToRun );
		agentOutput.setMusician( null );
		agentOutput.setInstrument( null );
		System.out.println( "+++ Recorded:\n----------\n\n" + agentOutput);
		
		//And the output the sequencer recorded
		Fragment recordedOutput = new Fragment();
		TrackToFragment ttf = new TrackToFragment( track, info, recordedOutput );
		ttf.addToFragment();
		recordedOutput = recordedOutput.copyChunk( 0.0, beatsToRun - offset );
		System.out.println( "++++ Fragment:\n----------\n\n" + recordedOutput );
		
		//And compare them!
		assertEquals( recordedOutput, agentOutput );
		
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
			java.util.Timer t = new java.util.Timer();
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
					if( noteMessages ) System.out.println( "Note on " + info.getTickBeat( sequencer.getTickPosition() ) );
					return noteOff( 0, pitch, 100 ); 
				}
				else
				{
					on = true;
					if( noteMessages ) System.out.println( "Note on " + info.getTickBeat( sequencer.getTickPosition() ) );
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
				if( noteMessages ) System.out.println( "Note on " + info.getTickBeat( sequencer.getTickPosition() ) );
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
				if( noteMessages ) System.out.println( "Note off " + info.getTickBeat( sequencer.getTickPosition() ) );
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
				if( messageMessages ) System.out.println( "Sent message: " + msg + " at " + info.getTickBeat( sequencer.getTickPosition() ) );
			}
			catch( Exception e )
			{
				System.err.println( "Couldn't send message!" + e );
				e.printStackTrace();
			}
		}
	}

	
	
}
