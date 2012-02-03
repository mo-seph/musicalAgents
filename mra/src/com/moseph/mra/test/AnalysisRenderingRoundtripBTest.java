package com.moseph.mra.test;

import java.util.Map;
import static java.lang.Math.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.agent.reasoning.*;

import junit.framework.TestCase;

public class AnalysisRenderingRoundtripBTest extends TestCase
{
	
	MusicianInformation me = new MusicianInformation( new Musician("me") );
	Context context = new Context( me );
	RenderingSystem renderer;
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
	
	public void setUp()
	{
		context.setQuantisation( 4.0 );
		context.setAttribute( "ForceFragmentLengthPatterns", "true" );
		renderer = new RenderingSystem();
		reasoner = new AcousticMirrorReasoner( context, null );
		ForceAnalysisSettings settings = new ForceAnalysisSettings();
		settings.setForceDistanceWeighted( false );
		settings.setForceIncludeSelf( true );
		analyser = new AnalysisSystem( context, settings, analysers );
		analyser.setForceImmediateAnalysis( true );
		analyser.setAnalyseSelf( true );
		analyser.setMinNotes( 0 );
		reasoner.setCopyCurvesExactly( true );
		features = context.getFeatures();
	}
	
	public void testNoFeatures()
	{
		Fragment score = new Fragment();
		score.addNote( 0.5, 60, 0.5, 0.5 );
		score.addNote( 1.5, 64, 0.5, 0.5 );
		
		Fragment played = score.clone();
		runTestOnFragment( score, played, 2.0, 4.0 );
	}
	
	public void testTimingJitter()
	{
		Fragment score = new Fragment( 11.0 );
		Fragment played = new Fragment( 11.0 );
		for( double beat = 1.0; beat < 10; beat += 0.25 )
		{
			int pitch = (int) (beat * 4.0) + 40;
			double rand = ( random() - 0.5 ) * 0.10;
			score.addNote( beat, pitch, 0.9, 0.25 );
			played.addNote( beat + rand, pitch, 0.9, 0.25 );
		}
		runTestOnFragment( score, played, 11.0, 4.0 );
	}
	
	public void runTestOnFragment( Fragment score, Fragment played, double length, double quantisation )
	{
		score.setMusician( me.getMusician() );
		played = played.copyChunk( 0.0, length );
		played.setMusician( me.getMusician() );
		AnnotatedFragment annoF = new AnnotatedFragment( played, score, quantisation );
		Map<Note, Note> noteMap = annoF.getMapToScoredNotes();
		for( Note n : noteMap.keySet() )
			System.out.println( n + "->" + noteMap.get( n ));
		
		
		AnnotatedScore s = new AnnotatedScore();
		s.addAnnotation( annoF );
		System.out.println( "&&&&&&&&&&&&&&\n\nStarting analysis!\n&&&&&&&&&&&&&&\n");
		analyser.analyse( s );
		RenderPlan plan = reasoner.getNextPlan( score.getLength(), score );
		Fragment rendered = renderer.render( plan );
		rendered.setMusician( me.getMusician() );
		assertEquals( played, rendered );
	}
	
}