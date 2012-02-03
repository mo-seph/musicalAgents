package com.moseph.mra.agent.analyser;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.*;

import javax.swing.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;

public abstract class Analyser<T extends Feature>
{

	Context context;
	FeatureSet features;
	MusicianInformation me;
	GroupFeature feature;
	public static String featureName = "Unknown";
	boolean analyseSelf = false;
	double analysisSize = -1;
	AnalysisSystem analysisSystem;
	int minNotes = 3;
	boolean keepLastInContext = true;
	
	
	public Analyser( Context context )
	{
		this.context = context;
		this.features = context.getFeatures();
		this.me = context.getMyID();
	}
	
	public void init()
	{
		keepLastInContext = context.booleanAttribute( "KeepLastInContext" );
		feature = getFeatureSet();
		//System.out.println( "Putting " + feature + " in as " + this.getFeature() + "(" + feature.getClass() + ")"  );
		features.setGroupFeature( getFeature(), feature );
	}
	
	public void analyse( Score s )
	{
		//System.out.println( s.fragments().)
		//System.out.println( "++++++++++++++++++\nStarting analysis on length " + s.getLength() );
		
		//System.out.println( getClass().getName() + " Analyser running");
		
		//System.err.println( getFeature() );
		for( Fragment f : s.fragments() )
		{
			MusicianInformation info = musician( f );
			T old = (T)features.getOrCreate( getFeature() ).getValue( info );
			T result = null;
			
			if( f.getNumNotes() < getMinNotes() ) 
			{
				if( keepLastInContext ) 
				{
					continue;
				}
				System.out.println( "Setting null value!");
				feature.setValue( info, getNullFeature() );
				continue;
			}
			if( analyseAnnotations() && s instanceof AnnotatedScore )
			{
				//result = analyseAnnotatedFragment( ((AnnotatedScore)s).getAnnotation( f ));
				AnnotatedFragment af = ((AnnotatedScore)s).getAnnotation( info.getName() );
				//System.out.println( getClass() + " Analysing as AnnotatedFragment!(" + af.getPlayed().getLength() );
				//if( af == null )
					//System.out.println( "No annotated fragment found!" );
				result = analyseAnnotatedFragment( af );
			}
			else 
			{
				//System.out.println( "No annotatedFragment found");
				//System.out.println( getClass() + " Analysing as Fragment (" + f.getLength() +")" );
				result = analyseFragment( f );
			}
			
			//T update = smooth( old, result );
			T update = result;
			if( this instanceof GuiComponent ) updateGui( update );
			//System.out.println( "++\n++\n" + getFeature() + ": " + update );
			feature.setValue( info, update );
		}
	}
	
	MusicianInformation musician( Fragment f )
	{
		return context.getMusicianInformation( f.getPartIndex().getMusician().getName() );
	}
	
	Feature getOtherAnalyserAttribute( Fragment f, String name )
	{
		return context.getFeature( name ).getValue( musician( f ));
	}
	
	double getOtherAnalyserValue( Fragment f, String name )
	{
		return ((NumericFeature)getOtherAnalyserAttribute( f, name )).getValue();
	}
	
	boolean analyseAnnotations() { return false; }
	T analyseAnnotatedFragment( AnnotatedFragment f ) { return null; }
	abstract T analyseFragment( Fragment f );
	abstract T getNullFeature();
	//Dammit, want it to be abstract! But then it can't be static!
	
	public String getFeature() 
	{
		String name = "No feature given!";
		try
		{
			name = getClass().getField( "featureName" ).get(null) + "";
		} catch (Exception e ) { e.printStackTrace(); }
		return name;
	}
	public GroupFeature getFeatureSet() 
	{ 
		if( feature == null ) feature = createFeatureSet();
		feature.setIncludeSelf( analyseSelf );
		return feature; 
	}
	public GroupFeature createFeatureSet() { return feature; }
		
	T smooth( T old, T current )
	{
		return current;
	}
	
	public List<String> dependsOn()
	{
		return new Vector<String>();
	}
	
	public static double[] linearRegression( List<double[]> points )
	{
		return linearRegression( points, false );
	}

	/**
	 * Performs a linear regression on a list of double[] points
	 * @param points
	 * @param rejectOverslopes
	 * @return [ alpha, beta, number ]
	 */
	public static double[] linearRegression( List<double[]> points, boolean rejectOverslopes )
	{
		if( points.size() < 2 ) return new double[] { 0, 0, 0 };
		double sx = 0;
		double sy = 0;
		double sxx = 0;
		double sxy = 0;
		double n = 0;
		double xmax = 0;
		double xmin = 0;
		for( double[] point : points )
		{
			sx += point[0];
			sxx += point[0] * point[0];
			sy += point[1];
			sxy += point[0] * point[1];
			n++;
			xmax = max( xmax, point[0]);
			xmin = min( xmin, point[0]);
			//System.out.printf( "x: %f, y: %f\n", point[0], point[1] );
		}
		double beta = ( n * sxy - sx * sy ) / ( n * sxx - sx*sx );
		double alpha = ( sy - beta * sx ) / n;
		if( rejectOverslopes && abs( beta ) > xmax - xmin ) return new double[]{ 0, 0, 0};
		//System.out.printf( "offset: %f, slope: %f\n", alpha, beta );
		return new double[]{ alpha, beta, n };
	}

	public static double variance( List<Double> points )
	{
		double mean = 0;
		double diff = 0;
		for( double d : points ) mean += d;
		mean /= points.size();
		for( double d : points ) diff += ( d-mean ) * ( d-mean );
		return diff / points.size();
	}

	public boolean isAnalyseSelf()
	{
		return analyseSelf;
	}

	public void setAnalyseSelf( boolean analyseSelf )
	{
		this.analyseSelf = analyseSelf;
	}

	public void setAnalysisSystem( AnalysisSystem analysisSystem )
	{
		this.analysisSystem = analysisSystem;
	}

	public int getMinNotes()
	{
		return minNotes;
	}

	public void setMinNotes( int minNotes )
	{
		this.minNotes = minNotes;
	}
	
	void updateGui( T p ) { 	}
	
	public boolean skipEarlierNotes() { return true; }
	public boolean skipOverhangingNotes() { return false; }
	
	boolean shouldSkip( Note n )
	{
		boolean skip = false;
		if( skipEarlierNotes() && n.getStartsBefore() ) skip = true;
		if( skipOverhangingNotes() && n.getLongerThan() ) skip = true;
		return skip;
	}
	
	double getNoteDisplacement( Note played, Note scored )
	{
		return played.getOnset() - scored.getOnset();
	}

	double getNoteDisplacement( Note played, double quantisation )
	{
		played.calculateQuantisation( quantisation );
		return played.getOnset() - played.getQuantisedOnset();
	}
	
	
}
