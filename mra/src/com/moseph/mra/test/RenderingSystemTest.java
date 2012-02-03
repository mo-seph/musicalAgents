package com.moseph.mra.test;

import java.util.List;

import junit.framework.TestCase;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.reasoning.*;
import static java.lang.Math.*;

public class RenderingSystemTest extends TestCase
{
	Fragment piano = new Fragment( 2.0 ); 
	RenderingSystem renderer;
	BasicRenderPlan plan;
	
	public void setUp()
	{
		plan = new BasicRenderPlan( 4.0 );
		plan.setNotes( piano );
		piano.setName( "Piano" );
		piano.addNote(  new Note( 0.0, 60, 0.5, 1.0 ) );
		piano.addNote(  new Note( 1.0, 61, 0.5, 1.0 ) );
		piano.addNote(  new Note( 2.0, 62, 0.5, 2.0 ) );
		renderer = new RenderingSystem();
		renderer.setHumanize( false );
	}
	
	public void testDynamicCurve()
	{
		plan.setDynamicsCurve( new CurveContainer( 4.0, 0.2 ) );
		Fragment result = renderer.render( plan );
		for( Note n : result.getNotes() )
			assertEquals( 0.2, n.getVelocity() );
		CurveContainer c = new CurveContainer( 4.0, 0.0, 1.0 );
		plan.setDynamicsCurve( c );
		Fragment result2 = renderer.render( plan );
		for( Note n : result2.getNotes() )
			assertEquals( c.sample( n.getOnset() ), n.getVelocity() );
	}
	
	public void testTimingCurve()
	{
		plan.setTimingCurve( new CurveContainer( 4.0, 0.1 ) );
		Fragment result = renderer.render( plan );
		//Check that all the notes are 0.1 beats delayed
		for( Note n : result.getNotes() )
			assertEquals( 0.1, n.getOnset() % 1.0, 0.001 );
		CurveContainer c = new CurveContainer( 4.0, 0.0, 0.5 );
		plan.setTimingCurve( c );
		Fragment result2 = renderer.render( plan );
		//System.out.println( result2 );
		for( Note n : result2.getNotes() )
		{
			double origTime = floor( n.getOnset() );
			assertEquals( origTime + c.sample( origTime ), n.getOnset(), 0.001 );
		}
	}
	
	public void testLengthCurve()
	{
		//Default
		Fragment result = renderer.render( plan );
		List<Note> notes = result.getNotes();
		assertEquals( 0.7, notes.get( 0 ).getDuration(), 0.001 );
		assertEquals( 0.7, notes.get( 1 ).getDuration(), 0.001 );
		assertEquals( 1.4, notes.get( 2 ).getDuration(), 0.001 );
		
		//Single value
		plan.setLengthCurve( new CurveContainer( 4.0, 0.4 ) );
		result = renderer.render( plan );
		notes = result.getNotes();
		assertEquals( 0.4, notes.get( 0 ).getDuration(), 0.001 );
		assertEquals( 0.4, notes.get( 1 ).getDuration(), 0.001 );
		assertEquals( 0.8, notes.get( 2 ).getDuration(), 0.001 );
		
		//Curve
		CurveContainer c = new CurveContainer( 4.0, 0.5, 1.0 );
		plan.setLengthCurve( c );
		result = renderer.render( plan );
		notes = result.getNotes();
		assertEquals( c.sample( 0.0 ), notes.get( 0 ).getDuration(), 0.001 );
		assertEquals( c.sample( 1.0 ), notes.get( 1 ).getDuration(), 0.001 );
		assertEquals( 2 * c.sample( 2.0 ), notes.get( 2 ).getDuration(), 0.001 );
	}
	
	public void testNotesGrowingOverChunkBoundaries()
	{
		renderer.setRenderExpressivity( true );
		renderer.setApplyNoteDynamics( false );
		renderer.setApplyNoteTimings( false );
		renderer.setHumanize( false );
		Fragment input = new Fragment( new Note( 0.75, 35, 0.7, 0.2 ));
		Fragment expected = new Fragment( new Note( 0.75, 35, 0.7, 0.4 ));
		plan.setLengthCurve( new CurveContainer( 1.0, 2.0 ) );
		plan.setNotes( input.copyChunk( 0.0, 1.0 ) );
		Fragment result1 = renderer.render( plan );
		plan.setNotes( new Fragment( 1.0 ) );
		Fragment result2 = renderer.render( plan );
		assertEquals( expected.copyChunk( 0.0, 1.0 ), result1 );
		assertEquals( expected.copyChunk( 1.0, 2.0 ), result2 );
		
		result1.append( result2 );
		assertEquals( expected, result1 );
	}

	public void testNotesDisplacingBeforeChunkBoundaries()
	{
		renderer.setRenderExpressivity( true );
		renderer.setApplyNoteDynamics( false );
		renderer.setApplyNoteTimings( true );
		renderer.setApplyNoteLengths( false );
		renderer.setHumanize( false );
		Fragment input = new Fragment( new Note( 0.0, 35, 0.7, 0.2 ));
		Fragment expected = new Fragment( new Note( -0.05, 35, 0.7, 0.2 ));
		plan.setTimingCurve( new CurveContainer( 1.0, -0.05 ) );
		plan.setNotes( input.copyChunk( 0.0, 1.0 ) );
		Fragment result = renderer.render( plan );
		assertEquals( expected, result );
	}



	
}
