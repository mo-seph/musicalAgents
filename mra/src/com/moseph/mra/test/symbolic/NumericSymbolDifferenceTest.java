package com.moseph.mra.test.symbolic;

import java.util.*;

import junit.framework.TestCase;

import com.moseph.mra.*;
import com.moseph.mra.agent.MusicianInformation;
import com.moseph.mra.agent.analyser.GroupNumericFeature;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.symbolic.*;
import static com.moseph.mra.MRAUtilities.*;

public class NumericSymbolDifferenceTest extends TestCase
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
	NumericSymbolDifferenceExtractor differencer;
	MusicianInformation me = new MusicianInformation( new Musician( "me"));
	MusicianInformation them = new MusicianInformation( new Musician( "them"));
	GroupNumericFeature<NumericFeature> feature = new GroupNumericFeature<NumericFeature>( new ValuedAttribute(), me );
	Map<MusicianInformation, NumericTreeValue> val = new HashMap<MusicianInformation, NumericTreeValue>();
	
	public void setUp()
	{
		differencer = new NumericSymbolDifferenceExtractor( "TEST", feature, tree );
		a1 = tree.addTerm( "a1", 0.7, root );
		a2 = tree.addTerm( "a2", 0.8, "a1" );
		a3 = tree.addTerm( "a3", 0.9, "a2" );
		b1 = tree.addTerm( "b1", 0.3, root );
		b2 = tree.addTerm( "b2", 0.2, "b1" );
		b3 = tree.addTerm( "b3", 0.1, "b2" );
		feature.setValue( them, 0.5 );
		
	}
	
	public void testBasicOperation()
	{
		val = differencer.update();
		assertEquals( root, val.get( them ));
		val = differencer.update();
		assertNull( val.get( them ) );
		
		feature.setValue( me, 0.95 );
		feature.setValue( them, 0.05 );
		val = differencer.update();
		System.out.println( collectionToString( val  ));
		assertEquals( a3, val.get( me ));
		assertEquals( b3, val.get( them ));
		
		feature.setValue( me, 0.91 );
		feature.setValue( them, 0.15 );
		val = differencer.update();
		assertNull( val.get( me ));
		assertEquals( b2, val.get( them ) );
		
	}
	

}
