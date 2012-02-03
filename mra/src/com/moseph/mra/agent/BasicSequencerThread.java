package com.moseph.mra.agent;

import static com.moseph.mra.MRAConstants.ROOM_X;

import static com.moseph.mra.MRAConstants.ROOM_Y;
import static com.moseph.mra.MRAUtilities.fuzzyCompare;
import static com.moseph.mra.MRAUtilities.log;
import static com.moseph.mra.midi.MidiUtilities.byteToString;
import static com.moseph.mra.midi.MidiUtilities.pan;
import static com.moseph.mra.midi.MidiUtilities.programChange;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.signum;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

import java.io.File;
import java.util.*;

import javax.sound.midi.*;

import com.moseph.mra.*;
import com.moseph.mra.Instrument;
import com.moseph.mra.midi.*;
import static com.moseph.mra.midi.MidiUtilities.*;

public class BasicSequencerThread implements Runnable
{

	public static final int FRAGMENT_DEADLINE_MESSAGE = 1;
	public static final int REQUEST_MUSIC_MESSAGE = 2;
	public static final int PROPRIETRY_MESSAGE = 127;
	final boolean AVOID_PERC_CHANNEL = true;
	FragmentToTrack writer;
	double currentPosition;
	protected double fragmentSize;
	protected static Sequencer sequencerDevice = null;
	Sequence opSeq = null;
	Track systemTrack;
	protected SongInfo info;
	double DISTANCE_SCALE = 0.95;
	boolean writeOutput = true;
	MidiUtilities midiUtils = new MidiUtilities();
	boolean playMusic = true;
	Map<PartIndex,Integer> tracks = new HashMap<PartIndex,Integer>();
	protected double countIn = 4.0;
	double SCHEDULING_LATENCY_PROPORTION = 0.45;
	double REQUEST_LATENCY_PROPORTION = 0.5;
	double schedulingLatency = 0.5;
	double requestLatency = 0.5;
	boolean playLeadIn = true;
	boolean playMetronome = false;
	private static boolean connected = true;
	protected static OutputWrapper output;
	protected List<RecordAgentDetails> recordAgents = null;
	int metronomeTill = 0;
	int metronomeGrace = 15;
	static boolean doneInit = false;
	static boolean scaleVelocity = false;
	static boolean scheduleRealtimeFragments = true;
	
	public BasicSequencerThread( SongInfo info, double countIn, double fragmentSize, OutputWrapper output, MidiDevice sequencer )
	{
		this.countIn = countIn;
		this.fragmentSize = fragmentSize;
		this.info = info;
		this.output = output;
		schedulingLatency = fragmentSize * ( 1 - SCHEDULING_LATENCY_PROPORTION );
		requestLatency = fragmentSize * REQUEST_LATENCY_PROPORTION;
		BasicSequencerThread.sequencerDevice = (Sequencer)sequencer;
	}
	
	void muteIfNecessary()
	{
		if( ! connected )
		{
			try
			{
				System.out.println( "Silencing sequencer!!!!");
				sequencerDevice.getTransmitter().setReceiver( null );
			} catch (MidiUnavailableException e)
			{
				System.out.println( "Problem muting sequencer: " + e );
				e.printStackTrace();
			}
		}
		
	}

	public void initialise()
	{
		setupSequencer( output, sequencerDevice );
	}

	public void run()
	{
		start();
	}

	public void start()
	{
		//Really?
		incrementTime();
		sequencerDevice.startRecording();
		System.out.println( "Sequencer started");
	}
	
	public void endSequence()
	{
		System.out.println( "Stopping sequencer");
		sequencerDevice.stop();
		//System.out.println( "Closing devices");
		//Dont know why we can't close devices
		//if( output.isOpen() ) output.close();
		System.out.println( "Closing Sequencer ");
		sequencerDevice.close();
		System.out.println( "Sequence finished");
	}


	/*
	 * Callbacks
	 */
	public void doDissemination() {}
	
