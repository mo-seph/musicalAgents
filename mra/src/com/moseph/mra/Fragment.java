package com.moseph.mra;
//import com.moseph.music.*;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.INFO;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
A fragment is a collection of spans, with an optional offset relative to a bar
A fragment is built around a {@link SortedSet} of {@link Note}s.
It contains basic methods to aid handling of fragments, such as
combining and setting offsets. Fragments know how long they are,
rounded to the smallest number of beats which will encompass the
entire fragment.
@author David Murray-Rust
@version $Revision$, $Date$
*/
public class Fragment extends SpanContainer<Note> implements Serializable, MAXMLComponent
{
	protected Instrument instrument;
	protected Musician musician;
	protected boolean realtime = false;


	
	
/*******************************************************************************
*                                                                              *
* Constructors                                                                 *
*                                                                              *
*******************************************************************************/

	public Fragment( Note...n )
	{
		this( "Notes" );
		if( n == null ) return;
		for( Note p : n )
		{
			addNote( p );
		}
	}

	public Fragment()
	{
		super( "Notes");
		//events = new EventSet<Note>();
	}

	public Fragment( String name )
	{
		this();
		this.name = name;
	}
	public Fragment( Element fragmentElement )
	{
		this();
		if( fragmentElement.getTagName() != "fragment" )
		{
			log.log( INFO, "Wrong element type for Fragment constructor: " + fragmentElement );
		}
		length = getAttributeAsDouble( fragmentElement, "length" );
		name = fragmentElement.getAttribute( "name" );

		//Set instrument and musician if present
		String musicianName = fragmentElement.getAttribute( "musician" );
		if( musicianName.length() > 0 ) setMusician( new Musician( musicianName ) );
		String instrumentName = fragmentElement.getAttribute( "instrument" );
		if( instrumentName.length() > 0 ) setInstrument( new Instrument( instrumentName ) );
		addNotes( fragmentElement );
	}

	public Fragment( double length )
	{
		this();
		this.length = length;
	}

/*******************************************************************************
*                                                                              *
* Adding and removing spans                                                    *
*                                                                              *
*******************************************************************************/

	/**
	The main addNote method
	@param n a Note
	*/
	public void addNote( Note n )
	{
		super.addEvent( n );
	}

	/**
	Create a new note and add it
	@param onset
	@param pitch
	@param velocity
	@param duration
	*/
	public void addNote( double startTime, int pitch, double velocity, double duration )
	{
		addNote( new Note( startTime, pitch, velocity, duration ) );
	}

	
	/**
	Add a note from an XML element
	@param note an XML Element representing the note
	*/
	void addNote( Element note )
	{
		addNote( new Note( note ) );
	}

	public void addNotes( Note... n )
	{
		if( n == null ) return;
		for( Note note : n )
		{
			addNote( note.clone() );
		}
	}

	public void addNotes( Collection<Note> c )
	{
		addNotes( c, 0.0 );
	}

	public void addNotes( Collection<Note> c, double offset )
	{
		for( Note n: c )
		{
			addNote( n.copyAddOffset( offset ) );
		}
	}

	public void addNotes( Element parent )
	{
		NodeList notes = parent.getElementsByTagName( "note" );
		for( int i = 0; i < notes.getLength(); i++ )
		{
			addNote( (Element)notes.item( i ) );
		}
	}

/*******************************************************************************
*                                                                              *
* Adding, appending fragments                                                  *
*                                                                              *
*******************************************************************************/

	/**
	@param f the fragment to combine with this one
	*/
	public void addFragment( Fragment f, double offset )
	{
		addNotes( f.getSpans(), offset );
	}

	/*******************************************************************************
*                                                                              *
* Transformations                                                              *
*                                                                              *
*******************************************************************************/

	public void transpose( int amount )
	{
		for( Note i : events )
		{
			i.transpose( amount );
		}
	}
	
	public void humanize( double amount )
	{
		for( Note i : events )
		{
			i.setOnset( i.getOnset() + ( Math.random() - 0.5) * amount );
			i.setVelocity( i.getVelocity() + ( Math.random() - 0.5) * amount );
		}
		
	}
	
	public void scaleVelocity( double factor )
	{
		for( Note i: events ) i.setVelocity( i.getVelocity() * factor );
	}

/*******************************************************************************
*                                                                              *
* Copying methods                                                              *
*                                                                              *
*******************************************************************************/

	public Fragment copyChunk( double start, double end ) { return copyChunk( start, end, false ); }
	public Fragment copyChunk( double start, double end, boolean allowStartsBefore )
	{
		//A bit inefficient for now
		Fragment f = (Fragment)super.copyChunk(start, end, allowStartsBefore );
		f.setMusician( musician );
		f.setInstrument( instrument );
		f.setRealtime( realtime );
		return f;
	}

	Fragment getEmptyContainer( double length )
	{
		return new Fragment( length );
	}
	
	public Fragment clone()
	{
		Fragment f = (Fragment)super.clone();
		return f;
	}

	public Musician getMusician()
	{
		return musician;
	}

	public void setMusician( Musician musician )
	{
		this.musician = musician;
	}

	public Instrument getInstrument()
	{
		return instrument;
	}

