package com.moseph.mra.test;

import java.util.List;

import com.moseph.mra.*;

import junit.framework.TestCase;
import static com.moseph.mra.test.TestUtilities.*;

public class PathTest extends TestCase
{
	Piece p;
	
	public void setUp()
	{
		p = getBasicPiece();
	}
	
	public void testIndexing()
	{
		Path path = new Path( "/main/a/b");
		assertEquals( 0, path.getIndex( p ));
		path = new Path( "/main/a/c");
		assertEquals( 1, path.getIndex( p ));
		path = new Path( "/main/a/a");
		assertEquals( -1, path.getIndex( p ));
		path = new Path( "/main/a");
		//Not sure about this one...
		//assertEquals( -1, path.getIndex( p ));
		path = new Path( "/main/d/e");
		assertEquals( 2, path.getIndex( p ));
	}
	
	public void testSequence()
	{
		Path path = new Path( "/main/a/b");
		List<Section>sections = path.getSections( p );
		String[] names = { "main", "a", "b" };
		for( int i = 0; i < names.length; i++ )
		{
			assertEquals( sections.get(i).getName(), names[i] );
		}
	}
}
