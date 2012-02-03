package com.moseph.mra.agent.attribute;

import java.util.List;

public interface Feature
{

	public abstract Feature clone();

	public abstract int compareTo( Feature other );

	public abstract void setParameters( List<String> params );
	
	public abstract double distance( Feature other );
	
}