package com.moseph.mra;

//import com.moseph.music.*;
import static com.moseph.mra.MRAUtilities.*;
import static com.moseph.mra.MRAConstants.*;
import static java.util.logging.Level.*;
import static java.lang.Math.*;

import java.text.DecimalFormat;
import java.util.*;
import java.io.Serializable;
import java.util.logging.Level;

import javax.sound.midi.MidiEvent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.moseph.mra.agent.attribute.*;


/**
Represents a single note of music.
This represents a note, that is which note has been played,
which beat of the bar it falls within, where in that beat it falls and
how long it lasts.
@author David Murray-Rust
@version $Revision$, $Date$
*/
public class Note extends Span<Pitch> implements Comparable, Serializable, MAXMLComponent
{
	//int object;
	double velocity = 0.7;
	double quantisedOnset = Double.NaN;
	MidiEvent startEvent;
	List<NamedAttribute> annotations;
	double NOTE_VEL_THRESHOLD = 2.0 / 127.0; 
	
	public static void main( String[] args )
	{
		Note n = new Note( 2.0, 60, 1.0, 2.0 );
		System.out.println( "Note:" );
		System.out.println( n );
		System.out.println( "As XML:" );
		MRAUtilities.printMAXMLComponent( n );
		Document d = MRAUtilities.getMAXMLXMLDocument();
		Element e = n.getXMLElement( d );
		Note newNote = new Note( e );
		System.out.println( "Copy:" );
		System.out.println( newNote );
		System.out.println( "Offset:" );
		System.out.println( newNote.copyAddOffset( 0.25 ) );
		System.out.println( newNote.copyAddOffset( 0.5 ) );
	}


/*******************************************************************************
*                                                                              *
* Constructors                                                                 *
*                                                                              *
*******************************************************************************/


	/**
	main constructor when creating spans from MIDI data - the duration is unknown
	@param onset
	@param pitch
	@param velocity
	*/
	public Note( double startTime, int pitch, double velocity )
	{
		this( startTime, pitch, velocity, 0 );
		hasEnd = false;
	}

	/**
	Full constructor
	@param onset
	@param pitch
	@param velocity
	@param duration
	*/
	public Note( double startTime, int pitch, double velocity, double duration )
	{

		this( startTime, pitch, velocity, duration, false, false );
	}

	/**
	Full constructor
	@param onset
	@param pitch
	@param velocity
	@param duration
	@param startsBeforeFragment
	@param longerThanFragment
	*/
	public Note( double startTime, Pitch pitch, double velocity, double duration, boolean startsBeforeFragment, boolean longerThanFragment )
	{
		super( pitch, startTime, startTime + duration, startsBeforeFragment, longerThanFragment );
		this.velocity = velocity;
		//this.object = pitch;

	}

	public Note( double startTime, int pitch, double velocity, double duration, boolean startsBeforeFragment, boolean longerThanFragment )
	{
		this( startTime, new Pitch( pitch ), velocity, duration, startsBeforeFragment, longerThanFragment );
	}

	/**
	create a Note from it's XML representation
	@param noteElement
	*/
	public Note( Element noteElement )
	{
		this();
		//this( 1.0, 0, 60, 64, 0 );
		if( noteElement.getTagName() != "note" )
		{
			log.log( INFO, "Wrong element type for Note constructor: " + noteElement );
		}
		onset = getAttributeAsDouble( noteElement, "onset" );
		object = new Pitch( noteElement.getAttribute( "pitch" ) );
		velocity = getAttributeAsDouble( noteElement, "velocity" );
		endTime = getAttributeAsDouble( noteElement, "duration" ) + onset;
		longerThanFragment = noteElement.getAttribute( LONGER_THAN_FRAGMENT ).equals( "true" );
		startsBeforeFragment = noteElement.getAttribute( STARTS_BEFORE_FRAGMENT ).equals( "true" );
	}
	
	public Note()
	{
		super();
	}

/*******************************************************************************
*                                                                              *
* Utility/Comparison Methods                                                   *
*                                                                              *
*******************************************************************************/

	/**
	Compares the note to another (Span). If the other span has the same start and
	end times, then some note features are used
	If start and duration are the same, then the lower pitch is lower
	Otherwise, they are the same
	@param o
	*/
	public int compareTo( Object o )
	{
	int sup = super.compareTo( o );
		if( sup != 0 ) return sup;

		if( ! ( o instanceof Note ) )
		{
			return 1;
		}
		Note n = (Note)o;
		int pc = object.compareTo( n.object );
		if( pc != 0 ) return pc;
		return 0;
	}

