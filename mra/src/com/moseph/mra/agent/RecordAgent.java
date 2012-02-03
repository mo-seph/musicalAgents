package com.moseph.mra.agent;

import javax.sound.midi.*;

import com.moseph.mra.*;
import com.moseph.mra.midi.*;
import static com.moseph.mra.midi.MidiUtilities.*;

public class RecordAgent extends MusicalAgent
{
	RecordAgentDetails myDetails;
	FragmentAdaptor adaptor;
	SongInfo info;
	Sequencer sequencer;
	static double  PASTE_OFFSET = 3 * Conductor.getFragmentSize();
	//static double  PASTE_OFFSET = 4.0;
	
	protected void initialise()
	{
		super.initialise();
		System.out.println( "----++++ Record agent created! " + getLocalName()  );
		Object[] args = getArguments();
		myDetails = (RecordAgentDetails) args[0];
		sendPathMessage( new Path( "/main") );
		adaptor = myDetails.getAdaptor();
		if( adaptor.getFragment() != null ) outputBuffer = adaptor.getFragment();
		if( adaptor == null ) 
			adaptor = new FragmentAdaptor( null, myDetails.getInfo(), outputBuffer );
		
		//If we've not got an input, and we're allowed to ignore this, warn and carry on
		if( myDetails.getInput() == null && myDetails.isIgnoreNoInputDevice() ) 
			System.err.println( "!!!!!! No Input for recordAgent");
		//Otherwise try and assign one, so we error on failure
		else 
			myDetails.getInput().setReceiver( adaptor );
		
		if( myDetails.getTrack() != null ) adaptor.setTrack( myDetails.getTrack() );
		id.setX(  myDetails.x  );
		id.setY(  myDetails.y  );
	}
	
	public Receiver getReceiver()
	{
		return adaptor;
	}

	@Override
	Fragment getOutput( double start, double end )
	{
		if( sequencer == null )
		{
			sequencer = BasicSequencerThread.getSequencerDevice();
			float bpm = sequencer.getTempoInBPM();
			info = new SongInfo( new TimeSignature( 4,4 ), bpm, 384 );
		}
		double cutStart = start - PASTE_OFFSET;
		double cutEnd = end - PASTE_OFFSET;
		double seqPos = info.getTickBeat( sequencer.getTickPosition() );
		
		Fragment ret = null;
		// DIRTY FILTH HACK TO AVOID SYNCHRONIZATION COS I AM LAXY
		// if the output fails (probably concurrent mod exception)
		// try and get it again...
		try
		{
			ret = super.getOutput( cutStart, cutEnd );
		}
		catch( Exception e1 )
		{
			try
			{
				ret = super.getOutput( cutStart, cutEnd );
			}
			catch( Exception e2 )
			{
				System.err.println( "Tried twice to get the output and couldn't! " + e2 + "( "+ e1 + ")");
				e2.printStackTrace();
			}
		}
		//System.out.println( "++ Record asked for music from " + start + " to " + end + 
				//", and has until " + ( outputBuffer.getLength() ) + ", seq till " + seqPos );
		//System.out.println( "++ Returning from " + cutStart + " to " + cutEnd );
		if( cutEnd > seqPos )
			System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n\n\nSequencer Underrun!!!!!!!!!!!!!!\n\n\n!!!!!!!!!!!!!!!!!\n");
		
		//System.out.println( "Record returning: " + ret );
		//return new Fragment( end - start );
		ret.setRealtime( true );
		return ret;
	}

	public static double getPasteOffset()
	{
		return PASTE_OFFSET;
	}
	
	

}
