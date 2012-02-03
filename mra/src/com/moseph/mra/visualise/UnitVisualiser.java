package com.moseph.mra.visualise;

import java.awt.Color;
import java.util.List;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.moseph.mra.Attribute;
import com.moseph.mra.Unit;

public class UnitVisualiser extends MRAVisualiser
{	
	Unit data;
	
	public UnitVisualiser()
	{
		super();
	}
	
	public void setData( Object u )
	{
		if( ! ( u instanceof Unit ))
		{
			log.log( Level.WARNING, "Can't visualise a " + u.getClass() + " using a " + getClass() );
		}
		data = (Unit)u;
		//System.out.println( "Setting Unit data for " + getClass() + " to " + u );
		super.setData( u );
	}
	
	void dataFromObject( )
	{
		if( data == null || !( data instanceof Unit )) return;
		Unit unit = (Unit)data;
		List<Attribute>atts = unit.getAttributes();
		if( atts.size() > 0 )
		{
			add( getListPanel( atts, "Attributes") );	
		}
		else add( new JLabel( data + "" ));
			
		
	}
	

	void setupBorder()
	{
		if( data == null ) return;
		setForeground( MRAVisualiser.getColorFor( data ) );
		String bordername = data.getName();
		if( bordername.length() <= 0 ) bordername = data.getClass() + "";
		setBorder( BorderFactory.createTitledBorder( 
				BorderFactory.createMatteBorder( 2, 2, 2, 2, getForeground() ), data.getName()));
	}
	
	
}
