package com.moseph.mra.experiments;

import static com.moseph.mra.midi.MidiUtilities.*;

import javax.sound.midi.*;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
public class ProgChange
{
	public static void main( String[] args )
	{
		MidiMessage m = programChange( 15, 1 );
		MidiDevice.Info devInfo = getOutputMidiDevice( "AMT", 1 );
		MidiDevice dev;
		try
		{
			dev = MidiSystem.getMidiDevice( devInfo );
			dev.open();
			dev.getReceiver().send( m, -1 );
		} catch (MidiUnavailableException e)
		{
			System.out.println( "Could not open MIDI device: " + e );
			e.printStackTrace();
		}
	}

}
