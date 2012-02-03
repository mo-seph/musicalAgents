package com.moseph.mra.agent;

import static jade.lang.acl.ACLMessage.REQUEST;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static java.lang.Math.*;
import static com.moseph.mra.MRAUtilities.*;
import static com.moseph.mra.MRAConstants.*;
import static com.moseph.mra.midi.MidiUtilities.*;
import jade.core.AID;
import jade.core.Runtime;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.*;

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;

import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;
import javax.swing.*;
import javax.swing.Action;

import com.moseph.mra.*;
import com.moseph.mra.agent.gui.MIDISelector;
import com.moseph.mra.experiments.MIDIOutput;
import com.moseph.mra.midi.*;
import com.moseph.mra.parser.MRAParser;

public class Conductor extends MusicallyAwareAgent
{
	double currentPosition = 0.0;
	static double fragmentSize = 1.0;
	SongInfo info;
	Map<String, PartIndex> partsByName = new HashMap<String, PartIndex>();
	ConductorDataBuffer data = new ConductorDataBuffer();
	BasicSequencerThread sequencer;
	boolean playLeadIn = true;
	boolean allowInput = true;
	String outputDir;
	String outputFilename;
	boolean playMetronome = false;
	static boolean noDeviceRequest = false;
	static Score currentScore = new Score();
	static boolean storeOutput = false;
	boolean mergeAllInputs = false;
	boolean openAllDevices = true;
	boolean echoInputNotesToConsole = false;
	static boolean echoRecording = false;
	double runTime = Double.POSITIVE_INFINITY;
	//List<String> recordAgentStringDefs = new Vector<String>();
	List<RecordAgentDetails> passedRecordAgents = new Vector<RecordAgentDetails>();
	MidiPatchbay patchbay = MidiPatchbay.getInstance();
	boolean waitForEnterAtStart = true;

	public static void main( String[] args )
	{
		MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();
		for ( MidiDevice.Info d : devices )
		{
			System.out.println( d.getName() + ": " + d.getDescription() );
		}
	}

	protected void initialise()
	{
		super.initialise();
		setQueueSize( 400 );
		System.out.println( "Conductor Starting with count in: " + countIn );
		System.out.println( "Setting up" );
		try
		{
			doFileParse();
			info = new SongInfo( piece.getTimeSignature(), (int) piece.getBPM(), 384 );
			System.out.println( info + "" );
		}
		catch( Exception e )
		{
			System.out.println( "Could not make piece: " + e );
			info = new SongInfo( new TimeSignature( 4, 4 ), 120, 384 );
		}
		info.printSongInfo();
		addBehaviour( musicCollection() );
		getMidiDevice();
	}

	protected void startSequencer( OutputWrapper output, Sequencer sequencerDevice,  List<RecordAgentDetails> agents )
	{
		
		sequencer = new SequencerThread( this, data, info, countIn, fragmentSize, output, sequencerDevice );
		//Do any extra setup in here...
		if( !playLeadIn ) sequencer.setPlayLeadIn( false );
		if( playMetronome ) sequencer.setPlayMetronome( true );
		sequencer.setRecordAgents( agents );
		sequencer.initialise();
		Thread t = new Thread( new ThreadGroup( "Sequencer" ), sequencer, "Sequencer", (int)200e6 );
		t.setPriority( Thread.MAX_PRIORITY );
		
		
		if( waitForEnterAtStart )
		{
			System.out.println( "+++++++++++++++++\n\n+++++++++++++++++\nPress enter to start\n++++++++++++++");
			waitForEnter();
		}
		System.out.println( "%%%%%%%%%%%%%%%%%%%%%%\nStarting sequencer! (" + runTime + " beats)");
		t.start();
	}

	void getMidiDevice()
	{
		final JFrame f = new JFrame( "Choose MIDI Output Device");
		if( noDeviceRequest )
		{
			System.out.println( "No device to be selected!");
			createRecordingAgents( passedRecordAgents );
			startSequencer( patchbay.getDefaultOutput(), (Sequencer)getSequencerDevices().get(0), passedRecordAgents );
			return;
		}
		f.setSize( 300, 500 );
		final MIDISelector s = new MIDISelector()
		{
			public void devicesSelected( OutputWrapper output, Sequencer sequencer )
			{
				List<RecordAgentDetails> rad = getRecordAgentDetails();
				rad.addAll( passedRecordAgents );
				createRecordingAgents( rad );
				startSequencer( output, sequencer, rad );
				f.setVisible( false );
			}
		};
		
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.add( s );
		f.setVisible( true );
	}
	
