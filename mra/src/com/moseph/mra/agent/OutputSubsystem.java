package com.moseph.mra.agent;

import com.moseph.mra.*;
import com.moseph.mra.agent.reasoning.*;

import static com.moseph.mra.MRAConstants.*;
import static com.moseph.mra.MRAUtilities.*;
import static java.lang.Math.*;

public class OutputSubsystem
{
	double leadIn = 8.0;
	double gotMusicTill;
	double grace = 4.0;
	boolean loaded = false;
	boolean takingABreak = false;
	RenderingSystem renderer;
	StructuralDecider structuralDecider;
	Fragment outputBuffer;
	static double bufferMax = 10.0;
	double bufferOffset = 0.0;
	Reasoner reasoner;
	
	public OutputSubsystem( Fragment outputBuffer, RenderingSystem renderer, Reasoner reasoner, double leadIn, double grace )
	{
		this.outputBuffer = outputBuffer;
		outputBuffer.clear( leadIn );
		this.leadIn = leadIn;
		gotMusicTill = leadIn;
		this.grace = grace;
		this.renderer = renderer;
		this.reasoner = reasoner;
		renderer.setRenderExpressivity( false );
	}
	
	public void musicRequest( double start, double end )
	{
		if( start > gotMusicTill ) 
		{
			System.out.println( "*************\nSkipping from " + gotMusicTill + " to " + start );
			gotMusicTill = max( start, gotMusicTill );
			//outputBuffer.expandTo( gotMusicTill );
		}
	}
	
	/**
	 * This function fills the output subsystem's output buffer until the
	 * given time (although the subsystem will add its grace period as well)
	 * Deals with issues like running out of music etc.
	 * @param needMusicTill
	 */
	public void fillBufferTill( double needMusicTill )
	{
		double lengthToGet = needMusicTill + grace - gotMusicTill;
		//System.out.println( "Generating music from " + gotMusicTill + " to " + ( needMusicTill + grace ) + "(" + lengthToGet + ")" );
		if( lengthToGet <= 0.0 ) return;
		Fragment f = new Fragment( lengthToGet );
		if( ! takingABreak )
		{
			RenderPlan r = reasoner.getNextPlan( lengthToGet );
			f = renderer.render( r, gotMusicTill );
		}
		outputBuffer.addFragment( f, gotMusicTill - bufferOffset );
		gotMusicTill += f.getLength();
		if( outputBuffer.getLength() > 2 * bufferMax )
		{
			outputBuffer = outputBuffer.copyChunk( bufferMax, outputBuffer.getLength() );
			bufferOffset += bufferMax;
		}
	}
	
	public void setGrace( double newGrace )
	{
		double extraGrace = newGrace - grace;
		if( extraGrace > 0 ) fillBufferTill( gotMusicTill + extraGrace );
		grace = newGrace;
	}

	public boolean isTakingABreak()
	{
		return takingABreak;
	}

	public static void setBufferMax( double bufferMax )
	{
		OutputSubsystem.bufferMax = bufferMax;
	}

	public void setTakingABreak( boolean takingABreak )
	{
		this.takingABreak = takingABreak;
	}
	
	public Fragment getOutput( double start, double end )
	{
		if( takingABreak ) return new Fragment( end - start );
		return outputBuffer.copyChunk( start - bufferOffset, end - bufferOffset );
	}

}
