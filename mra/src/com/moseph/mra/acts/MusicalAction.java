package com.moseph.mra.acts;

import com.moseph.mra.TemporalEvent;
import com.moseph.mra.agent.MusicianInformation;
import com.moseph.mra.symbolic.*;

public class MusicalAction extends TemporalEvent
{
	Value value;
	Relationship rSelf;
	Relationship rOther;
	MusicianInformation musician;

	public MusicalAction( MusicianInformation musician, Value value, Relationship rSelf )
	{
		super();
		this.musician = musician;
		this.value = value;
		this.rSelf = rSelf;
	}
	
	public MusicalAction( MusicianInformation musician, Value value, Relationship rSelf, Relationship rOther, double time )
	{
		super( time );
		this.musician = musician;
		this.value = value;
		this.rSelf = rSelf;
		this.rOther = rOther;
	}
	
	public String toSingleString()
	{
		return value + "{o:" + rOther + ",s:" + rSelf + "}";
	}
	
	public String toString()
	{
		return "\n" + rSelf + "--" + value + "\n" + rOther + "_/" + "\t(" + getOnset() + ")";
	}
	public Relationship getROther()
	{
		return rOther;
	}
	public void setROther( Relationship other )
	{
		rOther = other;
	}
	public Relationship getRSelf()
	{
		return rSelf;
	}
	public void setRSelf( Relationship self )
	{
		rSelf = self;
	}
	public Value getValue()
	{
		return value;
	}
	public void setValue( Value value )
	{
		this.value = value;
	}
	
	public String getShortContentString()
	{
		if( value instanceof PatternValue ) return "<>";
		return value.toString();
	}

	public MusicianInformation getMusician()
	{
		return musician;
	}

	public void setMusician( MusicianInformation musician )
	{
		this.musician = musician;
	}
}