	void needMoreMusic() {}

	
	public void scheduleScore( Score s )
	{
		for( Fragment f : s.fragments() )
		{
			
			if( f.isRealtime() && ! scheduleRealtimeFragments ) continue;
			//System.out.println( "++++++++ Playing +++++++\n" + f );
			f = f.clone();
			if( scaleVelocity ) scaleVelocityByPosition( f );
			//applyInstrumentScale( f );
			int chan = getChannelForFragment( f );
			addEventNow( pan( chan, getPanForFragment( f ) ) );
			addEventNow( distance( chan, getDistanceForFragment( f ) ) );
			if( f.getMusician().getY() < -10000 )
			{
				System.err.println( "ANO, chan " + chan + " " + f.getMusician().getY() + ":" + f.getMusician().getX() );
				addEventNow( allNotesOff( chan ));
			}
			if( playMusic )
				writer.addToTrack( f, chan, currentPosition );
		}
	}
	
	Score getScoreToSchedule() { return new Score(); }

	String getOutputFilename() { return "output.mid"; }

	/*
	 * Initialisation functions
	 */
	void setupDevices() throws MidiUnavailableException
	{
		if( sequencerDevice == null ) sequencerDevice = MidiSystem.getSequencer( false );
			System.out.println( "++++ Using seqeuncer: " + sequencerDevice.getClass() + " ++++" );
			sequencerDevice.open();
			if( output != null )
			{
				/*
				try
				{ output.open();
				
				} catch( MidiUnavailableException e )
				{
					System.err.println( "Output already open: ");
					e.printStackTrace();
				}
				*/
				System.out.println( "setting output to " + output.getDeviceInfo() );
				sequencerDevice.getTransmitter().setReceiver( output );
			}
			else System.out.println( "Using default MIDI output");	
	}
	
	void setupSequencer( OutputWrapper outputDevice, Sequencer seqDev )
	{
		this.sequencerDevice = seqDev;
		this.output = outputDevice;
		//log.log( INFO, "Setting up sequencer" );
		System.out.println( "+.+> Setting up sequencer!" );
		try
		{
			setupDevices();
			opSeq = new Sequence( Sequence.PPQ, info.pulsesPerQuarterNote, 1 );
			sequencerDevice.setSequence( opSeq );
			systemTrack = opSeq.getTracks()[ 0 ];
			writer = new FragmentToTrack(systemTrack, info );
			if( playLeadIn )
				for( double beat = 0.0; beat < countIn; beat += 1.0 )
					writer.addEvent( MidiUtilities.noteOn( 9, 77,127 ), beat );
			writer.addEvent( MidiUtilities.noteOff( 0,0 ), 1 );
			writer = new FragmentToTrack( systemTrack, info );
			
			setupRecording();
		
			sequencerDevice.setTempoInBPM( info.beatsPerMinute );
			System.err.println( "PPQ: " + sequencerDevice.getSequence().getResolution() );
		}
		catch( Exception e )
		{
			//log.log( WARNING, "Could not open Sequencer: " + e );
			System.err.println( "Could not open Sequencer: " + e );
			e.printStackTrace();
			sequencerDevice.close();
		}
		muteIfNecessary();
		sequencerDevice.addMetaEventListener( new CallbackEventListener( this ) );
		scheduleNextCallback( fragmentSize );
		doneInit = true;
	}
	
	void setupRecording() 
	{
		if( recordAgents != null )
		{
			for( RecordAgentDetails agent : recordAgents )
			{
				System.out.println( ">>>> Creating a track for Agent: " + agent.getName() );
				agent.setInfo( info );
				Track t = opSeq.createTrack();
				MidiMessage msg = pan( 0, 64 );
				MidiEvent e = new MidiEvent(msg, 0);
				t.add( e );
				agent.setTrack( t );
			}
		}
		else
		{
			System.out.println( "++ No recording! (No recordAgents specified)");
		}
	}

	/*
	 * Main sequencer functions
	 */
	
