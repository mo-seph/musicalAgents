package com.moseph.mra.test;

import java.util.*;

import junit.framework.TestCase;
import static com.moseph.mra.agent.analyser.NumericAnalyser.*;
import static java.lang.Math.*;

public class LinearRegressionTest extends TestCase
{
	List<double[]>points = new Vector<double[]>();
	
	public void setUp()
	{
		points.clear();
	}
	
	public void testChord()
	{
		points.add( new double[] { 0.01, 0.5 } );
		points.add( new double[] { 0.03, 0.4 } );
		points.add( new double[] { 0.02, 1.0 } );
		double[] answer = linearRegression( points, true );
		System.out.printf( "a:%f, b: %f\n", answer[0], answer[1]);
		assertEquals( 0.0, answer[ 0 ]);
		assertEquals( 0.0, answer[ 1 ]);
		
	}
	public void testChordAndNote()
	{
		points.add( new double[] { 0.01, 0.5 } );
		points.add( new double[] { 0.03, 0.4 } );
		points.add( new double[] { 0.02, 1.0 } );
		points.add( new double[] { 1.02, 0.5 } );
		double[] answer = linearRegression( points );
		System.out.printf( "a:%f, b: %f\n", answer[0], answer[1]);
		assertTrue( abs( answer[ 1 ] ) < 1 );
		//assertEquals( Double.NaN, answer[ 1 ]);
		
	}
	public void testVerticalLine()
	{
		points.add( new double[] { 0.0, 1.0 } );
		points.add( new double[] { 0.0, -1.0 } );
		double[] answer = linearRegression( points );
		assertEquals( Double.NaN, answer[ 0 ]);
		assertEquals( Double.NaN, answer[ 1 ]);
	}
	
	public void testHorizontalLine()
	{
		points.add( new double[] {1.0, 0.0 });
		points.add( new double[] {2.0, 0.0 });
		double[] answer = linearRegression( points );
		assertEquals( 0.0, answer[ 0 ]);
		assertEquals( 0.0, answer[ 1 ]);
	}
	
	public void testOffsetHorizontalLine()
	{
		points.add( new double[] {1.0, 1.0 });
		points.add( new double[] {2.0, 1.0 });
		double[] answer = linearRegression( points );
		assertEquals( 1.0, answer[ 0 ]);
		assertEquals( 0.0, answer[ 1 ]);
	}
	
	public void testMultiplePoints()
	{
		points.add( new double[] {0.0, 0.0 });
		points.add( new double[] {1.0, 1.0 });
		points.add( new double[] {2.0, 2.0 });
		points.add( new double[] {3.0, 3.0 });
		double[] answer = linearRegression( points );
		assertEquals( 0.0, answer[ 0 ]);
		assertEquals( 1.0, answer[ 1 ]);
		
	}
	public void testDiagonalDownwardsLine()
	{
		points.add( new double[] {0.0, 1.0 });
		points.add( new double[] {1.0, 0.0 });
		double[] answer = linearRegression( points );
		assertEquals( 1.0, answer[ 0 ]);
		assertEquals( -1.0, answer[ 1 ]);
	}
	
	public void testDiagonalLine()
	{
		points.add( new double[] {0.0, 0.0 });
		points.add( new double[] {1.0, 1.0 });
		double[] answer = linearRegression( points );
		assertEquals( 0.0, answer[ 0 ]);
		assertEquals( 1.0, answer[ 1 ]);
	}
	
	public void testSinglePoint()
	{
		double[] answer = linearRegression( points );
		points.add( new double[] {1.0, 1.0 });
		assertEquals( 0.0, answer[ 0 ]);
		assertEquals( 0.0, answer[ 1 ]);
	}
	public void testEmptySet()
	{
		double[] answer = linearRegression( points );
		assertEquals( 0.0, answer[ 0 ]);
		assertEquals( 0.0, answer[ 1 ]);
	}
	

}
