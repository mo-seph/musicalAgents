package com.moseph.mra.test;

import com.moseph.mra.agent.attribute.Accent;
import com.moseph.mra.agent.attribute.Accent.AccentValue;

import junit.framework.TestCase;

public class AttributeTest extends TestCase
{

	//Accent test
	public void testAccents()
	{
		Accent strong = new Accent( "STRONG");
		Accent strongb = new Accent( AccentValue.STRONG );
		Accent soft = new Accent( "SOFT");
		Accent strongClone = (Accent)strong.clone();
		Accent strongCopy = new Accent( strong );
		assertEquals( "String creation == Enum creation", strong, strongb  );
		assertEquals( "Clone == original", strong, strongClone );
		assertEquals( "Copy == original", strong, strongCopy );
		assertFalse( "Different values are not equal", strong.equals( soft ) );
		assertTrue( "Clones have correct type", strongClone instanceof Accent );
		assertTrue( "Comparison ordering works", strong.compareTo(soft) > 0 );
		assertTrue( "Comparison ordering works", soft.compareTo(strong) < 0 );
		assertTrue( "Comparison ordering works", strong.compareTo(strong) == 0 );
	}
}
