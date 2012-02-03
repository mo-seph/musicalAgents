package com.moseph.mra;

import static com.moseph.mra.MRAConstants.SPLIT_THRESHOLD;
import static com.moseph.mra.MRAUtilities.warn;
import static java.util.logging.Level.WARNING;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

public class Channel<T extends TemporalEvent> extends Unit implements Serializable, Cloneable
{

	protected double length;
	protected EventSet<T> events;
	
	public Channel()
	{
		super( "" );
		define();
		events = new EventSet<T>();
	}
	
	public Channel( double length )
	{
		this();
		this.length = length;
		events.growBucketsFor( length );
	}
	
	public Channel( String name )
	{
		this();
		setName( name );
	}

	/**
	@param f the fragment to combine with this one
	*/
	public void addEventContainer( Channel<T> f, double offset )
	{
		addEvents( f.getEvents(), offset );
	}

	/**
	Appends fragment f to the current fragment.
	
	@param f the fragment to combine with this one
	*/
	public void append( Channel<T> f )
	{
		double newLength = length + f.length;
		addEventContainer( f, length );
		length = newLength;
	}
	
	/**
	 * Remove all the events from the channel, and set the length to 0.0
	 *
	 */
	public void clear()
	{
		clear( 0.0 );
	}
	
	/**
	 * Remove all the events from the channel, and set the length to newLength
	 * @param newLength
	 */
	public void clear( double newLength )
	{
		events.clear();
		length = newLength;
	}
	

	/*******************************************************************************
	*                                                                              *
	* Copying methods                                                              *
	*                                                                              *
	*******************************************************************************/
	public Channel<T> copyChunk( double start, double end )
	{
		//A bit inefficient for now
		Channel<T> f = getEmptyContainer( end - start );
		for( T n : events.getPotentiallyActive( start, end ) )
		{
			//System.out.println( n.getOnset() + " vs " + ( start + SPLIT_THRESHOLD ) );
			if( n.getOnset() <  start  ) continue;
			//System.out.println( "OK");
			//Notes are sorted, so the first one which is outside the range
			//is the end of the usable note list
			if( n.getOnset() >= end  ) break;
			T newSpan = (T)n.copyAddOffset( 0.0 - start );

			f.addEvent(newSpan );
		}
		return f;
	}

	Channel<T> getEmptyContainer( double length )
	{
		return new Channel<T>( length );
	}

	public Channel<T> clone()
	{
		Channel c;
		try
		{
			c = (Channel) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			System.err.println( e );
			e.printStackTrace();
			c = new Channel( "Cloning broken" );
		}
		c.events = new EventSet<T>();
		c.addEvents( getEvents() );
		return c;
	}

	/*******************************************************************************
	*                                                                              *
	* Channel compatibility methods                                                              *
	*                                                                              *
	*******************************************************************************/
	public void addUnit( T u )
	{
		if( u instanceof TemporalEvent) addEvent(u);
		else warn( "Tried to add a " + u.getClass() + " to a EventContainer" );
		u.use();
	}

	public List<T> getUnits()
	{
		return new Vector<T>(events);
	}

	public void merge( Channel<T> c )
	{
		addEventContainer( c, 0.0 );
	}

	/**
	The main addNote method
	@param n a Note
	*/
	public void addEvent( T n )
	{
		events.add( n );
		if( n.getOnset() > length )
		{
			length = (double)n.getOnset();
		}
	}
	
	public void addEvents( List<T> events )
	{
		addEvents( events, 0.0 );
	}

	public void addEvents( List<T> events, double offset )
	{
		for( T newEvent : events ) addEvent( (T)newEvent.copyAddOffset( offset ) );
	}

	public double getLength()
	{
		return length;
	}

	public List<T> getEvents()
	{
		return new Vector<T>( events );
	}

	/*******************************************************************************
	*                                                                              *
	* Comparison                                                                   *
	*                                                                              *
	*******************************************************************************/
	public boolean equals( Object o )
	{
		//System.out.println( "Comparing " + getClass() + " with " + o.getClass() );
		if( ! ( o instanceof Channel ) ) return false;
		//System.out.println( "Carrying on..." );
		Channel f = (Channel) o;
	
		if( events.size() != f.events.size() ) return false;
		//System.out.println( "Same number of spans" );
		if( ! name.equals( f.name ) ) return false;
		//System.out.println( "Same name" );
		Iterator<T> a = events.iterator();
		Iterator<T> b = (Iterator<T>)f.events.iterator();
		while( a.hasNext() && b.hasNext() )
		{
			//if( a.next() != b.next() ) return false;
			T n1 = a.next();
			T n2 = b.next();
			//System.out.println( "Comparing " + n1 + " and " + n2 );
			if( ! n1.equals( n2 ) )
			{
				//System.out.println( n1 + " != " + n2 );
				return false;
			}
			//System.out.println( "OK" );
		}
		if( a.hasNext() || b.hasNext() ) return false;
		return true;
	}
	
	/*******************************************************************************
	*                                                                              *
	* XMLisation and printing                                                      *
	*                                                                              *
	*******************************************************************************/
	public String toString()
	{
		String ret = "EventContainer: " + name;
		ret += "\n";
		Iterator i = events.iterator();
		while( i.hasNext() )
		{
			ret += "\t" + i.next() + "\n";
		}
		return ret;
	}
	
	public String toMRAString()
	{
		return toMRAString( 0 );
	}
	
	public String toMRAString( int indent )
	{
		return toString();
	}

	public void setLength( double length )
	{
		this.length = length;
	}
	
	public void remove( T toRemove )
	{
		events.remove( toRemove );
	}

	
}
