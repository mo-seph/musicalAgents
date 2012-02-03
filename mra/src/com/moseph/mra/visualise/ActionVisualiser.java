package com.moseph.mra.visualise;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import com.moseph.mra.Action;
import com.moseph.mra.Attribute;

public class ActionVisualiser extends UnitVisualiser
{

	public ActionVisualiser()
	{
		super();
	}

	void dataFromObject()
	{
		if( dat == null | !( dat instanceof Action )) return;
		Action act = (Action)dat;
		add( getListPanel( act.getAttributes(), null ) );

	}
	
	
}
