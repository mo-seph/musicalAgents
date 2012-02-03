package com.moseph.mra.test;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.agent.reasoning.*;

import junit.framework.TestCase;

public class AnalysisRenderingRoundtripTest extends TestCase
{
	
	Piece p = new Piece( "test");
	MusicianInformation me = new MusicianInformation( new Musician("me") );
	Musician he = new Musician("him");
	MusicianInformation him = new MusicianInformation( he );
	Context context = new Context( me );
	RenderingSystem renderer;
	Reasoner reasoner;
	StructuralDecider structuralDecider;
	OutputSubsystem output;
	Fragment notes;
	BasicRenderPlan plan;
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
			"LengthRegression",
			"LengthAverage",
			"LengthChange",
			"LengthPattern",
			"LengthVariance"
			};
	
	public void setUp()
	{
		Section main = new Section( "main" );
		p.addChild( main );
		Section a = new Section( "a");
		notes = new Fragment( "Notes" );
		a.addChannel(  notes );
		for( double i = 0; i <= 4.0; i += 0.25 ) notes.addNote( i, 60, 0.5, 0.25 );
		main.addChild( a );
		context.setPiece( p );
		context.setQuantisation( 4.0 );
		renderer = new RenderingSystem();
		structuralDecider = new StructuralDecider( context );
		reasoner = new AcousticMirrorReasoner( context, structuralDecider );
		ForceAnalysisSettings settings = new ForceAnalysisSettings();
		settings.setForceDistanceWeighted( false );
		settings.setForceIncludeSelf( false );
		analyser = new AnalysisSystem( context, settings, analysers );
		analyser.setForceImmediateAnalysis( true );
		analyser.setMinNotes( 0 );
		plan = new BasicRenderPlan( 4.0 );
		plan.setNotes( notes );
		features = context.getFeatures();
	}
	
	public void testDynamics()
	{
		double start = 0.5;
		double slope = 0.05;
		double end = start + plan.getLength() * slope;
		double average = (start + end) / 2;
		plan.setDynamicsCurve( new CurveContainer( 4.0, start, end ) );
		runPlan();
		features.printFeatures();
		
		//Check that the analysis matches the starting values
		ValuedAttribute dynamics = (ValuedAttribute)features.getValue( DynamicAverageAnalyser.featureName, him );
		ValuedAttribute change = (ValuedAttribute)features.getValue( DynamicChangeAnalyser.featureName, him );
		assertEquals( average, dynamics.getValue(), 0.01 );
		assertEquals( slope, change.getValue(), 0.01 );
	}
	
	public void testLengthBasic()
	{
		plan.setLengthCurve( new CurveContainer( 4.0, 0.7 ) );
		runPlan();
		
		//Check that the analysis matches the starting values
		ValuedAttribute length = (ValuedAttribute)features.getValue( LengthAverageAnalyser.featureName, him );
		ValuedAttribute change = (ValuedAttribute)features.getValue( LengthChangeAnalyser.featureName, him );
		assertEquals( 0.7, length.getValue(), 0.01 );
		assertEquals( 0.0, change.getValue(), 0.01 );
	}
	public void testLength()
	{
		double start = 0.5;
		double slope = 0.05;
		double end = start + plan.getLength() * slope;
		double average = (start + end) / 2;
		plan.setLengthCurve( new CurveContainer( 4.0, start, end ) );
		runPlan();
		
		//Check that the analysis matches the starting values
		ValuedAttribute length = (ValuedAttribute)features.getValue( LengthAverageAnalyser.featureName, him );
		ValuedAttribute change = (ValuedAttribute)features.getValue( LengthChangeAnalyser.featureName, him );
		assertEquals( average, length.getValue(), 0.01 );
		assertEquals( slope, change.getValue(), 0.01 );
	}
	
	public void testDisplacement()
	{
		double start = 0.0;
		double slope = 0.01;
		double end = start + plan.getLength() * slope;
		double average = (start + end) / 2;
		plan.setTimingCurve( new CurveContainer( 4.0, start, end ) );
		runPlan();
		
		//Check that the analysis matches the starting values
		ValuedAttribute displacement = (ValuedAttribute)features.getValue( DisplacementAverageAnalyser.featureName, him );
		ValuedAttribute change = (ValuedAttribute)features.getValue( DisplacementChangeAnalyser.featureName, him );
		assertEquals( average, displacement.getValue(), 0.01 );
		assertEquals( slope, change.getValue(), 0.01 );
	}
	
	public void testDynamicsPattern()
	{
		double start = 0.5;
		double slope = 0.05;
		double end = start + plan.getLength() * slope;
		double average = (start + end) / 2;
		plan.setDynamicsCurve( new CurveContainer( 4.0, start, end ) );
		runPlan();
		
		//Check that the analysis matches the starting values
		PatternAttribute dynamicsPattern = (PatternAttribute)features.getValue( DynamicPatternAnalyser.featureName, him );
		System.out.println( dynamicsPattern );
		
	}
	
	void runPlan()
	{
		//Use them to render a fragment
		Fragment result = renderer.render( plan );
		result.setMusician( he);
		notes.setMusician( he);
		
		//Analyse the fragment
		AnnotatedScore annoScore = new AnnotatedScore();
		annoScore.addAnnotation( new AnnotatedFragment( result, notes, 4.0 ) );
		analyser.analyse( annoScore );
	}
	
	

}
