package com.moseph.mra.agent.gui;

import java.awt.event.ActionEvent;
import static com.moseph.mra.MRAConstants.*;

import javax.swing.*;
import javax.swing.event.*;

import com.illposed.osc.*;

public class DummyOSCPanel extends JPanel
{
	OSCPortOut sender;
	public DummyOSCPanel()
	{
		try { sender = new OSCPortOut(); }
		catch (Exception e1) { e1.printStackTrace(); }
		add( getSliderPanel() );
	}
	
	JComponent getSliderPanel()
	{
		Box b = new Box( BoxLayout.X_AXIS );
		String[] names = { "player1", "player2", "player3" };
		for( String name : names )
		{
			final JSlider slider = new JSlider( JSlider.VERTICAL, 0, 100, 50 );
			final String s = name;
			ChangeListener a = new ChangeListener()
			{
				public void stateChanged(ChangeEvent e) 
				{
					
					double value = (double)(slider.getValue() * 2 - 100 ) * ROOM_X /100;
					send( s, value + ""  );
				}
			};
			slider.addChangeListener( a );
			slider.setBorder( BorderFactory.createTitledBorder( name ));
			b.add( slider );
		}
		return b;
	}
	
	void send( String agent, String message )
	{
		try 
		{
			sender.send( new OSCMessage("/" + agent, new String[] { message } ) );
		} 
		catch (Exception e) 
		{
			System.out.println("Couldn't send: " + e );
		}
	
	}
}
