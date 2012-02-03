package com.moseph.mra;

import static com.moseph.mra.MRAUtilities.*;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;

import com.moseph.mra.agent.attribute.MRAAttribute;

public class TemporalEvent<O extends MRAAttribute> extends Unit implements Comparable, Serializable, Cloneable
{

	protected double onset;
	private static final long serialVersionUID = 421L;
	String contentString = null;
	int fakeDifference = 0;
	protected O object;
	private static int fakeDifferenceCounter = 0;

	public TemporalEvent()
	{
		super();
		fakeDifference = fakeDifferenceCounter++;
	}
	
	public TemporalEvent( double startTime )
	{
		this();
		this.onset = startTime;
	}
	
	public TemporalEvent( O content )
	{
		this();
		setObject( content );
	}
	/**
	Compares the object to another
	It is lower if the start time is earlier, and higher if it is later
	If they are the same, then the note which finished earlier is lower
	If start and duration are the same, then the lower pitch is lower
	Otherwise, they are the same
	@param o
	*/
	public int compareTo( Object o )
	{
		int real = realCompare( o );
		if( real != 0 ) return real;
		if( fakeDifferences() ) return fakeCompare( (TemporalEvent)o );
		return 0;
	}
	
	public int realCompare( Object o )
	{
		if( ! ( o instanceof TemporalEvent ) ) return 1;
		TemporalEvent n = (TemporalEvent)o;
		if( onset < n.getOnset() - FUZZY_COMPARISON_THRESHOLD ) return -1;
		if( onset > n.getOnset() + FUZZY_COMPARISON_THRESHOLD ) return 1;
		return 0;
	}
	
	public int fakeCompare( TemporalEvent o )
	{
		return fakeDifference - o.fakeDifference;
	}
	
	public boolean fakeDifferences()
	{
		return false;
	}

	public boolean equals( Object o )
	{
		System.out.println( "TE starting compare");
		if( ! ( o instanceof TemporalEvent ) )
		{
			return false;
		}
		if( fakeDifferences() ) return false;
		TemporalEvent n = (TemporalEvent) o;
		System.out.println( "TE start OK!");
		if( !fuzzyCompare( onset, n.getOnset() ) ) return false;
		return true;
	}

	/*******************************************************************************
	*                                                                              *
	* Accessors                                                                    *
	*                                                                              *
	*******************************************************************************/
	public double getOnset()
	{
		return onset;
	}

	/**
	Sets the start time
	@param onset
	*/
	public void setOnset( double startTime )
	{
		this.onset = startTime;
	}

	/**
	Offsets the start time
	@param offset
	*/
	public void addOffset( double offset )
	{
		onset += offset;
	}

	public void addAttribute( Attribute att )
	{
		if( att.getName().equals( "Start")) { setOnset( parseDoubleFor( att.stringValue(), "Start") ); }
		else
		{
			log.log( Level.WARNING, "Funny attribute given for " + getClass() + ": " + att );
			super.addAttribute( att );
		}
	}
	public void setAttributes( List<String> vals )
	{
		if( vals.size() > 0 ) setOnset( parseDoubleFor( vals.get(0), "Onset"));
		contentString = join( vals.subList(1, vals.size() ));
	}

	/**
	Creates a copy of the note with the given start time
	@param onset
	*/
	public TemporalEvent copySetStartTime( double startTime )
	{
		TemporalEvent copy = clone();
		copy.setOnset( startTime );
		return copy;
	}

	/**
	Creates a copy of the note with the given offset added
	@param offset
	*/
	public TemporalEvent copyAddOffset( double offset )
	{
		TemporalEvent copy = clone();
		copy.addOffset( offset );
		return copy;
	}

	/**
	Creates an exact copy of the note 
	*/
	public TemporalEvent clone()
	{
		//return copy();
		TemporalEvent e;
		try
		{
			e = (TemporalEvent) super.clone();
			if( object != null ) e.setObject( object.clone() );
			if( fakeDifferences() ) e.fakeDifference = fakeDifferenceCounter++;
			return e;
		}
		catch (CloneNotSupportedException e1)
		{
			System.err.println( "Could not clone!");
			e1.printStackTrace();
			return null;
		}
	}

	/**
	Gets a string representation of the span
	*/
	public String toString()
	{
		return "[" + getRangeString() + ": " + getContentString() + "]" + getClass().getName();
	}

	
	
	public String getRangeString()
	{
		String range = "(" + formatBeat( onset ) + ")";
		return range;
	}

	public String getContentString()
	{
		String ret = "<undefined>";
		if( contentString != null ) ret = contentString;
		if( name != null ) ret = name + "->" + ret;
		return ret;
	}

	/**
	Returns a string representing the informal name of the subidivison the note falls on,
	as in "I ee + uh" for counting semiquavers
	*/
	public String getSubdivisionString()
	{
		int beat = (int)Math.floor( onset );
		double subdivision = onset - beat;
		int subdivisIndex = (int)Math.floor(  subdivision * MRAConstants.subdivisionStrings.length );
		return MRAConstants.subdivisionStrings[ subdivisIndex ];
	}
	
	public double[] getRange()
	{
		return new double[] { onset, onset };
	}

	/*******************************************************************************
	*                                                                              *
	* Accessors                                                                    *
	*                                                                              *
	*******************************************************************************/
	public O getObject()
	{
		return object;
	}

	public void setObject( O object )
	{
		this.object = object;
	}

}