	public boolean equals( Object o )
	{
		int sup = super.compareTo( o );
		if( sup != 0) return false;
		if( ! ( o instanceof Note ) ) return false;
		Note n = (Note) o;
		if( ! object.equals( n.getPitch() ) ) return false;
		if( longerThanFragment != n.longerThanFragment ) return false;
		if( startsBeforeFragment != n.startsBeforeFragment ) return false;
		if( !fuzzyCompare( velocity, n.getVelocity(), NOTE_VEL_THRESHOLD ) ) return false;
		return true;
	}

	public Pitch getPitch()
	{
		return (Pitch)object;
	}
	
	public int getPitchNumber()
	{
		return getPitch().getPitch();
	}

	public double getVelocity()
	{
		return velocity;
	}

	/**
	Sets the pitch
	@param pitch
	*/
	public void setPitch( int pitch )
	{
		this.object = new Pitch( pitch );
	}
	
	public void setPitch( String pitch )
	{
		this.object = new Pitch( pitch );
	}

	public void transpose( int amount )
	{
		getPitch().transpose( amount );
	}

	/**
	Sets the velocity
	@param velocity
	*/
	public void setVelocity( double velocity )
	{
		this.velocity = velocity;
	}

	public void addAttribute( Attribute att )
	{
		if( att.getName().equals( "Pitch")) {setPitch( att.stringValue() ); }
		else if( att.getName().equals( "Velocity")) {velocity = parseDoubleFor( att.stringValue(), "Velocity"); }
		else
		{
			super.addAttribute( att );
		}
	}
	
	public void addAnnotation( String annotation )
	{
		if( annotation.equals( "^"))
			addAnnotation( new Accent( "ACCENT"));
		else if( annotation.equals( "."))
			addAnnotation( new NoteLength( "STACCATO"));
		else if( annotation.equals( "'"))
			addAnnotation( new NoteLength( "STACCATISSIMO"));
		else if( annotation.equals( "_"))
		{
			addAnnotation( new NoteLength( "MARCATO"));
			addAnnotation( new Accent( "ACCENT"));
		}
		else
		{
			System.err.println( "Unknown accent: " + annotation  );
		}
	}
	
	public void addAnnotation( NamedAttribute n )
	{
		if( annotations == null ) annotations = new Vector<NamedAttribute>();
		annotations.add( n );
	}
	
	public List<NamedAttribute> getAnnotations()
	{
		if( annotations == null ) annotations = new Vector<NamedAttribute>();
		return annotations;
	}
	
	public void setAttributes( List<String> atts )
	{
		if( atts.size() > 0 ) setPitch( atts.get(0) );
		if( atts.size() > 1 ) setOnset( parseDoubleFor( atts.get(1), "Start Time"));
		if( atts.size() > 2 ) setDuration( parseDoubleFor( atts.get(2), "Duration"));
		//if( atts.size() > 3 ) setVelocity( parseDoubleFor( atts.get(3), "Velocity"));		
		if( atts.size() > 3 ) addAnnotation( atts.get(3));		
	}


/*******************************************************************************
*                                                                              *
* Copiers                                                                      *
*                                                                              *
*******************************************************************************/

	public Note copyAddOffset( double offset )
	{
		return (Note)super.copyAddOffset( offset );
	}
	
	public Note copySetStartTime( double startTime )
	{
		return (Note)super.copySetStartTime( startTime );
	}
	
	public Note copySetDuration( double duration )
	{
		return (Note)super.copySetDuration( duration);
	}
	
	/**
	Creates a copy of the note with the given pitch
	@param pitch
	*/
	public Note copySetPitch( int pitch )
	{
		Note n = clone();
		n.setPitch( pitch );
		return n;
	}

	/**
	Creates a copy of the note with the given velocity
	@param velocity
	*/
	public Note copySetVelocity( double velocity )
	{
		Note n = clone();
		n.setVelocity( velocity );
		return n;
	}

	/**
	Creates an exact copy of the note 
	*/
	public Note clone()
	{
		return (Note)super.clone();
	}



/*******************************************************************************
*                                                                              *
* Output methods                                                               *
*                                                                              *
*******************************************************************************/

	/**
	Gets a string representation of the fragment
	*/

	public String toFullString()
	{
		String start = startsBeforeFragment ? "<" : " ";
		String end = longerThanFragment ? ">" : " ";
		return start + friendlyName() + " on " + onset + " for " + endTime + " v: " + velocity + end;
	}

