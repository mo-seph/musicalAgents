package com.moseph.mra.test;

import java.util.*;

import junit.framework.TestCase;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.PatternAttribute;
import com.moseph.mra.agent.reasoning.*;
import com.sun.org.apache.bcel.internal.generic.BALOAD;

import static java.lang.Math.*;

/**
 * This test should test the entirety of the analysis system and the acoustic mirror reasoner
 * To do this, we will create a piece of music, with certain known characteristics and feed it piecewise
 * into the analysis system. After each piece, we will extract a plan, and check it against the desired
 * value
 * @author dave
 *
 */
public class AcousticMirrorFullTest extends TestCase
{
	boolean gotError = false;
	boolean stopOnErrors = false;
	boolean testCurves = true;
	boolean testPatterns = true;
	boolean testDynamics = true;
	boolean testLength = true;
	boolean testTiming = true;
	double quantise = 4.0;
	double fragmentSize = 1.0;
	double LEN_COMPARE_THRESHOLD = 0.02;
	double TIM_COMPARE_THRESHOLD = 0.02;
	double DYN_COMPARE_THRESHOLD = 0.05;
	MusicianInformation me = new MusicianInformation( new Musician("jimmy") );
	Context context = new Context( me );
	AcousticMirrorReasoner reasoner;
	AnalysisSystem analyser; 
	FeatureSet features;
	String[] analysers = {
			"DisplacementAverage",
			"DisplacementChange",
			"DisplacementPattern",
			"DisplacementVariance",
			"DynamicAverage",
			"DynamicChange",
			"DynamicPattern",
			"DynamicVariance",
			"LengthAverage",
			"LengthChange",
			"LengthPattern",
			"LengthVariance"
			};
	
	public void doSetUp( double quantisation, int minNotes, boolean stopOnErr, double patternLength )
	{
		context.setQuantisation( quantisation );
		context.setAttribute( "PatternLength", patternLength + "" );
		reasoner = new AcousticMirrorReasoner( context, null );
		ForceAnalysisSettings settings = new ForceAnalysisSettings();
		settings.setForceDistanceWeighted( false );
		settings.setForceIncludeSelf( true );
		analyser = new AnalysisSystem( context, settings, analysers );
		
		//analyser.setForceImmediateAnalysis( true );
		analyser.setAnalyseSelf( true );
		if( minNotes >= 0 ) analyser.setMinNotes( minNotes );
		stopOnErrors = stopOnErr;
		//reasoner.setCopyCurvesExactly( true );
		features = context.getFeatures();
	}
	
	public void testNoInput()
	{
		Fragment toAnalyse = new Fragment();
		doSetUp( quantise, -1, true, 1.0 );
		features.printFeatures();
		System.out.println( "Running test on fragment: " + toAnalyse );
		List<RenderPlan> plans = new Vector<RenderPlan>();
		double[] dynCurve = new double[] {AcousticMirrorReasoner.DYNAMICS_DEFAULT, AcousticMirrorReasoner.DYNAMICS_DEFAULT}; //Dynamics curve
		double[] dynPat = new double[] { Double.NaN, Double.NaN, Double.NaN, Double.NaN }; //Dynamics Pattern
		double[] lenCurve = new double[] {AcousticMirrorReasoner.LENGTH_DEFAULT, AcousticMirrorReasoner.LENGTH_DEFAULT}; //Length curve
		double[] lenPat = new double[] {Double.NaN, Double.NaN, Double.NaN, Double.NaN }; //Length Pattern - won't find last value as it overlaps end...
		double[] timCurve = new double[] {AcousticMirrorReasoner.TIMING_DEFAULT, AcousticMirrorReasoner.TIMING_DEFAULT }; //Timing Curve - first one is the average of the first frame
		double[] timPat = new double[] {Double.NaN, Double.NaN, Double.NaN, Double.NaN }; //Timing Pattern
		
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//2-3 (Dyn patt switches polarity, timPat enters)
		runPlan( toAnalyse, plans, fragmentSize );
	}
	
	public void testNoisy()
	{
		for( int i = 0; i < 1000; i++ )
		{
			double original = ( random() - 0.5 ) * 20;
			double noiseLevel = random();
			double noisy = noisy( original, noiseLevel );
			System.out.println( original + " at " + noiseLevel + " gives: " + noisy );
			assertTrue( noisy < original + noiseLevel );
			assertTrue( noisy > original - noiseLevel );
		}
	}
	
