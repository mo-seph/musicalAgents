package com.moseph.mra.visualise;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import com.moseph.mra.Action;
import com.moseph.mra.Behaviour;
import com.moseph.mra.logic.ExpressionTerm;

public class BehaviourVisualiser extends MRAVisualiser
{
	String name = "";
	public BehaviourVisualiser()
	{
		super();
	}

	public BehaviourVisualiser( Object o )
	{
		super( o );
	}


	void dataFromObject()
	{
		if( dat == null | !( dat instanceof Behaviour )  ) return;
		Behaviour aa = (Behaviour)dat;
		Action a = aa.getAction();
		Object e = aa.getExpression();
		if( e == null ) e = aa.getDecision();
		Box b = new Box( BoxLayout.Y_AXIS);
		Box bt = new Box( BoxLayout.X_AXIS );
		bt.add( new JLabel( "Do: "));
		bt.add( getVisualiser( a ));
		Box bb = new Box( BoxLayout.X_AXIS );
		bb.add( new JLabel( "When: "));
		if( e != null ) bb.add( getVisualiser( e ));
		else bb.add( new JLabel( "Null "));
		b.add( bt );
		b.add( bb );
		b.setBorder( BorderFactory.createEtchedBorder() );
		add( b );
	}
	
	void setupBorder()
	{
		BorderFactory.createTitledBorder( "Action: " + name );
	}
}
