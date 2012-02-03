package com.moseph.mra.agent.reasoning;

import java.util.*;

import javax.swing.*;
import static java.lang.Math.*;
import static java.lang.Double.*;

import sun.net.www.content.text.plain;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.PatternAttribute;
import com.moseph.mra.visualise.*;

public class AcousticMirrorReasoner extends GuiReasoner implements Reasoner, GuiComponent
{
	double dynamicLevel = 0.5;
	double timingLevel = 0.0;
	double lengthLevel = 0.7;
	boolean hadDynamics = false;
	boolean hadTiming = false;
	boolean hadLength = false;
	
	PatternAttribute dynamicsPattern;
	PatternAttribute lengthPattern;
	PatternAttribute timingPattern;
	
	public static final double DYNAMICS_DEFAULT = 0.5;
	public static final double LENGTH_DEFAULT = 0.7;
	public static final double TIMING_DEFAULT = 0.0;
	
	double timingPatternScale = 1.0;
	double lengthPatternScale = 1.0;
	double dynamicsPatternScale = 1.0;
	
	boolean copyCurvesExactly = false;
	
	double SMOOTHING_FACTOR = 0.0;
	
	public AcousticMirrorReasoner( Context c, StructuralDecider decider )
	{
		super( c, decider );
	}
	
	List<String> getNecessaryAnalysers()
	{
		List<String> nec = super.getNecessaryAnalysers();
		nec.add( "DynamicPattern" );
		nec.add( "DynamicAverage" );
		nec.add( "DynamicChange" );
		nec.add( "LengthPattern" );
		nec.add( "LengthAverage" );
		nec.add( "LengthChange" );
		nec.add( "DisplacementPattern" );
		nec.add( "DisplacementAverage" );
		nec.add( "DisplacementChange" );
		return nec;
	}
	
	

	@Override
	void fillPlan( BasicRenderPlan plan )
	{
		setDynamicsCurve( plan );
		setDynamicsPattern( plan );
		setLengthCurve( plan );
		setLengthPattern( plan );
		setTimingCurve( plan );
		setTimingPattern( plan );
		updateGraphs( plan );
		//System.out.println( "Plan!: " + plan );
	}



	void setDynamicsCurve( BasicRenderPlan plan )
	{
		GroupNumericFeature dynamics = (GroupNumericFeature)features.getOrCreate( DynamicAverageAnalyser.featureName );
		GroupDualNumericFeature dynamicRegression = (GroupDualNumericFeature)features.getOrCreate( DynamicRegressionAnalyser.featureName );
		CurveResult cr = calculateCurve( plan.length, dynamics, dynamicRegression, dynamicLevel, hadDynamics, DYNAMICS_DEFAULT );
		plan.setDynamicsCurve( cr.curve );
		dynamicLevel = cr.newLevel;
		hadDynamics = cr.hasData;
	}
	

	void setLengthCurve( BasicRenderPlan plan )
	{
		GroupNumericFeature length = (GroupNumericFeature)features.getOrCreate( LengthAverageAnalyser.featureName );
		GroupDualNumericFeature lengthRegression = (GroupDualNumericFeature)features.getOrCreate( LengthRegressionAnalyser.featureName );
		CurveResult cr = calculateCurve( plan.length, length, lengthRegression, lengthLevel, hadLength, LENGTH_DEFAULT );
		plan.setLengthCurve( cr.curve );
		lengthLevel = cr.newLevel;
		hadLength = cr.hasData;
	}
	
	void setTimingCurve( BasicRenderPlan plan )
	{
		GroupNumericFeature timing = (GroupNumericFeature)features.getOrCreate( DisplacementAverageAnalyser.featureName );
		GroupDualNumericFeature timingRegression = (GroupDualNumericFeature)features.getOrCreate( DisplacementRegressionAnalyser.featureName );
		CurveResult cr = calculateCurve( plan.length, timing, timingRegression, timingLevel, hadTiming, TIMING_DEFAULT );
		plan.setTimingCurve( cr.curve );
		timingLevel = cr.newLevel;
		hadTiming = cr.hasData;
	}

	CurveResult calculateCurve( double length,  GroupNumericFeature baseFeature, GroupDualNumericFeature featureRegression, double currentLevel, boolean hasPrevious, double defaultLevel )
	{
		//System.out.println( "Doing curve calculation. Length " + length + " feature: " + baseFeature.getAverageValue() +
				//", regression: " + featureRegression.getAverage() + ", current: " + currentLevel + ", pref: " + hasPrevious + ", def: " + defaultLevel );
		double targetLevel = currentLevel;
		double analysisLevel = baseFeature.getAverageValue();
		double curveStartVal = featureRegression.getAverageValue1();
		double curveEndVal = curveStartVal + featureRegression.getAverageValue2() * length;
		
		if( isNaN( analysisLevel ) || ( copyCurvesExactly && isNaN( curveStartVal ) ) )
		{
			targetLevel = currentLevel;
		}
		else if( copyCurvesExactly )
		{
			currentLevel = curveStartVal;
			targetLevel = curveEndVal;
		}
		else
		{
				if( hasPrevious )
					targetLevel = smooth( currentLevel, analysisLevel, SMOOTHING_FACTOR );
				else
				{
					currentLevel = targetLevel = analysisLevel;
					hasPrevious = true;
				}
		}
		CurveResult res = new CurveResult();
		res.curve = new CurveContainer( length, currentLevel, targetLevel );
		res.newLevel = targetLevel;
		res.hasData = hasPrevious;
		//System.out.println( "Returning curve:\n" + res );
		return res;
	}
	
	class CurveResult
	{
		public CurveContainer curve;
		public double newLevel;
		public boolean hasData;
		public String toString()
		{
			return "Curve: " + curve + "\nLevel: " + newLevel;
		}
	}



	PatternAttribute calculatePattern( double length, GroupPatternFeature feature, double scale, PatternAttribute current )
	{
		PatternAttribute avg = feature.getAverage();
		avg.scale( scale );
		if( current == null ) current = avg;
		else
		{
			current = current.getToLength( avg );
			current.overwriteWith( avg, SMOOTHING_FACTOR );
		}
		if( current == null ) current = new PatternAttribute( length, quantisation );
		return current;
	}
	
	void setDynamicsPattern( BasicRenderPlan plan )
	{
		GroupPatternFeature accents = (GroupPatternFeature)features.getOrCreate( DynamicPatternAnalyser.featureName );
		dynamicsPattern = calculatePattern( plan.length, accents, dynamicsPatternScale, dynamicsPattern );
		plan.setDynamicsPattern( dynamicsPattern );
	}

	void setLengthPattern( BasicRenderPlan plan )
	{
		GroupPatternFeature length = (GroupPatternFeature)features.getOrCreate( LengthPatternAnalyser.featureName );
		lengthPattern = calculatePattern( plan.length, length, lengthPatternScale, lengthPattern );
		plan.setLengthPattern( lengthPattern );
	}

	void setTimingPattern( BasicRenderPlan plan )
	{
		GroupPatternFeature timing = (GroupPatternFeature)features.getOrCreate( DisplacementPatternAnalyser.featureName );
		timingPattern = calculatePattern( plan.length, timing, timingPatternScale, timingPattern );
		plan.setTimingPattern( timingPattern );
	}
	
	public boolean isCopyCurvesExactly()
	{
		return copyCurvesExactly;
	}



	public void setCopyCurvesExactly( boolean copyCurvesExactly )
	{
		this.copyCurvesExactly = copyCurvesExactly;
	}
	

}
