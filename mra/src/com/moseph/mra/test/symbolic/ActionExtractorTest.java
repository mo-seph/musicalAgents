package com.moseph.mra.test.symbolic;

import com.moseph.mra.*;
import com.moseph.mra.acts.ActionExtractor;
import com.moseph.mra.agent.*;
import com.moseph.mra.symbolic.*;

import static com.moseph.mra.MRAUtilities.*;

import junit.framework.TestCase;

public class ActionExtractorTest extends TestCase
{
	double analysisLength = 1.0;
	ActionExtractor extractor;
	MusicianInformation me = new MusicianInformation( new Musician( "me"));
	MusicianInformation them = new MusicianInformation( new Musician( "them"));
	Context context = new Context( me );
	AnalysisSystem analyser = new AnalysisSystem( context, analysisLength );
	
	NumericTreeValue root = new NumericTreeValue( "Root", 0.0 );
	NumericTreeLattice tree = new NumericTreeLattice( root );
	NumericTreeValue a1;
	NumericTreeValue a2;
	NumericTreeValue a3;
	NumericTreeValue b1;
	NumericTreeValue b2;
	NumericTreeValue b3;
	
	public void setUp()
	{
		LatticeManager lattices = new EmptyLatticeManager( 10 );
		context.setLatticeManager( lattices );
		analyser.setAnalyseSelf( true );
		analyser.setMinNotes( 0 );
		analyser.addAnalyser( "DisplacementAverage" );
		analyser.addAnalyser( "DisplacementChange" );
		analyser.addAnalyser( "DisplacementPattern" );
		analyser.addAnalyser( "DynamicAverage" );
		analyser.addAnalyser( "DynamicChange" );
		analyser.addAnalyser( "DynamicPattern" );
		analyser.addAnalyser( "LengthChange" );
		analyser.addAnalyser( "LengthAverage" );
		analyser.addAnalyser( "LengthPattern" );
		
		a1 = tree.addTerm( "mf", 0.7, root );
		a2 = tree.addTerm( "f", 0.8, "a1" );
		a3 = tree.addTerm( "ff", 0.9, "a2" );
		b1 = tree.addTerm( "mp", 0.3, root );
		b2 = tree.addTerm( "p", 0.2, "b1" );
		b3 = tree.addTerm( "pp", 0.1, "b2" );
		lattices.setDefaultLattice( tree );
		extractor = new ActionExtractor( context, lattices );
		extractor.initialise();
	}
	
	public void testBasicOperation()
	{
		Score s = new Score();
		Fragment other = new Fragment( 1.0 );
		other.addNote( 0.0, 64, 0.7, 0.8 );
		other.setMusician( them.getMusician() );
		s.add( other );
		analyser.analyse( s );
		extractor.update();
		System.out.println( collectionToString( extractor.getQueues() ));
		
		other.addNote( 0.2, 67, 0.7, 0.1 );
		other.addNote( 0.4, 67, 0.7, 0.1 );
		other.addNote( 0.6, 67, 0.7, 0.1 );
		other.addNote( 0.7, 67, 0.7, 0.1 );
		s.add( other );
		analyser.analyse( s );
		extractor.update();
		System.out.println( collectionToString( extractor.getQueues() ));
		
		
	}
	
	

}
