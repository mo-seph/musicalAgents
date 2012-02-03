package com.moseph.mra.agent.analyser;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.MusicianInformation;
import com.moseph.mra.agent.attribute.Feature;

public abstract class GroupFeature<T extends Feature>
{
	MusicianInformation me;
	Map<MusicianInformation,T> features = new HashMap<MusicianInformation,T>();
	Set<MusicianInformation> otherMusicians = new HashSet( features.keySet() );
	T instance;
	protected boolean includeSelf = false;
	protected boolean distanceWeight = false;
	boolean relative = false;
	
	public GroupFeature( T instance, MusicianInformation me )
	{
		this.instance = instance;
		this.me = me;
	}
	
	public void setValue( MusicianInformation m, T f  )
	{
		features.put( m, f );
		updateTotals();
		if( ! m.equals( me ) ) otherMusicians.add(  m );
		//getValue( m );
	}
	
	public abstract T getAverage();
	
	public T getValue( MusicianInformation m )
	{
		return features.get( m );
	}
	
	public void updateTotals() {}
	
	public void clearValue( MusicianInformation m )
	{
		features.remove( m );
		otherMusicians.remove( m );
		updateTotals();
	}
	
	public Set<MusicianInformation> getMusicians()
	{
		return new HashSet<MusicianInformation>( features.keySet() );
	}
	
	
	/**
	 * Gets all the musicians except "me"
	 * @return
	 */
	public Set<MusicianInformation> getOtherMusicians()
	{
		return otherMusicians;
	}
	public List<T> getValues()
	{
		return new Vector<T>( features.values() );
	}
	
	public List<T> getOthersValues()
	{
		Vector<T>vals = new Vector<T>();
		for( MusicianInformation m : getOtherMusicians() ) vals.add( features.get( m ));
		return vals;
	}
	
	public List<T> getValuesToAverage()
	{
		if( includeSelf ) return getValues();
		return getOthersValues();
	}

	public boolean isIncludeSelf()
	{
		return includeSelf;
	}

	public void setIncludeSelf( boolean includeSelf )
	{
		this.includeSelf = includeSelf;
	}
	
	public void retireMusician( MusicianInformation m )
	{
		features.remove( m  );
		otherMusicians.remove( m );
		updateTotals();
	}

	public boolean isDistanceWeight()
	{
		return distanceWeight;
	}

	public void setDistanceWeight( boolean distanceWeight )
	{
		this.distanceWeight = distanceWeight;
	}

	public boolean isRelative()
	{
		return relative && me != null;
	}

	public void setRelative( boolean relative )
	{
		if( me != null ) this.relative = relative;
	}
	
	public String toString()
	{
		return getClass().getSimpleName() + "<" + instance.getClass().getSimpleName() + ">" + MRAUtilities.collectionToString( features, 1 );
	}
	
	public void printState()
	{
		//System.out.println( "")
		for( MusicianInformation m : features.keySet() )
		{
			System.out.println( "^^^ " + m.getName() + " :: " + features.get( m ));
		}
	}
	
	public int getCounted()
	{
		if( includeSelf ) return features.size();
		return otherMusicians.size();
	}
}
