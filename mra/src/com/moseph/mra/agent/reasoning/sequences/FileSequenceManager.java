package com.moseph.mra.agent.reasoning.sequences;

import com.moseph.mra.acts.ActRelation;

public class FileSequenceManager implements SequenceManager
{
	SequenceCollection<ActRelation> s = new SequenceCollection<ActRelation>( 3  );
	
	public FileSequenceManager( String filename )
	{
		SequenceCollection seqFile = SequenceCollection.fromFile( filename );
		if( seqFile == null ) System.err.println( "++++++++\n\n++++++++++++\n\n++++++++++==\n Problem loading sequence file " 
				+ filename + "\n falling back to empty sequences...");
		else s = seqFile;
		//System.out.println( "Sequence Collection: \n" + s );
	}
	
	public SequenceCollection<ActRelation> getSequences()
	{
		return s;
	}
	
}
