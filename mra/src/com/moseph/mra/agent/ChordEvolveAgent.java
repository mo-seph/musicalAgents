package com.moseph.mra.agent;


import java.util.HashSet;
import java.util.Vector;

import com.moseph.mra.*;

import static com.moseph.mra.MRAConstants.*;
import java.lang.Math;

/**
A ChordEvolveAgent is a basic test of the musical agent shell

every time it recieves another fragment of music, it creates a similar
length fragment in the output buffer consisting of random notes

@author David Murray-Rust
@version $Revision$, $Date$
*/
public class ChordEvolveAgent extends MusicalAgent
{
	int index = 60;
	boolean after = false;
	static int numChoices = 3;
	double stepSize = 1.0;
	boolean leading;
	int position = 0;
	int rotation = 0;
	int numAgents = 3;

	public static void main( String[] args )
	{
		/*
		HashSet<Integer> playedNotes = new HashSet<Integer>();
		playedNotes.add( C3 );
		playedNotes.add( E3 );
		HashSet<Integer> potentialDegrees = getPotentialDegrees( playedNotes );
		HashSet<Integer> chosenDegrees = chooseDegrees( potentialDegrees );
		HashSet<Integer> allowableNotesb = chooseNotes( chosenDegrees );
		for( int i:allowableNotesb ) {System.out.println(i);}
		*/
		for( int i = 0; i < 10; i++ )
		System.out.println( pickRandomNote() );

	}

	static int[][] degrees =
	{
		{C3},
		{D3},
		{E3,Eb3},
		{F3},
		{Gb3,G3},
		{A3},
		{Bb3,B3}//,
		/*
		{C4},
		{D4},
		{E4,Eb4},
		{F4},
		{Gb4,G4},
		{A4},
		{Bb4,B4}
		*/
	};

	HashSet<Integer> allowableNotes = new HashSet<Integer>();
	{
		allowableNotes.add( C3 );
		allowableNotes.add( E3 );
		allowableNotes.add( G3 );
	}

	protected void initialise()
	{
		super.initialise();
		if( getArguments().length > 0 ) 
		{ 
			try
			{
				position = Integer.parseInt( getArguments()[0].toString() );
				numAgents = Integer.parseInt( getArguments()[1].toString() );
			}
			catch( Exception e )
			{
				System.out.println( "Funny position: " + getArguments()[0] + ": " + e );
			}
		}


		//System.out.println( "Setting up" );
		//System.out.println( "Done" );
	}


	/**
	@param bar
	*/
	void gotInput( Score input, double start, double length )
	{
		super.gotInput( input, start, length );
		if( rotation == position ) { chooseAllowableNotes( input ); }
		rotation ++;
		Vector<Integer> allowed = new Vector<Integer>(allowableNotes);
		//This is the normal version
		//for( double i = Math.ceil( start + length ); i < start + 2*length; i += stepSize )
		//Hacked to be half time so we get the same stuff twice...
		for( double i = Math.ceil( start + length ); i < start + 2.0*length; i += stepSize )
		{
			/*
			for( int note: allowableNotes )
			{
				outputBuffer.addNote( i, note, 0.8, (stepSize * 0.8 ) );
			}
			*/
			int noteNumber = allowed.elementAt( (int)( Math.random() * allowed.size() ) );
			double time = i;
			time += (stepSize/numAgents) * position;
			double velocity = ( position == 0 ) ? 1.0 : 0.85 + (0.05 * (Math.random() - 0.5));
			outputBuffer.addNote( time, noteNumber, velocity, stepSize  );
			//outputBuffer.addNote( time + 0.5 * length, noteNumber, 0.8, stepSize );
		}
		//System.out.println( outputBuffer.copyChunk( start, length ).toString() );
	}

	void chooseAllowableNotes( Score s )
	{
		HashSet<Integer> playedNotes = new HashSet<Integer>();
		for( Fragment fragment : s.fragments() )
		{
			if( fragment.getMusician().equals( id ) ) continue;
			for( Note note : fragment.getNotes() )
			{
				playedNotes.add( getDegreeForNote( note.getPitchNumber() ) );
			}
		}
		//for( int i : playedNotes ) { System.out.println( "> " + id + ": " + i ); }
		if( playedNotes.size() <= 2 ) 
		{
			playedNotes.add( pickRandomNote() );
			playedNotes.add( pickRandomNote() );
		}
		HashSet<Integer> potentialDegrees = getPotentialDegrees( playedNotes );
		HashSet<Integer> chosenDegrees = chooseDegrees( potentialDegrees );
		allowableNotes = chooseNotes( chosenDegrees );
	}

	static int pickRandomNote()
	{
		int i = (int)( Math.random() * degrees.length );
		int j = (int)( Math.random() * degrees[i].length );
		System.out.println( i + ", " + j );
		return degrees[i][j];
	}

	static HashSet<Integer> getPotentialDegrees( HashSet<Integer> played )
	{
		int scaleLength = degrees.length;
		HashSet<Integer> potentials = new HashSet<Integer>();
		for( int i : played )
		{
			potentials.add( ( i + 2 + scaleLength ) % scaleLength );//Degree a third above
			potentials.add( ( i - 2 + scaleLength ) % scaleLength );//Degree a third below
		}
		return potentials;
	}

	static HashSet<Integer> chooseDegrees( HashSet<Integer> potentials )
	{
		Vector<Integer> v = new Vector<Integer>( potentials );
		HashSet<Integer> chosen = new HashSet<Integer>();
		for( int i = 0; i < numChoices; i++ )
		while( chosen.size() < numChoices )
		{
			chosen.add( v.elementAt( (int)(Math.random() * v.size() ) ) );
		}
		return chosen;
	}

	static HashSet<Integer> chooseNotes( HashSet<Integer> chosenDegrees )
	{
		HashSet<Integer> chosen = new HashSet<Integer>();
		for( int deg : chosenDegrees )
		{
			//maybe sometimes add an octave?
			chosen.add( degrees[deg][ (int)(Math.random() * degrees[deg].length ) ] + (Math.random() > 0.7 ? 12 : 0 ) );
		}
		return chosen;
	}

	static int getDegreeForNote( int pitch )
	{
		for( int i = 0; i < degrees.length; i++ )
		{
			for( int j = 0; j < degrees[i].length; j++ )
			{
				if( degrees[i][j] == pitch ) return i;
			}
		}
		return -1;
	}
}
