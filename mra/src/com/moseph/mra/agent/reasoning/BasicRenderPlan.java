package com.moseph.mra.agent.reasoning;

import java.util.Map;

import com.moseph.mra.*;
import com.moseph.mra.agent.attribute.*;

public class BasicRenderPlan implements RenderPlan
{
	public Fragment notes;
	double length;
	double patternOffset = 0.0;
	private CurveContainer timingCurve;
	private CurveContainer dynamicsCurve;
	private CurveContainer lengthCurve;
	private PatternAttribute timingPattern;
	private PatternAttribute dynamicsPattern;
	private PatternAttribute lengthPattern;
	
	public BasicRenderPlan( double length )
	{
		this.length = length;
	}
	
	public Fragment getNotes()
	{
		return notes;
	}
	
	public void setNotes( Fragment f )
	{
		notes = f;
	}

	public double getLength()
	{
		return length;
	}

	public CurveContainer getDynamicsCurve()
	{
		return dynamicsCurve;
	}

	public void setDynamicsCurve( CurveContainer dynamicsCurve )
	{
		this.dynamicsCurve = dynamicsCurve;
	}

	public PatternAttribute getDynamicsPattern()
	{
		return dynamicsPattern;
	}

	public void setDynamicsPattern( PatternAttribute dynamicsPattern )
	{
		this.dynamicsPattern = dynamicsPattern;
	}

	public CurveContainer getLengthCurve()
	{
		return lengthCurve;
	}

	public void setLengthCurve( CurveContainer lengthCurve )
	{
		this.lengthCurve = lengthCurve;
	}

	public PatternAttribute getLengthPattern()
	{
		return lengthPattern;
	}

	public void setLengthPattern( PatternAttribute lengthPattern )
	{
		this.lengthPattern = lengthPattern;
	}

	public CurveContainer getTimingCurve()
	{
		return timingCurve;
	}

	public void setTimingCurve( CurveContainer timingCurve )
	{
		this.timingCurve = timingCurve;
	}

	public PatternAttribute getTimingPattern()
	{
		return timingPattern;
	}

	public void setTimingPattern( PatternAttribute timingPattern )
	{
		this.timingPattern = timingPattern;
	}

	public double getPatternOffset()
	{
		return patternOffset;
	}

	public void setPatternOffset( double patternOffset )
	{
		this.patternOffset = patternOffset;
	}
	
	public String toString()
	{
		String ret = "Render Plan: ";
		ret += "\n\t Timing: " + timingPattern;
		ret += "\n\t Timing: " + timingCurve;
		ret += "\n\t Dynamic: " + dynamicsPattern;
		ret += "\n\t Dynamic: " + dynamicsCurve;
		ret += "\n\t Length: " + lengthPattern;
		ret += "\n\t Length: " + lengthCurve;
		return ret;
	}
}
