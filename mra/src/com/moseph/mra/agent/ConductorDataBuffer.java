package com.moseph.mra.agent;

import jade.core.AID;

import java.util.*;

import com.moseph.mra.*;

public class ConductorDataBuffer
{
	Set<AID> asked = new HashSet<AID>();
	Score currentScore = new Score();
	
	void addRecipient( AID aid )
	{
		asked.add( aid );
	}
	
	void removeRecipient( AID aid )
	{
		asked.remove( aid );
	}
	
	void addFragment( Fragment f )
	{
		currentScore.add( f, 0.0 );
	}
	
	void addFragment( Fragment f, AID sender )
	{
		addFragment( f );
		removeRecipient( sender );
	}
	
	void reset()
	{
		asked.clear();
		currentScore = new Score();
	}
	
	Score getCurrentScore()
	{
		return currentScore;
	}
	
	int numMissing()
	{
		return asked.size();
	}
	
	double getLength()
	{
		return currentScore.getLength();
	}

	boolean checkReception( double start, double end )
	{
		if( numMissing() != 0 )
		{
			System.out.printf( "music from %f to %f missing %f, got %f beats\n",
					start, end, (double)numMissing(), getLength() );
			return false;
		}
		return true;
		
	}
}
