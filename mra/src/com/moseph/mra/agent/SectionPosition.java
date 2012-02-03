package com.moseph.mra.agent;

import com.moseph.mra.Section;

public class SectionPosition implements Cloneable, Comparable
{
	public Section section;
	public int position;
	public int repeats;
	
	public SectionPosition( Section section, int position, int repeats )
	{
		this.section = section;
		this.position = position;
		this.repeats = repeats;
	}
	public SectionPosition( Section section, int position ) { this( section, position, 0 ); }
	public SectionPosition( Section section ) { this( section, 0, 0 ); }
	
	/**
	 * Returns true if this section is on it's last subsection. i.e. if a and b
	 * are children of c, it will return true for c/b but not c/a
	 * @return
	 */
	public boolean onLast()
	{
		return ( position >= section.getNumChildren() - 1 );
	}
	
	public boolean moreRepeats()
	{
		int target = section.getNumRepeats();
		if( target < 0 ) return true;
		return( repeats < target ) ;
	}
	
	public boolean done()
	{
		if( moreRepeats() ) return false;
		return onLast();
	}
	
	
	public boolean equals( Object o )
	{
		return compareTo( o ) == 0;
	}
	
	public int compareTo( Object o )
	{
		if( ! ( o instanceof SectionPosition ) ) return toString().compareTo( o.toString() );
		SectionPosition s = (SectionPosition)o;
		int posComp = position - s.position;
		int repComp = repeats - s.repeats;
		if( posComp != 0 ) return posComp;
		if( repComp != 0 ) return repComp;
		int secComp = section.getName().compareTo( s.section.getName() );
		return secComp;
	}
	
	public String toString()
	{
		return section.getName() + "." + position + ":" + repeats;
	}
}