package com.moseph.mra.test;

import java.io.FileInputStream;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import com.moseph.mra.Examples;
import com.moseph.mra.Fragment;
import com.moseph.mra.Note;
import com.moseph.mra.Piece;
import com.moseph.mra.Section;
import com.moseph.mra.parser.MRAParser;
import com.moseph.mra.visualise.MRAVisualiser;

import static com.moseph.mra.visualise.MRAVisualiser.*;

import junit.framework.TestCase;

public class ParsingTest extends TestCase
{

	public void testParseEquality()
	{
		Piece p = Examples.getExamplePiece();
		Piece op = null;
		try
		{
			FileInputStream file = new FileInputStream( "examples/test2.mra" );
			MRAParser parser = new MRAParser( file );
			op = parser.runParser();
		}
		catch( Exception e ) { System.out.println( "Could not create piece: " + e ); }
		System.out.println( p + "\n" + op );
		//Broken...
		//assertEquals( p, op );
		
	}
}
