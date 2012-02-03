package com.moseph.mra.blob;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.*;

public class BlobLearnerUI extends JPanel
{
	BlobLearner bl;
	
	public BlobLearnerUI( BlobLearner b )
	{
		this.bl = b;
		
		initInterface();
	}
	
	void initInterface()
	{
		setLayout( new BorderLayout() );
		add( getButtons(), BorderLayout.NORTH );
		add( getInfo(), BorderLayout.CENTER );
	}
	
	JComponent getButtons()
	{
		Box buttonBox = new Box( BoxLayout.Y_AXIS  );
		Box topButtons = new Box( BoxLayout.X_AXIS );
		buttonBox.add( topButtons );
		
		Action finishLearning = new AbstractAction( "Play")
		{
			public void actionPerformed( ActionEvent e )
			{
				bl.learningFinished();
				//removeAll();
			}
		};
		Action resetCollisions = new AbstractAction( "Reset Collisions")
		{
			public void actionPerformed( ActionEvent e )
			{
				bl.resetCollisions();
			}
		};
		topButtons.add( new JButton( finishLearning ));
		buttonBox.add( new JButton( resetCollisions ));
		return buttonBox;
	}
	
	JComponent getInfo()
	{
		JComponent infoPanel = new Box( BoxLayout.X_AXIS );
		
		infoPanel.add( new ColorSlider( "R", bl.getRedScale() )
		{
			public void setValue( double val ) { bl.setRedScale( val ); }
		}) ;
		infoPanel.add( new ColorSlider( "G", bl.getGreenScale() )
		{
			public void setValue( double val ) { bl.setGreenScale( val ); }
		}) ;
		infoPanel.add( new ColorSlider( "B", bl.getBlueScale() )
		{
			public void setValue( double val ) { bl.setBlueScale( val ); }
		}) ;
		infoPanel.add( new ColorSlider( "Sep", bl.MIN_COLSEP )
		{
			public void setValue( double val ) { bl.MIN_COLSEP = val; }
		}) ;
				
		return infoPanel;
	}
	
	abstract class ColorSlider extends JSlider implements ChangeListener
	{
		int range = 100;
		public ColorSlider( String name )
		{
			this( name, 0.5 );
		}
		public ColorSlider( String name, double value )
		{
			setBorder( BorderFactory.createTitledBorder( name ));
			setOrientation( JSlider.VERTICAL );
			addChangeListener( this );
			super.setValue( (int)(value * 0.5 * range ) );
		}
		
		double getColValue()
		{
			return (double)getValue() /(double)range * 2;
		}

		public void stateChanged( ChangeEvent e )
		{
			setValue( getColValue() );
		}
		
		abstract void setValue( double val );
	}
}
