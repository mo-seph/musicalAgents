package com.moseph.mra.agent;

import com.moseph.mra.Fragment;

public class PartFollower extends PartChunker
{
	MusicianInformation other;
	String partname;
	
	public PartFollower( Context c, MusicianInformation other, String partname )
	{
		super( c, new PartFollowerDecider( c, other, partname ) );
		this.partname = partname;
	}
	
	//Revise current position, based on new input
	public void newMaterial( Fragment f )
	{
		
	}
	
	public void setPartname( String name )
	{
		partname = name;
	}
}