	public void testPatternsOnAllAspects()
	{
		double dynamicsNoise = 0.0;
		double timingNoise = 0.0;
		double lengthNoise = 0.0;
		Fragment toAnalyse = new Fragment();
		
		double[] pdp = new double[] { 0.5, -0.5, -0.5, 0.5 };
		PatternAttribute playingDynPat = new PatternAttribute( quantise, pdp );
		double[] plp = new double[] { 0.0, 0.0, 0.0, 0.0 };
		PatternAttribute playingLenPat = new PatternAttribute( quantise, plp );
		double[] ptp = new double[] { 0.0, 0.0, 0.0, 0.0 };
		PatternAttribute playingTimPat = new PatternAttribute( quantise, ptp );
		
		CurveContainer dynamicsCurve = new CurveContainer();
		dynamicsCurve.addEvent( new Curve( 0.0, 3.9, 0.6, 0.6 ) );
		dynamicsCurve.addEvent( new Curve( 6.0, 11.9, 0.0, 0.0 ) );
		dynamicsCurve.addEvent( new Curve( 12.0, 15.9, 0.8, 0.8 ) );
		doSetUp( quantise, 1, true, 1.0 );
		
		double noteStep = 0.25;
		double noteLength = 0.20;
		double timingBase = 0.0;
		double lengthBase = 1.0;
		
		for( double i = 0; i < 2.0; i+= noteStep )
		{
			if( dynamicsCurve.sample( i ) < 0.01 ) continue;
			double velocity = noisy( dynamicsCurve.sample( i ) + playingDynPat.getValue( i ), dynamicsNoise );
			double onset = noisy( i + timingBase + playingTimPat.getValue( i ), timingNoise );
			double length = noisy( ( lengthBase + playingLenPat.getValue( i ) ) * noteLength, lengthNoise );
			int pitch = (int)(i * quantise);
			toAnalyse.addNote( onset, pitch, velocity, length );
		}
		
		pdp = new double[] { -0.5, 0.5, 0.5, -0.5 };
		playingDynPat = new PatternAttribute( quantise, pdp );
		
		ptp = new double[] { 0.05, -0.05, -0.05, 0.05 };
		playingTimPat = new PatternAttribute( quantise, ptp );
		
		for( double i = 2.0; i < 12.0; i+= noteStep )
		{
			if( dynamicsCurve.sample( i ) < 0.01 ) continue;
			double velocity = noisy( dynamicsCurve.sample( i ) + playingDynPat.getValue( i ), dynamicsNoise );
			double onset = noisy( i + timingBase + playingTimPat.getValue( i ), timingNoise );
			double length = noisy( ( lengthBase + playingLenPat.getValue( i ) ) * noteLength, lengthNoise );
			int pitch = (int)(i * quantise);
			toAnalyse.addNote( onset, pitch, velocity, length );
		}
		
		pdp = new double[] { -0.1, 0.0, 0.0, -0.1 };
		playingDynPat = new PatternAttribute( quantise, pdp );
		ptp = new double[] { 0.0, -0.0, -0.0, 0.0 };
		playingTimPat = new PatternAttribute( quantise, ptp );
		
		for( double i = 12.0; i < 20.0; i+= noteStep )
		{
			if( dynamicsCurve.sample( i ) < 0.01 ) continue;
			double velocity = noisy( dynamicsCurve.sample( i ) + playingDynPat.getValue( i ), dynamicsNoise );
			double onset = noisy( i + timingBase + playingTimPat.getValue( i ), timingNoise );
			double length = noisy( ( lengthBase + playingLenPat.getValue( i ) ) * noteLength, lengthNoise );
			int pitch = (int)(i * quantise);
			toAnalyse.addNote( onset, pitch, velocity, length );
		}
		
		System.out.println( "Running test on fragment: " + toAnalyse );
		List<RenderPlan> plans = new Vector<RenderPlan>();
		double[] dynCurve = new double[] {0.6,0.6}; //Dynamics curve
		double[] dynPat = new double[] { 0.0, 0.0, 0.0, 0.0 }; //Dynamics Pattern
		double[] lenCurve = new double[] {0.8, 0.8}; //Length curve
		double[] lenPat = new double[] {0.0, 0.0, 0.0, 0.0 }; //Length Pattern - won't find last value as it overlaps end...
		double[] timCurve = new double[] {0.0, 0.0 }; //Timing Curve - first one is the average of the first frame
		double[] timPat = new double[] {0.0, 0.0, 0.0, 0.0 }; //Timing Pattern
		
		//0.0 - 1.0
		dynPat = new double[] { 0.5, -0.5, -0.5, 0.5 }; //Dynamics Pattern
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//1-2
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//2-3 (Dyn patt switches polarity, timPat enters)
		timPat = new double[] {0.0166, -0.0166, -0.0166, 0.0166 }; //Timing Pattern
		dynPat = new double[] { 0.17, -0.17, -0.17, 0.17 }; //Dynamics Pattern
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//3-4
		timPat = new double[] {0.025, -0.025, -0.025, 0.025 }; //Timing Pattern
		dynPat = new double[] { 0.0, -0.0, -0.0, 0.0 }; //Dynamics Pattern
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//4-5
		timPat = new double[] {0.0375, -0.0375, -0.0375, 0.0375 }; //Timing Pattern
		dynPat = new double[] { -0.17, 0.17, 0.17, -0.17 }; //Dynamics Pattern
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//5-6
		timPat = new double[] {0.05, -0.05, -0.05, 0.05 }; //Timing Pattern
		dynPat = new double[] { -0.5, 0.5, 0.5, -0.5 }; //Dynamics Pattern
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//6-7
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//7-8
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//8-9
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//9-10
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//10-11
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//11-12
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//12-13
		dynCurve = new double[] {0.6,0.75}; //Dynamics curve
		dynPat = new double[] { -0.05, 0.05, 0.05, -0.05 }; //Dynamics Pattern
		timPat = new double[] {0.0, -0.0, -0.0, 0.0 }; //Timing Pattern
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//13-14
		//14-15
		//15-16
		
		runPlan( toAnalyse, plans, fragmentSize );
		//fail();
	}
	
