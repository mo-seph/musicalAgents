package com.moseph.mra.agent.analyser;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.Context;
import static java.lang.Math.*;
import static com.moseph.mra.MRAUtilities.*;

public class AnnotatedFragment
{
	Fragment played;
	Fragment scored;
	double quantisation;
	Map<Note,Note> originalNotes = new HashMap<Note,Note>();
	double TIMING_THRESHOLD = 0.05;
	
	public AnnotatedFragment( Fragment played, Fragment scored, double quantisation )
	{
		this.played = played;
		this.scored = scored;
		scored.setMusician( played.getMusician() );
		this.quantisation = quantisation;
		buildLinks();
	}
	
	void buildLinks()
	{
		calculateQuantisations();
		matchNotes();
	}
	
	void calculateQuantisations()
	{
		for( Note n : played.getNotes() )
			n.calculateQuantisation( quantisation );
	}
	
	void matchNotes()
	{
		originalNotes.clear();
		for( Note n : played.getNotes() )
			originalNotes.put( n, getMatchingNote( n ));
	}
	
	Note getMatchingNote( Note target )
	{
		for( Note n : scored.getNotes() )
		{
			if( abs( target.getQuantisedOnset() - n.getOnset()) < TIMING_THRESHOLD )
				if( target.getPitchNumber() == n.getPitchNumber() )
					return n;
		}
		return null;
	}
	
	public Map<Note, Note> getMapToScoredNotes()
	{
		return originalNotes;
	}

	public Fragment getPlayed()
	{
		return played;
	}

	public Fragment getScored()
	{
		return scored;
	}

	public String toString()
	{
		return played + " \n maps to \n " + scored + "\n";
	}
	
	public void add( AnnotatedFragment other, double offset )
	{
		played.addFragment( other.played, offset );
		scored.addFragment( other.scored, offset );
		buildLinks();
	}
	
	public AnnotatedFragment copyChunk( double start, double end )
	{
		Fragment cPlayed = played.copyChunk( start, end );
		Fragment cScored = scored.copyChunk( start, end );
		return new AnnotatedFragment( cPlayed, cScored, quantisation );
	}

	public double getQuantisation()
	{
		return quantisation;
	}

}
