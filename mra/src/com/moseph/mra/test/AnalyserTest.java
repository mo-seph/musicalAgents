package com.moseph.mra.test;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.PatternAttribute;
import static com.moseph.mra.MRAConstants.*;

import junit.framework.TestCase;
import static java.lang.Math.*;

public class AnalyserTest extends TestCase
{
	MusicianInformation me = new MusicianInformation( new Musician( "me" ));
	Context context = new Context( me );
	
	public void testDependency()
	{
		AnalysisSystem analysis = new AnalysisSystem(context);
		//analysis.addAnalyser( "DisplacementRegression", 0.0 );
		analysis.addAnalyser( "DisplacementChange", 0.0 );
		context.getFeatures().listFeatures();
		assertTrue( context.getFeatures().contains( DisplacementRegressionAnalyser.featureName ));
		analysis.setMinNotes( 0 );
	}
	
	public void testPatternOffsets()
	{
		double analysisSize = 7.0;
		AnalysisSystem analysis = new AnalysisSystem( context, analysisSize );
		analysis.setMinNotes( 0 );
		context.addPatternPoint( 0.0 );
		context.addPatternPoint( 5.0 );
		double step = 1.5;
		for( double i = 0.0; i < 10.0; i+= step )
		{
			Score s = new Score( );
			s.forceLength( step );
			//System.out.println( "Set score length to: " + s.getLength() );
			analysis.analyse( s );
			double ws = max( i - step - analysisSize, -2.0 * step );
			double windowEnd = i + step;
			System.out.printf( "wEnd: %f, ws: %f, offset: %f, rWs: %f\n", windowEnd, ws, analysis.getPatternOffset(), analysis.getWindow().getStartPoint() );
			if( windowEnd <= 5.0 )
				assertEquals( max( ws, -2.0 * step ), analysis.getPatternOffset()  );
			else 
				assertEquals( max( ws, -2.0 * step ) - 5.0, analysis.getPatternOffset());
		}
	}
	
	void doPatternTest( Fragment f, double length, double analysisSize, double stepSize, double patternSize, double[][] expected )
	{
		doPatternTest( f, length, analysisSize, stepSize, patternSize, 2.0, expected );
	}
	
	void doPatternTest( Fragment f, double length, double analysisSize, double stepSize, double patternSize, double quantisation, double[][] expected )
	{
		context.setQuantisation( quantisation );
		context.setAttribute( "NoSlopeCompensation", "true" );
		context.setAttribute( "PatternLength", patternSize + "");
		ForceAnalysisSettings fs = new ForceAnalysisSettings();
		fs.setForceIncludeSelf( true );
		AnalysisSystem analysis = new AnalysisSystem( context, analysisSize, fs, new String[] { "DynamicPattern" } );
		analysis.setMinNotes( 0 );
		f.setMusician( me.getMusician() );
		Score s = new Score();
		s.add( f );
		GroupPatternFeature gpf = (GroupPatternFeature)context.getFeatures().getOrCreate( DynamicPatternAnalyser.featureName );
		int index = 0;
		for( double beat = 0.0; beat < length; beat += stepSize )
		{
			System.out.println( "&************\nAnalysis for beat " + beat + "\n****************");
			Score chunk =  s.copyChunk( beat, beat+stepSize);
			chunk.forceLength( stepSize );
			System.out.println( chunk );
			analysis.analyse( chunk );
			PatternAttribute pattern = gpf.getAverage();
			double[] predicted = expected[index];
			PatternAttribute pred = new PatternAttribute( quantisation, predicted );
			
			if( ! pred.sloppyCompare( pattern, 0.05 ) )
			{
				System.err.println( "Analysed: " + pattern + "\nExpected: " + pred );
				//assertEquals( pred, pattern );
			}
			//for( int i = 0; i < predicted.length; i++ )
				//assertEquals( predicted[ i ], pattern.getValue( (double)i / context.getQuantisation() ));
			System.out.println( ">>>>>>" + gpf.getAverage() );
			index++;
		}
	}
	
	public void testPatternOffsetsGetUsedBasic()
	{
		Fragment f = getTestFragment();
		double[][] expected = new double[][] { { 1.0, 0.5 }, { 0.5, 0.5 } };
		doPatternTest( f, 2.0, 1.0, 1.0, 1.0, expected );
	}
	
	public void testPatternOffsetsGetUsedLongerWindow()
	{
		Fragment f = getTestFragment();
		double[][] expected = new double[][] { { 1.0, 0.5 }, { 0.75, 0.5 } };
		doPatternTest( f, 2.0, 2.0, 1.0, 1.0, expected );
	}
	
