package com.moseph.mra.test;

import com.moseph.mra.*;
import com.moseph.mra.agent.MusicianInformation;
import com.moseph.mra.agent.analyser.GroupNumericFeature;
import com.moseph.mra.agent.attribute.*;

public class TestUtilities
{

	static int cInd = 0;
	
	public static MusicianInformation getMusicianInformation( String name, String path, double x, double y)
	{
		
		Musician mus = new Musician( name );
		mus.setX( x );
		mus.setY( y );
		MusicianInformation musInf = new MusicianInformation();
		musInf.setMusician( mus );
		musInf.setPath( new Path( path ) );
		return musInf;
	}
	
	public static void addMusiciansAtDistance( GroupNumericFeature musicians, String path, double distance, int number, Piece piece )
	{
		for( int i = 0; i < number; i++ )
			addMusicianAtDistance( musicians, path, distance, piece );
	}
	
	public static void addMusicianAtDistance( GroupNumericFeature musicians, String path, double distance, Piece piece )
	{
		double theta = Math.random() * Math.PI * 2;
		String name = "a" + cInd++;
		MusicianInformation i = 
			getMusicianInformation( name, path, distance * Math.sin( theta ), distance * Math.cos( theta ) );
		NumericFeature f = new PathFeature( new Path( path ), piece );
		musicians.setValue( i, f );
	}
	
	/**
	 * Returns a basic piece to use:
	 * test (Piece)
	 *   |
	 * main
	 *   ^ 
	 *  | |
	 *  a d
	 *  ^ |
	 *  bce 
	 * @return
	 */
	public static Piece getBasicPiece()
	{
		Piece piece = new Piece( "test");
		Section main = new Section( "main");
		Section a = new Section( "a");
		Section b = new Section( "b");
		Section c = new Section( "c");
		Section d = new Section( "d");
		Section e = new Section( "e");
		
		main.addChild( a );
		main.addChild( d );
		a.addChild( b );
		a.addChild( c );
		d.addChild( e );
		piece.addChild( main );
		piece.activate();
		main.activate();
		a.activate();
		d.activate();
		return piece;
		
	}
}
