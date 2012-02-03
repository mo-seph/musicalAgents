package com.moseph.mra.agent.attribute;

import java.util.List;
import java.util.logging.Logger;

import com.moseph.mra.MRAUtilities;

public abstract class MRAAttribute implements Feature
{
	protected static Logger log = MRAUtilities.getLogger();
	
	/* (non-Javadoc)
	 * @see com.moseph.mra.attribute.Feature#clone()
	 */
	public abstract MRAAttribute clone();
	/* (non-Javadoc)
	 * @see com.moseph.mra.attribute.Feature#compareTo(com.moseph.mra.attribute.MRAAttribute)
	 */
	public abstract int compareTo( Feature other );
	
	public int compareTo( Object other )
	{
		if( other instanceof Feature ) return compareTo( (Feature) other );
		return toString().compareTo( other.toString() );
	}
	
	/* (non-Javadoc)
	 * @see com.moseph.mra.attribute.Feature#setParameters(java.util.List)
	 */
	public void setParameters( List<String> params )
	{
		//Do nothing
		log.warning( "Set parameters not impelemeted for " + getClass() );
	}
}
