package com.moseph.mra.test;

import java.util.*;

import com.moseph.mra.MRAUtilities;
import static com.moseph.mra.MRAUtilities.*;

import junit.framework.TestCase;

public class MRAUtilitiesTest extends TestCase
{
	
	public void testPowerList()
	{
		List<String> a = new Vector<String>();
		List<String> b = new Vector<String>();
		a.add( "a1");
		a.add( "a2");
		b.add( "b1");
		b.add( "b2");
		List<List<String>> input = new Vector<List<String>>();
		input.add( a );
		input.add( b );
		List<List<String>> result = MRAUtilities.powerList( input );
		System.out.println( collectionToString( result ));
	}
	


}
