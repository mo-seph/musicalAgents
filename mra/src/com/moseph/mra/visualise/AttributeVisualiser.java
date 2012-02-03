package com.moseph.mra.visualise;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import com.moseph.mra.Attribute;

public class AttributeVisualiser extends MRAVisualiser
{
	String name = "";
	public AttributeVisualiser()
	{
		super();
	}

	public AttributeVisualiser( Object o )
	{
		super( o );
	}


	void dataFromObject()
	{
		if( dat == null | !( dat instanceof Attribute )) return;
		Attribute att = (Attribute)dat;
		name = att.getName();
		Object val = att.getValue();
		if( val instanceof String ) { add( new JLabel( name + ": " + val )); }
		else
		{
			Box b = new Box( BoxLayout.X_AXIS );
			b.add( new JLabel( name + ": " ));
			b.add( getVisualiser( val ));
			add( b );
		}
	}
	
	void setupBorder()
	{
		BorderFactory.createTitledBorder( "Action: " + name );
	}
}
