package com.moseph.mra.agent;

import java.util.Vector;

public class SimpleChordAgent extends MusicalAgent
{
	static final int[] notes = { 64, 67, 71, 74 };
	protected void initialise()
	{
		super.initialise();
	}
	
	void gotInput( double start, double length )
	{
		//super.gotInput( start, length );
		for( double d = 0.0; d < length; d += 0.5 )
		{
			for( int noteNumber : notes )
				outputBuffer.addNote( start + length + d + ( 0.3 * Math.random() ), noteNumber, 0.5, 0.5  );
			
		}
	}

		
}