	/**
	 * Tests modifications to the dynamics, timing and note lengths in a phrase.
	 * Modifications are made using curves (but of course this gives rise to pattern effects
	 *
	 * Current issue: when taking an average by extrapolating a regression analysis, we can come up with
	 * well out of bound values. e.g. a 4 bar section where the first beat contains a rapidyly increasing 
	 * dynamic, and the next 3 bars contain silence. It's probably worth going back to straight averages...
	 */
	public void testCurvesForAllAspectsWithGapsAndNoise()
	{
		context.setAttribute( "KeepLastInContext", false );
		//testPatterns = false;
		stopOnErrors = true;
		quantise = 4.0;
		double onsetNoise = 0.00;
		double lengthNoise = 0.00;
		double dynamicsNoise = 0.00;
		TIM_COMPARE_THRESHOLD = 0.01;
		doSetUp( quantise, 1, true, 1.0 );
		Fragment toAnalyse = new Fragment();
		CurveContainer dynamicsCurve = new CurveContainer();
		dynamicsCurve.addEvent( new Curve( 0.0, 3.9, 0.2, 0.2 ) );
		dynamicsCurve.addEvent( new Curve( 4.0, 7.9, 0.6, 0.6 ) );
		dynamicsCurve.addEvent( new Curve( 8.0, 15.9, 0.0, 0.0 ) );
		dynamicsCurve.addEvent( new Curve( 16.0, 20.0, 0.8, 0.8 ) );
		
		CurveContainer timingCurve = new CurveContainer();
		timingCurve.addEvent( new Curve( 0.0, 3.9, 0.1, -0.1 ) );
		timingCurve.addEvent( new Curve( 4.0, 7.9, 0.0, 0.1 ) );
		timingCurve.addEvent( new Curve( 8.0, 12.0, 0.0, 0.0 ) );
		//timingCurve.addEvent( new Curve( 8.0, 15.9, 0.0, 0.0 ) );
		//timingCurve.addEvent( new Curve( 16.0, 20.0, 0.8, 0.8 ) );
		
		CurveContainer lengthCurve = new CurveContainer();
		lengthCurve.addEvent( new Curve( 0.0, 20.0, 1.0, 1.0 ) );
		//lengthCurve.addEvent( new Curve( 4.0, 7.9, 0.6, 0.6 ) );
		//lengthCurve.addEvent( new Curve( 8.0, 15.9, 0.0, 0.0 ) );
		//lengthCurve.addEvent( new Curve( 16.0, 20.0, 0.8, 0.8 ) );
		
		double noteStep = 0.25;
		double noteLength = 0.20;
		for( double i = 0; i < 20.0; i+= noteStep )
		{
			if( dynamicsCurve.sample( i ) > 0.01 )
			{
				double velocity = noisy( dynamicsCurve.sample( i ), dynamicsNoise );
				double onset = noisy( i + timingCurve.sample( i ), onsetNoise );
				int pitch = (int)(i * quantise);
				double length = noisy( lengthCurve.sample( i ) * noteLength, lengthNoise );
				toAnalyse.addNote( onset, pitch, velocity, length );
			}
		}
		System.out.println( "Running test on fragment: " + toAnalyse );
		List<RenderPlan> plans = new Vector<RenderPlan>();
		double[] dynCurve = new double[] {0.2,0.2}; //Dynamics curve
		double[] dynPat = new double[] { 0.0, 0.0, 0.0, 0.0 }; //Dynamics Pattern
		double[] lenCurve = new double[] {0.8, 0.8}; //Length curve
		double[] lenPat = new double[] {0.0, 0.0, 0.0, Double.NaN }; //Length Pattern - won't find last value as it overlaps end...
		double[] timCurve = new double[] {0.075, 0.075 }; //Timing Curve - first one is the average of the first frame
		double[] timPat = new double[] {0.0, 0.0, 0.0, 0.0 }; //Timing Pattern
		
		//0.0 - 1.0
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		
		
		//1.0 - 2.0
		timCurve = new double[] {0.075, 0.05 }; //Timing Curve
		lenPat = new double[] {0.0, 0.0, 0.0, 0.0 }; //Length Pattern - won't find last value as it overlaps end...
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		
		//2.0 - 3.0
		timCurve = new double[] {0.05, 0.025 }; //Timing Curve
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		
		//3.0 - 4.0
		timCurve = new double[] {0.025, 0.0 }; //Timing Curve
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		
		//From 4.0 - 5.0
		//Now, because there are different regions of notes, we get a pattern effect
		//Values taken from the straigh analysis system, but they seem correct
		dynCurve = new double[] { 0.2, 0.3 };
		dynPat = new double[] {  0.04,  0.01, -0.01, -0.04  };
		timCurve = new double[] {0.0, -0.015 }; //Timing Curve
		timPat = new double[] {0.01, 0.0, 0.0, -0.01 }; //Timing Pattern
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 5.0 - 6.0
		dynCurve = new double[] { 0.3, 0.4 };
		timCurve = new double[] {-0.015, -0.01 }; //Timing Curve
		dynPat = new double[] {  0.06,  0.02, -0.02, -0.06  };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 6.0 - 7.0
		dynCurve = new double[] { 0.4, 0.5 };
		dynPat = new double[] {  0.04,  0.01, -0.01, -0.04  };
		timCurve = new double[] {-0.01, 0.01 }; 
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 7.0 - 8.0
		dynCurve = new double[] { 0.5, 0.6 };
		dynPat = new double[] { 0.0, 0.0, 0.0, 0.0 }; //Dynamics Pattern
		timCurve = new double[] {0.01, 0.05 }; 
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 8.0 to 9.0
		dynCurve = new double[] { 0.6, 0.6 };
		timCurve = new double[] {0.05, 0.06 }; 
		timPat = new double[] {0.00, 0.0, 0.0, 0.00 }; //Timing Pattern
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 9.0 to 10.0
		timCurve = new double[] {0.06, 0.075 }; 
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 10.0 to 11.0
		timCurve = new double[] {0.075, 0.087 }; 
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 11 to 12
		timCurve = new double[] {0.087, 0.087 }; 
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 12 to 13
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 13 to 14
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 14 to 15
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 15 to 16
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 16 to 17
		timCurve = new double[] {0.087, 0.0 }; 
		dynCurve = new double[] { 0.6, 0.8 };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 17 to 18
		timCurve = new double[] {0.00, 0.0 }; 
		dynCurve = new double[] { 0.8, 0.8 };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 18 to 19
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 19 to 20
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		
		runPlan( toAnalyse, plans, fragmentSize );
	}
	
