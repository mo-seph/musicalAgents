package com.moseph.mra.visualise;

import javax.swing.JLabel;

import com.moseph.mra.Note;

public class NoteVisualiser extends UnitVisualiser
{
	public NoteVisualiser()
	{
		super();
	}
	
	public void dataFromObject()
	{
		if( data == null || !( data instanceof Note )) return;
		add( new JLabel( data.toString() ));
		
	}

}
