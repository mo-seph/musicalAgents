package com.moseph.mra.test;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.reasoning.*;

import junit.framework.TestCase;
import static com.moseph.mra.MRAConstants.*;
import static com.moseph.mra.MRAUtilities.*;

public class OutputSubsystemTest extends TestCase
{
	OutputSubsystem output;
	Musician id = new Musician( "kev");
	MusicianInformation me = new MusicianInformation( id );
	Context context = new Context( me );
	Fragment outputBuffer;
	Section a = new Section( "a");
	Section b = new Section( "b");
	Section d = new Section( "d");
	Section main = new Section( "main");
	Fragment f;
	Fragment g;
	Fragment expected;
	Piece piece;
	Reasoner reasoner;
	RenderingSystem renderer = new RenderingSystem();
	
	public void setUp()
	{
		piece = new Piece( "Monkeys");
		f = new Fragment( "Notes");
		f.addNotes
		(
			new Note( 0.0, C3, 0.5, 2.0 ),
			new Note( 2.0, D3, 0.5, 2.0 ),
			new Note( 4.0, E3, 0.5, 2.0 ),
			new Note( 6.0, F3, 0.5, 2.0 ),
			new Note( 8.0, G3, 0.5, 2.0 )
		);
		a.addChannel( f );
		
		g = new Fragment( "Notes");
		g.addNotes
		(
			new Note( 0.0, C4, 0.5, 2.0 ),
			new Note( 2.0, D4, 0.5, 2.0 ),
			new Note( 4.0, E4, 0.5, 2.0 ),
			new Note( 6.0, F4, 0.5, 2.0 ),
			new Note( 8.0, G4, 0.5, 2.0 )
		);
		b.addChannel( g );
		context.setPiece( piece );
		
		main.addChild( a );
		d.addChild( b );
		d.addChild( a );
		main.addChild( d );
		piece.addChild( main );
		
		expected = new Fragment();
		expected.append( f );
		expected.append( g );
		expected.append( f );
		
		outputBuffer = new Fragment();
		StructuralDecider structuralDecider = new StructuralDecider( context );
		renderer.setSkipExpressivity( true );
		reasoner = new DefaultReasoner( context, structuralDecider );
		output = new OutputSubsystem( outputBuffer, renderer, reasoner, 0.0, 0.0 );
		System.out.println( piece.printStructure()  );
		
	}
	
	public void testInitial()
	{
		output.fillBufferTill( 2.0 );
		System.out.println( outputBuffer );
		output.fillBufferTill( 10.0 );
		System.out.println( outputBuffer );
	}
	
	public void testSections()
	{
		output.fillBufferTill( 30.0 );
		assertEquals( expected, outputBuffer );
	}
	
	public void testMultipleSectionCalls()
	{
		Fragment got = new Fragment();
		output.setGrace( 2.0 );
		double chunk = 1.7;
		for( double i = 0.0; i < 65.0; i+= chunk )
		{
			output.fillBufferTill( i );
			Fragment thisChunk = output.getOutput( i, i + chunk );
			got.append( thisChunk );
		}
		assertEquals( expected, got );
	}
	
	public void testLeadIn()
	{
		outputBuffer = new Fragment( 8.0 );
		StructuralDecider structuralDecider = new StructuralDecider( context);
		output = new OutputSubsystem( outputBuffer, renderer, reasoner, 8.0, 0.0 );
		Fragment f = new Fragment( 8.0 );
		f.append( expected );
		
		output.fillBufferTill( 56.0 );
		assertEquals( f, outputBuffer );
	}
	
	public void testIteratedLeadIn()
	{
		OutputSubsystem.setBufferMax( 1000 );
		outputBuffer = new Fragment( 8.0 );
		StructuralDecider structuralDecider = new StructuralDecider( context);
		output = new OutputSubsystem( outputBuffer, renderer, reasoner, 8.0, 0.0 );
		Fragment f = new Fragment( 8.0 );
		f.append( expected );
		
		for( double i = 1.0; i < 56.0; i++ )
			output.fillBufferTill( i );
		assertEquals( f, outputBuffer );
	}
	public void testGrace()
	{
		output.setGrace( 2.0 );
	}

}
