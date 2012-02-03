package com.moseph.mra.symbolic;

public interface LatticeManager
{
	public NumericTreeLattice getLattice( String featureName );
	public NumericTreeLattice getPatternLattice( String featureName );
	
	public NumericTreeLattice getDefaultLattice();
	public NumericTreeLattice getDefaultPatternLattice();
	public NumericPatternLattice getPatternLatticeFor( String featureName );
	public void setDefaultLattice( NumericTreeLattice tree );
	
	
}
