package com.moseph.mra.symbolic;

import java.util.*;
import static com.moseph.mra.MRAUtilities.*;

public class NumericPatternValue extends PatternValue<Double, NumericTreeValue, NumericPatternValue>
{
	public NumericPatternValue( int size, TreeLattice<Double, NumericTreeValue> lattice )
	{
		super( size, lattice );
	}
	
	public NumericPatternValue( int size, TreeLattice<Double, NumericTreeValue> lattice, NumericTreeValue...values)
	{
		super(size, lattice);
		//if( values.length != size ) return;
		//System.out.println( collectionToString( Arrays.asList( values ) ));
		setValues( Arrays.asList( values ) );
	}
	
	NumericPatternValue createCopy( List<NumericTreeValue> in )
	{
		return new NumericPatternValue( size, valueLattice, in.toArray( new NumericTreeValue[]{} ));
	}
	
	NumericPatternValue createCopy()
	{
		return new NumericPatternValue( size, valueLattice, values.toArray( new NumericTreeValue[]{} ));
	}

	@Override
	NumericPatternValue createEmptyCopy()
	{
		return new NumericPatternValue( size, valueLattice );
	}
	
	public NumericPatternValue getAny()
	{
		NumericPatternValue any = createEmptyCopy();
		return any;
	}
	
	public NumericPatternValue getIncomp()
	{
		NumericPatternValue inc = createEmptyCopy();
		for( int i = 0; i < size; i++ ) inc.set( i, valueLattice.getIncompatible() );
		return inc;
	}

}
