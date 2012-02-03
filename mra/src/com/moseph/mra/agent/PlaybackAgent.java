package com.moseph.mra.agent;

import java.io.*;

import javax.sound.midi.*;

import com.moseph.mra.*;
import com.moseph.mra.midi.*;

public class PlaybackAgent extends MusicalAgent
{
	String playbackFileName;
	int playbackTrackNum = 0;
	FilePlayback playback;
	double graceTime = 4.0;
	 
	protected void initialise()
	{
		super.initialise();
		System.out.println( "++++++++++++++ Starting Playback Agent");
		context.setPiece( piece );
		outputBuffer = new Fragment( countIn );
		if( doLeadIn ) {}
		playback = new FilePlayback( playbackFileName, playbackTrackNum );
		addLength( countIn );
	}
	
	void applyArgument( AgentArgument a )
	{
		if( a.arg.equalsIgnoreCase( "PlaybackFile")) playbackFileName = a.val;
		if( a.arg.equalsIgnoreCase( "TrackNumber")) playbackTrackNum = Integer.parseInt( a.val );
		else super.applyArgument( a ); 
	}
	
	void musicRequest( double start, double end )
	{
		while( outputBuffer.getLength() < end + graceTime  )
		{
			addLength( end - start );
		}
	}
	
	void addLength( double length )
	{
		double pos = playback.getPosition();
		Fragment chunk = playback.getNextChunk( length );
		outputBuffer.addFragment( chunk, pos );
		System.out.println( "Asked for " + length + ", now got until " + outputBuffer.getLength() + " playback pos: " + pos );
		System.out.println( "Chunk was\n" + chunk + ", playback pos is now " + playback.getPosition()  );
		if( outputBuffer.getLength() < pos ) outputBuffer.setLength( pos );
	}
	
}