	@Override
	public String getContentString()
	{
		String annos = "";
		if( annotations != null )
			for( NamedAttribute n : getAnnotations() ) annos += " " + n.getValue();
		if( velocity <= 0.0 ) return friendlyName() + annos;
		return "[" + formatBeat( getDuration() ) + "]  " + friendlyName() + " (" + formatBeat( getVelocity() ) + ")" + annos;
	}


	/**
	gets an XML element representing this component (and any subcomponents)
	@param d the document to which the element will belong
	*/
	public Element getXMLElement( Document d )
	{
		Element myElement = d.createElement( "note" );
		myElement.setAttribute( "onset", onset+"" );
		myElement.setAttribute( "pitch", getPitch().getPitch()+"" );
		myElement.setAttribute( "velocity", velocity+"" );
		myElement.setAttribute( "duration", getDuration() +"" );
		myElement.setAttribute( "noteName", friendlyName() );
		if( longerThanFragment ) myElement.setAttribute( LONGER_THAN_FRAGMENT, "true" );
		if( startsBeforeFragment ) myElement.setAttribute( STARTS_BEFORE_FRAGMENT, "true" );
		return myElement;
	}

/*******************************************************************************
*                                                                              *
* Internal prettyprinting methods                                              *
*                                                                              *
*******************************************************************************/

	/**
	A rough idea of the velocity of the note
	*/
	public String getVelocityName()
	{
		return getVelocityName( velocity );
	}

	/**
	A rough idea of the velocity of the note
	*/
	public static String getVelocityName( double vel )
	{
		return vel + "";
		//int index = (int)( MRAConstants.loudnessNames.length * vel );
		//if( index >= MRAConstants.loudnessNames.length ) index = MRAConstants.volumeNames.length - 1;
		//return MRAConstants.loudnessNames[ index ];
	}

	/**
	Returns a string representing the informal name the note
	*/
	public String friendlyName()
	{
		return getNoteName() + getOctave();
	}

	/**
	Returns a string representing the name of the note, using accidentals from the keys closest to C
	*/
	public String getNoteName()
	{
		return getPitch().getNoteName();
	}

	/**
	Returns the number corresponding to the MIDI numbering of octaves on which this note falls
	*/
	public int getOctave()
	{
		return getPitch().getOctave();
	}

/*******************************************************************************
*                                                                              *
* MIDI Input Methods                                                           *
*                                                                              *
*******************************************************************************/

	/**
	Used by the MIDI input methods to set the starting MIDI event for a note
	@param e
	*/
	public void setEvent( MidiEvent e )
	{
		startEvent = e;
	}

	/**
	Returns the MIDI event (if any) which started this note
	*/
	public MidiEvent getStartEvent()
	{
		return startEvent;
	}


	/**
	is used to close a note when the corresponding noteOff event has
	been found.
	@param duration the time in subdivisions for which the note lasts.
	*/
	public void close( double duration )
	{
		this.endTime = onset + duration;
		this.longerThanFragment = false;
		this.hasEnd = true;
		startEvent = null;
	}


	@Override
	protected boolean mergeTest( Span s )
	{
		if( !( s instanceof Note )) return false;
		if( ! super.mergeTest(s)) return false;
		Note other = (Note) s;
		if( ! object.equals( other.object ) ) return false;
		return true;
	}


	public double getQuantisedOnset()
	{
		return quantisedOnset;
	}


	public void setQuantisedOnset( double quantisedOnset )
	{
		this.quantisedOnset = quantisedOnset;
	}
	
	public void calculateQuantisation( double quantiseLevel )
	{
		setQuantisedOnset( getQuantisedValue( getOnset(), quantiseLevel ) );
	}
	
	public static double getQuantisedValue( double value, double quantise )
	{
		double val = rint( value * quantise) / quantise;
		//System.out.println( "Quantising " + value + " into " + quantise + " gives " + val );
		return rint( value * quantise) / quantise;
	}
	
	public String toMRAString()
	{
		return toMRAString(0);
	}
	
	/**
	 * Should return a string like:
	 * ( Note: F2, 0.2, 0.2)
	 * @param level
	 * @return
	 */
	public String toMRAString( int level )
	{
		DecimalFormat df = new DecimalFormat( "0.00");
		String prefix = "";
		for( int i = 0; i < level; i++ ) prefix += "\t";
		String ret = prefix + "( Note: " + getPitch().getNoteName() + getPitch().getOctave() + ", ";
		ret += df.format( getOnset() ) + ", ";
		ret += df.format( getDuration() ) + ")\n";
		
		return ret;
	}
	
}
