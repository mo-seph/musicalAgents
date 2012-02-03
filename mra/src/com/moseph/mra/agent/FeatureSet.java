package com.moseph.mra.agent;

import java.util.*;

import com.moseph.mra.MRAUtilities;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;

public class FeatureSet 
{
	Map<String,GroupFeature>features = new HashMap<String,GroupFeature>();
	Context context;
	
	public FeatureSet( Context context )
	{
		this.context = context;
	}
	
	public void setFeature( String feature, MusicianInformation inf, Feature value )
	{
		GroupFeature feat = getOrCreate( feature );
		
		//System.out.println( "Setting " + feature + " to " + value + " for " + inf.getName() );
		feat.setValue( context.getMusicianInformation(inf ), value );
	}
	
	public GroupFeature getOrCreate( String name )
	{
		GroupFeature f = features.get( name );
		if( f == null )
		{
			f = createFeatureGroup( name );
			System.out.println( "Creating feature " + name + " for: " );
			//new Exception().printStackTrace();
			features.put( name, f );
		}
		return f;
	}
	
	public Set<String> getFeatureNames()
	{
		return features.keySet();
	}
	
	public GroupFeature getFeature( String name )
	{
		return features.get(  name );
	}
	
	public void setGroupFeature( String name, GroupFeature f )
	{
		features.put( name, f );
	}
	
	public Feature getValue( String feature, MusicianInformation musician)
	{
		musician = context.getMusicianInformation( musician );
		GroupFeature f = getOrCreate( feature );
		return f.getValue( musician );
	}
	
	public double getNumericValue( String feature, MusicianInformation musician )
	{
		return ((NumericFeature)getValue( feature, context.getMusicianInformation( musician ) )).getValue();
	}
	
	/**
	 * Here name is synonymous with type...
	 * @param name
	 * @return
	 */
	GroupFeature createFeatureGroup( String name )
	{
		//System.out.println( "Creating feature group: " + name );
		if( name.equalsIgnoreCase( "Path"))
		{
			//System.out.println( "Creating feature group: " + name );
			GroupNumericFeature p = new GroupNumericFeature( new PathFeature( "/main", context.getPiece() ), context.getMyID() );
			p.setDistanceWeight( true );
			p.setIncludeSelf( false );
			p.setRelative( true );
			return p;
		}
		return new GroupNumericFeature( new ValuedAttribute(), context.getMyID() );
	}
	
	public void retireMusician( MusicianInformation m )
	{
		for( GroupFeature g : features.values() )
			g.retireMusician( m );
	}
	
	public boolean contains( String featureName )
	{
		return features.containsKey( featureName );
	}
	
	public void listFeatures()
	{
		for( String s : features.keySet() ) System.out.println( "Feature: " + s ) ;
	}
	
	public void printFeatures()
	{
		System.out.println( "*** Feature set ***\n");
		for( String s : features.keySet() ) System.out.println( "* Feature: " + s + " :: " + features.get( s ) + "\n :: " + features.get( s ).getAverage() ) ;
	}

	public void updateLocationSensitiveTotals()
	{
		for( GroupFeature f : features.values() )
		{
			if( f.isDistanceWeight() ) f.updateTotals();
		}
	}
	
	public String toString()
	{
		return MRAUtilities.collectionToString( features );
	}
}
