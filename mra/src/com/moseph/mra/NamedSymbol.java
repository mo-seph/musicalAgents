package com.moseph.mra;

public interface NamedSymbol
{

	public abstract String getName();

	public abstract void define();

	public abstract void use();

	public abstract boolean isDefined();

	public abstract boolean isUsed();

}