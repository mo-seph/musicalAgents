package com.moseph.mra;
//import com.moseph.music.*;
import static java.util.logging.Level.INFO;

import java.io.Serializable;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.moseph.mra.agent.MusicianInformation;

/**
A Score is a collection of {@link Fragment}s, indexed by agent/instrument
@author David Murray-Rust
@version $Revision$, $Date$
*/
public class Score extends Unit implements Serializable, MAXMLComponent
{
	Map<PartIndex, Fragment> fragments;
	Map<Musician, Fragment> fragByMusician;
	Map<String, Fragment> fragByName;
	double length;

	public static void main( String[] args )
	{
		Score s = getExampleScore();
		System.out.println( s );
	}

	public Score()
	{
		fragments = new HashMap<PartIndex, Fragment>();
		fragByMusician = new HashMap<Musician, Fragment>();
		fragByName = new HashMap<String, Fragment>();
		//System.out.println( "Score init!");
		length = 0.0;
	}

	public Score( Element element )
	{
		this();
		if( element.getTagName() != "score" )
		{
			log.log( INFO, "Wrong element type for Score constructor: " + element );
		}

		//Set instrument and musician if present
		NodeList frags = element.getElementsByTagName( "fragment" );
		for( int i = 0; i < frags.getLength(); i++ )
		{
			add( (Element)frags.item( i ), 0.0 );
		}
	}

	public Score( Fragment f )
	{
		this();
		add( f, 0.0 );
	}

/*******************************************************************************
*                                                                              *
* Addition methods                                                             *
*                                                                              *
*******************************************************************************/

	public void add( Fragment input, Musician m, Instrument i, double pos )
	{
		Fragment f = fragments.get( new PartIndex( m, i ) );
		if( f == null )
		{
			f = new Fragment();
			f.setMusician( m );
			f.setInstrument( i );
			f.setRealtime( input.isRealtime() );
			fragments.put( new PartIndex( m, i ), f );
		}
		f.addFragment( input, pos );
		fragByMusician.put( m, f );
		if( m != null)
			fragByName.put( m.getName(), f );
		if( f.getLength() > length ) length = f.getLength();
	}

	public void add( Fragment f )
	{
		add( f, 0.0 );
	}

	public void add( Fragment f, double pos )
	{
		add( f, f.getMusician(), f.getInstrument(), pos );
	}

	public void add( Element e, double pos )
	{
		add( new Fragment( e ), pos );
	}

	public void add( Score s, double pos )
	{
		for( Fragment f : s.fragments.values() )
		{
			add( f, pos );
		}
	}

	public void append( Score s )
	{
		add( s, length );
	}

/*******************************************************************************
*                                                                              *
* Copying Methods                                                              *
*                                                                              *
*******************************************************************************/

	public Score copyChunk( double start, double end )
	{
		Score s = new Score();
		for( Fragment f : fragments.values() ) s.add( f.copyChunk( start, end ) );
		return s;
	}

/*******************************************************************************
*                                                                              *
* Utility Methods                                                              *
*                                                                              *
*******************************************************************************/

	public double getLength()
	{
		for( Fragment f : fragments.values() ) length = Math.max( length, f.getLength() );
		return length;
	}
	public Collection<Fragment> fragments()
	{
		return fragments.values();
	}
	
	public Fragment getFragmentForMusician( Musician m )
	{
		return fragByMusician.get( m );
	}
	public Fragment getFragmentForName( String name )
	{
		return fragByName.get( name);
	}

	public String toString()
	{
		String op = "Score:\n";
		for( Fragment f : fragments.values() )
		{
			op += f;
		}
		return op;
	}

	public static Score getExampleScore()
	{
		Score s = new Score();
		Fragment f = Fragment.getExampleFragment();
		f.setMusician( new Musician( "Bob" ) );
		f.setInstrument( new Instrument( "AcousticPiano" ) );
		s.add( f );
		Fragment f2 = Fragment.getExampleFragment();
		f2.setMusician( new Musician( "Bob" ) );
		f2.setInstrument( new Instrument( "PizzicatoStrings" ) );
		Fragment f3 = Fragment.getExampleFragment();
		f3.setMusician( new Musician( "Jim" ) );
		f3.setInstrument( new Instrument( "Trombone" ) );
		Fragment f4 = Fragment.getExampleFragment();
		f4.setMusician( new Musician( "Jim" ) );
		f4.setInstrument( new Instrument( "Clarinet" ) );
		s.add( f, 2.0 );
		s.add( f2 );
		s.add( f3, 1.0 );
		s.add( f4 );
		return s;
	}

	/**
	gets an XML element representing this component (and any subcomponents)
	@param d the document to which the element will belong
	*/
	public Element getXMLElement( Document d )
	{
		Element myElement = d.createElement( "score" );
		myElement.setAttribute( "length", length+"" );
		for( Fragment f : fragments.values() )
		{
			myElement.appendChild( f.getXMLElement( d ) );
		}
		return myElement;
	}
	
	public List<Fragment> getFragments()
	{
		return new Vector<Fragment>( fragments.values() );
	}
	
	public void forceLength( double newLength )
	{
		length = newLength;
		for( Fragment f : fragments.values() ) f.setLength( newLength );
	}
	
	public void removeFragmentForName( String name )
	{
		Fragment f = fragByName.get( name );
		for( Musician m : fragByMusician.keySet() )
		{
			if( m == null ) continue;
			if( m.getName().equals( name ))
			{
				fragByMusician.remove( m );
				break;
			}
		}
		for( PartIndex p : fragments.keySet() )
		{
			if( p.getMusician() == null ) continue;
			if( p.getMusician().getName().equals( name ))
			{
				fragments.remove( p );
				break;
			}
		}
	}

}
