package com.moseph.mra.experiments;

import javax.sound.midi.*;

import com.moseph.mra.midi.MidiUtilities;
import com.sun.media.sound.MidiUtils;

import static com.moseph.mra.midi.MidiUtilities.*;

public class MIDIRecordingExperiment
{

	Sequencer sequencer;
	Sequence sequence;
	Track track;
	
	public static void main( String[] args )
	{
		MIDIRecordingExperiment exp = new MIDIRecordingExperiment();
		exp.collect();
		MidiUtilities.waitForEnter();
		exp.printSequence();
		exp.playback();
		
		MidiUtilities.waitForEnter();
		exp.closeAll();
	}
	
	public MIDIRecordingExperiment()
	{
		try
		{
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequence =  new Sequence( Sequence.PPQ, 384, 1 );
			sequencer.setSequence( sequence );
			track = sequence.getTracks()[0];
			sequencer.recordEnable( track, -1 );
			System.out.println( sequencer.getReceiver() );
			for( MidiDevice dev : MidiUtilities.getInputDevices() )
			{
				System.out.println( "Opening " + dev.getDeviceInfo() );
				dev.open();
				dev.getTransmitter().setReceiver( sequencer.getReceiver() );
			}
		} 
		catch (Exception e)
		{
			System.err.println( "No midi! " + e );
			System.exit( 1 );
		}
		
	}
	
	public void collect()
	{
			sequencer.startRecording();
	}
	
	public void printSequence()
	{
		sequencer.stopRecording();
		sequencer.stop();
		sequencer.close();
		System.out.println( "Finished capture!");
		for( Track t : sequence.getTracks() )
		{
			System.out.println( "Track! with " + t.size() + " events");
			for( int i = 0; i < t.size(); i++ )
			{
				System.out.println( MidiUtilities.eventToString( t.get( i ) ));
			}
		}
		System.out.println( "T: " + track.size() );
	}
	
	public void playback()
	{
		try
		{
			sequencer.open();
			sequencer.setSequence( sequence );
			sequencer.start();
		} catch (MidiUnavailableException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidMidiDataException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeAll()
	{
		for( MidiDevice.Info d : MidiSystem.getMidiDeviceInfo() )
		{
			try
			{
				MidiSystem.getMidiDevice( d ).close();
			} catch (MidiUnavailableException e)
			{
				System.out.println( "Couldn't close device: " + d );
				e.printStackTrace();
			}
		}
	}
}
