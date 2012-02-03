package com.moseph.mra;

import java.util.logging.Logger;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import com.moseph.mra.midi.MidiUtilities;

/**
contains general information about a song.
This class is created around a particular {@link Sequence}, and should
be used to get {@link Fragment}s for representing the sequence. It keeps
track of open spans, can work out time and key signatures and more.
@author David Murray-Rust
@version $Revision$, $Date$
*/
public class SongInfo
{
	TimeSignature timeSignature;
	public float beatsPerMinute;
	public int pulsesPerQuarterNote;
	int pulsesPerBar;
	int pulsesPerBeat;
	Sequence sequence;
	Track[] tracks;
	protected static Logger log = MRAUtilities.getLogger();
	double timeScale = 1.0;

	public SongInfo( Sequence sequence )
	{
		this.sequence = sequence;
		tracks = sequence.getTracks();
		extractTempoAndSignature();
		if( timeSignature == null ) timeSignature = new TimeSignature( 4, 4 );
		if( beatsPerMinute == 0.0 ) beatsPerMinute = 120;
		setPulses();
	}

	public SongInfo( TimeSignature timeSignature, float beatsPerMinute, int pulsesPerQuarterNote )
	{
		this.timeSignature = timeSignature;
		this.beatsPerMinute = beatsPerMinute;
		this.pulsesPerQuarterNote = pulsesPerQuarterNote;
		setPulses();
	}
	
	void setPulses()
	{
		pulsesPerBar = (int)( 4 * pulsesPerQuarterNote * timeSignature.beats / timeSignature.type );
		pulsesPerBeat = (int)( 4 * pulsesPerQuarterNote / timeSignature.type );
	}

	void extractTempoAndSignature()
	{
		boolean gotTempo = false;
		boolean gotSignature = false;
		pulsesPerQuarterNote = sequence.getResolution();

		//Scan each track for information
		//for( int i = 0; i < tracks.length; i++ )
		//assuming info is on first track...
		searchLoop: for( int i = 0; i < 1; i++ )
		{
			Track t = tracks[ i ];
			for( int j = 0; j < t.size(); j++ )
			{
				MidiEvent e = t.get( j );
				MidiMessage m = e.getMessage();
				if( ( m instanceof MetaMessage ) )
				{
					MetaMessage mm = (MetaMessage) m;
					if( !gotSignature && MidiUtilities.isTimeSignature( mm ) )
					{
						timeSignature = MidiUtilities.getTimeSignature( mm );
						//PPQN * 4 = pulses per bar in 4/4
						//PPBar(4/4) / beatType = pulsesPerBeat
						//pulsesPerBeat * beats = pulsesPerBar
						pulsesPerBar = (int)( 4 * pulsesPerQuarterNote * timeSignature.beats / timeSignature.type );
						pulsesPerBeat = (int)( 4 * pulsesPerQuarterNote / timeSignature.type );
						gotSignature = true;
					}
					else if ( !gotTempo && MidiUtilities.isTempo( mm ) )
					{
						beatsPerMinute = MidiUtilities.getBPM( mm );
						gotTempo = true;
					}
					if( gotTempo && gotSignature )
					{
						break searchLoop;
					}
				}
			}
		}
		//printSongInfo();
	}

	public void printSongInfo()
	{
		System.out.println( "Time Signature: " + timeSignature );
		if( tracks != null ) System.out.println( "Tracks: " + tracks.length );
		System.out.println( "BPM: " + beatsPerMinute );
		System.out.println( "Resolution: " + pulsesPerQuarterNote );
		System.out.println( "Pulses per bar: " + pulsesPerBar );
	}

	public double getEventBeat( MidiEvent e )
	{
		return getTickBeat( e.getTick() ) * timeScale;
	}
	
	public double getTickBeat( long tick )
	{
		return (double) tick / pulsesPerBeat;
	}


	public long getBeatTick( double beat )
	{
		return (long) ( beat * pulsesPerBeat );
	}

	public long getStartTicks( Note n )
	{
		return getBeatTick( n.getOnset() );
	}

	public long getEndTicks( Note n )
	{
		return getBeatTick( n.getEndTime() );
	}

	public double getTimeScale()
	{
		return timeScale;
	}

	public void setTimeScale( double timeScale )
	{
		this.timeScale = timeScale;
	}

}
