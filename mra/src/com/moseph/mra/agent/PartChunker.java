package com.moseph.mra.agent;

import com.moseph.mra.*;
import static java.lang.Math.*;

public class PartChunker
{

	protected Section currentSection = null;
	protected Fragment workingFragment = null;
	protected Context context;
	protected StructuralDecider decider;
	protected double position;
	
	public PartChunker( Context context )
	{
		this( context, new StructuralDecider( context ));
	}
	public PartChunker( Context context, StructuralDecider decider )
	{
		this.context = context;
		this.decider = decider;
		currentSection = decider.getCurrentSection();
		workingFragment = decider.getCurrentFragment();
		position = 0.0;
	}
	
	
	/**
	 * This will return a fragment of the apropriate length - guaranteed.
	 * If the underlying structure does not provide enough material, silence will
	 * be added up to the required length.
	 * 
	 * We start at $position within the current Section, and attempt to get
	 * $length beats. If there are less than $length beats available, we get
	 * as much as possible, load the next section, and try again, until we have $length
	 * @param length
	 * @return
	 */
	public Fragment getNextChunk( final double length )
	{
		//System.out.println( "Rendering from " + position + " to " + ( position + length ));
		final Fragment returned = new Fragment( 0.0 );
		
		double required = length - returned.getLength();
		while( required > 0 )
		{
			@SuppressWarnings("unused") double got = 0.0;
			if( currentSection != null ) 
			{
				currentSection.recalculateLength();
				got = currentSection.getLength();
			}
			if( currentSection != null && position >= currentSection.getLength() )
			{
				loadNextSection();
			}
			
			if( currentSection != null  )
			{
				double toGet = min( required, currentSection.getLength() - position );
				Fragment f = null;
				if( workingFragment != null ) 
					f = nextChunk( toGet );
				else
					f = new Fragment( toGet );
				returned.append( f );
				position += toGet;
				//System.out.println( "Asked to render: " + toGet + ", Got: " + length );
			}
			//If we've finished, then just return full length blank fragments
			else
			{
				returned.append( new Fragment( required ));
			}
			required = length - returned.getLength();
		}
		return returned;
	}
	
	Fragment nextChunk( double length )
	{
		double endpoint = position + length;
		//System.out.println( "Rendering from " + position + " to " + endpoint + " of \n" + workingFragment );
		if( endpoint > currentSection.getLength() ) endpoint = currentSection.getLength();
		return workingFragment.copyChunk( position, endpoint );
	}

	protected void loadNextSection()
	{
		decider.update();
		currentSection = decider.getCurrentSection();
		workingFragment = decider.getCurrentFragment();
		//System.out.println( "Working Fragment: " + workingFragment );
		if( workingFragment == null && currentSection != null )
			workingFragment = new Fragment( currentSection.getLength() );
		position = 0.0;
	}

	public Section getCurrentSection()
	{
		return currentSection;
	}

	public void setCurrentSection( Section currentSection )
	{
		this.currentSection = currentSection;
	}

	public double getPosition()
	{
		return position;
	}
	public Fragment getWorkingFragment()
	{
		return workingFragment;
	}

	public void setWorkingFragment( Fragment workingFragment )
	{
		this.workingFragment = workingFragment;
	}

}
