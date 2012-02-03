package com.moseph.mra.agent.analyser;

import java.awt.BorderLayout;
import java.util.*;

import javax.swing.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.visualise.CurveDisplay;

public abstract class CurveAnalyser extends DualNumericAnalyser implements GuiComponent
{
	double lastCurveLength;
	double quantisation = 4.0;
	boolean hasGui = false;
	CurveDisplay display;
	
	public CurveAnalyser( Context context, double smoothing )
	{
		super( context, smoothing );
		quantisation = context.getQuantisation();
	}
	
	DualNumericFeature analyseAnnotatedFragment( AnnotatedFragment f )
	{
		Map<Note,Note> scored = f.getMapToScoredNotes();
		List<double[]>points = new Vector<double[]>();
		for( Note playedNote : f.played.getNotes() )
		{
			if( shouldSkip( playedNote )) continue;
			double[] p = getNoteValue( playedNote, scored.get( playedNote ) );
			if( p != null ) points.add( p );
		}
		double[] results = linearRegression( points, true );
		lastCurveLength = f.played.getLength();
		return new DualValuedAttribute( results[0], results[1] );
	}
	
	abstract double[] getNoteValue( Note played );
	abstract double[] getNoteValue( Note played, Note scored );

	DualNumericFeature analyseFragment( Fragment f )
	{
		List<double[]>points = new Vector<double[]>();
		for( Note n : f.getNotes() )
		{
			if( shouldSkip( n )) continue;
			double[] p = getNoteValue( n );
			//System.out.println( "Value of " + p[0] + "," + p[1] + " for " + n );
			if( p != null ) points.add( p );
		}
		double[] results = linearRegression( points, true );
		lastCurveLength = f.getLength();
		//System.out.println( getClass().getSimpleName() + "Regression result: {" + results[0] + ", " + results[1] + "}");
		return new DualValuedAttribute( results[0], results[1] );
	}
	
	
	public JComponent getGUIComponent()
	{
		if( !hasGui ) display = new CurveDisplay( new CurveContainer<Curve>( 1.0, 0.0 ));
		JPanel p = new JPanel();
		p.setLayout( new BorderLayout() );
		p.setBorder( BorderFactory.createTitledBorder( getFeature() ) );
		p.add( BorderLayout.CENTER, display );
		hasGui = true;
		return p;
	}

	@Override
	void updateGui( Feature p )
	{
		updateGui( (DualNumericFeature)p );
	}

	void updateGui( DualNumericFeature feature )
	{
		if( !hasGui ) return;
		CurveContainer c = new CurveContainer( lastCurveLength,
				feature.getValue1(), feature.getValue1() + feature.getValue2() * lastCurveLength );
		display.setCurve( c );
	}
}
