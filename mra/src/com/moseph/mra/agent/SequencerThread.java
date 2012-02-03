package com.moseph.mra.agent;

import java.util.*;

import javax.sound.midi.*;

import com.moseph.mra.*;
import com.moseph.mra.experiments.MidiReceiveTest;
import com.moseph.mra.midi.*;

import static com.moseph.mra.MRAConstants.*;
import static com.moseph.mra.MRAUtilities.*;
import static com.moseph.mra.midi.MidiUtilities.*;
import static java.util.logging.Level.*;
import static java.lang.Math.*;

public class SequencerThread extends BasicSequencerThread
{
	Conductor agent;
	protected ConductorDataBuffer data;
	
	public SequencerThread( Conductor agent, ConductorDataBuffer data, SongInfo info, double countIn, double fragmentSize )
	{
		this( agent, data, info, countIn, fragmentSize, null, null );
	}
	public SequencerThread( Conductor agent, ConductorDataBuffer data, SongInfo info, double countIn, double fragmentSize, OutputWrapper output, MidiDevice sequencer )
	{
		super( info, countIn, fragmentSize, output, sequencer );
		this.agent = agent;
		this.data = data;
	}
	
	public void doDissemination()
	{
		agent.disseminateCurrentFragment();
		data.reset();
	}
	
	public Score getScoreToSchedule()
	{
		return data.getCurrentScore();
	}
	
	void needMoreMusic()
	{
		agent.askForNextFragment();
	}
	
	void incrementTime()
	{
		super.incrementTime();
		agent.incrementTime( fragmentSize );
	}
	
	String getOutputFilename()
	{
		return agent.getFileToWrite( currentPosition );
	}
	
}
