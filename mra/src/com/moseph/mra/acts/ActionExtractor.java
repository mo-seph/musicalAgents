package com.moseph.mra.acts;

import java.util.*;
import static com.moseph.mra.MRAUtilities.*;

import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.symbolic.*;

public class ActionExtractor
{
	Context context;
	FeatureSet features;
	FeatureMusicianMap<ActQueue> queues = new FeatureMusicianMap<ActQueue>();
	List<SymbolDifferenceExtractor> extractors = new Vector<SymbolDifferenceExtractor>();
	Map<MusicianInformation,MusicianInformation> relativeMusicians = new HashMap<MusicianInformation, MusicianInformation>();
	LatticeManager latticeManager;
	
	public ActionExtractor( Context context, LatticeManager latticeManager )
	{
		this.context = context;
		this.features = context.getFeatures();
		this.latticeManager = latticeManager;
	}
	
	
	public void initialise()
	{
		for( String fName : features.getFeatureNames() )
		{
			GroupFeature f = features.getFeature( fName );
			if( f instanceof GroupNumericFeature )
			{
				GroupNumericFeature<NumericFeature> g = (GroupNumericFeature<NumericFeature>)f;
				extractors.add( new NumericSymbolDifferenceExtractor( fName, g, latticeManager.getLattice( fName ) ));
			}
			else if ( f instanceof GroupPatternFeature )
			{
				System.out.println( "Lattice: " + latticeManager.getPatternLattice( fName ) );
				System.out.println( "Size: " + context.intAttribute( "PatternSize"));
				GroupPatternFeature<PatternAttribute> g =  (GroupPatternFeature<PatternAttribute>)f;
				extractors.add( 
						new PatternSymbolDifferenceExtractor( 
								fName, g, latticeManager.getPatternLattice( fName ), context.intAttribute( "PatternSize")));
				System.err.println( "Pattern differencer with size " + context.intAttribute( "PatternSize" ) );
			}
		}
	}
	
	public void setRelativeMusician( MusicianInformation from, MusicianInformation to )
	{
		from = context.getMusicianInformation( from );
		to = context.getMusicianInformation( to );
		relativeMusicians.put( from, to );
	}
	
	MusicianInformation getRelativeMusician( MusicianInformation mus )
	{
		mus = context.getMusicianInformation( mus );
		if( relativeMusicians.containsKey( mus ) ) return relativeMusicians.get( mus );
		return context.getMyID();
	}
	
	/**
	 * Has side effects - only call once!
	 *
	 */
	public FeatureMusicianMap<MusicalAction> update()
	{
		FeatureMusicianMap<MusicalAction> ret = new FeatureMusicianMap<MusicalAction>();
		for( SymbolDifferenceExtractor sd : extractors )
		{
			String featureName = sd.getName();
			sd.update();
			Map<MusicianInformation,MusicalAction> update = sd.getActions();
			//System.out.println( collectionToString( update ));
			for( MusicianInformation mi : update.keySet() )
			{
				MusicalAction act = update.get( mi );
				ActQueue queue = getQueueForMusician( mi, featureName );
				if( act == null ) System.err.println( "Null act!");
				
				//Set some features of the act that we can only do here
				act.setOnset( context.getHeardMusicTill() );
				MusicianInformation relativeMusician = getRelativeMusician( mi );
				Value vOther = context.getCurrentValue( relativeMusician, featureName );
				//if( vOther == null ) System.err.println( "No value for " + featureName + " for " + relativeMusician.getName() +":"+collectionToString( context.getOthersValues() ));
				act.setROther( sd.getLattice().getRelationship( act.value, vOther ) );
				queue.addAction( act );
				ret.setValue( featureName, mi.getName(), act );
				//System.err.println("ACT: " + act );
				//System.err.println( queue );
			}
			for( MusicianInformation mi : update.keySet() )
			{
				MusicalAction act = update.get( mi );
				context.setCurrentValue( mi.getName(), featureName, act.getValue() );
			}
		}
		return ret;
	}
	
	public ActQueue getQueueForMusician( String musicianName, String featureName )
	{
		return getQueueForMusician( context.getMusicianInformation( musicianName ), featureName );
	}
	
	/**
	 * Accesses the  FeatureMusicianMap queues which contains the event queues, indexed by feature
	 * name then musician name
	 * @param inf
	 * @param featureName
	 * @return
	 */
	public ActQueue getQueueForMusician( MusicianInformation inf, String featureName )
	{
		String musName = inf.getName();
		if( ! queues.containsValue( featureName, musName )) 
		{
			ActQueue newActQueue = new ActQueue( context.getMusicianInformation( musName ));
			newActQueue.setName( musName + ":-" + featureName );
			queues.setValue( featureName, musName, newActQueue );
		}
		ActQueue queue = queues.getValue( featureName, musName );
		return queue;
	}
	
	
	
	public FeatureMusicianMap<ActQueue> getQueues()
	{
		return queues;
	}
	

}
