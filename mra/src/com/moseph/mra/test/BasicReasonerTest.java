package com.moseph.mra.test;

import junit.framework.TestCase;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;

public class BasicReasonerTest extends TestCase
{
	Context context = new Context( new MusicianInformation( new Musician( "tmp") ) );
	Piece p;
	Section a;
	StructuralDecider decider;
	Fragment piano = new Fragment( 2.0 ); 
	RenderingSystem renderer;
	
	public void setUp()
	{
		context.setPartname( "Piano");
		piano.setName( "Piano" );
		p = new Piece( "testpiece" );
		a = new Section( "a");
		a.setLength( 2.0 );
		a.addChannel( piano );
		p.addChild( a );
		p.activate();
		context.setPiece( p );
		decider = new StructuralDecider( context );
		renderer = new RenderingSystem();
	}
	
	public void testTimings()
	{
		/*
		CurveContainer timing = new CurveContainer( 1.0 );
		timing.setName( "Timing");
		a.addChannel( timing );
		double[] originalOnsets = new double[] { 0.0, 0.5, 0.8 };
		for( double onset : originalOnsets )
		{
			piano.addNote( new Note( onset, 64, 0.5, 1.0 ) );
		}
		
		Fragment f = renderer.renderNextChunk( 1.0 );
		System.out.println( piano + "" );
		System.out.println( f + "" );
		int index = 0;
		for( Note n : f.getNotes() )
		{
			double rendered = n.getOnset();
			double expected = timing.sample( originalOnsets[ index ]  );
			double original = originalOnsets[ index ];
			System.out.printf( "From: %f Got : %f Expected: %f\n", original, rendered, expected );
			assertEquals( n.getOnset(), timing.sample( originalOnsets[ index ] ) );
			index++;
		}
		*/
	}
	
	/*
	public void testDynamics()
	{
		renderer.setRenderExpressivity( false );
		renderer.setApplyDynamicCurve( true );
		CurveContainer dynamic = new CurveContainer( 1.0 );
		dynamic.setName( "Dynamics");
		a.addChannel( dynamic );
		piano.addNote( new Note( 0.0, 64, 0.5, 1.0 ) );
		piano.addNote( new Note( 0.25, 65, 0.5, 1.0 ) );
		piano.addNote( new Note( 0.5, 66, 0.5, 1.0 ) );
		piano.addNote( new Note( 0.75, 67, 0.5, 1.0 ) );
		dynamic.addEvent( new Curve( 0.25, 1.0, 0.0, 0.5 ));
		
		Fragment f = renderer.getNextChunk( 1.0 );
		Fragment exp = new Fragment( 1.0 );
		exp.setName( "Notes");
		exp.addNote( new Note( 0.0, 64, RenderingSystem.dynamicToVelocity( 0.0 ), 1.0 ) );
		exp.addNote( new Note( 0.25, 65, RenderingSystem.dynamicToVelocity( 0.0 ), 1.0 ) );
		exp.addNote( new Note( 0.5, 66, 
				RenderingSystem.dynamicToVelocity( 0.5 / 3.0 ), 1.0 ) );
		exp.addNote( new Note( 0.75, 67, 
				RenderingSystem.dynamicToVelocity( 1.0 / 3.0 ), 1.0 ) );
		exp = exp.copyChunk( 0.0, 1.0 );
		
		assertEquals( exp, f );
	}
	*/
	
	/*
	
	public void testRunOffSong()
	{
		Fragment fa = renderer.getNextChunk( 1.0 );
		Fragment fb = renderer.getNextChunk( 1.0 );
		Fragment fc = renderer.getNextChunk( 1.0 );
		fa.append( fb );
		fa.append( fc );
		assertEquals( new Fragment( 3.0 ), fa );
	}
	
	public void testBasicRendering()
	{
		renderer.setRenderExpressivity( false );
		Fragment f = new Fragment( 2.0 );
		f.addNote( new Note( 0.0, 64, 0.5, 1.0 ) );
		f.addNote( new Note( 0.25, 65, 0.5, 1.0 ) );
		f.addNote( new Note( 0.5, 66, 0.5, 1.0 ) );
		piano.addFragment( f, 0.0 );
		assertEquals( f.copyChunk( 0.0, 1.0 ), renderer.getNextChunk( 1.0 ));
		assertEquals( f.copyChunk( 1.0, 2.0 ), renderer.getNextChunk( 1.0 ));
	}
	
	*/
	
}
