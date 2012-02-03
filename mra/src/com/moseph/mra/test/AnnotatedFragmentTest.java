package com.moseph.mra.test;

import java.util.*;
import static java.lang.Math.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.analyser.AnnotatedFragment;

import junit.framework.TestCase;

public class AnnotatedFragmentTest extends TestCase
{
	AnnotatedFragment annotation;
	Fragment played;
	Fragment scored;
	List<Note> playedNotes = new Vector<Note>();
	List<Note> scoredNotes = new Vector<Note>() ;
	double quantisation = 4;
	Note halfBeat;
	Note veryLateBeat;
	

	protected void setUp() throws Exception
	{
		played = new Fragment( 2.0 );
		scored = new Fragment( 2.0 );
		//Add a note to each on the first and second beats (approx)
		playedNotes.add( new Note( 0.05, 60, 0.6, 1.0 ));
		scoredNotes.add( new Note( 0, 60, 0.6, 1.0 ));
		playedNotes.add( new Note( 0.95, 61, 0.6, 1.0 ));
		scoredNotes.add( new Note( 1, 61, 0.6, 1.0 ));
		playedNotes.add( new Note( 0.5, 66, 0.6, 1.0 ));
		scoredNotes.add( new Note( 0.5, 66, 0.6, 1.0 ));
		
		for( Note n : playedNotes ) played.addNote( n );
		for( Note n : scoredNotes ) scored.addNote( n );
		
		//Add some random extra notes to the played version
		halfBeat = new Note( 0.5, 62, 0.6, 0.3 );
		veryLateBeat = new Note( 0.1, 64, 0.6, 0.3 );
		played.addNote( halfBeat );
		played.addNote( veryLateBeat );
		annotation = new AnnotatedFragment( played , scored, quantisation );
	}
	
	public void testQuantisation()
	{
		assertEquals( 0.0, playedNotes.get( 0 ).getQuantisedOnset() );
		assertEquals( 1.0, playedNotes.get( 1 ).getQuantisedOnset() );
		assertEquals( 0.5, halfBeat.getQuantisedOnset() );
		assertEquals( 0.0, veryLateBeat.getQuantisedOnset() );
	}
	
	public void testNotMappingWrongNotes()
	{
		Map<Note,Note>toOrig = annotation.getMapToScoredNotes();
		assertNull( toOrig.get( halfBeat ));
		assertNull( toOrig.get( veryLateBeat ));
		assertNotNull( toOrig.get( playedNotes.get(0)));
	}
	
	public void testProperMapping()
	{
		Map<Note,Note>toOrig = annotation.getMapToScoredNotes();
		for( int i = 0; i < scoredNotes.size(); i++ )
		{
			assertEquals( scoredNotes.get(i), toOrig.get( playedNotes.get(i)) );
		}
	}
	
	public void testLongRandomFragment()
	{
		AnnotatedFragment annotated = getLargeRandomFragment();
		List<Note> scored = annotated.getScored().getNotes();
		List<Note> played = annotated.getPlayed().getNotes();
		Map<Note,Note>toOrig = annotated.getMapToScoredNotes();
		for( int i = 0; i < scored.size(); i++ )
		{
			assertEquals( scored.get(i), toOrig.get( played.get(i)) );
		}
	}
	
	public void testAddingFragment()
	{
		AnnotatedFragment first = getLargeRandomFragment();
		AnnotatedFragment second = getLargeRandomFragment();
		
		Fragment firstPlayed = first.getPlayed();
		Fragment secondPlayed = second.getPlayed();
		Fragment firstScored = first.getScored();
		Fragment secondScored = second.getScored();
		
		first.add( second, 10.0 );
		
		Fragment played = firstPlayed.clone();
		Fragment scored = firstScored.clone();
		played.addFragment( secondPlayed, 10.0 );
		scored.addFragment( secondScored, 10.0 );
		
		assertEquals( played, first.getPlayed() );
		assertEquals( scored, first.getScored() );
	}
	
	public AnnotatedFragment getLargeRandomFragment()
	{
		Fragment scored = new Fragment( 10.0 );
		Fragment played = new Fragment( 10.0 );
		int oldPitch = 5;
		for( double beat = 0.5; beat < 9.0; beat += 0.5 )
		{
			int offset =(int)random() * 20;
			int pitch = oldPitch + offset + 50; 
			oldPitch = offset;
			double playedTime = beat + (random() - 0.5) * 0.2;
			scored.addNote( beat, pitch, 0.5, 0.5 );
			played.addNote( playedTime, pitch, 0.5, 0.5 );
		}
		return new AnnotatedFragment( played, scored, quantisation );
		
	}
	

}