	void scheduleNextCallback( double nextFragmentStart )
	{
		/*
		System.out.printf( "Free mem: %04f, Total: %04f, Max: %04f\n", 
				(double)Runtime.getRuntime().freeMemory() / 1000000,
				(double)Runtime.getRuntime().totalMemory() / 1000000,
				(double)Runtime.getRuntime().maxMemory() / 1000000 );
				*/
		double nextFragmentDeadline = nextFragmentStart - schedulingLatency;
		double nextFragmentRequest = nextFragmentDeadline - requestLatency;
		try
		{
			log.log( FINE, "Scheduling callbacks; req: " + nextFragmentRequest + ", sched: " + nextFragmentDeadline );
			MetaMessage reqMessage = new MetaMessage();
			reqMessage.setMessage( PROPRIETRY_MESSAGE, new byte[] { REQUEST_MUSIC_MESSAGE }, 1 );
			MetaMessage schedMessage = new MetaMessage();
			schedMessage.setMessage( PROPRIETRY_MESSAGE, new byte[] { FRAGMENT_DEADLINE_MESSAGE }, 1 );
			writer.addEvent( reqMessage, nextFragmentRequest );
			writer.addEvent( schedMessage, nextFragmentDeadline );
			writer.addEvent( MidiUtilities.noteOff( 0,0 ), nextFragmentDeadline + 5.1 );
			
		}
		catch( Exception e )
		{
			log.log( INFO, "Could not schedule callbacks: " + e );
		}
	}

	public void scheduleCurrentMusic()
	{
		checkSequencerTiming();
		scheduleScore( getScoreToSchedule() );
		if( playMetronome )
			for( int beat = metronomeTill + 1; beat <= (int)currentPosition + metronomeGrace; beat ++ )
			{
				if( beat >= countIn ) 
					writer.addEvent( MidiUtilities.noteOn( 9, 77,100 ), (double)beat );
			}
		metronomeTill = (int)currentPosition + metronomeGrace;
		maybeWriteOutput();
		checkSequencerTiming();
	}
	void incrementTime()
	{
		currentPosition += fragmentSize;
	}
	
	void checkSequencerTiming()
	{
		long sequencerPosition = sequencerDevice.getTickPosition();
		long agentPosition = info.getBeatTick( currentPosition );
		if( agentPosition < sequencerPosition ) System.out.println( "+++ Sequencer at " + sequencerPosition + ", agents at " + agentPosition );
	}

	public void fragmentDeadline()
	{
		log.log( FINE, "Fragment deadline!" );
		scheduleNextCallback( currentPosition + fragmentSize );
		scheduleCurrentMusic();
		doDissemination();
		incrementTime();
	}
	/*
	 * Writing midi files
	 */
	
	void maybeWriteOutput()
	{
		if( ! fuzzyCompare( currentPosition % 500, 0.0 ) )  return;
		System.out.println( "Beat " + currentPosition );
		if( writeOutput ) writeToFile( getOutputFilename() );
	}
	

	public void writeToFile( String filename )
	{
		System.out.println( "\n\n++++++++\nWriting file: " + filename );
		try
		{
			MidiSystem.write( opSeq, 1, new File( filename ) );
		}
		catch( Exception e )
		{
			System.err.println( "Could not write midi file: ("+ filename +")" + e );
		}
	}

	
	void addEventNow( MidiMessage msg )
	{
		writer.addEvent( msg, currentPosition );
	}

	/*
	 * General Fragment to MIDI functions
	 */
	
	void applyInstrumentScale( Fragment f )
	{
		Instrument i = f.getPartIndex( ).getInstrument();
		if( i != null )
		{
			if( i.getVolumeScale() != 1.0  )
			{
				f.scaleVelocity( i.getVolumeScale() );
			}
			if( i.getOctaveTranspose() != 0 )
			{
				f.transpose( i.getOctaveTranspose() * 12 );
			}
		}
	}
	
	void scaleVelocityByPosition( Fragment f )
	{
		double y = f.getPartIndex().getMusician().getY();
		double yprop = ( y / ( 2 * ROOM_Y ) ) + 0.5;
		double scaling = ( yprop * DISTANCE_SCALE ) + ( 1 - DISTANCE_SCALE );
		scaling = max( scaling, 0.0 );
		scaling = min( scaling, 1.0 );
		f.scaleVelocity( scaling );
	}

