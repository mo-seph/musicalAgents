package com.moseph.mra.test.noCoverage;

import java.io.*;
import java.util.*;

import junit.framework.TestCase;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.GroupNumericFeature;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.parser.MRAParser;
import static com.moseph.mra.agent.ScoreAgent.*;
import static com.moseph.mra.MRAUtilities.*;

public class DistanceWeighting extends TestCase
{
	Piece piece;
	Collection<MusicianInformation> nearMusicians;
	Collection<MusicianInformation> farMusicians;
	Musician me;
	MusicianInformation iMe;
	Path p ;
	GroupNumericFeature featuresA;
	GroupNumericFeature featuresB;
	Context c;
	int cInd = 0;
	
	public void setUp()
	{
		FileInputStream file;
		me = new Musician( "me" );
		iMe = new MusicianInformation();
		iMe.setMusician( me );
		
		try
		{
			if( piece == null )
			{
				file = new FileInputStream( "examples/InC.mra" );
				MRAParser parser = new MRAParser( file );
				piece = parser.runParser();
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		me.setX( 0.0 );
		me.setY( 0.0 );
		p = new Path( "/main/s1");
		nearMusicians = new Vector<MusicianInformation>();
		String[] names = { "a1", "a2", "a3", "a4" };
		String[] paths = { "/main/s4", "/main/s3", "/main/s6", "/main/s3" };
		double[][] coords = { { 0.5, 0.5 }, { 0.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 1.0 } };
		for( int i = 0; i < 4; i++ ) nearMusicians.add( getMusicianInformation( names[i], paths[i], coords[i][0], coords[i][1] ) );
		
		c = new Context(iMe);
		c.setPiece( piece);
		featuresA = new GroupNumericFeature(new PathFeature(p, piece ), iMe );
		featuresA.setDistanceWeight( true );
		featuresA.setRelative( true );
		featuresB = new GroupNumericFeature(new PathFeature(p, piece ), iMe );
		featuresB.setDistanceWeight( true );
		featuresB.setRelative( true );
	}
	
	MusicianInformation getMusicianInformation( String name, String path, double x, double y)
	{
		
		Musician mus = new Musician( name );
		mus.setX( x );
		mus.setY( y );
		MusicianInformation musInf = new MusicianInformation();
		musInf.setMusician( mus );
		musInf.setPath( new Path( path ) );
		return musInf;
	}
	
	void addMusiciansAtDistance( GroupNumericFeature musicians, String path, double distance, int number )
	{
		for( int i = 0; i < number; i++ )
			addMusicianAtDistance( musicians, path, distance );
	}
	
	void addMusicianAtDistance( GroupNumericFeature musicians, String path, double distance )
	{
		double theta = Math.random() * Math.PI * 2;
		String name = "a" + cInd++;
		MusicianInformation i = 
			getMusicianInformation( name, path, distance * Math.sin( theta ), distance * Math.cos( theta ) );
		NumericFeature f = new PathFeature( new Path( path ), piece );
		musicians.setValue( i, f );
	}
	
	/**
	 * Tests that adding many musicians playing the same thing at the same distance
	 * has a constant average
	 *
	 */
	public void testInvariantWithNumberOfMusicians()
	{
		//featuresA.setDistanceWeight( true );
		featuresA.setRelative( true );
		featuresA.setValue( iMe, new PathFeature( new Path( "/main/s1"), piece  ));
		addMusicianAtDistance( featuresA, "/main/s2", 1.0 );
		double weight = featuresA.getAverageValue();
		double expected = GroupNumericFeature.getWeightForSquaredDistance( 1.0 );
		assertEquals( "Someone who is 1 away playing 1 ahead gives correct weight",
					expected, weight, 0.001 );
		addMusicianAtDistance( featuresA, "/main/s2", 1.0 );
		double weightb = featuresA.getAverageValue();
		assertEquals( "Another musician the same distance away playing the same section doesn't affect the result", 
				expected, weightb, 0.001  );
		addMusiciansAtDistance( featuresA, "/main/s2", 1.0, 20 );
		double weightc = featuresA.getAverageValue();
		assertEquals( "Many musicians at unit distance give the same result", 
				weight, weightc, 0.0001  );
		
	}
	
	public void testNearerHasMoreInfluence()
	{
		addMusicianAtDistance( featuresA, "/main/s2", 1.0 );
		addMusicianAtDistance( featuresA, "/main/s3", 10.0 );
		addMusicianAtDistance( featuresB, "/main/s2", 10.0 );
		addMusicianAtDistance( featuresB, "/main/s3", 1.0 );
		double weightA = featuresA.getAverageValue();
		double weightB = featuresB.getAverageValue();
		System.out.printf( "wA: %f, wB: %f", weightA, weightB );
		assertTrue( weightB > weightA );
	}
	
	/*
	 * Don't understand this test - how did it work? And it *shouldn't* be
	 * distance invariant...
	public void testInvarianceWithDistance()
	{
		addMusicianAtDistance( featuresA, "/main/s2", 1.0);
		addMusicianAtDistance( featuresB, "/main/s2", 10.0);
		double nearWeight = featuresA.getAverage();
		double farWeight = featuresB.getAverage();
		System.out.printf( "Near: %s, Far: %s", nearWeight, farWeight );
		assertEquals( "Distance invariant", farWeight, nearWeight );
	}
	*/
	
	
	public void testLinearSkipWeight()
	{
		featuresA.setValue( iMe, new PathFeature( new Path( "/main/s2"), piece  ));
		addMusiciansAtDistance( featuresA, "/main/s2", 1.0, 4 );
		System.out.println( "++++");
	
		Vector<MusicianInformation> mus = new Vector<MusicianInformation>( featuresA.getOtherMusicians() );
		Path p1 = new Path( "/main/s1");
		Path p3 = new Path( "/main/s3");
		Path p4 = new Path( "/main/s4");
		double w = GroupNumericFeature.getWeightForSquaredDistance( 1.0 );
		assertEquals( "People playing same path have zero skip", 0.0, featuresA.getAverageValue() );
		for( MusicianInformation i : mus ) featuresA.setValue( i, new PathFeature( p1, piece ));
		
		assertEquals( "People playing previous path at unit distance give -1 * w(1) skip", 
				-1.0 * w, featuresA.getAverageValue(), 0.001 );
		System.out.println( "*****\n*****\n");
		for( MusicianInformation i : mus ) featuresA.setValue( i, new PathFeature( p3, piece ));
		assertEquals( "People playing next path at unit distance give w(1) skip",
				w, featuresA.getAverageValue(), 0.001 );
		featuresA.setValue( mus.get(0), new PathFeature( p1, piece ));
		featuresA.setValue( mus.get(1), new PathFeature( p1, piece ));
		assertEquals( "Equal number playing next+prev path gives zero skip",
				0.0, featuresA.getAverageValue(), 0.001 );
		for( MusicianInformation i : mus ) featuresA.setValue( i, new PathFeature( p3, piece ));
		double w3 = featuresA.getAverageValue();
		featuresA.setValue( mus.get(0), new PathFeature( p4, piece ));
		double w4 = featuresA.getAverageValue();
		assertTrue( "moving a musician on to next path increases skip weight", w4 > w3 );
	}
	
	public void testDistanceWeighting()
	{
		double a = GroupNumericFeature.getWeightForSquaredDistance( 0.0 );
		double b = GroupNumericFeature.getWeightForSquaredDistance( 1.0 );
		double c = GroupNumericFeature.getWeightForSquaredDistance( 10.0 );
	
		assertTrue("Zero distances don't have infinite weighting", a < Double.MAX_VALUE   );
		assertTrue( "Further distances have a lower weighting", b > c );
	}
	
	public void testSigmoids()
	{
		double[] p0s = { 0.1, 0.2, 0.3 };
		double[] wBounds = { 1.0, 2.0, 3.0 };
		double pBound = 0.9;
		for( double p0 : p0s  )
		{
			for( double wBound : wBounds  )
			{
				assertEquals( "p0 correct for various values of p0 and wBound",
						p0, sigmoidalProbability( 0.0, p0, wBound, pBound ), 0.001 );
				assertEquals( "pBound correct for various values of p0 and wBound",
						pBound, sigmoidalProbability( wBound, p0, wBound, pBound ), 0.001 );
			}
		}
		assertEquals( 0.95, sigmoidalProbability( 3.0, 0.1, 3.0 ), 0.001);
		assertEquals( 0.95, sigmoidalProbability( 3.0, 0.9, 3.0 ), 0.001);
	}
}
