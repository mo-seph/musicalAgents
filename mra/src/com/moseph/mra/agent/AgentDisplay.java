package com.moseph.mra.agent;

import jade.core.AID;

import javax.swing.*;

public class AgentDisplay
{
	static private boolean showDisplay = false;
	JTabbedPane pane = new JTabbedPane();
	static private AgentDisplay instance = null;
	//static boolean madeOne = false;
	
	static synchronized AgentDisplay getInstance()
	{
		if( instance == null )
		{
			instance = new AgentDisplay();
			System.out.println(">>>>>>>>>>>>>>>>Need instance, but don't have one!" );//+ madeOne );
			//madeOne = true;
		}
		if( instance == null ) System.out.println( "-------------No instance!");
		return instance;
	}
	
	private AgentDisplay()
	{
		System.out.println( "************\nMaking Agent display!\n**************\n");
		JFrame f = new JFrame( "Agent Properties" );
		f.add( pane );
		f.setVisible( showDisplay );
	}
	
	void addDisplayForAgent( AID agent, JComponent display )
	{
		pane.add( agent.getLocalName(), display );
		pane.repaint();
	}
	
	static void addAgent( AID agent, JComponent display )
	{
		getInstance().addDisplayForAgent( agent, display );
	}

	public static boolean isShowDisplay()
	{
		return showDisplay;
	}

	public static void setShowDisplay( boolean showDisplay )
	{
		AgentDisplay.showDisplay = showDisplay;
	}
}
