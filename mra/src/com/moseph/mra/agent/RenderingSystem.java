package com.moseph.mra.agent;

import java.util.*;


import static java.lang.Math.*;
import static com.moseph.mra.MRAUtilities.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.agent.reasoning.*;

/**
 * Renders a plan into notes. For dynamics and displacement, the relevant curve is applied to each note
 * and then the value from the pattern is added in. For length, the value for the curve and the pattern are added
 * and the length of the note is scaled by this amount.
 * @author dave
 *
 */
public class RenderingSystem 
{
	Hashtable<String,Double> accentValues = new Hashtable<String,Double>();
	Hashtable<String,Double> noteLengths = new Hashtable<String,Double>();
	Hashtable<String,Double> notePlacements = new Hashtable<String,Double>();
	Fragment previous = new Fragment();
	boolean skipExpressivity = false;
	
	/*
	 * Note when testing - there's a confusing mess of options. Many tests fail because
	 * some aspect of rending has been turned off...
	 */
	boolean applyNoteDynamics = true;
	boolean applyNoteLengths = true;
	boolean applyNoteTimings = true;
	
	boolean applyTempoCurve = true;
	boolean applyDynamicCurve = true;
	boolean applyLengthCurve = true;
	boolean applyTimingPattern = true;
	boolean applyDynamicPattern = true;
	boolean applyLengthPattern = true;
	
	
	boolean applyDynamicAnnotations = false;
	boolean applyLengthAnnonations = false;
	boolean applyTimingAnnonations = false;
	
	boolean humanize = false;
	//double humanOnsetLevel = 0.0001;
	double humanOnsetLevel = 0.0000;
	double humanVelocityLevel = 0.01;
	double GRACENOTE_LENGTH = 0.05;
	double GRACENOTE_OVERLAP = 0.15;
	double patternOffset = 0.0;
	double lastPatternPoint = 0.0;
	RenderPlan planner = null;
	Context context;
	
	boolean boundTiming = false;
	double maxPositiveOnset = 0.1;
	double maxNegativeOnset = -0.1;
	
	boolean boundLength = true;
	double MAX_NOTE_LENGTH = 110.25;
	double MIN_NOTE_LENGTH = 0.01;
	
	public RenderingSystem()
	{
		setupAnnotationMappings();
		//System.out.println( "Rendering system starting with: " + currentSection.getPath() );
	}
	
	public RenderingSystem( Context c )
	{
		this();
		context = c;
	}
	

	public Fragment render( RenderPlan plan )
	{
		return render( plan, 0.0 );
	}
	public Fragment render( RenderPlan plan, double startPoint )
	{
		if( context != null )
			lastPatternPoint = context.getPatternPointBefore( startPoint );
		patternOffset = startPoint - lastPatternPoint;
		//Fragment f = super.nextChunk( length );
		Fragment f = plan.getNotes();
		//System.out.println( "Starting from:\n" + f + "\nLength: " + f.getLength() );
		if( skipExpressivity ) return f;
		
		
		//Not creating a new fragment any more, using the previous one, to conserver
		//nottes which grow beyond chunk boundaries
		//Fragment ret = new Fragment( plan.getLength() );
		//mapping from original notes to new ones
		List<NoteMapping> notes = new Vector<NoteMapping>();
		
		//Create a mapping from notes to new notes
		for( Note n : f.getNotes() )
			notes.add( new NoteMapping( n ) );
		
		//Apply the expressive functions to the part
		if( applyNoteDynamics ) doDynamics( notes, plan );
		if( applyNoteTimings ) doTiming( notes, plan );
		if( applyNoteLengths ) doLength( notes, plan );
		if( humanize ) humanize( notes );
		
		//Copy the notes to the return Fragment
		for( NoteMapping map : notes )
			previous.addNote( map.result );
		
		/*
		 * In case a note grows past the chunk boundary, we chop off the bit after the boundary
		 * and store it till next time, to use as the basis of the next chunk.
		 */
		Fragment ret = previous.copyChunk( 0.0, f.getLength(), true );
		previous = previous.copyChunk( f.getLength(), previous.getLength() );
		ret.setLength( f.getLength() );
		boundsCheck( ret );
		//System.out.println( "Finished with: " + ret );
		return ret;
	}
	
