package com.moseph.mra.symbolic;

import com.moseph.mra.agent.attribute.Feature;

public interface Symboliser<F extends Feature, V extends Value>
{
	public V symbolise( F feature );
}
