package com.moseph.mra.visualise;

import java.awt.Color;
import java.util.List;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import com.moseph.mra.Attribute;
import com.moseph.mra.Unit;
import com.moseph.mra.logic.ConnectiveExpression;
import com.moseph.mra.logic.ExpressionTerm;
import com.moseph.mra.logic.Function;
import com.moseph.mra.logic.Term;

public class ExpressionTermVisualiser extends MRAVisualiser
{
	
	public ExpressionTermVisualiser()
	{
		super();
	}
	
	void dataFromObject( )
	{
		if( dat == null || !( dat instanceof ExpressionTerm )) return;
		ExpressionTerm data = (ExpressionTerm)dat;
		if( data instanceof Term ) add( new JLabel( data.toString() ));
		else if ( data instanceof ConnectiveExpression )
		{
			ConnectiveExpression exp = (ConnectiveExpression)data;
			Box b  = new Box( BoxLayout.X_AXIS );
			b.add( new JLabel(exp.getConnectiveString()));
			b.add( getListPanel( exp.getConnectives(), null, BoxLayout.Y_AXIS ) );
			add( b );
		}
		else if( data instanceof Function )
		{
			Function f = (Function)dat;
			Box b = new Box( BoxLayout.X_AXIS );
			b.add( new JLabel( f.getName() ));
			for( ExpressionTerm e: f.getParameters() ) { b.add( getVisualiser( e )); }
			add( b );
		}
	}
	
/*
	void setupBorder()
	{
		if( data == null ) return;
		if( data.isDefined() && data.isUsed() ) { setForeground( Color.green ); }
		else if ( data.isUsed() ) { setForeground( Color.RED ); }
		else if ( data.isDefined() ) { setForeground( Color.ORANGE ); }
		else { setForeground( Color.cyan ); }
		String bordername = data.getName();
		if( bordername.length() <= 0 ) bordername = data.getClass() + "";
		setBorder( BorderFactory.createTitledBorder( 
				BorderFactory.createMatteBorder( 2, 2, 2, 2, getForeground() ), data.getName()));
	}*/
	
}