	void boundsCheck( Fragment f )
	{
		for( Note n : f.getNotes())
		{
			if( n.getVelocity() < 0  ) n.setVelocity( 0.0 );
		}
	}
	
	void doDynamics( List<NoteMapping>notes, RenderPlan plan )
	{
		if( applyDynamicCurve ) applyDynamicCurve( notes, plan );
		if( applyDynamicPattern ) applyDynamicsPattern( notes, plan );
		if( applyDynamicAnnotations) applyDynamicAnnotations( notes );
	}
	
	void doTiming( List<NoteMapping>notes, RenderPlan plan )
	{
		if( applyTempoCurve ) applyTimingCurve( notes, plan );
		if( applyTimingPattern ) applyTimingPattern( notes, plan );
		if( applyTimingAnnonations ) applyTimingAnnotations( notes );
		if( boundTiming ) enforceTimingBounds( notes );
	}
	
	void doLength( List<NoteMapping>notes, RenderPlan plan )
	{
		if( applyLengthCurve ) applyLengthCurve( notes, plan );
		//if( applyLengthPattern ) applyLengthPattern( notes, plan );
		if( applyLengthAnnonations ) applyLengthAnnotations( notes );
		if( boundLength ) enforceLengthBounds( notes );
	}
	
	
	void enforceTimingBounds( List<NoteMapping> notes )
	{
		for( NoteMapping map : notes )
		{
			double diff = map.result.getOnset() - map.original.getOnset();
			if( diff > maxPositiveOnset ) map.result.setOnset( map.original.getOnset() + maxPositiveOnset );
			if( diff < maxNegativeOnset ) map.result.setOnset( map.original.getOnset() + maxNegativeOnset );
		}
	}
	
	void enforceLengthBounds( List<NoteMapping> notes )
	{
		for( NoteMapping map : notes )
		{
			if( map.result.getDuration() < MIN_NOTE_LENGTH ) map.result.setDuration( MIN_NOTE_LENGTH );
			if( map.result.getDuration() > MAX_NOTE_LENGTH ) map.result.setDuration( MAX_NOTE_LENGTH );
		}
	}
	
	void applyLengthCurve( List<NoteMapping> notes, RenderPlan plan )
	{
		CurveContainer lengthCurve = plan.getLengthCurve();
		PatternAttribute pattern = plan.getLengthPattern();
		if( lengthCurve == null ) lengthCurve = new CurveContainer( plan.getLength(), 0.7 );
		for( NoteMapping n : notes )
		{
			Note orig = n.getOriginal();
			if( orig.getLongerThan() ) continue;
			Note res = n.getResult();
			double scoreTime = orig.getOnset();
			double scoreDuration = orig.getDuration();
			double curveVal = lengthCurve.sample( scoreTime );
			double pattVal = getPatternValue( orig, pattern );
			double renderDuration = scoreDuration * ( curveVal + pattVal );
			n.result.setDuration( renderDuration );
			
			//System.out.printf( "set duration from %f to %f at %f, pattVal: %f, curve: %f\n", scoreDuration, renderDuration, scoreTime, pattVal, curveVal );
		}
	}

	void applyTimingCurve( List<NoteMapping> notes, RenderPlan plan )
	{
		CurveContainer timingCurve = plan.getTimingCurve();
		if( timingCurve == null ) timingCurve = new CurveContainer( plan.getLength(), 0.0 );
		for( NoteMapping n : notes )
		{
			if( n.original.getStartsBefore() ) continue;
			double scoreTime = n.original.getOnset();
			n.result.moveTo( scoreTime + timingCurve.sample( scoreTime ) );
		}
	}

	void applyDynamicCurve( List<NoteMapping> notes, RenderPlan plan )
	{
		CurveContainer dynamicCurve = plan.getDynamicsCurve();
		if( dynamicCurve == null ) dynamicCurve = new CurveContainer( plan.getLength(), 0.5 );
		for( NoteMapping n : notes )
		{
			double scoreTime = n.original.getOnset();
			//could use (samplePoint % dynamicCurve.getLength() ) to wrap if necessary
			double value = dynamicCurve.sample( scoreTime );
			//Not sure about using dynamics (-.5 to +.5 or velocity 0.0-1.0)
			//n.result.setVelocity( dynamicToVelocity( value ) );
			n.result.setVelocity( value );
		}
	}
	
