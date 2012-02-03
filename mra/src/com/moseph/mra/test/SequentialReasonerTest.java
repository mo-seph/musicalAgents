package com.moseph.mra.test;

import java.util.*;

import junit.framework.TestCase;

import com.moseph.mra.*;
import com.moseph.mra.acts.*;
import static com.moseph.mra.symbolic.Relationship.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.DynamicAverageAnalyser;
import com.moseph.mra.agent.attribute.ValuedAttribute;
import com.moseph.mra.agent.reasoning.*;
import static com.moseph.mra.agent.reasoning.SequenceReasoner.*;
import static com.moseph.mra.agent.reasoning.ValueReasoner.Aspect.*;
import com.moseph.mra.agent.reasoning.sequences.*;
import com.moseph.mra.symbolic.*;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import static com.moseph.mra.MRAUtilities.*;

public class SequentialReasonerTest extends TestCase
{
	MusicianInformation a = new MusicianInformation( new Musician( "a"));
	MusicianInformation b = new MusicianInformation( new Musician( "b"));
	Context context = new Context( a );
	Piece piece;
	Section sect;
	StructuralDecider decider;
	Fragment piano = new Fragment( 2.0 ); 
	RenderingSystem renderer;
	SequenceReasoner reasoner;
	AnalysisSystem analysisSystem = new AnalysisSystem( context );
	FeatureMusicianMap<MusicalAction> currentActions = new FeatureMusicianMap<MusicalAction>();
	double chunkLength = 2.0;
	LatticeManager lattices = new EmptyLatticeManager( 10 );
	
	NumericTreeValue root = new NumericTreeValue( "Root", 0.0 );
	NumericTreeLattice tree = new NumericTreeLattice( root );
	NumericTreeValue mf;
	NumericTreeValue f;
	NumericTreeValue ff;
	NumericTreeValue mp;
	NumericTreeValue p;
	NumericTreeValue pp;
	SequenceManager sequence = new DefaultSequenceManager();
	SequenceCollection<ActRelation> sequences = sequence.getSequences();
	
	
	public void setUp()
	{
		context.setSequenceManager( sequence );
		context.setPartname( "Piano");
		piano.setName( "Piano" );
		piece = new Piece( "testpiece" );
		sect = new Section( "a");
		sect.setLength( 2.0 );
		sect.addChannel( piano );
		piece.addChild( sect );
		piece.activate();
		context.setPiece( piece );
		
		mf = tree.addTerm( "mf", 0.7, root );
		f = tree.addTerm( "f", 0.8, "mf" );
		ff = tree.addTerm( "ff", 0.9, "f" );
		mp = tree.addTerm( "mp", 0.3, root );
		p = tree.addTerm( "p", 0.2, "mp" );
		pp = tree.addTerm( "pp", 0.1, "p" );
		lattices.setDefaultLattice( tree );
		context.setLatticeManager( lattices );
		
		
		
		
		decider = new StructuralDecider( context );
		renderer = new RenderingSystem();
		reasoner = new SequenceReasoner( context, decider )
		{
			public FeatureMusicianMap<MusicalAction> getCurrentActs()
			{
				return currentActions;
			}
		};
		reasoner.initNecessaryAnalysers( analysisSystem );
	}
	
	//TODO: Test cold start
	
	public void itestBasicOperation()
	{
		List<ActMap> acts = new Vector<ActMap>();
		List<BasicRenderPlan> plans = new Vector<BasicRenderPlan>();
		
		ActMap map1 = new ActMap();
		map1.setValue( DynamicAverageAnalyser.featureName, b.getName(), 
				new MusicalAction( b, ff, DISJOINT, DISJOINT, 0.0 ) );
		
		BasicRenderPlan plan1 = new BasicRenderPlan( chunkLength );
		//This isn't right!
		plan1.setDynamicsCurve( new CurveContainer( chunkLength, ff.getValue(), ff.getValue() ) );
		
		acts.add( map1 );
		plans.add( plan1 );
		
		acts.add( new ActMap() );
		plans.add( plan1 );
		
		ActMap map2 = new ActMap();
		map1.setValue( DynamicAverageAnalyser.featureName, b.getName(), 
				new MusicalAction( b, ff, DISJOINT, DISJOINT, 0.0 ) );
		runTest( acts, plans );
	}
	