	/**
	 * Decides what MIDI channel to play the given Fragment on. Tracks are 0 - 15, but
	 * we miss out 10 as it is the percussion channel and seems to only play drums
	 * If the Fragment is from a new player, a program change relating to that player's instrument
	 * will be sent
	 * @param f Fragment to get channel for
	 * @return the channel number to use
	 */
	int getChannelForFragment( Fragment f )
	{
		PartIndex p = f.getPartIndex();
		int chan = 0; 
		if( ! tracks.containsKey( p ) )
		{
			chan = tracks.keySet().size() % 15;
			if( AVOID_PERC_CHANNEL )
			{
				//We want to avoid channel 10 (chan 9 in zero indexing) so we number
				//0 - 14, and increment 9 or above...
				chan = tracks.keySet().size() % 15;
				//if( chan >= 9 ) chan++;
			}
			tracks.put( p, chan );
			addEventNow( programChange( chan, p.getInstrument().getProgramNumber() ) );
		}
		else
		{
			chan  = tracks.get( p );
		}
		return chan;
	}

	int getPanForFragment( Fragment f )
	{
		Musician m = f.getPartIndex().getMusician();
		double xprop = ( m.getX() / ( 2 * ROOM_X ) );// + 0.5;
		double sig = signum( xprop );
		double xval = abs( xprop );
		double mxprop = pow( xval, 0.8 ) * sig + 0.5;
		int pan = (int)( mxprop * 128);
		if( pan < 0 ) pan = 0;
		if( pan > 127 ) pan = 127;
		//System.out.println( "Setting pan to " + pan + " xprop: " + xprop + " mxprop: " + mxprop ) ;
		return pan;
	}
	
	int getDistanceForFragment( Fragment f )
	{
		double y = f.getPartIndex().getMusician().getY();
		double yprop = ( y / ( 2 * ROOM_Y ) ) + 0.5;
		double scaling = ( yprop * DISTANCE_SCALE ) + ( 1 - DISTANCE_SCALE );
		scaling = max( scaling, 0.0 );
		scaling = min( scaling, 1.0 );
		return (int)(scaling * 127);
	}

	/*
	 * General getters and setters
	 */
	
	public void setRequestLatency( double requestLatency )
	{
		this.requestLatency = requestLatency;
	}

	public void setSchedulingLatency( double schedulingLatency )
	{
		this.schedulingLatency = schedulingLatency;
	}

	public void setPlayLeadIn( boolean playLeadIn )
	{
		this.playLeadIn = playLeadIn;
	}

	public boolean isPlayMetronome()
	{
		return playMetronome;
	}

	public void setPlayMetronome( boolean playMetronome )
	{
		this.playMetronome = playMetronome;
	}

	public List<RecordAgentDetails> getRecordAgents()
	{
		return recordAgents;
	}

	public void setRecordAgents( List<RecordAgentDetails> recordAgents )
	{
		this.recordAgents = recordAgents;
	}
	
	public double getCurrentPosition()
	{
		return currentPosition;
	}
	
	public Sequencer getSequencer()
	{
		return sequencerDevice;
	}

	class CallbackEventListener implements MetaEventListener
	{
		BasicSequencerThread seqThread;
	
		CallbackEventListener( BasicSequencerThread c )
		{
			this.seqThread = c;
		}
	
		public void meta( MetaMessage m )
		{
			byte[] data = m.getData();
			log.log( FINE, "Conductor got meta message : " + m.getType() + ", " + byteToString( data ) );
			if( m.getType() != PROPRIETRY_MESSAGE ) return;
			int action = (int)data[ 0 ];
			switch( action )
			{
				case REQUEST_MUSIC_MESSAGE:		seqThread.needMoreMusic();break;
				case FRAGMENT_DEADLINE_MESSAGE:	seqThread.fragmentDeadline();break;
				default: log.log( INFO, "Funny meta message: " + action );
			}
		}
	}

	public static Sequencer getSequencerDevice()
	{
		return sequencerDevice;
	}

	public static void setConnected( boolean connected )
	{
		BasicSequencerThread.connected = connected;
	}
	
	public static boolean isDoneInit()
	{
		return doneInit;
	}
	
	public static void setScaleVelocity( boolean scale )
	{
		scaleVelocity = scale;
	}

	/*
	public static MidiDevice getOutput()
	{
		return output;
	}
	*/
	
	public static Receiver getOutput()
	{
		return output;
	}

	public static boolean isScheduleRealtimeFragments()
	{
		return scheduleRealtimeFragments;
	}

	public static void setScheduleRealtimeFragments( boolean scheduleRealtimeFragments )
	{
		BasicSequencerThread.scheduleRealtimeFragments = scheduleRealtimeFragments;
	}

}
