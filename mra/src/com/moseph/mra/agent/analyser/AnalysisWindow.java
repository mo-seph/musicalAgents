package com.moseph.mra.agent.analyser;

import com.moseph.mra.Score;

public class AnalysisWindow
{
	double length;
	double position;
	double startPoint = 0.0;
	AnnotatedScore current = new AnnotatedScore();
	Score currentPlain = new Score();
	boolean isAnnotated = false;
	
	public AnalysisWindow( double length )
	{
		this.length = length;
		position = 0.0;
	}
	
	public void add( Score s )
	{
		if( s instanceof AnnotatedScore )
		{
			current.addAnnotatedScore( (AnnotatedScore)s, position );
			isAnnotated = true;
		}
		else
		{
			currentPlain.add( s, position );
			isAnnotated = false;
		}
		position += s.getLength();
		double oldPosition = position;
		if( position > length )
		{
			startPoint += position - length;
			//System.out.println( "Windowing!");
			double offset = position - length;
			if( isAnnotated )
			{
				current = current.copyAnnotatedChunk( offset, position );
				current.forceLength( length );
			}
			else
			{
				currentPlain = currentPlain.copyChunk( offset, position );
				currentPlain.forceLength( length );
			}
			position = length;
		}
		else
		{
			if( isAnnotated ) current.forceLength( position );
			else currentPlain.forceLength( position  );
		}
		//System.out.println( "Current length: " + current.getLength() + ", plain: " + currentPlain.getLength() );
		//System.out.println( "Position: " + position + ", old: " + oldPosition + ", start: " + startPoint );
	}
	
	public double getStartPoint()
	{
		return startPoint;
	}
	
	public double getEndPoint()
	{
		return startPoint + position;
	}
	
	public Score getCurrent()
	{
		if( isAnnotated)
			return current;
		else
			return currentPlain;
	}

}
