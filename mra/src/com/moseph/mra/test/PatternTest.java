package com.moseph.mra.test;

import java.util.Vector;

import junit.framework.TestCase;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;

public class PatternTest extends TestCase
{
	MusicianInformation me = new MusicianInformation( new Musician( "bob" ));
	MusicianInformation him = new MusicianInformation( new Musician( "norm" ));
	Piece p = TestUtilities.getBasicPiece();
	Context context = new Context( me  );
	FeatureSet features = context.getFeatures();
	Score s = new Score();
	Fragment activeFragment;
	AnalysisSystem analysis;
	
	public void setUp()
	{
		context.setQuantisation( 4.0 );
		context.setPiece( p );
		context.setAttribute( "PatternLength", "2.0" );
		Fragment f = new Fragment( 1.0 );
		f.setMusician( him.getMusician() );
		f.setInstrument( new Instrument( "AcousticPiano") );
		s.add( f );
		activeFragment = s.fragments().iterator().next();
		analysis = new AnalysisSystem( context, null, 
				new String[] { "DynamicRegression", "DynamicAverage", "DynamicPattern", "DisplacementPattern" } );
		analysis.setForceImmediateAnalysis( true );
		analysis.setMinNotes( 0 );
	}	
	
	public void testEqualNotes()
	{
		activeFragment.setLength( 2.0 );
		activeFragment.addNote( 0.0, 60, 0.5, 0.5 );
		activeFragment.addNote( 0.5, 60, 0.5, 0.5 );
		analysis.analyse( s );
		PatternAttribute acc = (PatternAttribute)features.getValue( DynamicPatternAnalyser.featureName, him );
		System.out.println( acc );
		assertEquals( 0.0, acc.getValue( 0.0 ));
		assertNull( acc.getFeature( 1.0 ));
	}
		
	public void testUnequalNotes()
	{
		activeFragment.addNote( 0.0, 60, 0.0, 0.5 );
		activeFragment.addNote( 0.5, 60, 1.0, 0.5 );
		activeFragment.addNote( 1.0, 60, 0.0, 0.5 );
		activeFragment.addNote( 1.5, 60, 1.0, 0.5 );
		analysis.analyse( s );
		PatternAttribute acc = (PatternAttribute)features.getValue( DynamicPatternAnalyser.featureName, him );
		DualValuedAttribute regression = (DualValuedAttribute)features.getValue( DynamicRegressionAnalyser.featureName, him );
		double start = regression.getValue1();
		double end = start + regression.getValue2() * activeFragment.getLength();
		CurveContainer slope = new CurveContainer( activeFragment.getLength(), start, end );
		assertEquals( 0.0 - slope.sample( 0.0 ), acc.getValue( 0.0 ));
		assertEquals( 1.0 - slope.sample( 0.5 ), acc.getValue( 0.5 ));
		assertEquals( 0.0 - slope.sample( 1.0 ), acc.getValue( 1.0 ));
		assertEquals( 1.0 - slope.sample( 1.5 ), acc.getValue( 1.5 ));
		
	}

	public void testDisplacements()
	{
		double[][] notes =
		{
				{ 0.0, 0.05 },
				{0.25, -0.05 },
				{ 1.0, -0.05 }
		};
		for( double[] note : notes )
			activeFragment.addNote( note[0] + note[1], 60, 1.0, 0.5 );
		
		analysis.analyse( s );
		PatternAttribute acc = (PatternAttribute)features.getValue( DisplacementPatternAnalyser.featureName, him );
		
		DualValuedAttribute regression = (DualValuedAttribute)features.getValue( DisplacementRegressionAnalyser.featureName, him );
		double start = regression.getValue1();
		double end = start + regression.getValue2() * activeFragment.getLength();
		CurveContainer slope = new CurveContainer( activeFragment.getLength(), start, end );
		System.out.println( acc );
		
		for( double[] note : notes )
		{
			double expected = note[1] - slope.sample( note[0] );
			assertEquals( expected,  acc.getValue( note[0] ), 0.001 );
		}
		
		/*
		activeFragment.addNote( 0.0, 60, 1.0, 0.5 );
		analysis.analyse( s );
		acc = (PatternAttribute)features.getValue( DisplacementPatternAnalyser.featureName, him );
		assertEquals( 0.025, acc.getValue( 0.0 ), 0.0001);
		*/
	}
	
	public void testAveraging()
	{
		PatternAttribute a = new PatternAttribute( 2, 2 );
		PatternAttribute b = new PatternAttribute( 2, 2 );
		Vector<PatternAttribute> others = new Vector<PatternAttribute>();
		others.add( a );
		others.add( b );
		
		
		
		a.setValue( 0.0, new ValuedAttribute( 1.0 ));
		a.setValue( 0.5, new ValuedAttribute( 1.0 ));
		a.setValue( 1.0, new ValuedAttribute( 0.0 ));
		
		b.setValue( 0.0, new ValuedAttribute( 0.0 ));
		b.setValue( 1.5, new ValuedAttribute( 0.5 ));
		b.setValue( 1.0, new ValuedAttribute( 1.0 ));
		
		PatternAttribute c= new PatternAttribute( 2, 2 );
		c.setValueToAverageOf( others );
		
		//System.out.println( c );
		assertEquals( 0.5, c.getValue( 0.0 ) );
	}
	
	public void testZeroLength()
	{
		PatternAttribute p = new PatternAttribute( 0, 5 );
		try
		{
			p.setValue( 1.0, new ValuedAttribute( 0.5 ) );
		} catch (Exception e )
		{
			fail( "Couldn't set info on a zero length pattern: " + e );
		}
		
	}
	
	public void testCreation()
	{
		double quantise = 4.0;
		double[] values = { 0.0, 1.0, 2.0, 3.0, 4.0 };
		PatternAttribute a = new PatternAttribute( 1.25, quantise );
		PatternAttribute b = new PatternAttribute( quantise, values );
		assertFalse( a.sloppyCompare( b, 0.001 ) );
		
		for( int i = 0; i < values.length; i++ ) a.setValue( i/quantise, values[i] );
		assertTrue( a.sloppyCompare( b, 0.001 ) );
	}
	
	public void testAddTo()
	{
		PatternAttribute original = new PatternAttribute( 4, 1 );
		original.setValue( 2.0, 1.0 );
		
		PatternAttribute next = new PatternAttribute( 10, 1 );
		next.setValue( 3, 2 );
		original = original.getToLength( next );
		original.overwriteWith( next, 0 );
		
		double[][] expected = { { 2, 1 }, {3, 2}, {6, 1} };
		doTestPattern( original, expected );
		System.out.println( original.getValue( 1.0 ) );
		
		PatternAttribute num2 = new PatternAttribute( 10, 1 );
		num2.setValue( 2, 0.5 );
		original.overwriteWith( num2, 0.0 );
		expected = new double[][] { { 2, 0.5 }, {3, 2}, {6, 1} };
		doTestPattern( original, expected );
	}
	
	void doTestPattern( PatternAttribute p, double[][] expected )
	{
		for( double[] exp : expected )
			assertEquals( exp[1], p.getValue( exp[0] ) );
	}
}
