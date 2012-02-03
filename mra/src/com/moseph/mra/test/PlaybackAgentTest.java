package com.moseph.mra.test;

import jade.Boot;
import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.*;

import java.io.File;
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
public class PlaybackAgentTest extends TestCase
{
	double fragmentSize = 0.5;
	
	public void setUp()
	{
	}
	
	public void testNothing()
	{
		assertTrue( true );
	}
	
	/*
	public void testSimpleOperation()
	{
		Fragment frag = new Fragment();
		
		for( double d = 0; d < 5; d += 1.0 )
			frag.addNote( d, 45, 0.7, 0.6 );
		runTest( frag, "tmp/test-a.mid");
	}
	
	public void testBasicOperation()
	{
		runTest( new Fragment(), "tmp/test-a.mid");
	}
	*/
	
	void runTest( Fragment frag, String filename )
	{
		try
		{
			File f = new File( filename );
			f.delete();
			SongInfo si = new SongInfo( new TimeSignature( 4,4 ), 60, 384 );
			Sequence seq = new Sequence( Sequence.PPQ, 384 );
			Track track = seq.createTrack();
			FragmentToTrack ftt = new FragmentToTrack(track, si );
			ftt.addToTrack( frag, 0 );
			MidiSystem.write( seq, 1, new File( filename ) );
			
			Boot.main( new String[] {} );
			ContainerController controller = Runtime.instance().createAgentContainer( new ProfileImpl() );
			AgentController a;
			a = controller.createNewAgent( "playback",
					"com.moseph.mra.agent.PlaybackAgent", new Object[] { "PlaybackFile=" + filename } );
			System.out.println( "Created agent!");
			a.activate();
			a.start();
			System.out.println( "Started agent!");
			
		}
		catch( Exception e )
		{
			e.printStackTrace();
			fail( e.getMessage() );
		}
		MidiUtilities.waitForEnter();

	}
}