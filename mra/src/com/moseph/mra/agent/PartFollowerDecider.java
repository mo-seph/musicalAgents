package com.moseph.mra.agent;

public class PartFollowerDecider extends StructuralDecider
{
	String partname = null;
	
	public PartFollowerDecider( Context c, MusicianInformation other, String partname )
	{
		super( c );
	}

	public String getPartname()
	{
		return partname;
	}

	public void setPartname( String partname )
	{
		this.partname = partname;
	}

}
