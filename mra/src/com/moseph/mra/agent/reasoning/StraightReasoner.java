package com.moseph.mra.agent.reasoning;

import com.moseph.mra.CurveContainer;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.attribute.PatternAttribute;

public class StraightReasoner extends DefaultReasoner implements Reasoner
{
	
	public StraightReasoner( Context c, StructuralDecider sd  )
	{
		super( c, sd );
		//System.out.println( "*&*&*&*&*&*&*&*&*&*&*&*\n*&*&*&*&*&*&*&*&*&*&*&\nStraight Reasoner Created!");
	}
	
	void fillPlan( BasicRenderPlan p )
	{
		//System.out.println( "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n&&&&&&&&&&&&&&&&&&&&&&&&&\nStraightReasoner");
		p.setDynamicsCurve( new CurveContainer( p.length, 0.7 ) );
		p.setTimingCurve( new CurveContainer( p.length, 0.0 ) );
		p.setLengthCurve( new CurveContainer( p.length, 0.7 ) );
		p.setDynamicsPattern( new PatternAttribute( p.length, context.getQuantisation() ) );
		p.setTimingPattern( new PatternAttribute( p.length, context.getQuantisation() ) );
		p.setLengthPattern( new PatternAttribute( p.length, context.getQuantisation() ) );
		//System.out.println( p );
	}

}
