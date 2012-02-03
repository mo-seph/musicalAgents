package com.moseph.mra.acts;

import java.io.Serializable;

import com.moseph.mra.symbolic.*;

public class ActRelation implements Serializable
{
	Relationship rSelf;
	Relationship rOther;

	public ActRelation( Relationship rSelf, Relationship rOther )
	{
		this.rSelf = rSelf;
		this.rOther = rOther;
	}
	
	public ActRelation( MusicalAction action )
	{
		this.rSelf = action.getRSelf();
		this.rOther = action.getROther();
	}
	
	public String toString()
	{
		return rSelf + ":" + rOther;
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
	
	public boolean equals( Object other )
	{
		if( other == null ) return false;
		if( !( other instanceof ActRelation ) ) return false;
		ActRelation o = (ActRelation)other;
		return rSelf == o.rSelf && rOther == o.rOther;
	}
}
