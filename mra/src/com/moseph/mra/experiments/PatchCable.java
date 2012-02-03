package com.moseph.mra.experiments;

import static com.moseph.mra.midi.MidiUtilities.*;
public class PatchCable
{
	public static void main( String[] args )
	{
		String input = "AMT";
		if( args.length > 0 ) input = args[0];
		System.out.println( "Args length: " + args.length );
		System.out.println( "Opening device " + input );
		for( int i = 0; i < 7; i++ )
			for( int j = 0; j < 7; j++ )
				patchCable( input, i, input, j );
		waitForEnter();
		
	}

}
