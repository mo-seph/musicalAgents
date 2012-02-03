package com.moseph.mra.test;

import static com.moseph.mra.test.TestUtilities.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;

import junit.framework.TestCase;

public class FeatureSetTest extends TestCase
{

	Musician bob = new Musician( "bob");
	Musician jim = new Musician( "jim");
	Musician me = new Musician( "me");
	MusicianInformation iBob = new MusicianInformation( bob );
	MusicianInformation iJim = new MusicianInformation( jim );
	MusicianInformation iMe = new MusicianInformation( me );
	Context context = new Context(iMe);
	FeatureSet features;
	Piece piece;
	
	

	protected void setUp() throws Exception
	{
		super.setUp();
		piece = getBasicPiece();
		
		context.setPiece( piece );
		features = context.getFeatures();
		me.setPosition( 0.0, 0.0 );
		jim.setPosition( 1.0, 0.0 );
		bob.setPosition( 0.0, 1.0 );
	}
	
	public void testSetPath()
	{
		context.setPath( iMe, new Path( "/main/a/b" ) );
		//GroupFeature ipaths = features.getOrCreate( "Path");
		GroupFeature ipaths = context.getFeature( "Path");
		System.out.println( ipaths.getClass() );
		GroupNumericFeature paths = (GroupNumericFeature)ipaths;
		//paths.setDistanceWeight( true );
		assertTrue( "We've just added ourself, so there should be a null weight", Double.isNaN( paths.getAverageValue() ) );
		
		double w1 = GroupNumericFeature.getWeightForSquaredDistance( 1.0 );
		context.setPath( iMe, new Path( "/main/a/c" ) );
		context.setPath( iBob, new Path( "/main/d/e"));
		assertEquals( "One in front gives w", w1, paths.getAverageValue() );
		context.setPath( iBob, new Path( "/main/a/b"));
		assertEquals( "One in behind gives -w", -w1, paths.getAverageValue() );
		context.setPath( iJim, new Path( "/main/d/e"));
		assertEquals( "One in front, one behind should give zero", 0.0, paths.getAverageValue() );
		context.setPath( iBob, new Path( "/main/d/e"));
		assertEquals( "Two in front gives w", w1, paths.getAverageValue() );
	}
	
	
}
