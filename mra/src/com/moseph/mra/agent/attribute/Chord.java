package com.moseph.mra.agent.attribute;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moseph.mra.*;

import static com.moseph.mra.MRAUtilities.*;


public class Chord extends MRAAttribute {

	Pitch root;
	ChordExtension extension;
	public final Pattern definitionPattern = Pattern.compile( "([A-G][#b]?)(\\w*)");
	
	public Chord( Pitch root, ChordExtension extension )
	{
		this.root = root;
		this.extension = extension;
	}
	
	public Chord( String def )
	{
		setChord( def );
	}
	
	public Chord()
	{
	}
	
	public void setParameters( List<String> values )
	{
		//log.warning( join( values ) );
		if( values.size() > 0 ) setChord( values.get(0));
		//log.warning( toString() );
	}
	
	void setChord( String chordString )
	{
		Matcher m = definitionPattern.matcher( chordString );
		if( m.matches() )
		{
			root = new Pitch( m.group(1));
			extension = ChordExtension.extensionFromString( m.group(2));
		}
		
	}
	@Override
	public MRAAttribute clone() {
		return new Chord( root, extension );
	}

	@Override
	public int compareTo(Feature other) {
		if( !( other instanceof Chord )) return toString().compareTo( other.toString() );
		Chord c = (Chord)other;
		int rootCmp = root.compareTo( c.root );
		if( rootCmp != 0 ) return rootCmp;
		int extCmp = extension.compareTo( c.extension );
		if( extCmp != 0 ) return extCmp;
		return 0;
	}

	public boolean equals( Object other )
	{
		if( other instanceof MRAAttribute )
		{
			int cmp = compareTo( (MRAAttribute)other );
			return cmp == 0;			
		}
		return false;
	}
	
	public String toString()
	{
		if( extension != null && root != null )
			return root.getNoteName() + "" + extension;
		return "Undefined chord";
	}
	
	public enum ChordExtension
	{
		MAJOR ( "", 0, 4, 7 ),
		MINOR ( "m", 0, 3, 7 );
		
		public final int[] extension;
		public final String prettyName;
		
		ChordExtension( String pn, int...ext )
		{
			this.extension = ext;
			this.prettyName = pn;
		}
		ChordExtension( int...ext )
		{
			this( null, ext );
		}
		
		public String toString()
		{
			if( prettyName != null ) return prettyName;
			return name();
		}
		
		public static ChordExtension extensionFromString( String ext )
		{
			if( ext.equals( "min") || ext.equals( "minor") || ext.equals( "m"))
				return MINOR;
			else
				return MAJOR;
		}
	}
	
	public double distance( Feature f )
	{
		if( compareTo( f ) == 0 ) return 0.0;
		return 1.0;
	}

	
}
