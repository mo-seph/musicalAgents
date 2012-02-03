package com.moseph.mra.test;

import com.moseph.mra.*;
import com.moseph.mra.midi.MidiUtilities;

import junit.framework.*;
import javax.sound.midi.*;

import static com.moseph.mra.midi.MidiUtilities.*;

public class MidiUtilityTest extends TestCase
{
	public static void main(String args[]) 
	{ 
		//junit.textui.TestRunner.run(suite());
		MidiUtilityTest t = new MidiUtilityTest();
		t.testTimeSignature();
	}
	public void testTimeSignature()
	{
		TimeSignature ts = new TimeSignature( 4, 4 );
		MetaMessage mm = MidiUtilities.createTimeSignature( ts );
		TimeSignature ts2 = getTimeSignature( mm );
		System.out.println( "Time signature test\nMidi Message: " + messageToString( mm ) + "\nTime Signatures: " + ts + ": " + ts2 );
		assertTrue( isTimeSignature( mm ) );
		assertEquals( ts.beats, ts2.beats );
		assertEquals( ts.type, ts2.type );
	}

	public static Test suite() 
	{ 
		return new TestSuite(MidiUtilityTest.class);
	}
}