	void createRecordingAgents( List<RecordAgentDetails> agents )
	{
		ContainerController controller = getContainerController();
		if( openAllDevices ) openAllDevices();
		for( RecordAgentDetails agent : agents )
		{
			
			try
			{
				System.out.println( "++++> Creating Agent! " + agent.name );
				agent.setAgentID( new AID( agent.getName(), false ) );
				FragmentAdaptor adaptor = new FragmentAdaptor( null, info, new Fragment() );
				adaptor.setChannel( agent.getChannel() );
				if( echoRecording )
				{
					System.out.println( "Conductor enabling echo recording");
					adaptor.setEchoOutput( true );
				}
				try
				{
					List<InputWrapper> recDevs = new Vector<InputWrapper>();
					recDevs.add( agent.getInput() );
					//if( mergeAllInputs ) recDevs = patchbay.getInputs();
					for( InputWrapper dev : recDevs )
					{
						Receiver recv = adaptor;
						if( echoInputNotesToConsole )  recv = new TeeReceiver( adaptor, "[" + agent.getName() + "]" );
						//System.out.println( "Setting up with device: " + dev.getDeviceInfo().getName()  );
						
						dev.setReceiver( recv );
					}
				} catch( Exception e )
				{
					System.err.println( "Could not create input link: " + e );
					e.printStackTrace();
				}
				agent.setAdaptor( adaptor );
				AgentController a = controller.createNewAgent( agent.name, "com.moseph.mra.agent.RecordAgent", new Object[] { agent } );
				a.start();
			} catch (StaleProxyException e)
			{
				System.err.println( "Could not create record agent: " + e );
				e.printStackTrace();
			}
		}
	}

	/***************************************************************************
	 * * Behaviours * *
	 **************************************************************************/

	// One behaviour collects them
	Behaviour musicCollection()
	{
		return new BlockingReceiver( this, getFragmentTemplate() )
		{
			void receivedMessage( ACLMessage m )
			{
				//System.out.println( "Got input: " + getFragmentFromMessage( m ) + " from " + m.getSender().getLocalName() );
				data.addFragment( getFragmentFromMessage( m ), m.getSender() );
			}
		};
	}

	/***************************************************************************
	 * * Scheduling using the sequencer * *
	 **************************************************************************/

	/***************************************************************************
	 * * Sending, receiving and scheduling msuic * *
	 **************************************************************************/

	void askForNextFragment()
	{
		// if( currentPosition >= countIn ) askForFragment( currentPosition,
		// fragmentSize );
		askForFragment( currentPosition, fragmentSize );
	}

	void askForFragment( double start, double length )
	{
		log.log( FINE, "Conductor (" + getName() + ") asking for music from "
				+ start + " to " + ( start + length ) );
		ACLMessage req = getMessageToAllMusicians();

		for ( DFAgentDescription ad : getMusicians() )
			if( !ad.getName().getLocalName().equals( "space" )
					&& !ad.getName().getLocalName().equals( "osc" ) )
				data.addRecipient( ad.getName() );
		req.setPerformative( REQUEST );
		req.setContent( MUSIC_REQUEST );
		req.addUserDefinedParameter( START_PARAM, start + "" );
		req.addUserDefinedParameter( LENGTH_PARAM, length + "" );
		// System.out.println( "Asking for music from " + start + " to " +
		// (start+length) + " from " + asked.size() );
		send( req );
	}

	void disseminateCurrentFragment()
	{
		/*
		System.out.printf( "++ Free mem: %04f, Total: %04f, Max: %04f\n", 
				(double)Runtime.getRuntime().freeMemory() / 1000000,
				(double)Runtime.getRuntime().totalMemory() / 1000000,
				(double)Runtime.getRuntime().maxMemory() / 1000000 );
				*/
		log.log( FINE, "Conductor (" + getName()
				+ ") disseminating music from " + currentPosition + " to "
				+ ( currentPosition + fragmentSize ) );
		ACLMessage inf = getMessageToAllMusicians();
		data.getCurrentScore().forceLength( fragmentSize );
		putScoreIntoMessage( data.getCurrentScore(), currentPosition, fragmentSize, inf );
		send( inf );
		if( storeOutput ) currentScore.add( data.getCurrentScore(), currentPosition );
		if( currentPosition >= runTime )
		{
			System.out.println( "Writing to: " + outputDir + outputFilename );
			sequencer.writeToFile( outputDir + outputFilename );
			System.out.println( "Stopping sequencer");
			sequencer.endSequence();
			Runtime.instance().setCloseVM( false );
			/*
			try
			{
				System.out.println( "Killing container");
				getContainerController().kill();
			} catch (StaleProxyException e)
			{
				System.out.println( "Couldn't kill container: " + e );
				e.printStackTrace();
			}
			*/
			//System.out.println( "Shutting down runtime");
			//Runtime.instance().shutDown();
			System.out.println( "THANATOS!");
			System.exit( 0 );
			//die();
		}
	}

