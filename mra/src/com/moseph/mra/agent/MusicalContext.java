package com.moseph.mra.agent;

import java.util.*;

import com.moseph.mra.agent.analyser.GroupFeature;
import com.moseph.mra.agent.attribute.Feature;

/**
 * Used for storing the current musical context; intended to allow different update rules to be put in place
 * @author dave
 *
 */
public class MusicalContext
{
	Map<String, Map<MusicianInformation,Feature>> currentValues;
	Context context;
	FeatureSet features;
	
	public MusicalContext( Context c )
	{
		this.context = c;
		features = context.getFeatures();
		currentValues = new HashMap<String, Map<MusicianInformation,Feature>>();
	}
	
	public void update()
	{
		for( String name : features.getFeatureNames() )
		{
			GroupFeature f = features.getFeature( name );
			if( ! currentValues.containsKey( name ))
				currentValues.put( name, new HashMap<MusicianInformation, Feature>() );
			//Generics breaking again...
			for( Object musician : f.getMusicians() )
			{
				MusicianInformation mi = (MusicianInformation) musician;
				updateEntry( mi, name, f.getValue( mi ));
			}
				
		}
	}
	
	void updateEntry( MusicianInformation musician, String featureName, Feature newValue )
	{
		Feature oldValue = currentValues.get( featureName ).get( musician );
		if( newValue != null ) currentValues.get( featureName ).put( musician, newValue );
	}
	
	public Feature getCurrentValue( MusicianInformation musician, String featureName )
	{
		return currentValues.get( featureName ).get( musician );
	}
	

}
