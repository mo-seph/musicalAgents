package com.moseph.mra.experiments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import static javax.sound.midi.ShortMessage.*;
import javax.sound.midi.Track;

import static com.moseph.mra.midi.MidiUtilities.*;

import com.moseph.mra.SongInfo;
import com.moseph.mra.TimeSignature;
import com.moseph.mra.midi.MidiUtilities;

public class PatchAndPanExperiment
{
	public static void main( String[] args )
	{
		Sequencer sequencer = null;
		Sequence sequence = null;
		SongInfo info = new SongInfo( new TimeSignature( 4, 4 ), 100, 384 );
		int t1chan = 9;
		int t2chan = 10;
		try
		{
			sequence = new Sequence( Sequence.PPQ, info.pulsesPerQuarterNote, 3 );
			//int halfSec = 1920;
			Track track1 = sequence.getTracks()[ 1 ];
			track1.add( new MidiEvent( programChange( t1chan, 5 ), info.getBeatTick(0.0)));
			track1.add( new MidiEvent( pan( t1chan, 0 ), info.getBeatTick(0.0)));
			for( double beat = 0.0; beat < 4; beat += 1.0 )
			{
				track1.add( new MidiEvent( noteOn( t1chan, 44,127 ), info.getBeatTick( beat ) ) );
			}
			//Track track2 = sequence.getTracks()[2];
			Track track2 = track1;
			track2.add( new MidiEvent( programChange( t2chan, 8 ), info.getBeatTick(0.0)));
			track2.add( new MidiEvent( pan( t2chan, 127 ), info.getBeatTick(0.0)));
			for( double beat = 0.0; beat < 4; beat += 1.0 )
			{
				track2.add( new MidiEvent( noteOn( t2chan, 44,127 ), info.getBeatTick( beat + 0.5 ) ) );
			}
			
			
			//Make the sequence play to the end...
			track1.add( new MidiEvent( noteOn( 44,127 ), info.getBeatTick( 1000 ) ) );
		}
		catch( InvalidMidiDataException e )
		{
			System.out.println( "Could not create Sequence: " + e );
		}

		try
		{
			sequencer = MidiSystem.getSequencer( true );
			sequencer.open();
			sequencer.setSequence( sequence );
			sequencer.start();
			/*
			try
			{
				for( int i = 0; i < 16; i++ ) 
				{
					ShortMessage pc = new ShortMessage(); //programChange( 9 );
					pc.setMessage( PROGRAM_CHANGE, i, 3, 3 );
					MidiSystem.getReceiver().send( pc, -1 );
				}
			}
			catch( Exception e ) { System.out.println( "Couldn't send message: " + e ); e.printStackTrace();}
			*/
		}
		catch( Exception e )
		{
			System.out.println("Could not open Sequencer: " + e );
			sequencer.close();
		}
		BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
		try
		{
			in.readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println( "Closing sequencer...");
		sequencer.close();
		System.exit( 0 );
	}
}
