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

public class GuiReasoner extends DefaultReasoner implements Reasoner, GuiComponent
{
	CurveDisplay dynamicCurveDisplay = new CurveDisplay();
	CurveDisplay timingCurveDisplay = new CurveDisplay();
	CurveDisplay lengthCurveDisplay = new CurveDisplay();
	PatternDisplay dynamicPatternDisplay = new PatternDisplay( 10 );
	PatternDisplay timingPatternDisplay = new PatternDisplay( 10 );
	PatternDisplay lengthPatternDisplay = new PatternDisplay( 10 );
	
	public GuiReasoner( Context c, StructuralDecider decider )
	{
		super( c, decider );
		dynamicPatternDisplay.forceMax( 0.3 );
		dynamicPatternDisplay.forceMin( -0.3 );
		lengthPatternDisplay.forceMax( 0.3 );
		lengthPatternDisplay.forceMin( -0.3 );
		timingPatternDisplay.forceMax( 0.3 );
		timingPatternDisplay.forceMin( -0.3 );
	}
	
	public JComponent getGUIComponent()
	{
		Box b = new Box( BoxLayout.Y_AXIS );
		
		Box timingBox = new Box( BoxLayout.Y_AXIS );
		timingBox.setBorder( BorderFactory.createTitledBorder( "Timing" ) );
		timingBox.add( timingPatternDisplay );
		timingBox.add( timingCurveDisplay );
		b.add( timingBox );
		
		Box dynamicBox = new Box( BoxLayout.Y_AXIS );
		dynamicBox.setBorder( BorderFactory.createTitledBorder( "Dynamic" ) );
		dynamicBox.add( dynamicPatternDisplay );
		dynamicBox.add( dynamicCurveDisplay );
		b.add( dynamicBox );
		
		Box lengthBox = new Box( BoxLayout.Y_AXIS );
		lengthBox.setBorder( BorderFactory.createTitledBorder( "Length" ) );
		lengthBox.add( lengthPatternDisplay );
		lengthBox.add( lengthCurveDisplay );
		b.add( lengthBox );
		
		return b;
	}
	
	public void updateGraphs( RenderPlan plan )
	{
		timingPatternDisplay.setPattern( plan.getTimingPattern() );
		dynamicPatternDisplay.setPattern( plan.getDynamicsPattern() );
		lengthPatternDisplay.setPattern( plan.getLengthPattern() );
		timingCurveDisplay.setCurve( plan.getTimingCurve() );
		dynamicCurveDisplay.setCurve( plan.getDynamicsCurve() );
		lengthCurveDisplay.setCurve( plan.getLengthCurve() );
	}

}
