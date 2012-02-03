package com.moseph.mra.agent;

import java.io.FileInputStream;

import com.moseph.mra.Piece;
import com.moseph.mra.parser.MRAParser;

public class AgentUtilities
{
	static int numAgents = 1;
	
	public static int getAgentNumber() { return numAgents++; };
	
	public static Piece parseFile( String filename )
	{
		if( filename == null ) return null;
		try
		{
			FileInputStream file = new FileInputStream( filename );
			MRAParser parser = new MRAParser( file );
			return parser.runParser();
		}
		catch( Exception e )
		{
			System.out.println( "Could not create piece: " + e );
			return null;
		}
	}

	public static final String[] instrumentNames =
	{
		"AcousticPiano", 
		"PizzicatoStrings",
		"Trombone",
		"MusicBox",
		"Clarinet", 
		"Vibraphone", 
		"TubularBell",
		"AcousticGuitarsteel",
		//"ElectricGuitarjazz",
		"AcousticBass"
	};
}
