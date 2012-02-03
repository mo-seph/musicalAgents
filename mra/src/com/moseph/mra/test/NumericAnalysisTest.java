package com.moseph.mra.test;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;

import junit.framework.TestCase;

public class NumericAnalysisTest extends TestCase
{
	MusicianInformation me = new MusicianInformation( new Musician( "bob" ));
	MusicianInformation him = new MusicianInformation( new Musician( "norm" ));
	Piece p = TestUtilities.getBasicPiece();
	Context context = new Context( me  );
	FeatureSet features = context.getFeatures();
	DensityAnalysis density = new DensityAnalysis(context, 0.0);
	DynamicAverageAnalyser dynamics = new DynamicAverageAnalyser(context, 0.0);
	Score s = new Score();
	Fragment activeFragment;
	
	public void setUp()
	{
		context.setPiece( p );
		Fragment f = new Fragment( 1.0 );
		f.setMusician( him.getMusician() );
		f.setInstrument( new Instrument( "AcousticPiano") );
		s.add( f );
		activeFragment = s.fragments().iterator().next();
		density.init();
		dynamics.init();
		density.setMinNotes( 0 );
		dynamics.setMinNotes( 0 );
		
	}
	
	public void testCorrectFeatureSets()
	{
		features.listFeatures();
		assertTrue( features.contains( "Density"));
		
	}

	public void testDynamicsAndDensity()
	{
		double densityValue = 0.0;
		double dynamicsValue = 0.0;
		
		
		System.out.println( "Analysing: " + s );
		density.analyse( s );
		assertEquals( features.getOrCreate( "Density"), density.getFeatureSet() );
		densityValue = ( (GroupNumericFeature) features.getOrCreate( "Density") ).getAverageValue();
		assertEquals( 0.0, densityValue );
		
		dynamics.analyse( s );
		dynamicsValue = ( (GroupNumericFeature) features.getOrCreate( "Dynamics") ).getAverageValue();
		//assertEquals( 0.0, dynamicsValue );
		
		assertEquals( features.getOrCreate( "Density"), density.getFeatureSet() );
		activeFragment.addNote( 0.0, 64, 0.5, 1.0 );
		//System.out.println( activeFragment );
		density.analyse( s );
		densityValue = ( (GroupNumericFeature) features.getOrCreate( "Density") ).getAverageValue();
		assertEquals( 1.0, densityValue );
		
		dynamics.analyse( s );
		dynamicsValue = ( (GroupNumericFeature) features.getOrCreate( "Dynamics") ).getAverageValue();
		assertEquals( 0.5, dynamicsValue );
	}

}
