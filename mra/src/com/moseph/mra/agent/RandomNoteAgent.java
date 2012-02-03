package com.moseph.mra.agent;


import static com.moseph.mra.MRAConstants.Ab3;
import static com.moseph.mra.MRAConstants.B3;
import static com.moseph.mra.MRAConstants.Bb3;
import static com.moseph.mra.MRAConstants.C3;
import static com.moseph.mra.MRAConstants.D3;
import static com.moseph.mra.MRAConstants.Eb3;
import static com.moseph.mra.MRAConstants.F3;
import static com.moseph.mra.MRAConstants.G3;

import java.util.Vector;

/**
A RandomNoteAgent is a basic test of the musical agent shell

every time it recieves another fragment of music, it creates a similar
length fragment in the output buffer consisting of random notes

@author David Murray-Rust
@version $Revision$, $Date$
*/
public class RandomNoteAgent extends MusicalAgent
{
	int index = 60;
	double stepSize = 0.5;
	Vector<Integer> allowableNotes = new Vector<Integer>();
	{
		allowableNotes.add( C3 );
		allowableNotes.add( D3 );
		allowableNotes.add( Eb3 );
		allowableNotes.add( F3 );
		allowableNotes.add( G3 );
		allowableNotes.add( Ab3 );
		allowableNotes.add( Bb3 );
		allowableNotes.add( B3 );
	}

	protected void initialise()
	{
		super.initialise();

		System.out.println( "Setting up" );
		for( double i = 0.0; i < 120.0; i += stepSize )
		{
			int offset = (int) ( Math.random() * 14 - 2 );
			index += offset;
			index = index % allowableNotes.size();
			if( index < 0 ) index += allowableNotes.size();
			outputBuffer.addNote( i, allowableNotes.elementAt( index ), 0.8, (stepSize * 0.8 ) );
			//log.log( FINE, outputBuffer.toString() );
		}
		System.out.println( "Done" );
	}


	/**
	@param bar
	*/
	/*
	void gotInput( double start, double length )
	{
		super.gotInput( start, length );

		for( double i = Math.ceil( start + length ); i < start + 2*length; i += stepSize )
		{
			//int pitch = allowableNotes.elementAt( (int)( Math.random() * allowableNotes.size() ) );
			int offset = (int) ( Math.random() * 14 - 2 );
			index += offset;
			index = index % allowableNotes.size();
			if( index < 0 ) index += allowableNotes.size();
			outputBuffer.addNote( i, allowableNotes.elementAt( index ), 0.8, (stepSize * 0.8 ) );
			//log.log( FINE, outputBuffer.toString() );
		}
		System.out.println( outputBuffer.toString() );
	}
	*/
}
