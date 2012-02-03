package com.moseph.mra.agent.analyser;

import java.awt.BorderLayout;
import java.util.*;

import javax.swing.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.parser.ParseException;
import com.moseph.mra.visualise.PatternDisplay;

import static java.lang.Math.*;

public abstract class CurveNormalisedPatternAnalyser extends PatternAnalyser implements GuiComponent
{
	GroupDualNumericFeature regression;
	//double patternLength = -1.0;
	double patternLength = 1.0;
	boolean noSlopeCompensation = false;
	boolean hasGui = false;
	PatternDisplay display = null;
	boolean forceFragmentLengthPatterns = false;

	
	public CurveNormalisedPatternAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
		String force =  context.getAttributeString( "ForceFragmentLengthPatterns"  );
		if( force != null )
		{
			try
			{
				forceFragmentLengthPatterns = Boolean.parseBoolean( force );
			}
			catch( NumberFormatException e ) { System.out.println( "Bad boolean value for ForceFragmentLengthPatterns: " + force ); }
		}
		if( context.getAttributeString( "PatternLength" ) != null )
		{
			try
			{
				patternLength = Double.parseDouble( context.getAttributeString( "PatternLength" ) );
				System.out.println( "Pattern Length set to: " +  patternLength );
			} catch( Exception e )
			{
				System.out.println( "Could not set pattern length: " + e );
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println( "No Pattern Length set! Using default");
			System.out.println( context.getAllAttsAsString() );
		}
		if( "true".equals( context.getAttributeString( "NoSlopeCompensation"  )) )
			noSlopeCompensation = true;
	}

	public void init()
	{
		super.init();
		minNotes = 5;
		regression = (GroupDualNumericFeature)features.getOrCreate( getRegressionAnalyserFeatureName() );
		if( analysisSize > 0.0 && patternLength < 0.0 ) patternLength = analysisSize;
	}

	protected PatternAttribute analyseFragment( Fragment f )
	{
		//Offset of the start of the pattern from the neares PatternPoint
		double offset = 0.0;
		if( analysisSystem != null ) offset = analysisSystem.getPatternOffset();
		while( offset < 0.0 ) offset += patternLength;
		//System.out.println( "Using offset: " + offset + " to analyse:\n" + f );
		CurveContainer<Curve> slope = getCurve( f );
		double thisPatternLength = patternLength;
		if( thisPatternLength < 0.0 ) thisPatternLength = analysisSize;
		if( thisPatternLength < 0.0 || forceFragmentLengthPatterns ) thisPatternLength = ceil( f.getLength() );
		AveragingPatternAttribute p = new AveragingPatternAttribute( thisPatternLength, context.getQuantisation() );
		for( Note n : f.getNotes() )
		{
			if( shouldSkip( n )) continue;
			double actual = getValueForNote( n );
			if( Double.isNaN(actual ) ) continue;
			double predicted = slope.sample(  n.getQuantisedOnset() );
			//System.out.printf( "+++ actual: %f, predicted: %f for" + n + "\n", actual, predicted );
			double val = actual - predicted;
			if( noSlopeCompensation || Double.isNaN( predicted )) val = actual;
			if( Double.isNaN( val ) ) System.out.println( "Bad value from " + predicted + " and " + actual );
			
			
			//A bit slow, but we need to add the offset before we quantise, otherwise strange things can happen
			n = n.copyAddOffset( offset );
			n.calculateQuantisation( quantisation );
			double addPoint = n.getQuantisedOnset();
			p.addValue( addPoint, new ValuedAttribute( val ) );
			//System.out.println( "Adding value " + val + " at " + addPoint + " gives a value of " + p.getValue( addPoint ));
		}
		//System.out.println( "Pattern: " + p );
		return p;
	}
	
	protected PatternAttribute analyseAnnotatedFragment( AnnotatedFragment f )
	{
		CurveContainer<Curve> slope = getCurve( f.getPlayed() );
		AveragingPatternAttribute p = new AveragingPatternAttribute( f.getPlayed().getLength(), context.getQuantisation() );
		Map<Note,Note> map = f.getMapToScoredNotes();
		for( Note played : map.keySet() )
		{
			if( shouldSkip( played )) continue;
			played.calculateQuantisation( quantisation );
			Note scored = map.get( played );
			double actual = getAnnotatedValueForNote( played, scored );
			if( actual == Double.NaN ) continue;
			double predicted = slope.sample(  played.getQuantisedOnset() );
			p.addValue( played.getQuantisedOnset(), new ValuedAttribute( actual - predicted ) );
		}
		return p;
	}
	
	protected CurveContainer getCurve( Fragment f )
	{
		double start = regression.getNumericValue1( musician( f ) );
		double end = start + f.getLength() * regression.getNumericValue2( musician( f ) );
		return new CurveContainer<Curve>( f.getLength(), start, end );
	}

	@Override
	public List<String> dependsOn()
	{
		List<String> deps = super.dependsOn();
		deps.add( getRegressionAnalyserName() );
		return deps;
	}
	
	abstract String getRegressionAnalyserName();
	abstract String getRegressionAnalyserFeatureName();
	
	double getValueForNote( Note n )
	{
		return Double.NaN;
	}
	
	double getAnnotatedValueForNote( Note played, Note scored )
	{
		return getValueForNote( played );
	}
	
	public JComponent getGUIComponent()
	{
		JPanel p = new JPanel();
		p.setLayout( new BorderLayout() );
		if( display == null )
		{
			display = new PatternDisplay();
			display.setIsCurveCompensate( !noSlopeCompensation );
			hasGui = true;
		}
		p.add( BorderLayout.CENTER, display );
		p.setBorder( BorderFactory.createTitledBorder( getFeature() ) );
		return p;
	}
	
	void updateGui( PatternAttribute p )
	{
		if( hasGui ) display.setPattern( p );
	}
	
	PatternAttribute getNullFeature()
	{
		return new PatternAttribute( patternLength, quantisation );
	}

	
}
