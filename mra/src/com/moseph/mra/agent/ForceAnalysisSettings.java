package com.moseph.mra.agent;

import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;

public class ForceAnalysisSettings
{
	boolean forceRelative = false;
	boolean forceNotRelative = false;
	boolean forceDistanceWeighted = false;
	boolean forceNotDistanceWeighted = false;
	boolean forceIncludeSelf = false;
	boolean forceNotIncludeSelf = false;
	
	public void applyToAnalyser( Analyser a )
	{
		if( forceIncludeSelf ) a.getFeatureSet().setIncludeSelf( true );
		if( forceNotIncludeSelf ) a.getFeatureSet().setIncludeSelf( false );
		if( a instanceof NumericAnalyser ) applyToNumericAnalyser( (NumericAnalyser) a );
	}
	
	public void applyToNumericAnalyser( NumericAnalyser a )
	{
		if( forceRelative ) a.getFeatureSet().setRelative( true );
		if( forceNotRelative ) a.getFeatureSet().setRelative( false );
		if( forceDistanceWeighted ) a.getFeatureSet().setDistanceWeight( true );
		if( forceNotDistanceWeighted ) a.getFeatureSet().setDistanceWeight( false );
	}

	public void setForceDistanceWeighted( boolean forceDistanceWeighted )
	{
		this.forceDistanceWeighted = forceDistanceWeighted;
		if( forceDistanceWeighted ) forceNotDistanceWeighted = false;
	}

	public void setForceIncludeSelf( boolean forceIncludeSelf )
	{
		this.forceIncludeSelf = forceIncludeSelf;
		if( forceIncludeSelf ) forceNotIncludeSelf = false;
	}

	public void setForceNotDistanceWeighted( boolean forceNotDistanceWeighted )
	{
		this.forceNotDistanceWeighted = forceNotDistanceWeighted;
		if( forceNotDistanceWeighted ) forceDistanceWeighted = false;
	}

	public void setForceNotIncludeSelf( boolean forceNotIncludeSelf )
	{
		this.forceNotIncludeSelf = forceNotIncludeSelf;
		if( forceNotIncludeSelf ) forceIncludeSelf = false;
	}

	public void setForceNotRelative( boolean forceNotRelative )
	{
		this.forceNotRelative = forceNotRelative;
		if( forceNotRelative ) forceRelative = false;
	}

	public void setForceRelative( boolean forceRelative )
	{
		this.forceRelative = forceRelative;
		if( forceRelative ) forceNotRelative = false;
	}
}
