package com.moseph.mra;


import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;

import com.moseph.mra.agent.attribute.MRAAttribute;

import static com.moseph.mra.MRAUtilities.*;
import static com.moseph.mra.MRAConstants.*;
import static java.util.logging.Level.*;


public class Span<O extends MRAAttribute> extends TemporalEvent<O> 
{

	protected double endTime;
	protected boolean longerThanFragment = false;
	protected boolean startsBeforeFragment = false;
	public static final String LONGER_THAN_FRAGMENT = "hanging";
	public static final String STARTS_BEFORE_FRAGMENT = "continuation";
	boolean hasEnd = true;
	static double NON_ENDED_RANGE_FUDGE_FACTOR = 150;
	
	public Span()
	{
		super();
	}
	
	public Span( double onset, double endTime, boolean startsBeforeFragment, boolean longerThanFragment )
	{
		this( null, onset, endTime, startsBeforeFragment, longerThanFragment );
		setObject( object );
	}
	
	public Span( O object, double onset, double endTime, boolean startsBeforeFragment, boolean longerThanFragment )
	{
		this();
		this.onset = onset;
		this.endTime = endTime;
		this.longerThanFragment = longerThanFragment;
		this.startsBeforeFragment = startsBeforeFragment;
		setObject( object );
	}
	
	public Span( O object )
	{
		this();
		hasEnd = false;
		setObject( object );
	}
	
	public Span( O object, double onset, double endTime )
	{
		this( onset, endTime );
		setObject( object );
	}
	public Span( double onset, double endTime )
	{
		this( onset, endTime, false, false );
	}
	
	/**
	@return the number of beats from the start of the fragment this note is in to
	the end of the note, rounded up.
	*/
	public int getExtent()
	{
		return (int)Math.ceil( endTime );
	}

	/**
	Compares the object to another
	It is lower if the start time is earlier, and higher if it is later
	If they are the same, then the note which finished earlier is lower
	If start and duration are the same, then the lower pitch is lower
	Otherwise, they are the same
	@param o
	*/
	public int realCompare( Object o )
	{
		if( ! ( o instanceof Span ) ) return 1;
		int sup = super.realCompare( o );
		if( sup != 0 ) return sup;
	
		Span n = (Span)o;
		//Done by super
		//if( onset < n.getOnset() )	return -1;
		//if( onset > n.getOnset() ) return 1;
		if( ! fuzzyCompare( endTime, n.endTime )) return (int)(( endTime - n.endTime ) * 1000 );
		if( endTime > n.endTime + FUZZY_COMPARISON_THRESHOLD ) return 1;
		if( object == null && n.object != null ) return -1;
		if( object != null )
		{
			int objComp = object.compareTo( n.object );
			//System.out.println( objComp + ": " + object + ", " + n.object );
			if( objComp != 0 ) return objComp;
		}
		return 0;
	}

	public boolean equals( Object o )
	{
		if( ! ( o instanceof Span ) ) return false;
		Span n = (Span) o;
		if( !super.equals( o ) ) return false;
		if( !fuzzyCompare( endTime, n.endTime ) ) return false;
		if( longerThanFragment != n.longerThanFragment ) return false;
		if( startsBeforeFragment != n.startsBeforeFragment ) return false;
		if( object != null && ! object.equals( n.object ) ) return false;
		if( object == null && n.object != null ) return false;
		return true;
	}

	public double getEndTime()
	{
		return endTime;
	}

	public double getDuration()
	{
		return endTime - onset;
	}

	public boolean getStartsBefore()
	{
		return startsBeforeFragment;
	}

	@Override
	public void addOffset( double offset )
	{
		super.addOffset( offset );
		endTime += offset;
	}
	
	public void moveTo( double newOnset )
	{
		double duration = getDuration();
		setOnset( newOnset );
		setDuration( duration );
	}

	public boolean getLongerThan()
	{
		return longerThanFragment;
	}

	/**
	Sets the duration
	@param duration
	*/
	public void setDuration( double duration )
	{
		this.endTime = onset + duration;
		hasEnd = true;
	}

	public void setEndTime( double endTime )
	{
		this.endTime = endTime;
		hasEnd = true;
	}
	
	/**
	Sets whether the note started before the start of the fragment
	@param startsBeforeFragment
	*/
	public void setStartsBefore( boolean startsBeforeFragment )
	{
		this.startsBeforeFragment = startsBeforeFragment;
	}

	/**
	Sets whether the note continues past the end fragment
	@param longerThanFragment
	*/
	public void setLongerThan( boolean longerThanFragment )
	{
		this.longerThanFragment = longerThanFragment;
	}

	public void addAttribute( Attribute att )
	{
		if( att.getName().equals( "Start")) { setOnset( parseDoubleFor( att.stringValue(), "Start") ); }
		else super.addAttribute( att );

	}
	public void setAttributes( List<String> vals )
	{
		super.setAttributes( vals );
		if( vals.size() > 0 ) setOnset( parseDoubleFor( vals.get(0), "Duration"));
		if( vals.size() > 1 ) setEndTime( parseDoubleFor( vals.get(1), "End Time"));
		if( vals.size() > 2 && object != null ) object.setParameters( vals.subList( 2, vals.size() ));
	}

	/**
	Creates a copy of the note with the given duration
	@param duration
	*/
	public Span copySetDuration( double duration )
	{
		Span copy = clone();
		copy.setDuration( duration );
		return copy;
	}

	/**
	Creates a copy of the note and offsets it by the given amount
	@param offset
	*/
	public Span copyAddOffset( double offset )
	{
		return (Span) super.copyAddOffset( offset );
	}

	/**
	Creates an exact copy of the note 
	*/
	public Span clone()
	{
		return (Span)super.clone();
	}

	public String getContentString()
	{
		if( object != null ) return object.toString();
		return super.getContentString();
	}

	public String getRangeString()
	{
		String range = "(" + 
			(startsBeforeFragment ? "<" : " ") +
			formatBeat( onset ) + " - " + formatBeat( endTime ) +
			( longerThanFragment ? ">" : " ") + ")";
		return range;
	}
	
	public boolean mergeIfPossible( Span s )
	{
		if( mergeTest( s )) { mergeWith( s ); return true; }
		return false;
	}
	
	protected boolean mergeTest( Span s )
	{
		if( ! s.getStartsBefore() )
		{
			//log.log( FINE, "New span doesn't start before");
			return false;
		}
		if( ! longerThanFragment ) 
		{
			//log.log( FINE, "This span doesn't extend");
			return false;
		}
		if( Math.abs( s.getOnset() - getEndTime() ) >= MERGE_THRESHOLD )
		{
			//log.log( FINE, "Onset does not match end time" );
			return false;
		}
		if( object != null && ( ! object.equals( s.getObject() )) )
		{
			//System.out.println( "Objects different: " + object + ", " + s.getObject() );
			return false;
		}
		if( object == null && s.object != null ) return false;
		return true;
	}
	
	protected void mergeWith( Span s )
	{
		setDuration( s.getEndTime() - getOnset() );
		setLongerThan( s.getLongerThan() );
	}
	
	public double[] getRange()
	{
		if( !isEnded() ) return new double[] { onset, onset + NON_ENDED_RANGE_FUDGE_FACTOR };
		return new double[] { onset, endTime };
	}
	
	public boolean isEnded()
	{
		return hasEnd;
	}

}
