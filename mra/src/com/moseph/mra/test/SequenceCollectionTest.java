package com.moseph.mra.test;

import java.io.*;
import java.util.*;

import com.moseph.mra.agent.reasoning.sequences.*;

import junit.framework.TestCase;

public class SequenceCollectionTest extends TestCase
{
	//String[] alphabet = { "A", "B", "C" };
	String[] alphabet = { "A", "B"};

	public void testBasicOperation()
	{
		SequenceCollection<String> seqColl = new SequenceCollection<String>( 5 );
		List<String> seq = new Vector<String>();
		for( int i = 0; i < 1000; i++ )
		{
			seq.add( randomSymbol() );
			seqColl.addSequence( seq );
		}
		assertEquals( 6, seqColl.getCompletions( seq ).length );
		System.out.println( SequenceBucket.seqString( seq ));
		seqColl.calculateProbabilities();
		for( BucketNode<String> bn : seqColl.getCompletions( seq ))
			System.out.println( bn );
	}
	
	public String randomSymbol()
	{
		return alphabet[ (int)(Math.random() * alphabet.length )];
	}
	
	public void testSerialization()
	{
		SequenceCollection<String> seqColl = new SequenceCollection<String>( 2 );
		List<String> seq = new Vector<String>();
		for( int i = 0; i < 1000; i++ )
		{
			seq.add( randomSymbol() );
			seqColl.addSequence( seq );
		}
		try
		{
			File tmpFile = File.createTempFile( "sequence", "test" );
			tmpFile.deleteOnExit();
			FileOutputStream fout = new FileOutputStream( tmpFile );
			ObjectOutputStream out = new ObjectOutputStream( fout );
			out.writeObject( seqColl );
			out.close();
			fout.close();
			FileInputStream fin = new FileInputStream( tmpFile );
			ObjectInputStream in = new ObjectInputStream( fin );
			Object o = in.readObject();
			assertEquals( seqColl.toString(), o.toString() );
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail( "Error!" );
		}
	}
}
