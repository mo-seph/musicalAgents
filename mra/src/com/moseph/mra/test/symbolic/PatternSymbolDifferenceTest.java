package com.moseph.mra.test.symbolic;

import java.util.*;

import junit.framework.TestCase;

import com.moseph.mra.*;
import com.moseph.mra.acts.MusicalAction;
import com.moseph.mra.agent.MusicianInformation;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.symbolic.*;
import static com.moseph.mra.MRAUtilities.*;
import static com.moseph.mra.symbolic.Relationship.*;

public class PatternSymbolDifferenceTest extends TestCase
{
	NumericTreeValue root = new NumericTreeValue( "Root", 0.0 );
	TreeLattice<Double, NumericTreeValue> tree = new TreeLattice<Double, NumericTreeValue>( root );
	NumericTreeValue a1;
	NumericTreeValue a2;
	NumericTreeValue a3;
	NumericTreeValue b1;
	NumericTreeValue b2;
	NumericTreeValue b3;
	NumericSymboliser symboliser = new NumericSymboliser( tree );
	PatternSymbolDifferenceExtractor differencer;
	MusicianInformation me = new MusicianInformation( new Musician( "me"));
	MusicianInformation them = new MusicianInformation( new Musician( "them"));
	GroupPatternFeature<PatternAttribute> feature = 
		new GroupPatternFeature<PatternAttribute>( new PatternAttribute( 1.0, new double[] { 0.0, 0.0 } ), me );
	Map<MusicianInformation, NumericPatternValue> val = new HashMap<MusicianInformation, NumericPatternValue>();
	NumericPatternValue a1a1;
	NumericPatternValue a1a2;
	NumericPatternValue rootroot;
	
	public void setUp()
	{
		differencer = new PatternSymbolDifferenceExtractor( "TEST", feature, tree, 2 );
		a1 = tree.addTerm( "a1", 0.7, root );
		a2 = tree.addTerm( "a2", 0.8, "a1" );
		a3 = tree.addTerm( "a3", 0.9, "a2" );
		b1 = tree.addTerm( "b1", 0.3, root );
		b2 = tree.addTerm( "b2", 0.2, "b1" );
		b3 = tree.addTerm( "b3", 0.1, "b2" );
		feature.setValue( them, new PatternAttribute( 1.0, 0.5, 0.5) );
		a1a1 = new NumericPatternValue( 2, tree, a1, a1 );
		a1a2 = new NumericPatternValue( 2, tree, a1, a2 );
		rootroot = new NumericPatternValue( 2, tree, root, root );
		
	}
	
	public void testBasicOperation()
	{
		val = differencer.update();
		assertEquals( rootroot, val.get( them ));
		assertNull( val.get( me ) );
		val = differencer.update();
		assertNull( val.get( them ) );
		assertNull( val.get( me ) );
		
		feature.setValue( me, new PatternAttribute( 1.0, 0.75, 0.75 ) );
		feature.setValue( them, new PatternAttribute( 1.0, 0.75, 0.85 ) );
		val = differencer.update();
		System.out.println( collectionToString( val  ));
		assertEquals( a1a1, val.get( me ));
		assertEquals( a1a2, val.get( them ));
		
	}
	
	public void testRelations()
	{
		Map<MusicianInformation, MusicalAction> actions;
		differencer.update();
		actions = differencer.getActions();
		assertEquals( DISJOINT, actions.get( them ).getRSelf() );
		assertNull( actions.get( me ) );
		System.out.println( collectionToString( actions ));
		
		//Do one round with no change just to make sure
		val = differencer.update();
		actions = differencer.getActions();
		assertNull( actions.get( them ) );
		assertNull( actions.get( me ) );
		
		feature.setValue( me, new PatternAttribute( 1.0, 0.75, 0.75 ) );
		feature.setValue( them, new PatternAttribute( 1.0, 0.75, 0.85 ) );
		val = differencer.update();
		actions = differencer.getActions();
		System.out.println( collectionToString( actions  ));
		assertEquals( DISJOINT, actions.get( me ).getRSelf() );
		assertEquals( SUBSUMES, actions.get( them ).getRSelf() );
		
	}
	

}
