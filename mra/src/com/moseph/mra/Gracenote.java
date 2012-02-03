package com.moseph.mra;

import static com.moseph.mra.MRAUtilities.formatBeat;

import org.w3c.dom.Element;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

public class Gracenote extends Note
{
	public static final double GN_LENGTH = 0.25;

	public Gracenote( double startTime, int pitch, double velocity )
	{
		super( startTime, pitch, velocity, GN_LENGTH );
		//endTime = 0.0;
	}

	public Gracenote()
	{
		super();
		setDuration( GN_LENGTH );
		//endTime = 0.0;
	}
	
	public void setOnset( double onset )
	{
		super.setOnset( onset );
		setDuration( GN_LENGTH );
	}
	
	/*
	public double getDuration()
	{
		return GN_LENGTH;
	}
	
	public double getEndTime()
	{
		return getOnset() + GN_LENGTH;
	}

	public void setDuration()
	{
		return;
	}
	
	public void setEndTime()
	{
		return;
	}
	
	public String getRangeString()
	{
		String range = "(" + formatBeat( onset ) + ")";
		return range;
	}

	public String getContentString()
	{
		return friendlyName();
	}

	*/
}
