package com.moseph.mra.agent.attribute;

public interface NumericFeature extends Feature
{

	public abstract double getValue();

	public abstract void setValue( double value );

	public abstract boolean equals( NumericFeature op );

}