package com.moseph.mra.midi.analysis;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import com.moseph.mra.*;
import com.moseph.mra.acts.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.reasoning.sequences.*;
import com.moseph.mra.midi.*;
import com.moseph.mra.symbolic.*;
import com.moseph.mra.visualise.*;

import static com.moseph.mra.MRAUtilities.*;

public class TwoFragmentAnalyser
{



	protected FilePlayback track1;
	protected FilePlayback track2;
	protected Fragment frag1 = new Fragment();
	protected Fragment frag2 = new Fragment();
	protected double chunkSize = 0.5;
	protected double pos = 0.0;
	protected ActionExtractor extractor;
	MusicianInformation me = new MusicianInformation( new Musician( "me"));
	MusicianInformation them = new MusicianInformation( new Musician( "them"));
	protected MusicianInformation a = new MusicianInformation( new Musician( "a"));
	protected MusicianInformation b = new MusicianInformation( new Musician( "b"));
	protected Context context = new Context( me );
	protected AnalysisSystem analyser = new AnalysisSystem( context, 10 );
	protected DefaultLatticeManager latticeManager = new DefaultLatticeManager( 5 );
	Map<String,List<ActRelation>> actHistory = new HashMap<String, List<ActRelation>>();
	Map<String,MusicalAction> previousActs = new HashMap<String, MusicalAction>();
	FeatureMusicianMap<MusicalAction> previousMusicianActs = new FeatureMusicianMap<MusicalAction>();
	int sequenceOrder = 4;
	
	SequenceCollection<ActRelation> sequences;
	
	public static void main( String[] args )
	{
		if( args.length == 0 ) 
		{
			System.err.println( "Need a filename");
			System.exit( 1 );
		}
		TwoFragmentAnalyser tfa = new TwoFragmentAnalyser( args[0]);
		while( tfa.getPosition() < 400 )
		{
			tfa.skip();
		}
		while( tfa.hasMoreEvents() && tfa.getPosition() < 500.0 )
		{
			tfa.nextChunk();
		}
		tfa.done();
		if( args.length > 1 ) tfa.writeSequences( args[1] );
	}	
	
	
	public TwoFragmentAnalyser( String filename )
	{
		sequences = new SequenceCollection<ActRelation>( sequenceOrder );
		context.setAttribute( "PatternLength", 2 );
		context.setQuantisation( 5.0 );
		context.setLatticeManager( latticeManager );
		track1 = new FilePlayback( filename, 1 );
		track2 = new FilePlayback( filename, 2 );
		frag1.setMusician( a.getMusician() );
		frag2.setMusician( b.getMusician() );
		
		setUpAnalyser();
		
	}
	
	public void setUpAnalyser()
	{
		analyser.setAnalyseSelf( true );
		analyser.setMinNotes( -1 );
		analyser.addAnalyser( "DisplacementAverage" );
		analyser.addAnalyser( "DisplacementChange" );
		analyser.addAnalyser( "DisplacementPattern" );
		analyser.addAnalyser( "DynamicAverage" );
		analyser.addAnalyser( "DynamicChange" );
		analyser.addAnalyser( "DynamicPattern" );
		analyser.addAnalyser( "LengthChange" );
		analyser.addAnalyser( "LengthAverage" );
		analyser.addAnalyser( "LengthPattern" );
		analyser.analyse( new Score() );
		extractor = new ActionExtractor( context, latticeManager );
		
		extractor.initialise();
		extractor.setRelativeMusician( a, b );
		extractor.setRelativeMusician( b, a );
	}
	public void nextChunk()
	{
		
		System.out.println( "**********************************\n*******************************\nFrom " 
				+ pos + " to " + ( pos+chunkSize) + "\n**********************************\n");
		Fragment aFrag =  track1.getNextChunk( chunkSize );
		Fragment bFrag =  track2.getNextChunk( chunkSize );
		frag1.append( aFrag );
		frag2.append( bFrag );
		aFrag.setMusician( a.getMusician() );
		bFrag.setMusician( b.getMusician() );
		Score s = new Score();
		s.add( aFrag );
		s.add( bFrag );
		s.forceLength( chunkSize );
		//System.out.println( "Adding: " + s );
		
		analyser.analyse( s );
		
		pos += chunkSize;
		frag1.setLength( pos );
		frag2.setLength( pos );
		context.setHeardMusicTill( pos );
		FeatureMusicianMap<MusicalAction> update = extractor.update();
		processUpdate( update );
	}
	
	public void skip()
	{
		pos += chunkSize;
	}
	public boolean hasMoreEvents()
	{
		return track1.hasMoreEvents() || track2.hasMoreEvents();
	}
	public double getPosition()
	{
		return pos;
	}
	
	protected void done()
	{
		addActionsToSequences();
		//printSequences();
	}
	
	void processUpdate( FeatureMusicianMap<MusicalAction> update )
	{
		for( String feature : update.features() )
		{
			for( MusicalAction act : update.getFeatureValues( feature ))
			{
				MusicalAction previous = previousActs.get( feature );
				if( previous != null && previous.getMusician() == act.getMusician() )
				{
					addSameRelationship( feature, previous );
				}
				addActRelationship( feature, new ActRelation( act ) );
			}
		}
	}
	
	void addActRelationship( String feature, ActRelation act )
	{
		if( ! actHistory.containsKey( feature )) actHistory.put( feature, new Vector<ActRelation>() );
		actHistory.get( feature ).add( act );
	}
	
	void addSameRelationship( String feature, MusicalAction previous )
	{
		MusicianInformation createActFor = a;
		if( previous.getMusician().getName().equals( a.getName() )) createActFor = b;
		
		Relationship rOther = Relationship.DISJOINT;
		MusicalAction selfAction = previousMusicianActs.getValue( feature, createActFor.getName() );
		if( selfAction != null )
		{
			ValueLattice latt = latticeManager.getLattice( feature );
			rOther = latt.getRelationship( selfAction.getValue(), previous.getValue() );
		}
		addActRelationship( feature, new ActRelation( Relationship.SAME, rOther ) );
	}
	
	void addActionsToSequences()
	{
		for( List<ActRelation> actList : actHistory.values() )
		{
			for( int i = 0; i < actList.size(); i++ )
			{
				int end = i + sequenceOrder + 1;
				if( end >= actList.size() ) end = actList.size() - 1;
				System.out.println( "Adding sequence: " + collectionToString( actList.subList( i, end ) ) );
				sequences.addSequence( actList.subList( i, end ) );
			}
		}
		sequences.calculateProbabilities();
	}
	
	void printSequences()
	{
		System.out.println( "&&&&&&&&&&&&&&&&&&&&&&&&&&&\n\n*****************************8\n\n");
		System.err.println( sequences );
	}
	
	public void writeSequences( String filename )
	{
		System.out.println( "Writing sequences to file: " + filename );
		sequences.toFile( filename );
	}
	
}