package com.moseph.mra.agent.attribute;


public class Scale extends MRAAttribute {

	Scales scale;
	Pitch root;
	
	public Scale( Pitch pitch, Scales scale)
	{
		this.root = pitch;
		this.scale = scale;
	}
	
	@Override
	public MRAAttribute clone() {
		return new Scale( root, scale );
	}

	@Override
	public int compareTo(Feature other) {
		if( ! ( other instanceof Scale)  ) return toString().compareTo( other.toString() );
		Scale o = (Scale)other;
		int rootCmp = root.compareTo( o.root );
		if( rootCmp != 0 ) return rootCmp;
		int extensionCmp = scale.compareTo( o.scale );
		if( extensionCmp != 0 ) return extensionCmp;
		return 0;
	}
	
	public static enum Scales
	{
		MAJOR( 0, 2, 4, 5, 7, 9, 11 ),
		MINOR( 0, 2, 3, 5, 7, 8, 11 );
		public final int[] degrees;
		Scales( int...degs )
		{
			degrees = degs;
		}
		
	}
	
	public double distance( Feature f )
	{
		if( compareTo( f ) == 0 ) return 0.0;
		return 1.0;
	}

}
