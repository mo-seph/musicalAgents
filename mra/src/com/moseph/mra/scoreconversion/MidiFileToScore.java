package com.moseph.mra.scoreconversion;

import java.io.*;
import java.util.*;

import javax.sound.midi.*;

import com.moseph.mra.*;
import com.moseph.mra.midi.MidiFileReader;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class MidiFileToScore
{
	final static String dir = "examples/CantoOstinato/";
	final static String outputDir = "scores/CantoOstinato/";
	
	public static void main( String[] args )
	{
		new MidiFileToScore().run();
	}
	
	public void run()
	{
		runRange( "69" );
		runRange( "1-9" );
		runRange( "88" );
	}
	
	void runRange( String filePrefix )
	{
		List<Fragment> LH = sectionFile( dir + filePrefix + "-LH.mid", getDefs( filePrefix ) );
		List<Fragment> RH = sectionFile( dir + filePrefix + "-RH.mid", getDefs( filePrefix ) );
		Piece p = new Piece( "CantoOstinato" + filePrefix );
		Section main = new Section( "main");
		p.addChild( main );
		for( int i = 0; i < LH.size(); i++ )
		{
			System.out.println( "_________\n");
			Fragment lh = LH.get( i );
			Fragment rh = RH.get( i );
			String secName = lh.getName();
			Section sec = new Section( secName );
			lh.setName( "LH" );
			rh.setName( "RH" );
			sec.addChannel( lh );
			sec.addChannel( rh );
			//System.out.println( sec.printAllNotes() );
			main.addChild( sec );
		}
		System.out.println( p.printAllNotes() );
		String filename = outputDir + filePrefix + ".mra";
		try
		{
			FileOutputStream fos = new FileOutputStream( filename );
			PrintWriter bos = new PrintWriter( fos );
			bos.print( p.printAllNotes() );
			bos.close();
			try
			{
				fos.close();
			} catch (IOException e)
			{
				System.out.println( "COuldn't close file: " + filename + ": " + e );
				e.printStackTrace();
			}
		} catch (FileNotFoundException e)
		{
			System.out.println( "Couldn't open output file (" + filename + ") : " + e );
			e.printStackTrace();
		}
	}
	
	List<Fragment> sectionFile( String filename, List<SectionDefinition> defs )
	{
		Fragment f = fileToFragment( filename );
		return sectionise( f, defs );
	}
	
	List<Fragment> sectionise( Fragment f, List<SectionDefinition> defs )
	{
		List<Fragment> frags = new Vector<Fragment>();
		for( int i = 0; i < defs.size(); i++ )
		{
			SectionDefinition def = defs.get( i );
			double start = def.start * 2;
			double end = f.getLength();
			if( i < defs.size() - 1 ) end = defs.get( i+1 ).start * 2;
			System.out.println( def.name + " from " + start + " to " + end );
			Fragment section = f.copyChunk( start, end );
			section.setName( def.name );
			frags.add( section );
		}
		return frags;
	}
	
	Fragment fileToFragment( String filename )
	{
		return fileToFragment( filename, 0 );
	}
	
	Fragment fileToFragment( String filename, int index )
	{
		MidiFileReader fileReader = new MidiFileReader();
		try
		{
			fileReader.readFile( filename, 0.2 );
		} catch (Exception e)
		{
			System.out.println( "Couldn't read MIDI file: (" + filename + ") " + e );
			e.printStackTrace();
		}
		List<Fragment> fragments = fileReader.getFragments();
		return fragments.get( 0 );
	}
	
	class SectionDefinition
	{
		public double start;
		public String name;
		public SectionDefinition( String name, double start )
		{
			this.start = start;
			this.name = name;
		}
	}
	
	List<SectionDefinition> get1Defs()
	{
		List<SectionDefinition> defs = new Vector<SectionDefinition>();
		defs.add( new SectionDefinition( "s2", 0 ) );
		defs.add( new SectionDefinition( "s3", 1 ) );
		defs.add( new SectionDefinition( "s4", 2 ) );
		defs.add( new SectionDefinition( "s5", 3 ) );
		defs.add( new SectionDefinition( "s6", 4 ) );
		defs.add( new SectionDefinition( "s7", 5 ) );
		defs.add( new SectionDefinition( "s8", 6 ) );
		defs.add( new SectionDefinition( "s9", 8 ) );
		defs.add( new SectionDefinition( "s9b", 10 ) );
		defs.add( new SectionDefinition( "s10", 11 ) );
		defs.add( new SectionDefinition( "s10b", 13 ) );
		defs.add( new SectionDefinition( "s11", 14 ) );
		defs.add( new SectionDefinition( "s11b", 16 ) );
		defs.add( new SectionDefinition( "s12", 17 ) );
		return defs;
	}
	
	List<SectionDefinition> get69Defs()
	{
		List<SectionDefinition> defs = new Vector<SectionDefinition>();
		defs.add( new SectionDefinition( "s69", 0 ) );
		defs.add( new SectionDefinition( "s70", 1 ) );
		defs.add( new SectionDefinition( "s71", 2 ) );
		defs.add( new SectionDefinition( "s72", 3 ) );
		defs.add( new SectionDefinition( "s73", 4 ) );
		defs.add( new SectionDefinition( "s74", 5 ) );
		defs.add( new SectionDefinition( "s75", 14 ) );
		defs.add( new SectionDefinition( "s76", 18 ) );
		defs.add( new SectionDefinition( "s77", 22 ) );
		defs.add( new SectionDefinition( "s78", 26 ) );
		return defs;
	}

	List<SectionDefinition> get88Defs()
	{
		List<SectionDefinition> defs = new Vector<SectionDefinition>();
		defs.add( new SectionDefinition( "s88A1", 0 ) );
		defs.add( new SectionDefinition( "s88A2", 1 ) );
		defs.add( new SectionDefinition( "s88A3", 2 ) );
		defs.add( new SectionDefinition( "s88B1", 3 ) );
		defs.add( new SectionDefinition( "s88A4", 5 ) );
		defs.add( new SectionDefinition( "s88B2", 6 ) );
		defs.add( new SectionDefinition( "s88A5", 8 ) );
		defs.add( new SectionDefinition( "s88B3", 9 ) );
		defs.add( new SectionDefinition( "s88A6", 11 ) );
		defs.add( new SectionDefinition( "s88A7", 12 ) );
		defs.add( new SectionDefinition( "s88B4", 13 ) );
		defs.add( new SectionDefinition( "s88A8", 15 ) );
		defs.add( new SectionDefinition( "s88C1", 16 ) );
		defs.add( new SectionDefinition( "s88A9", 18 ) );
		defs.add( new SectionDefinition( "s88B5", 19 ) );
		defs.add( new SectionDefinition( "s88A10", 21 ) );
		defs.add( new SectionDefinition( "s88C2", 22 ) );
		defs.add( new SectionDefinition( "s88C3", 24 ) );
		defs.add( new SectionDefinition( "s88A11", 26 ) );
		return defs;
	}
	
	List<SectionDefinition> getDefs( String file )
	{
		if( file.equalsIgnoreCase( "1-9" )) return get1Defs();
		if( file.equalsIgnoreCase( "69" )) return get69Defs();
		if( file.equalsIgnoreCase( "88" )) return get88Defs();
		return null;
	}
}