	public void testDynamicsBlocksWithGapsAndNoise()
	{
		context.setAttribute( "KeepLastInContext", false );
		//testPatterns = false;
		stopOnErrors = true;
		quantise = 4.0;
		double noiseLevel = 0.05;
		doSetUp( quantise, 1, true, 1.0 );
		Fragment toAnalyse = new Fragment();
		CurveContainer dynamicsCurve = new CurveContainer();
		dynamicsCurve.addEvent( new Curve( 0.0, 3.9, 0.2, 0.2 ) );
		dynamicsCurve.addEvent( new Curve( 4.0, 7.9, 0.6, 0.6 ) );
		dynamicsCurve.addEvent( new Curve( 8.0, 15.9, 0.0, 0.0 ) );
		dynamicsCurve.addEvent( new Curve( 16.0, 20.0, 0.8, 0.8 ) );
		
		double noteStep = 0.25;
		double noteLength = 0.20;
		for( double i = 0; i < 20.0; i+= noteStep )
		{
			double velocity = dynamicsCurve.sample( i );
			if( velocity > 0.01 )
			{
				double noise = random() * 2 * noiseLevel - noiseLevel;
				System.out.println( "Noise: " + noise );
				toAnalyse.addNote( i, (int)( i * quantise ), velocity + noise, noteLength );
			}
		}
		System.out.println( "Running test on fragment: " + toAnalyse );
		List<RenderPlan> plans = new Vector<RenderPlan>();
		double[] dynCurve = new double[] {0.2,0.2}; //Dynamics curve
		double[] dynPat = new double[] { 0.0, 0.0, 0.0, 0.0 }; //Dynamics Pattern
		double[] lenCurve = new double[] {0.8, 0.8}; //Length curve
		double[] lenPat = new double[] {0.0, 0.0, 0.0, 0.0 }; //Length Pattern
		double[] timCurve = new double[] {0.0, 0.0 }; //Timing Curve
		double[] timPat = new double[] {0.0, 0.0, 0.0, 0.0 }; //Timing Pattern
		
		RenderPlan startPlan =  getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat );
		
