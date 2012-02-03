package com.moseph.mra.agent;

import java.util.*;

import com.moseph.mra.*;

public class CopycatAgent extends MusicalAgent
{
	Fragment ret = new Fragment( 2.0 );
	Fragment close;
	double offset = 4.0;
	Musician current = null;
	double noNotes =0.0;
	
	@Override
	Fragment getOutput( double start, double end )
	{
		Fragment f = super.getOutput( start, end );
		//System.out.println( "Copycat " + getName() + " returning from " + start + " to " + end + ":\n" + f );
		return f;
	}

	

	protected void initialise()
	{
		super.initialise();
		System.out.println( "Copycat setup!!!!");
		sendPathMessage( new Path( "/main") );
	}
	@Override
	void gotInput( Score newMusic, double start, double length )
	{
		super.gotInput( newMusic, start, length );
		
		boolean shouldSwitch = false;
		//Start with empty fragment
		ret = new Fragment( length );
		//If we're not playing anyone, then look for someone
		if( current == null )
		{
			List<Fragment> others = getOthersFragmentsWithMusic( newMusic );
			if( others.size() > 0 ) ret = others.get( (int)(Math.random() * others.size() ) );
			current = ret.getMusician();
			
			ret.openNotesAtStart();
		}
		else
		{
			//If we're carrying on, play the next fragment
			ret = newMusic.getFragmentForName( current.getName() );
			if( ret != null )
			{
			}
			if( ret != null && ret.getNumNotes() == 0 ) noNotes += length;
			else noNotes = 0.0;
			if( noNotes > 3.0 ) shouldSwitch = true;
			
			//If we're changing, play the closing fragment
			if( ret == null || shouldSwitch )
			{
				//System.out.println( "No output from " + current );
				//System.out.println( "Current: " + newMusic );
				ret = close;
				current = null;
			}
		}
		ret.scaleVelocity( 0.75 );
		ret.stripQuietNotes( 0.11 );
		close = ret.getClosingFragment( length );
		
		double addPoint = start + offset;
		//System.out.println( id.getName() + " adding at " + addPoint + ":\n" + ret );
		outputBuffer.addFragment( ret, addPoint );
	}
	
	List<Fragment> getOthersFragments( Score s )
	{
		Vector<Fragment> ret = new Vector<Fragment>();
		for( Fragment f : s.fragments() )
			if( f.getMusician().getName().equals( id.getName() ) )
				ret.add(  f );
		return ret;
	}
	
	List<Fragment> getOthersFragmentsWithMusic( Score s )
	{
		Vector<Fragment> ret = new Vector<Fragment>();
		for( Fragment f : s.fragments() )
			if( ! f.getMusician().getName().equals( id.getName() ) && f.getNotes().size() > 0 )
			{
				ret.add(  f );
			}
		return ret;
	}
	
	void applyArgument( AgentArgument a )
	{
		if( a.arg.equalsIgnoreCase( "Offset")) offset = Double.parseDouble( a.val );
		else super.applyArgument( a );
	}

}
