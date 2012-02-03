package com.moseph.mra.agent;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.analyser.*;

public class ScoreFollower
{
	Context context;

	Map<MusicianInformation, PartFollower> followers = new HashMap<MusicianInformation, PartFollower>();

	double quantisationLevel = 4.0;

	public ScoreFollower( Context c )
	{
		this.context = c;
		quantisationLevel = c.getQuantisation();
	}

	public AnnotatedScore annotateScores( Score s )
	{
		AnnotatedScore a = new AnnotatedScore();
		for (Fragment f : s.fragments())
		{
			System.out.println( "Played fragment by " + f.getMusician() );
			PartFollower follow = getPartFollower( context.getMusicianInformation( f ) );
			follow.newMaterial( f );
			AnnotatedFragment annotated = new AnnotatedFragment( f, follow.nextChunk( f.getLength() ), quantisationLevel );
			a.addAnnotation( annotated );
		}
		return a;
	}
	
	public void setPart( MusicianInformation musician, String partName )
	{
		getPartFollower( musician ).setPartname( partName );
	}

	PartFollower getPartFollower( MusicianInformation info )
	{
		System.out.println( "Getting a part follower for " + info.getName() );
		if (followers.containsKey( info ))
			return followers.get( info );
		PartFollower follow = new PartFollower( context, info, null );
		followers.put( info, follow );
		return follow;
	}

}
