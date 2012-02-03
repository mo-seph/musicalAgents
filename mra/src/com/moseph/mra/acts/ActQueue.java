package com.moseph.mra.acts;

import com.moseph.mra.Channel;
import com.moseph.mra.agent.MusicianInformation;

public class ActQueue extends Channel<MusicalAction>
{
	MusicianInformation musician;
	public ActQueue( MusicianInformation mus  )
	{
		super( mus.getName() );
		this.musician = mus;
	}
	
	public void addAction( MusicalAction action )
	{
		addEvent( action );
	}

}
