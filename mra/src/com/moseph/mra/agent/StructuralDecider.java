package com.moseph.mra.agent;

import java.util.*;
import static com.moseph.mra.MRAUtilities.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.PathFeature;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

public class StructuralDecider
{
	Context context;
	Section currentSection;
	Fragment currentFragment;
	Piece piece;
	boolean forceUpdate = false;
	StructuralPosition position;
	List<Path> leaves;
	boolean chooseClosest = true;
	
	public StructuralDecider( Context c )
	{
		context = c;
		piece = c.getPiece();
		position = new StructuralPosition( piece );
		position.reset();
		applyPosition();
		//System.out.println( "First section: " + currentSection );
		leaves = piece.getLeafPaths();
	}
	
	public void reset()
	{
	}
	
	public void forceUpdate() { forceUpdate = true; }
	
	public void getApropriateSection()
	{
		if( chooseClosest )
			chooseClosestSection();
		else
			jumpToAverageSection();
	}
	
	void chooseClosestSection()
	{
		MusicianInformation close = context.getClosestActiveMusician();
		Path closestPath = context.getPath( close );
		//We can be sure it's a valid path as getClosestActiveMusician returns musicians w/ valid paths
		if( closestPath != null )
		{
			System.out.println( "Joining " + close.getName() + " on path " + closestPath );
			setPath( closestPath );
		}
		else jumpToAverageSection();
	}
	
	public void jumpToAverageSection()
	{
		double average = ((GroupNumericFeature) context.getFeature( "Path") ).getNonWeightedAverage();
		System.out.println( "Average path value: " + average );
		if( average > 0 && average < leaves.size() )
		{
			Path p = leaves.get( (int) average );
			setPath( p );
			System.out.println( "Moving to path: " + p );
		}
	}
	
	void applyPosition()
	{
		currentSection = position.getCurrentSection();
		getDefaultFragment();
	}
	
	public void setPath( Path p )
	{
		position.setPath( p );
		applyPosition();
	}

	public void update()
	{
		//System.out.println( "Decider updating...");
		if( currentSection != null )
		{
			boolean repeat = false;
			//All the logic goes here
			Map<String,Attribute> sectAttrs = currentSection.getAttributeMap();
			if( sectAttrs.containsKey( "RepeatUntil") )
			{
				String until = sectAttrs.get( "RepeatUntil").getValue().toString();
				if( until.equals( "followLocus"))
				{
					if( Math.random() > getLocusProbability()  ) repeat = true;
					//if( Math.random() > 0.1  ) repeat = true;
				}
		
			}
			if( forceUpdate )
			{
				System.out.println( "Forcing update!");
				forceUpdate = false;
				repeat = false;
			}
			
			if( !repeat )
			{
				position.nextLogicalSection();
				currentSection = position.getCurrentSection();
				if( currentSection != null )
					System.out.println( context.getMyID().getName() + " to next section: " + currentSection.getPath() );
			}
		}
		//else { System.out.println( "StrucDec: null"); }
		if( currentSection != null ) 
		{
			currentSection.activate();
			//System.out.println( "Structural decider update to: " + currentSection.getPath() );
		}
		//else { System.out.println( "StrucDec update: null"); }
		getDefaultFragment();
		context.setCurrentSection( currentSection );
	}
	
	double getLocusProbability() 
	{ 
		GroupFeature paths = context.getFeature( "Path");
		if( paths.getMusicians().size() < 2 ) 
		{
			//System.out.println( context.getMyID().getName() + ": ++ Not enough musicians!");
			return context.getBasicSectionSkipProbability();
		}
		double scoreWeighting = ((GroupNumericFeature) paths ).getAverageValue();
		return sigmoidalProbability( scoreWeighting, context.getBasicSectionSkipProbability(), context.getMaxSectionWeight() ) * currentSection.getLength();
	}
	
	void getDefaultFragment()
	{
		if( currentSection == null )
		{
			currentFragment = null;
			return;
		}
		Fragment next = null;
		if( getPartname() != null )
		{
			//System.out.println( "Looking for part: " + context.getPartname() );
			next = (Fragment)currentSection.getChannel( context.getPartname() );
		}
		if( next == null ) 
		{
			//System.out.println( "Looking for default part" );
			next = (Fragment)currentSection.getChannel( "Notes" );
			//if( next == null ) System.out.println( "Failed!\nSection:\n" + currentSection );
		}
		currentFragment = next;
	}
	
	String getPartname()
	{
		return context.getPartname();
	}
	
	public Fragment getCurrentFragment()
	{
		return currentFragment;
	}

	public void setCurrentFragment( Fragment currentFragment )
	{
		this.currentFragment = currentFragment;
	}

	public Section getCurrentSection()
	{
		return currentSection;
	}

	public void setCurrentSection( Section currentSection )
	{
		this.currentSection = currentSection;
	}
	
	public boolean forceSectionChange()
	{
		return forceUpdate;
	}
}
