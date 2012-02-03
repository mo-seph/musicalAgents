package com.moseph.mra.agent;

import java.awt.BorderLayout;
import java.util.*;

import javax.swing.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.visualise.*;
import static com.moseph.mra.MRAUtilities.*;

public class AnalysisSystem implements GuiComponent
{
	Context context;
	List<Analyser> analysers;
	Map<String,Analyser> analyserMap;
	public static final double DEFAULT_SMOOTHING = 0.3;
	double smoothing = DEFAULT_SMOOTHING;
	FeatureSet featureSet;
	MusicianInformation me;
	String myName;
	ForceAnalysisSettings settings;
	AnnotatedScore annotatedWindow;
	AnalysisWindow window;
	double analysisLength = 4.0;
	boolean analyseSelf = true;
	boolean forceImmediateAnalysis = false;
	static double DEFAULT_ANALYSIS_LENGTH = 4.0;
	double patternOffset = 0.0;
	Box guiBox;
	boolean hasGUI = false;
	Score current;
	Map<String,FragmentDisplay> visualisers = new HashMap<String, FragmentDisplay>();
	double chunkSize = 0.5;
	
	
	public AnalysisSystem( Context c, double analysisLength ) 
	{
		this.context = c;
		me = context.getMyID();
		//myName = me.getMusician().getName();
		this.analysisLength = analysisLength;
		featureSet = context.getFeatures();
		analysers = new Vector<Analyser>();
		analyserMap = new HashMap<String, Analyser>();
		window = new AnalysisWindow(analysisLength);
	}
	
	public AnalysisSystem( Context c )
	{
		this( c, DEFAULT_ANALYSIS_LENGTH );
	}
	
	public AnalysisSystem( Context c, double analysisLength, ForceAnalysisSettings settings )
	{
		this( c, analysisLength );
		this.settings = settings;
	}
	
	public AnalysisSystem( Context c,  ForceAnalysisSettings settings, String...analysers )
	{
		this( c, DEFAULT_ANALYSIS_LENGTH, settings, analysers );
	}
	public AnalysisSystem( Context c, double analysisLength, ForceAnalysisSettings settings, String...analysers )
	{
		this( c, analysisLength, settings );
		for( String name : analysers )
			addAnalyser( name );
	}
	
	public void analyse( Score s )
	{
		chunkSize = s.getLength();
		if( forceImmediateAnalysis )
			doAnalysis( s );
		else
		{
			window.add( s );
			setPatternOffset( window.getEndPoint() );
			//System.out.println( "Analysing: " + window.getStartPoint() + " with pattern offset " + getPatternOffset() );
			doAnalysis( window.getCurrent() );
		}
		//featureSet.printFeatures();
	}
	
	void doAnalysis( Score s )
	{
		current = s;
		if( ! analyseSelf ) s.removeFragmentForName( me.getName() );
		//System.out.println( "+++++++++++++ Doing analysis on length:" + s.getLength() + "\n" );
		//System.err.println( "Analysing: " + s.copyChunk( pa, end ) );
		for( Analyser analyser : analysers )
		{
			//System.out.println( "Running Analyser: " + analyser.getFeature() );
			analyser.analyse( s );
		}
		//System.out.println( context );
		//System.out.println( "Done Analysis!" );
		if( hasGUI ) updateGUI();
	}
	
	void setPatternOffset( double windowEnd )
	{
		double patternStart = context.getPatternPointBefore( windowEnd );
		//Fragments are delayed by 2 chunks by the time they're analysed!
		patternOffset = window.getStartPoint() - patternStart - chunkSize * 2;
		//System.out.println( "Setting pattern offset: " + patternOffset + " from ws: " + windowEnd + " and " + patternStart );
	}
	
	public void addAnalyser( String name )
	{
		addAnalyser( name, smoothing );
	}
	