		plans.add( startPlan );
		plans.add( startPlan );
		plans.add( startPlan );
		plans.add( startPlan );
		
		//From 4.0 - 5.0
		dynCurve = new double[] { 0.2, 0.3 };
		dynPat = new double[] {  0.04,  0.01, -0.01, -0.04  };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 5.0 - 6.0
		dynCurve = new double[] { 0.3, 0.4 };
		dynPat = new double[] {  0.06,  0.02, -0.02, -0.06  };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 6.0 - 7.0
		dynCurve = new double[] { 0.4, 0.5 };
		dynPat = new double[] {  0.04,  0.01, -0.01, -0.04  };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 7.0 - 8.0
		dynCurve = new double[] { 0.5, 0.6 };
		dynPat = new double[] { 0.0, 0.0, 0.0, 0.0 }; //Dynamics Pattern
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 8.0 to 9.0
		dynCurve = new double[] { 0.6, 0.6 };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 9.0 to 10.0
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 10.0 to 11.0
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 11 to 12
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 12 to 13
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 13 to 14
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 14 to 15
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 15 to 16
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 16 to 17
		dynCurve = new double[] { 0.6, 0.8 };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 17 to 18
		dynCurve = new double[] { 0.8, 0.8 };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 18 to 19
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 19 to 20
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		
		runPlan( toAnalyse, plans, fragmentSize );
	}

	public void testDynamicsBlocksWithGaps()
	{
		context.setAttribute( "KeepLastInContext", false );
		//testPatterns = false;
		stopOnErrors = true;
		quantise = 4.0;
		doSetUp( quantise, 1, true, 1.0 );
		Fragment toAnalyse = new Fragment();
		CurveContainer dynamicsCurve = new CurveContainer();
		dynamicsCurve.addEvent( new Curve( 0.0, 3.9, 0.2, 0.2 ) );
		dynamicsCurve.addEvent( new Curve( 4.0, 7.9, 0.6, 0.6 ) );
		dynamicsCurve.addEvent( new Curve( 8.0, 15.9, 0.0, 0.0 ) );
		dynamicsCurve.addEvent( new Curve( 16.0, 20.0, 0.8, 0.8 ) );
		
		double noteStep = 0.25;
		double noteLength = 0.20;
		for( double i = 0; i < 20.0; i+= noteStep )
		{
			double velocity = dynamicsCurve.sample( i );
			if( velocity > 0.01 ) toAnalyse.addNote( i, (int)( i * quantise ), velocity, noteLength );
		}
		System.out.println( "Running test on fragment: " + toAnalyse );
		List<RenderPlan> plans = new Vector<RenderPlan>();
		double[] dynCurve = new double[] {0.2,0.2}; //Dynamics curve
		double[] dynPat = new double[] { 0.0, 0.0, 0.0, 0.0 }; //Dynamics Pattern
		double[] lenCurve = new double[] {0.8, 0.8}; //Length curve
		double[] lenPat = new double[] {0.0, 0.0, 0.0, 0.0 }; //Length Pattern
		double[] timCurve = new double[] {0.0, 0.0 }; //Timing Curve
		double[] timPat = new double[] {0.0, 0.0, 0.0, 0.0 }; //Timing Pattern
		
		RenderPlan startPlan =  getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat );
		
		plans.add( startPlan );
		plans.add( startPlan );
		plans.add( startPlan );
		plans.add( startPlan );
		
		//From 4.0 - 5.0
		dynCurve = new double[] { 0.2, 0.3 };
		dynPat = new double[] {  0.04,  0.01, -0.01, -0.04  };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 5.0 - 6.0
		dynCurve = new double[] { 0.3, 0.4 };
		dynPat = new double[] {  0.06,  0.02, -0.02, -0.06  };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 6.0 - 7.0
		dynCurve = new double[] { 0.4, 0.5 };
		dynPat = new double[] {  0.04,  0.01, -0.01, -0.04  };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 7.0 - 8.0
		dynCurve = new double[] { 0.5, 0.6 };
		dynPat = new double[] { 0.0, 0.0, 0.0, 0.0 }; //Dynamics Pattern
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 8.0 to 9.0
		dynCurve = new double[] { 0.6, 0.6 };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 9.0 to 10.0
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 10.0 to 11.0
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 11 to 12
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 12 to 13
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 13 to 14
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 14 to 15
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 15 to 16
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 16 to 17
		dynCurve = new double[] { 0.6, 0.8 };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 17 to 18
		dynCurve = new double[] { 0.8, 0.8 };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 18 to 19
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 19 to 20
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		
		runPlan( toAnalyse, plans, fragmentSize );
	}
	public void testDynamicsBlocks()
	{
		//testPatterns = false;
		quantise = 4.0;
		doSetUp( quantise, 0, true, 1.0 );
		Fragment toAnalyse = new Fragment();
		CurveContainer dynamicsCurve = new CurveContainer();
		dynamicsCurve.addEvent( new Curve( 0.0, 3.9, 0.2, 0.2 ) );
		dynamicsCurve.addEvent( new Curve( 4.0, 9.9, 0.6, 0.6 ) );
		
		double noteStep = 0.25;
		double noteLength = 0.20;
		for( double i = 0; i < 10.0; i+= noteStep )
		{
			double velocity = dynamicsCurve.sample( i );
			toAnalyse.addNote( i, (int)( i * quantise ), velocity, noteLength );
		}
		System.out.println( "Running test on fragment: " + toAnalyse );
		List<RenderPlan> plans = new Vector<RenderPlan>();
		double[] dynCurve = new double[] {0.2,0.2}; //Dynamics curve
		double[] dynPat = new double[] { 0.0, 0.0, 0.0, 0.0 }; //Dynamics Pattern
		double[] lenCurve = new double[] {0.8, 0.8}; //Length curve
		double[] lenPat = new double[] {0.0, 0.0, 0.0, 0.0 }; //Length Pattern
		double[] timCurve = new double[] {0.0, 0.0 }; //Timing Curve
		double[] timPat = new double[] {0.0, 0.0, 0.0, 0.0 }; //Timing Pattern
		
		RenderPlan startPlan =  getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat );
		
		plans.add( startPlan );
		plans.add( startPlan );
		plans.add( startPlan );
		plans.add( startPlan );
		
		//From 4.0 - 5.0
		dynCurve = new double[] { 0.2, 0.3 };
		dynPat = new double[] {  0.04,  0.01, -0.01, -0.04  };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 5.0 - 6.0
		dynCurve = new double[] { 0.3, 0.4 };
		dynPat = new double[] {  0.06,  0.02, -0.02, -0.06  };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 6.0 - 7.0
		dynCurve = new double[] { 0.4, 0.5 };
		dynPat = new double[] {  0.04,  0.01, -0.01, -0.04  };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 7.0 - 8.0
		dynCurve = new double[] { 0.5, 0.6 };
		dynPat = new double[] { 0.0, 0.0, 0.0, 0.0 }; //Dynamics Pattern
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		//From 8.0 to 10.0
		dynCurve = new double[] { 0.6, 0.6 };
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		plans.add( getExpectedPlan( fragmentSize, dynCurve, dynPat, lenCurve, lenPat, timCurve, timPat ) );
		
		runPlan( toAnalyse, plans, fragmentSize );
	}
	
	public void testBasicOperation()
	{
		doSetUp( 4.0, 0, true, 1.0 );
		Fragment toAnalyse = new Fragment();
		toAnalyse.addNote( 0.0, 60, 0.5, 0.5 );
		toAnalyse.addNote( 0.25, 62, 0.5, 0.5 );
		toAnalyse.addNote( 0.5, 63, 0.5, 0.5 );
		toAnalyse.addNote( 0.75, 65, 0.5, 0.5 );
		toAnalyse.addNote( 1.0, 66, 0.5, 0.5 );
		toAnalyse.addNote( 1.25, 67, 0.5, 0.5 );
		toAnalyse.addNote( 1.5, 68, 0.5, 0.5 );
		
		List<RenderPlan> plans = new Vector<RenderPlan>();
		plans.add( getExpectedPlan( fragmentSize, 
				new double[] {0.5,0.5}, //Dynamics curve
				new double[] { 0.0, 0.0, 0.0, 0.0 }, //Dynamics Pattern
				new double[] {2.0, 2.0}, //Length curve
				new double[] {0.0, 0.0, 0.0, Double.NaN }, //Length Pattern
				new double[] {0.0, 0.0 }, //Timing Curve
				new double[] {0.0, 0.0, 0.0, 0.0 } //Timing Pattern
				) );
		plans.add( getExpectedPlan( fragmentSize, 
				new double[] {0.5,0.5}, //Dynamics curve
				new double[] { 0.0, 0.0, 0.0, 0.0 }, //Dynamics Pattern
				new double[] {2.0, 2.0}, //Length curve
				new double[] {0.0, 0.0, 0.0, 0.0 }, //Length Pattern
				new double[] {0.0, 0.0 }, //Timing Curve
				new double[] {0.0, 0.0, 0.0, 0.0 } //Timing Pattern
				) );
		plans.add( getExpectedPlan( fragmentSize, 
				new double[] {0.5,0.5}, //Dynamics curve
				new double[] { 0.0, 0.0, 0.0, 0.0 }, //Dynamics Pattern
				new double[] {2.0, 2.0}, //Length curve
				new double[] {0.0, 0.0, 0.0, 0.0 }, //Length Pattern
				new double[] {0.0, 0.0 }, //Timing Curve
				new double[] {0.0, 0.0, 0.0, 0.0 } //Timing Pattern
				) );
		runPlan( toAnalyse, plans, fragmentSize );
	}
	
	/**
	 * currently, pattern analysers require at least 5 notes, so this will all be null the first time round
	 *
	 */
	public void testBasicOperationWithMinNotes()
	{
		doSetUp( 4.0, -1, true, 1.0 );
		Fragment toAnalyse = new Fragment();
		toAnalyse.addNote( 0.0, 60, 0.5, 0.5 );
		toAnalyse.addNote( 0.25, 62, 0.5, 0.5 );
		toAnalyse.addNote( 0.5, 63, 0.5, 0.5 );
		toAnalyse.addNote( 0.75, 65, 0.5, 0.5 );
		toAnalyse.addNote( 1.0, 66, 0.5, 0.5 );
		toAnalyse.addNote( 1.25, 67, 0.5, 0.5 );
		toAnalyse.addNote( 1.5, 68, 0.5, 0.5 );
		
		List<RenderPlan> plans = new Vector<RenderPlan>();
		plans.add( getExpectedPlan( fragmentSize, 
				new double[] {0.5,0.5}, //Dynamics curve
				new double[] { Double.NaN, Double.NaN, Double.NaN, Double.NaN }, //Dynamics Pattern
				new double[] {2.0, 2.0}, //Length curve
				new double[] {Double.NaN, Double.NaN, Double.NaN, Double.NaN }, //Length Pattern
				new double[] {0.0, 0.0 }, //Timing Curve
				new double[] {Double.NaN, Double.NaN, Double.NaN, Double.NaN } //Timing Pattern
				) );
		plans.add( getExpectedPlan( fragmentSize, 
				new double[] {0.5,0.5}, //Dynamics curve
				new double[] { 0.0, 0.0, 0.0, 0.0 }, //Dynamics Pattern
				new double[] {2.0, 2.0}, //Length curve
				new double[] {0.0, 0.0, 0.0, 0.0 }, //Length Pattern
				new double[] {0.0, 0.0 }, //Timing Curve
				new double[] {0.0, 0.0, 0.0, 0.0 } //Timing Pattern
				) );
		plans.add( getExpectedPlan( fragmentSize, 
				new double[] {0.5,0.5}, //Dynamics curve
				new double[] { 0.0, 0.0, 0.0, 0.0 }, //Dynamics Pattern
				new double[] {2.0, 2.0}, //Length curve
				new double[] {0.0, 0.0, 0.0, 0.0 }, //Length Pattern
				new double[] {0.0, 0.0 }, //Timing Curve
				new double[] {0.0, 0.0, 0.0, 0.0 } //Timing Pattern
				) );
		runPlan( toAnalyse, plans, fragmentSize );
	}
	
	void runPlan( Fragment toAnalyse, List<RenderPlan> expected, double fragSize )
	{
		toAnalyse.setMusician( me.getMusician() );
		Score s = new Score();
		s.add( toAnalyse );
		double currentPos = 0.0;
		for( RenderPlan ex : expected )
		{
			System.out.println( "*******************************************\n" +
					"** Running plan from " + currentPos + " to " + ( currentPos + fragSize) );
			System.out.println( "*******************************************" );
			Score chunk = s.copyChunk( currentPos, currentPos+fragSize );
			System.out.println( chunk );
			chunk.forceLength( fragSize );
			analyser.analyse( chunk );
			features.printFeatures();
			RenderPlan got = reasoner.getNextPlan( fragSize );
			checkPlan( got, ex );
			currentPos += fragSize;
		}
		if( gotError ) fail( "Had an error at some point!" );
	}
	
	
	
	void checkPlan( RenderPlan got, RenderPlan expected )
	{
		System.out.println( "Got: " + got + "\nExpected: " + expected);
		if( testPatterns && testDynamics ) comparePatterns( "Dynamics", got.getDynamicsPattern(), expected.getDynamicsPattern(), DYN_COMPARE_THRESHOLD );
		if( testPatterns && testLength ) comparePatterns( "Length", got.getLengthPattern(), expected.getLengthPattern(), LEN_COMPARE_THRESHOLD );
		if( testPatterns && testTiming ) comparePatterns( "Timing", got.getTimingPattern(), expected.getTimingPattern(), TIM_COMPARE_THRESHOLD );
		if( testCurves && testDynamics ) compareCurves( "Dynamics", got.getDynamicsCurve(), expected.getDynamicsCurve(), DYN_COMPARE_THRESHOLD );
		if( testCurves && testLength ) compareCurves( "Length", got.getLengthCurve(), expected.getLengthCurve(), LEN_COMPARE_THRESHOLD );
		if( testCurves && testTiming ) compareCurves( "Timing", got.getTimingCurve(), expected.getTimingCurve(), TIM_COMPARE_THRESHOLD );
	}
	
	void compareCurves( String type, CurveContainer got, CurveContainer expected, double threshold )
	{
		boolean sloppy = got.sloppyCompare( expected, threshold );
		if( ! sloppy ) 
		{
			if( stopOnErrors ) assertEquals( "Comparing " + type, expected, got );
			else System.err.println( "Comparing " + type + " expected " +expected + ", got: " + got );
			gotError = true;
		}
	}
	void comparePatterns( String type, PatternAttribute got, PatternAttribute expected, double threshold )
	{
		boolean sloppy = got.sloppyCompare( expected, threshold );
		if( ! sloppy )
		{
			if( stopOnErrors ) assertEquals( "Comparing " + type, expected, got );
			else System.err.println( "Comparing " + type + " expected " +expected + ", got: " + got );
			gotError = true;
		}
	}
	
	BasicRenderPlan  getExpectedPlan( double length,  double[] dynCur, double[] dynPat, double[] lenCur, double[] lenPat, double[] timCur, double[] timPat )
	{
		return getExpectedPlan( length, null, dynCur, dynPat, lenCur, lenPat, timCur, timPat );
	}
	
	BasicRenderPlan  getExpectedPlan( double length, Fragment score, double[] dynCur, double[] dynPat, double[] lenCur, double[] lenPat, double[] timCur, double[] timPat )
	{
		BasicRenderPlan plan = new BasicRenderPlan(length);
		plan.setNotes( score );
		plan.setDynamicsCurve( new CurveContainer( length, dynCur[0], dynCur[1] ) );
		plan.setLengthCurve( new CurveContainer( length, lenCur[0], lenCur[1] ) );
		plan.setTimingCurve( new CurveContainer( length, timCur[0], timCur[1] ) );
		
		plan.setDynamicsPattern( new PatternAttribute( quantise, dynPat ) );
		plan.setLengthPattern( new PatternAttribute( quantise, lenPat ) );
		plan.setTimingPattern( new PatternAttribute( quantise, timPat ) );
		return plan;
	}
	
	double noisy( double original, double noiseLevel )
	{
		return original + ( random() * 2 * noiseLevel ) - noiseLevel;
	}

}
