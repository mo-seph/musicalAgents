package com.moseph.mra.agent.reasoning;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.PatternAttribute;

public class DefaultReasoner implements Reasoner
{
	Context context;
	FeatureSet features;
	StructuralDecider decider;
	PartChunker chunker;
	double quantisation;
	
	public DefaultReasoner( Context c, StructuralDecider decider )
	{
		this.context = c;
		this.decider = decider;
		quantisation = context.getQuantisation();
		features = context.getFeatures();
		if( decider != null ) chunker = new PartChunker( c, decider );
	}
	
	public Fragment nextChunk( double length )
	{
		Fragment f = chunker.getNextChunk( length );
		return f;
	}

	public BasicRenderPlan getNextPlan( double length )
	{
		return getNextPlan( length, null );
	}
	public BasicRenderPlan getNextPlan( double length, Fragment notes )
	{
		BasicRenderPlan plan = new BasicRenderPlan( length );
		if( chunker != null ) plan.setPatternOffset( chunker.getPosition() );
		if( notes == null && decider != null ) plan.setNotes( nextChunk( length ) );
		else plan.setNotes( notes );
		fillPlan( plan );
		return plan;
	}
	
	void fillPlan( BasicRenderPlan plan )
	{
		//System.out.println( "£££££££££££££££££££££££\n££££££££££££££££3\n£££££££££££££££££££££££\nDefault Reasoner");
		
	}
	
	public void initNecessaryAnalysers( AnalysisSystem sys )
	{
		for( String name : getNecessaryAnalysers() )
			sys.addAnalyser( name );
	}

	List<String> getNecessaryAnalysers()
	{
		List<String> nec = new Vector<String>();
		return nec;
	}

	static double smooth( double oldVal, double newVal, double smoothing )
	{
		return oldVal * smoothing + newVal * ( 1 - smoothing );
	}
	

}
