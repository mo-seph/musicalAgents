package com.moseph.mra.agent.analyser;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.Context;

/**
 * Maps annotated fragments onto a Score
 * Requires that the fragments have a name set!
 * @author dave
 *
 */
public class AnnotatedScore extends Score
{
	Map<Fragment,AnnotatedFragment> annotations = new HashMap<Fragment, AnnotatedFragment>();
	Map<String,AnnotatedFragment> annotationByName = new HashMap<String, AnnotatedFragment>();
	
	public AnnotatedScore()
	{
		super();
	}
	
	public void addAnnotation( AnnotatedFragment f )
	{
		addAnnotation( f, 0.0 );
	}
	
	public void addAnnotation( AnnotatedFragment f, double offset )
	{
		add( f.getPlayed(), offset );
		Musician player = f.getPlayed().getMusician();
		if( player == null )
		{
			System.err.println( "Unnamed Annotated Fragment given to AnnotatedScore");
			new Exception().printStackTrace();
			return;
		}
		String playerName = player.getName();
		AnnotatedFragment current = annotationByName.get( playerName );
		if( current == null )
		{
			System.out.println( "Making new annotation for " + playerName );
			Fragment newPlayed = new Fragment();
			Fragment newScored = new Fragment();
			newPlayed.setMusician( player );
			newScored.setMusician( player );
			current = new AnnotatedFragment( newPlayed, newScored, f.getQuantisation() );
			annotationByName.put( playerName, current );
		}
		annotations.remove( current.played );
		current.add( f, offset );
		annotations.put(  current.getPlayed(), current );
	}
	
	public AnnotatedScore copyAnnotatedChunk( double start, double end )
	{
		AnnotatedScore chunk = new AnnotatedScore();
		for( AnnotatedFragment f : annotations.values() )
			chunk.addAnnotation( f.copyChunk( start, end ) );
		return chunk;
	}
	
	public void addAnnotatedScore( AnnotatedScore score, double offset )
	{
		for( AnnotatedFragment af : score.getAnnotatedFragments() )
		{
			addAnnotation( af, offset );
		}
	}
	
	public List<AnnotatedFragment> getAnnotatedFragments()
	{
		return new Vector<AnnotatedFragment>( annotations.values() );
	}
	
	public AnnotatedFragment getAnnotation( Fragment f )
	{
		return annotations.get( f );
	}
	
	public AnnotatedFragment getAnnotation( String name )
	{
		return annotationByName.get( name );
	}
	public AnnotatedFragment getAnnotation( Musician m )
	{
		return getAnnotation( m.getName() );
	}
	
	public String toString()
	{
		String s = "Annotation: {";
		for( AnnotatedFragment f : annotations.values() )
		{
			s += f;
		}
		s += "\n}\n";
		return s;
	}

	@Override
	public void forceLength( double newLength )
	{
		super.forceLength( newLength );
		for( AnnotatedFragment af : annotations.values() )
		{
			af.scored.setLength( newLength );
			af.played.setLength( newLength );
		}
	}
	

}
