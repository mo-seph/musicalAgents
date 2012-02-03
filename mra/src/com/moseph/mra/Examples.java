package com.moseph.mra;

public class Examples
{
	//Should be the same as examples/test2.mra
	public static Piece getExamplePiece()
	{
		Piece p = new Piece( "test" );
		Section main = new Section( "Main");
		Section c = new Section( "Chorus");
		
		Fragment f = new Fragment( "Notes");
		f.addNotes(new Note( 0.0, 64, 0.7, 1.0 ), new Note( 1.0, 64, 0.7, 1.0 ));
		c.addChannel( f );
		c.setLength( 2.0 );
		
		Section b = new Section( "Chorus");
		f = new Fragment( "Notes");
		f.addNotes(new Note( 0.0, 65, 0.7, 1.0 ), new Note( 1.0, 65, 0.7, 1.0 ));
		b.addChannel( f );
		b.setLength( 2.0 );
		
		
		main.addChild( c );
		main.addChild( b );
		main.addChild( c );
		main.addChild( b );
		p.addChild( main );
		System.out.println( p + "");
		return p;
	}
	
	public static Piece getExamplePiece2()
	{
		Piece p = new Piece( "Test");
		Section s = new Section( "Main");
		Section c = new Section( "Chorus");
		Fragment f = new Fragment( "Notes");
		for( int i = 0; i < 2; i++ ) f.addNote( new Note( (double)i, 60 + i, 0.8, 0.2 ));
		c.addChannel( f );
		c.setLength( 2.0 );
		
		Section v = new Section( "Verse");
		Fragment fv = new Fragment( "Notes");
		for( int i = 0; i < 3; i++ ) fv.addNote( new Note( (double)i, 70 - i, 0.8, 0.7));
		v.addChannel( fv );
		v.setLength( 2.5 );
		
		s.addChild( c );
		s.addChild( v);
		s.addChild( c );
		s.addChild( v);
		s.addChild( c );
		s.addChild( v );
		p.addChild( s );
		return p;
	}
	
	public static Piece getExamplePiece1()
	{
		Piece p = new Piece( "Test");
		p.addChild( getExampleSection1());
		return p;
	}
	
	public static Section getExampleSection1()
	{
		Section s = new Section( "Main");
		Section c = new Section( "Chorus");
		Fragment f = new Fragment( "Notes");
		for( int i = 0; i < 2; i++ ) f.addNote( new Note( (double)i, 60 + i, 0.8, 0.2 ));
		c.addChannel( f );
		c.setLength( 2.0 );
		
		Section v = new Section( "Verse");
		Fragment fv = new Fragment( "Notes");
		for( int i = 0; i < 3; i++ ) fv.addNote( new Note( (double)i, 70 - i, 0.8, 0.7));
		v.addChannel( fv );
		v.setLength( 2.5 );
		
		s.addChild( c );
		s.addChild( v);
		s.addChild( c );
		s.addChild( v);
		s.addChild( c );
		s.addChild( v );
		return s;
	}	
}
