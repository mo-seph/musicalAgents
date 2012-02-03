package com.moseph.mra.agent.reasoning;

import com.moseph.mra.*;
import com.moseph.mra.agent.attribute.PatternAttribute;

public interface RenderPlan
{
	public abstract Fragment getNotes();
	public abstract double getLength();
	public abstract CurveContainer getTimingCurve();
	public abstract CurveContainer getDynamicsCurve();
	public abstract CurveContainer getLengthCurve();
	public abstract PatternAttribute getTimingPattern();
	public abstract PatternAttribute getDynamicsPattern();
	public abstract PatternAttribute getLengthPattern();
	public abstract double getPatternOffset();
	

}