	public void testPatternOffsetsGetUsedShiftingWindow()
	{
		Fragment f = getTestFragment();
		double[][] expected = new double[][] { { 1.0, 0.5, Double.NaN, Double.NaN }, { 1, 0.5, 0.5, 0.5 }, { 0.5, 0.5, 0.5, 0.5 }  };
		doPatternTest( f, 3.0, 2.0, 1.0, 2.0, expected );
	}
	
	public void testPatternOffsetsGetUsedShiftingLongerWindow()
	{
		Fragment f = getTestFragment();
		double[][] expected = new double[][] { { 1.0, 0.5, Double.NaN, Double.NaN }, { 1, 0.5, 0.5, 0.5 }, { 0.75, 0.5, 0.5, 0.5 }  };
		doPatternTest( f, 3.0, 3.0, 1.0, 2.0, expected );
	}
	
	
	public void testLongPatternWithOffset()
	{
		Fragment f = new Fragment();
		f.addNote( 0.0, C3, 0.5, 0.5 );
		f.addNote( 0.5, D3, 0.5, 0.5 );
		f.addNote( 1.0, E3, 1.0, 0.5 );
		f.addNote( 1.5, F3, 0.5, 0.5 );
		f.addNote( 2.0, G3, 0.5, 0.5 );
		f.addNote( 2.5, A3, 0.5, 0.5 );
		f.addNote( 3.0, Eb3, 1.0, 0.5 );
		f.addNote( 3.5, Db3, 0.5, 0.5 );
		context.addPatternPoint( 3.0 );
		double[][] expected = new double[][] { { 0.5, 0.5, Double.NaN, Double.NaN }, { 0.5, 0.5, 1.0, 0.5 }, { 0.5, 0.5, 1.0, 0.5 }, { 1.0, 0.5, 0.5, 0.5 }  };
		doPatternTest( f, 3.0, 2.0, 1.0, 2.0, expected );
	}
	
	public void testLongPatternWithOffsetAndShortChunks()
	{
		Fragment f = new Fragment( 5.0 );
		f.addNote( 0.0, C3, 1.0, 0.15 );
		f.addNote( 0.2, C3, 0.5, 0.15 );
		f.addNote( 0.4, C3, 0.5, 0.15 );
		f.addNote( 0.6, C3, 0.5, 0.15 );
		f.addNote( 0.8, C3, 0.5, 0.15 );
		f.addNote( 1.0, C3, 1.0, 0.15 );
		f.addNote( 1.2, C3, 0.5, 0.15 );
		f.addNote( 1.4, C3, 0.5, 0.15 );
		f.addNote( 1.6, C3, 0.5, 0.15 );
		f.addNote( 1.8, C3, 0.5, 0.15 );
		f.addNote( 2.0, C3, 1.0, 0.15 );
		f.addNote( 2.2, C3, 0.5, 0.15 );
		f.addNote( 2.4, C3, 0.5, 0.15 );
		f.addNote( 2.6, C3, 0.5, 0.15 );
		f.addNote( 2.8, C3, 0.5, 0.15 );
		context.addPatternPoint( 3.0 );
		double[][] expected = new double[][] { 
				{ 1.0, 0.5, Double.NaN, Double.NaN, Double.NaN },
				{ 1.0, 0.5, 0.5, Double.NaN, Double.NaN },
				{ 1.0, 0.5, 0.5, 0.5, Double.NaN },
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				{ 1.0, 0.5, 0.5, 0.5, 0.5 }, 
				
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				{ 1.0, 0.5, 0.5, 0.5, 0.5 },
				{ 1.0, 0.5, 0.5, 0.5, 0.5 } 
			};
		//Fragment f, length, analysisSize, stepSize, patternSize, quantisation, double[][] expected )
		doPatternTest( f, 5.0, 2.0, 0.25, 1.0, 5.0, expected );
	}
	
	
	Fragment getTestFragment()
	{
		Fragment f = new Fragment();
		f.addNote( 0.0, 60, 1.0, 0.5 );
		f.addNote( 0.5, 61, 0.5, 0.5 );
		f.addNote( 1.0, 62, 0.5, 0.5 );
		f.addNote( 1.5, 63, 0.5, 0.5 );
		f.addNote( 2.0, 62, 0.5, 0.5 );
		f.addNote( 2.5, 63, 0.5, 0.5 );
		f.addNote( 3.0, 64, 0.5, 0.5 );
		f.addNote( 3.5, 65, 0.5, 0.5 );
		f.addNote( 4.0, 64, 0.5, 0.5 );
		f.addNote( 4.5, 63, 0.5, 0.5 );
		f.addNote( 5.0, 62, 0.5, 0.5 );
		f.addNote( 5.5, 61, 0.5, 0.5 );
		f.addNote( 6.0, 65, 0.5, 0.5 );
		return f;
	}

}