	void applyDynamicsPattern( List<NoteMapping> notes, RenderPlan plan )
	{
		PatternAttribute pattern = plan.getDynamicsPattern();
		if( pattern == null ) return;
		for( NoteMapping n : notes )
		{
			Note orig = n.getOriginal();
			Note res = n.getResult();
			res.setVelocity( res.getVelocity() + getPatternValue( orig, pattern ) );
		}
	}
	
	void applyLengthPattern( List<NoteMapping> notes, RenderPlan plan )
	{
		PatternAttribute pattern = plan.getLengthPattern();
		if( pattern == null ) return;
		for( NoteMapping n : notes )
		{
			Note orig = n.getOriginal();
			Note res = n.getResult();
			if( orig.getLongerThan() ) continue;
			if( ! pattern.hasValue( orig.getOnset() ) ) continue;
			double val = pattern.getValue( orig.getOnset() );
			res.setDuration( res.getDuration() * ( val )  );
		}
	}
	
	void applyTimingPattern( List<NoteMapping> notes, RenderPlan plan )
	{
		PatternAttribute pattern = plan.getTimingPattern();
		if( pattern == null ) return;
		for( NoteMapping n : notes )
		{
			Note orig = n.getOriginal();
			if( orig.getStartsBefore() ) continue;
			Note res = n.getResult();
			res.addOffset(  getPatternValue( orig, pattern ) );
		}
	}
	
	double getPatternValue( Note note, PatternAttribute pattern )
	{
		if( pattern == null ) return 0.0;
		double time = note.getOnset() + patternOffset;
		if( pattern.hasValue( time )) return pattern.getValue( time );
		return 0.0;
	}
	
	
	void applyTimingAnnotations( List<NoteMapping> notes )
	{
		for( int i = 0; i < notes.size(); i++ )
		{
			NoteMapping map = notes.get(  i  );
			Note note = map.getOriginal();
			if( note instanceof Gracenote ) doGracenote( notes, i );
			//Think we were supposed to do something wtih this?
			//if( map.annotations == null  ) continue;
		}
	}
	


	void applyLengthAnnotations( List<NoteMapping> notes )
	{
		for( int i = notes.size() - 1; i >= 0; i-- )
		{
			NoteMapping map = notes.get( i );
			NoteLength len = new NoteLength( "NORMAL");
			//Find the last length annotation only
			List<MRAAttribute> annos = new Vector<MRAAttribute>();
			annos.addAll( map.original.getAnnotations() );
			if( map.annotations != null )
				annos.addAll( map.annotations );
			for( MRAAttribute att : annos )
				if( ( att instanceof NoteLength )) 
					len = (NoteLength)att;
			String length = len.getValue();
			if( length.equalsIgnoreCase( "LEGATO" ))
			{
				map.result.setEndTime( notes.get( i+1 ).result.getOnset() );
			}
			if( length.equalsIgnoreCase( "SLUR" ))
			{
				map.result.setEndTime( notes.get( i+1 ).result.getOnset() + 0.05 );
			}
			else
			{
				Double f = noteLengths.get( length );
				double factor = 1.0;
				if( f != null ) factor = f.doubleValue();
				double origDur = map.result.getDuration();
				map.result.setDuration( origDur * factor );
			}
		}
	}
	
	void applyDynamicAnnotations( List<NoteMapping> notes )
	{
		for( NoteMapping map : notes )
		{
			if( map.annotations == null  ) return;
			double adjust = 1.0;
			for( MRAAttribute att : map.annotations )
			{
				if( ! ( att instanceof Accent )) continue;
				Accent acc = (Accent) att;
				if( accentValues.contains( acc.getValue() ) )
						adjust += accentValues.get( acc.getValue() );
			}
			//Use the result because the dynamic curves may have altered velocity
			double origDynamic = map.result.getVelocity();
			double newDynamic = origDynamic * adjust;
			if( newDynamic > 1.0 ) newDynamic = 1.0;
			map.result.setVelocity( newDynamic );
		}
	}
	
