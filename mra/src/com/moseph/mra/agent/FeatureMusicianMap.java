package com.moseph.mra.agent;

import java.util.*;

import com.moseph.mra.acts.MusicalAction;

public class FeatureMusicianMap<T>
{
	Map<String,Map<String,T>> actions = new HashMap<String, Map<String,T>>();
	int size = 0;
	
	public void setValue( String featureName, String musicianName, T action )
	{
		assertFeature( featureName );
		actions.get( featureName ).put( musicianName, action );
		size++;
	}
	
	public T getValue( String featureName, String musicianName )
	{
		assertFeature( featureName );
		return actions.get( featureName ).get( musicianName );
	}
	
	public List<T> getFeatureValues( String featureName )
	{
		List<T> ret = new Vector<T>();
		if( actions.containsKey( featureName )) ret.addAll( actions.get( featureName ).values() );
		return ret;
	}
	
	public boolean containsFeature( String featureName )
	{
		return actions.containsKey( featureName );
	}
	public boolean containsValue( String featureName, String musicianName )
	{
		if( ! actions.containsKey( featureName ) ) return false;
		return actions.get( featureName).containsKey( musicianName );
	}
	
	public int getSize()
	{
		return size;
	}
	
	public int getSize( String featureName )
	{
		if( ! actions.containsKey( featureName )) return 0;
		return actions.get( featureName ).keySet().size();
	}
	
	void assertFeature( String featureName )
	{
		if( ! actions.containsKey( featureName )) actions.put( featureName, new HashMap<String, T>() );
	}
	
	public List<String> features()
	{
		return new ArrayList<String>( actions.keySet() );
	}
	
	public List<String> musicians( String featureName )
	{
		assertFeature( featureName );
		return new ArrayList<String>( actions.get( featureName ).keySet() );
	}

}
	