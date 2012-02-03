package com.moseph.mra.agent;

import java.util.*;

import com.moseph.mra.*;

public class StructuralPosition
{
	Piece piece;
	List<SectionPosition> currentPosition = new Vector<SectionPosition>();
	boolean finished = false;
	
	
	public StructuralPosition( Piece piece )
	{
		this.piece = piece;
	}
	
	public Section getFirstSection()
	{
		Section current = piece;
		while( ! current.isLeaf() ) current = current.getChildren().get(0);
		return current;
	}
	
	public void reset()
	{
		currentPosition.clear();
		getFirstFrom( piece );
		finished = false;
	}
	
	void getFirstFrom( Section current )
	{
		while( ! current.isLeaf() )
		{
			current = current.getChildren().get(0);
			currentPosition.add( new SectionPosition( current ));
		}
		activate();
	}
	
	public void setPath( Path p )
	{
		currentPosition.clear();
		Section prev = null;
		SectionPosition prevPos = null;
		for( Section s : p.getSections( piece ))
		{
			SectionPosition newPos = new SectionPosition( s );
			currentPosition.add( newPos );
			if( prev != null && prevPos != null )
				prevPos.position = prev.getSubsectionIndex( s );
			prev = s;
			prevPos = newPos;
		}
		activate();
	}
	
	/**
	 * Sets the position to the next "logical" section. This means that
	 * - if there is a specified number of repeats, this will be honoured
	 * - all other indications are ignored (i.e. RepeatUntil...)
	 *
	 */
	public void nextLogicalSection()
	{
		//Find first section with more
		for( int i = currentPosition.size() - 1; i >= 0; i-- )
		{
			SectionPosition c = currentPosition.get( i );
			if( c.onLast() ) 
			{
				if( c.moreRepeats() )
				{
					c.repeats++;
					c.position = 0;
					getFirstFrom( c.section );
					//System.out.println( "Repeating! " + c.section.getName() + ": " + this  );
					return;
				}
				else
				{
					//System.out.println( "Removing " + currentPosition.get(i));
					currentPosition.remove( i );
				}
			}
		}
		
		//Increment the position
		if( currentPosition.size() == 0 ) 
		{
			//System.out.println( "Finished Logical Structure!" );
			finished = true;
			return;
		}
		SectionPosition cur = currentPosition.get( currentPosition.size() - 1 );
		//System.out.println( "> " + this + ", getting from " + cur );
		cur.position++;
		Section cs = cur.section.getChildren().get( cur.position );
		currentPosition.add( new SectionPosition( cs ));
		getFirstFrom( cs );
		activate();
	}
	
	void activate()
	{
		for( SectionPosition s : currentPosition ) s.section.activate();
	}
	
	public List<SectionPosition> getCurrentPosition()
	{
		if( finished ) return new Vector<SectionPosition>();
		List<SectionPosition> ret = new Vector<SectionPosition>();
		for( SectionPosition s : currentPosition ) ret.add( s );
		return ret;
	}
	
	public Section getCurrentSection()
	{
		if( finished ) return null;
		return currentPosition.get( currentPosition.size() - 1 ).section;
	}
	
	public String toString()
	{
		String s = "[";
		for( SectionPosition p : currentPosition )
			s += " " + p.toString();
		return s + " ]";
	}
	
}