	public void addAnalyser( String name, double smooth )
	{
		System.out.println( "Adding analyser: " + name );
		Analyser analyser =  null;
		if( name.equals( "Dynamics")) analyser = new DynamicAverageAnalyser( context, smooth );
		else if( name.equals( "Density")) analyser = new DensityAnalysis( context, smooth );
		else 
		{
			if( analyserMap.containsKey( name )) return;
			String classname = "com.moseph.mra.agent.analyser." + name + "Analyser";
			try
			{
				Class analyserClass = Class.forName( classname );
				analyser = (Analyser)analyserClass.getConstructor( 
						new Class[] { context.getClass(), java.lang.Double.TYPE } ).newInstance( context, new Double( smooth ) );
			}
			catch( Exception e )
			{
				System.out.println( "Bad analyser for " + me.name + ": " + name + "(from classname: " + classname + "\n" + e );
				e.printStackTrace();
			}
		}
		if( analyser != null )
		{
			List<String> deps = analyser.dependsOn();
			String depsString = "[ ";
			for( String d : deps ) depsString += d + " ";
			depsString += " ]";
			//System.out.println( "+++" + depsString );
			for( String dep : deps )
			{
				//System.out.println( "+ Checking dep: " + dep );
				if( ! analyserMap.containsKey( dep ) )
				{
					//System.out.println( "Fixing dependancy: " + dep );
					addAnalyser( dep );
				}
			}
			analyser.setAnalyseSelf( analyseSelf );
			analyser.setAnalysisSystem( this );
			analyser.init();
			analysers.add( analyser );
			analyserMap.put( name, analyser );
		}
	}

	public void setSmoothing( double smoothing )
	{
		this.smoothing = smoothing;
	}

	public boolean isForceImmediateAnalysis()
	{
		return forceImmediateAnalysis;
	}

	public void setForceImmediateAnalysis( boolean forceImmediateAnalysis )
	{
		this.forceImmediateAnalysis = forceImmediateAnalysis;
	}

	public boolean isAnalyseSelf()
	{
		return analyseSelf;
	}

	public void setAnalyseSelf( boolean analyseSelf )
	{
		this.analyseSelf = analyseSelf;
	}

	/**
	 * Gets the offset between the start of the analysis window and the last "PatternPoint"
	 * This should be added to any pattern analysis which is done, so that patterns are correctly timed
	 * @return
	 */
	public double getPatternOffset()
	{
		return patternOffset;
	}
	
	public AnalysisWindow getWindow()
	{
		return window;
	}
	
	public JComponent getGUIComponent()
	{
		if( guiBox == null )
			guiBox = new Box( BoxLayout.Y_AXIS );
		addAnalysersToBox( guiBox );
		hasGUI = true;
		
		return guiBox;
	}
	
	void updateGUI()
	{
		if( !hasGUI || guiBox == null || current == null ) return;
		//guiBox.removeAll();
		for( Fragment f : current.getFragments()  )
		{
			String name = f.getMusician( ).getName();
			FragmentDisplay fv = visualisers.get( name );
			if( fv == null )
			{
				JPanel p = new JPanel();
				p.setBorder( BorderFactory.createTitledBorder( f.getMusician( ).getName() ) );
				p.setLayout( new BorderLayout() );
				fv = new FragmentDisplay( f );
				fv.setForceWidth( analysisLength );
				p.add( BorderLayout.CENTER, fv );
				guiBox.add( p );
				visualisers.put(  name, fv );
			}
			fv.setFragment( f );
		}
		//guiBox.revalidate();
	}
	
	void addAnalysersToBox( Box b )
	{
		for( Analyser a : analysers )
		{
			if( a instanceof GuiComponent )
				b.add( ( (GuiComponent)a ).getGUIComponent() );
		}
	}
	
	public void setMinNotes( int min )
	{
		for( Analyser a : analysers ) a.setMinNotes( min );
	}
	
	public List<Analyser> getAnalysers()
	{
		return new Vector<Analyser>( analysers );
	}

}
