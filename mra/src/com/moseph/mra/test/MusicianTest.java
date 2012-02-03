package com.moseph.mra.test;

import com.moseph.mra.Musician;

import junit.framework.TestCase;

public class MusicianTest extends TestCase
{
	public void testDistance()
	{
		Musician me = new Musician( "me");
		Musician other = new Musician( "him");
		me.setX( 0.0 );
		me.setY( 0.0 );
		other.setX( 1.0 );
		other.setY( 0.0 );
		assertEquals( 1.0, me.getSquaredDistance( other ));
		for( double theta = 0.0; theta < Math.PI * 4; theta += Math.PI / 8 )
		{
			other.setX( Math.sin( theta ));
			other.setY( Math.cos( theta ));
			double dist = me.getSquaredDistance( other );
			assertEquals( 1.0, dist, 0.0001 );
		}
		for( double theta = 0.0; theta < Math.PI * 4; theta += Math.PI / 8 )
		{
			other.setX( 2 * Math.sin( theta ));
			other.setY( 2 * Math.cos( theta ));
			assertEquals( 4.0, me.getSquaredDistance( other ), 0.0001 );
		}
	}
}