	void doGracenote( List<NoteMapping> notes, int index )
	{
		NoteMapping map = notes.get(  index );
		Note note = map.getOriginal();
		Note result = map.getResult();
		double noteStart = note.getOnset();
		result.setDuration( GRACENOTE_LENGTH + GRACENOTE_OVERLAP );
		result.setOnset( noteStart - GRACENOTE_LENGTH );
		/*
		 *Put in some stuff to shift the next note - not sure about this...
		while( index < notes.size() &&
				fuzzyCompare( noteStart, notes.get(index).getOriginal().getOnset() ) )
		{
			NoteMapping friend = notes.get( index );
			friend.result.setOnset( noteStart + GRACENOTE_LENGTH );
			friend.result.setDuration( friend.original.getDuration() - GRACENOTE_LENGTH );
			//System.out.println( 
					//"Adjust related note: " + notes.get(index).original + "->" +
					//notes.get(index).result );
			index++;
		}
		*/
		//System.out.println( "Turned " + note + " into " + result );
	
	}


	void humanize( List<NoteMapping> notes )
	{
		for( NoteMapping map : notes )
		{
			double newOnset = map.result.getOnset() + ( humanOnsetLevel * random() );
			map.result.setOnset( newOnset );
			double newVel = map.result.getVelocity() + ( humanVelocityLevel * random() );
			map.result.setVelocity( newVel );
		}
	}
	
	//Dynamics are in the range -.5 to +.5
	//Velocities are 0.0 to 1.0
	public static double dynamicToVelocity( double dynamic )
	{
		dynamic += 0.5;
		if( dynamic > 1.0 ) return 1.0;
		if( dynamic < 0.0 ) return 0.0;
		return dynamic;
	}


	public void setupAnnotationMappings()
	{
		accentValues.put( "VERY_SOFT", -0.2 );
		accentValues.put( "SOFT", -0.1 );
		accentValues.put( "NONE", 0.0 );
		accentValues.put( "ACCENT", 0.1 );
		accentValues.put( "STRONG", 0.2 );
		
		noteLengths.put( "STACCATISSIMO", 0.3 );
		noteLengths.put( "STACCATO", 0.5 );
		noteLengths.put( "MARCATO", 0.75 );
		noteLengths.put( "NORMAL", 1.0 );
		//noteLengths.put( "LEGATO", 1.0 );
		//noteLengths.put( "SLUR", 1.5 );
		//noteLengths.put( "HELD", 2.0 );
	}

	public void setApplyDynamicCurve( boolean applyDynamicCurve )
	{
		this.applyDynamicCurve = applyDynamicCurve;
	}

	public void setApplyNoteDynamics( boolean applyNoteDynamics )
	{
		this.applyNoteDynamics = applyNoteDynamics;
	}

	public void setApplyNoteLengths( boolean applyNoteLengths )
	{
		this.applyNoteLengths = applyNoteLengths;
	}

	public void setApplyNoteTimings( boolean applyNoteTimings )
	{
		this.applyNoteTimings = applyNoteTimings;
	}

	public void setApplyTempoCurve( boolean applyTempoCurve )
	{
		this.applyTempoCurve = applyTempoCurve;
	}
	
	public void setRenderExpressivity( boolean expressivity )
	{
		setApplyNoteLengths( expressivity );
		setApplyNoteTimings( expressivity );
		setApplyTempoCurve( expressivity );
		setApplyDynamicCurve( expressivity );
		setHumanize( expressivity );
	}

	public boolean isHumanize()
	{
		return humanize;
	}

	public void setHumanize( boolean humanize )
	{
		this.humanize = humanize;
	}


	public RenderPlan getPlanner()
	{
		return planner;
	}


	public void setPlanner( RenderPlan planner )
	{
		this.planner = planner;
	}

	private class NoteMapping
	{
		public Note original;
		public Note result;
		public List<MRAAttribute> annotations;
		NoteMapping( Note n )
		{
			original = n;
			result = n.clone();
			//System.out.println( "Mapping " + original + " to " + result );
		}
		Note getOriginal() { return original; }
		Note getResult() { return result; }
		public void addAnnotation( MRAAttribute annotation )
		{
			if( annotations == null ) annotations = new Vector<MRAAttribute>();
			annotations.add( annotation );
		}
	}

	public boolean isSkipExpressivity()
	{
		return skipExpressivity;
	}


	public void setSkipExpressivity( boolean skipExpressivity )
	{
		this.skipExpressivity = skipExpressivity;
	}

	
}