	void incrementTime( double amount )
	{
		currentPosition += amount;
	}

	public List<MessageTemplate> getKnownMessages()
	{
		List<MessageTemplate> sup = super.getKnownMessages();
		sup.add( getFragmentTemplate() );
		return sup;
	}

	public String[] getServiceNames()
	{
		return new String[] { CONDUCTOR_SERVICE };
	}

	public void otherQuitting( AID aid )
	{
	}
	
	void applyArgument( AgentArgument a )
	{
		System.out.println( "Applying Arguments: " + a );
		if( a.arg.equalsIgnoreCase( "PlayLeadIn")) playLeadIn = Boolean.parseBoolean( a.val );
		else if( a.arg.equalsIgnoreCase( "AllowInput")) allowInput = Boolean.parseBoolean( a.val );
		else if( a.arg.equalsIgnoreCase( "PlayMetronome")) playMetronome = Boolean.parseBoolean( a.val );
		else if( a.arg.equalsIgnoreCase( "OutputDir" )) outputDir = a.val;
		else if( a.arg.equalsIgnoreCase( "OutputFilename" )) outputFilename = a.val;
		else if( a.arg.equalsIgnoreCase( "NoDeviceRequest" )) noDeviceRequest = Boolean.parseBoolean( a.val );
		else if( a.arg.equalsIgnoreCase( "EchoRecording" )) echoRecording = Boolean.parseBoolean( a.val );
		else if( a.arg.equalsIgnoreCase( "RunTime" )) runTime = Double.parseDouble( a.val );
		else if( a.arg.equalsIgnoreCase( "RecordAgent" ))
		{
			System.out.println( "__+++++++++__________ Making new record agent: " + a.val );
			passedRecordAgents.add( new RecordAgentDetails( a.val ) );
		}
		//else if( a.arg.equalsIgnoreCase( "FragmentSize" )) fragmentSize = Double.parseDouble( a.val );
		else super.applyArgument( a );
	}
	
	public String getFileToWrite( double position )
	{
		if( experiment != null && subExperiment != null && outputDir != null )
			return outputDir + experiment + "-" + subExperiment + ".mid";
		System.out.println( ">" +outputDir + ", " + experiment + "-" + subExperiment );
		return "output/output" + ((int)currentPosition) + ".mid";
	}

	public static boolean isStoreOutput()
	{
		return storeOutput;
	}

	public static void setStoreOutput( boolean storeOutput )
	{
		Conductor.storeOutput = storeOutput;
	}

	public static Score getCurrentScore()
	{
		return currentScore;
	}
	
	public static double getFragmentSize()
	{
		return fragmentSize;
	}

	public static void setNoDeviceRequest( boolean noDeviceRequest )
	{
		Conductor.noDeviceRequest = noDeviceRequest;
	}

	public boolean isEchoInputNotesToConsole()
	{
		return echoInputNotesToConsole;
	}

	public void setEchoInputNotesToConsole( boolean echoInputNotesToConsole )
	{
		this.echoInputNotesToConsole = echoInputNotesToConsole;
	}

	public static void setFragmentSize( double fragmentSize )
	{
		Conductor.fragmentSize = fragmentSize;
	}

	public boolean isEchoRecording()
	{
		return echoRecording;
	}

	public void setEchoRecording( boolean echoRecording )
	{
		this.echoRecording = echoRecording;
	}
	
	public static void setScheduleRealtimeFragments( boolean sched )
	{
		BasicSequencerThread.setScheduleRealtimeFragments( sched );
	}
	
	public static void openAllDevices()
	{
		for( Info i : MidiSystem.getMidiDeviceInfo() )
		{
			try
			{
				MidiSystem.getMidiDevice( i ).open();
			} catch (MidiUnavailableException e)
			{
				System.out.println( "Conductor could not open device " + i.getName() + ": " + e );
				e.printStackTrace();
			}
		}
	}
}
