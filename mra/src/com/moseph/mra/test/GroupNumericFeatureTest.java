package com.moseph.mra.test;

import com.moseph.mra.Musician;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.GroupNumericFeature;
import com.moseph.mra.agent.attribute.*;

import junit.framework.TestCase;

public class GroupNumericFeatureTest extends TestCase
{
	MusicianInformation iBob = new MusicianInformation();
	MusicianInformation iJim = new MusicianInformation();
	MusicianInformation iMe = new MusicianInformation();
	Musician bob = new Musician( "bob");
	Musician jim = new Musician( "jim");
	Musician me = new Musician( "me");
	GroupNumericFeature features = new GroupNumericFeature<NumericFeature>( new ValuedAttribute(), iMe );

	protected void setUp() throws Exception
	{
		super.setUp();
		iBob.setMusician( bob );
		iJim.setMusician( jim );
		iMe.setMusician( me );
		me.setPosition( 0.0, 0.0 );
		jim.setPosition( 1.0, 0.0 );
		bob.setPosition( 0.0, 1.0 );
	}
	
	public void testAverage()
	{
		features.setIncludeSelf( true );
		features.setValue( iMe, new ValuedAttribute( 1.0 ));
		assertEquals( 1.0, features.getAverageValue() );
		features.setValue( iMe, new ValuedAttribute( 2.0 ));
		assertEquals( 2.0, features.getAverageValue() );
		features.setValue( iBob, new ValuedAttribute( 1.0 ));
		assertEquals( 1.5, features.getAverageValue() );
		features.setValue( iJim, new ValuedAttribute( 0.0 ));
		assertEquals( 1.0, features.getAverageValue() );
	}
	
	public void testWeightedAverage()
	{
		features.setIncludeSelf( true );
		features.setDistanceWeight( true );
		features.setValue( iMe, new ValuedAttribute( 1.0 ));
		assertEquals( 1.0, features.getAverageValue() );
		features.setValue( iMe, new ValuedAttribute( 0.0 ));
		features.setValue( iBob, new ValuedAttribute( 2.0 ));
		//Would be weight * 2.0, but two entries, so average is halved
		assertEquals( features.getDistanceWeight( iBob ), features.getAverageValue() );
		features.setValue( iJim, new ValuedAttribute( 1.0 ));
		double expected = features.getDistanceWeight( iBob ) * 2.0;
		expected += features.getDistanceWeight( iJim ) * 1.0;
		expected /= 3.0;
		assertEquals( expected, features.getAverageValue() );
	}

}
