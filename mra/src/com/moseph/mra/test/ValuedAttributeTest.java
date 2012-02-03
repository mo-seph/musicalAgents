package com.moseph.mra.test;

import com.moseph.mra.agent.attribute.ValuedAttribute;

import junit.framework.TestCase;

public class ValuedAttributeTest extends TestCase
{
	
	public void testEquality()
	{
		ValuedAttribute a = new ValuedAttribute( 0.5 );
		ValuedAttribute a2 = new ValuedAttribute( 0.5 );
		ValuedAttribute b = new ValuedAttribute( 0.6 );
		assertTrue( a.equals( a2 ));
		assertTrue( a.equals( (Object)a2 ));
		assertTrue( ! a.equals( b ));
	}

}
