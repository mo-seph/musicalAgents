package com.moseph.mra.agent.attribute;

public interface DualNumericFeature extends Feature
{

	public abstract double getValue1();
	public abstract double getValue2();

	public abstract void setValue1( double value );
	public abstract void setValue2( double value );

	public abstract boolean equals( DualNumericFeature op );

}