	public void setInstrument( Instrument i )
	{
		instrument = i;
	}
	
	public List<Note> getNotes()
	{
		return new Vector<Note>(events);
	}
	
	public int getNumNotes()
	{
		return events.size();
	}
	
	public List<Note> getHangingNotes()
	{
		List<Note> hanging = new Vector<Note>();
		for( Note n : events )
		{
			if( n.getLongerThan() ) hanging.add( n );
		}
		return hanging;
	}

	public PartIndex getPartIndex()
	{
		return new PartIndex(  musician, instrument );
	}

	public static Fragment getExampleFragment()
	{
		Fragment f = new Fragment( 4 );
		f.addNote( 0.0, 60, 0.7, 0.5 );
		f.addNote( 0.5, 60, 0.7, 0.5 );
		f.addNote( 1.0, 67, 0.7, 0.5 );
		f.addNote( 1.5, 67, 0.7, 0.5 );
		f.addNote( 2.0, 69, 0.7, 0.5 );
		f.addNote( 2.5, 69, 0.7, 0.5 );
		f.addNote( 3.0, 60, 0.7, 1.0 );
		return f;
	}

	public static Fragment getExampleB()
	{
		Fragment f = new Fragment( 4 );
		f.addNote( 0.0, 60, 0.7, 1.5 );
		f.addNote( 0.5, 62, 0.7, 2.0 );
		f.addNote( 1.5, 64, 0.7, 1.5 );
		return f;
	}

/*******************************************************************************
*                                                                              *
* Comparison                                                                   *
*                                                                              *
*******************************************************************************/

	public boolean equals( Object o )
	{
		//System.out.println( "Starting comparison");
		if( ! ( o instanceof Fragment ) ) return false;

		//System.out.println( "Got a fragment");
		Fragment f = (Fragment) o;
		if( instrument == null )
		{
			if( f.instrument != null ) return false;
		}
		else if( ! instrument.equals( f.instrument ) ) return false;
		//System.out.println( "Instrument OK");
		
		if( musician == null )
		{
			if( f.musician != null ) return false;
		}
		else if( ! musician.equals( f.musician ) ) return false;
		//System.out.println( "Musician OK");
		return super.equals( o );
	}

/*******************************************************************************
*                                                                              *
* XMLisation and printing                                                      *
*                                                                              *
*******************************************************************************/

	public String toString()
	{
		String ret = "Fragment: " + name + "(" + length + ")";
		if( musician != null ) ret += " by " + musician;
		if( instrument != null ) ret += " on " + instrument;
		ret += realtime ? " rt" : " **";
		ret += "\n";
		Iterator i = events.iterator();
		while( i.hasNext() )
		{
			ret += "\t" + i.next() + "\n";
		}
		return ret;
	}
	
	public String toMRAString( int level )
	{
		String prefix = "";
		for( int i = 0; i < level; i++ ) prefix += "\t";
		String ret = prefix + "(Channel Notes" + getName() + "\n";
		for( Note n : getNotes() ) ret += n.toMRAString( level + 1 );
		ret += prefix + ")\n";
		return ret;
	}

	/**
	gets an XML element representing this component (and any subcomponents)
	@param d the document to which the element will belong
	*/
	public Element getXMLElement( Document d )
	{
		Element myElement = d.createElement( "fragment" );
		myElement.setAttribute( "length", length+"" );
		myElement.setAttribute( "name", name );
		if( instrument != null ) myElement.setAttribute( "instrument", instrument.toString() );
		if( musician != null ) myElement.setAttribute( "musician", musician.toString() );
		addNotesToXML( myElement, d );
		return myElement;
	}

	/**
	Adds the spans in this fragment to an XML document
	@param e the XML element representing this Fragment
	@param d the XML document we are adding to
	*/
	void addNotesToXML( Element e, Document d )
	{
		for( Note n : events )
		{
			e.appendChild( n.getXMLElement( d ) );
		}
	}
	
	public void stripMidi()
	{
		for( Note n : getNotes() )
		{
			n.startEvent = null;
		}
	}

	public boolean isRealtime()
	{
		return realtime;
	}

	public void setRealtime( boolean realtime )
	{
		this.realtime = realtime;
	}
	
	public Fragment getClosingFragment( double length )
	{
		Fragment close = new Fragment( length );
		for( Note n : getNotes() )
			if( n.longerThanFragment )
				close.addNote( new Note( 0.0, n.getPitch(), 0.0, 0.0, true, false ) );
		return close;
	}
	
	public void closeOpenNotesAtEnd()
	{
		for( Note n: getNotes() )
			if( n.longerThanFragment )
			{
				n.setLongerThan( false );
				n.setEndTime( length );
			}
	}
	
	public void openNotesAtStart()
	{
		for( Note n : getNotes() )
			if( n.startsBeforeFragment )
			{
				n.setStartsBefore( false );
				n.setOnset( 0.0 );
			}
	}
	
	public void stripQuietNotes( double threshold )
	{
		for( Note n: getNotes() )
			if( n.velocity < threshold )
				remove( n );
	}
	
	public void recalculateLength()
	{
		length = 0.0;
		for( Note n: getNotes() )
			if( n.getEndTime() > length )
				length = n.getEndTime();
	}
}