	public void itestDynamicsValueOperation()
	{
		List<MusicalAction> acts = new Vector<MusicalAction>();
		List<Value> responses = new Vector<Value>();
		
		acts.add( new MusicalAction( b, ff, DISJOINT, DISJOINT, 0.0 ) );
		responses.add( ff );
		
		acts.add( null );
		responses.add( ff );
		
		dynamicsValueTest( acts, responses );
	}
	
	public void testDynamicsResponseOperation()
	{
		sequences.addSequence( new ActRelation( DISJOINT, DISJOINT ), new ActRelation( DISJOINT, SUBSUMED ));
		sequences.addSequence( new ActRelation( SUBSUMED, SUBSUMED ), new ActRelation( SUBSUMES, SUBSUMES ));
		sequences.calculateProbabilities();
		System.out.println( sequences );
		List<MusicalAction> acts = new Vector<MusicalAction>();
		List<ActRelation> responses = new Vector<ActRelation>();
		
		acts.add( new MusicalAction( b, ff, DISJOINT, DISJOINT, 0.0 ) );
		responses.add( new ActRelation( DISJOINT, SUBSUMED ));
		
		acts.add( new MusicalAction( b, pp, DISJOINT, DISJOINT, 0.0 ) );
		responses.add( new ActRelation( DISJOINT, SUBSUMED ));
		
		acts.add( new MusicalAction( b, p, SUBSUMED, SUBSUMED, 0.0 ) );
		responses.add( new ActRelation( SUBSUMES, SUBSUMES ));
		
		dynamicsRelationTest( acts, responses );
	}
	
	
	void runTest( List<ActMap> actions, List<BasicRenderPlan> plans )
	{
		for( int i = 0; i < actions.size(); i++ )
		{
			currentActions = actions.get( i );
			BasicRenderPlan plan = new BasicRenderPlan( chunkLength );
			reasoner.fillPlan( plan );
			System.err.println( "Checking " + plans.get(  i ) + "\n against: " + plan );
			checkPlanEquality( plans.get( i ), plan );
		}
	}
	
	void dynamicsValueTest( List<MusicalAction> actions, List<Value> values )
	{
		for( int i = 0; i < actions.size(); i++ )
		{
			currentActions = new ActMap();
			if( actions.get( i ) != null )
				currentActions.setValue( DynamicAverageAnalyser.featureName, b.getName(), actions.get( i ) );
			BasicRenderPlan plan = new BasicRenderPlan( chunkLength );
			reasoner.fillPlan( plan );
			Map<Aspect,Value> currentValues = reasoner.getCurrentValues();
			System.out.println( collectionToString( currentValues ));
			assertEquals( values.get( i ), currentValues.get( DYNAMICS ) );
		}
		
	}
	
	void dynamicsRelationTest( List<MusicalAction> actions, List<ActRelation> responses )
	{
		for( int i = 0; i < actions.size(); i++ )
		{
			currentActions = new ActMap();
			if( actions.get( i ) != null )
				currentActions.setValue( DynamicAverageAnalyser.featureName, b.getName(), actions.get( i ) );
			BasicRenderPlan plan = new BasicRenderPlan( chunkLength );
			reasoner.fillPlan( plan );
			Map<Aspect,ActRelation> currentRelations = reasoner.getCurrentRelations();
			System.out.println( collectionToString( currentRelations ));
			assertEquals( responses.get( i ), currentRelations.get( DYNAMICS ) );
		}
	}
	
	void checkPlanEquality( BasicRenderPlan expected, BasicRenderPlan found )
	{
		assertEquals( expected.getDynamicsCurve() + "", found.getDynamicsCurve() + "" );
	}
	
	class ActMap extends FeatureMusicianMap<MusicalAction>
	{
		
	}
	
}
