package com.moseph.mra;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.moseph.music.*;

/**
Provides a set of useful constants for all MAXML applications
@author David Murray-Rust
@version $Revision$, $Date$
**/

public class MRAConstants
{
	protected static Logger log = MRAUtilities.getLogger();
	public static final double MERGE_THRESHOLD = 0.1;
	public static final double SPLIT_THRESHOLD = 0.01;
	public static final double ROOM_X = 10.0;
	public static final double ROOM_Y = 10.0;

	/*
	Slightly confusing; to handle triplets, the beat 
	is divided into 12, each of which has a string attached
	*/
	public static final String[] subdivisionStrings = { "", "", "e", "e", "e", "+", "+", "+", "uh", "uh", "uh", ""};
	public static final String[] sharpNoteNames = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
	public static final String[] flatNoteNames = { "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B" };
	public static final String[] volumeNames = { "very quiet", "quiet", "moderate", "loud", "very loud"};
	public static final String[] loudnessNames = { "ppp", "pp", "p", "mp", "mf", "f", "ff", "fff"};

	public static final int C3 = 60;
	public static final int Db3 = 61;
	public static final int D3 = 62;
	public static final int Eb3 = 63;
	public static final int E3 = 64;
	public static final int F3 = 65;
	public static final int Gb3 = 66;
	public static final int G3 = 67;
	public static final int Ab3 = 68;
	public static final int A3 = 69;
	public static final int Bb3 = 70;
	public static final int B3 = 71;
	public static final int C4 = 72;
	public static final int Db4 = 73;
	public static final int D4 = 74;
	public static final int Eb4 = 75;
	public static final int E4 = 76;
	public static final int F4 = 77;
	public static final int Gb4 = 78;
	public static final int G4 = 79;
	public static final int Ab4 = 80;
	public static final int A4 = 81;
	public static final int Bb4 = 82;
	public static final int B4 = 83;



}
