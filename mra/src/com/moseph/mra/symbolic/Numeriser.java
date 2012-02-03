package com.moseph.mra.symbolic;

import com.moseph.mra.agent.attribute.Feature;

public interface Numeriser<F extends Feature, V extends Value>
{
	public F numerise( V value );
}
