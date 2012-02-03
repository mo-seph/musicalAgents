package com.moseph.mra.agent.gui;

import java.awt.event.ActionEvent;
import java.util.*;

import javax.swing.*;
import javax.swing.Action;

import com.moseph.mra.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.GroupNumericFeature;

public class ConductorInterface extends JPanel
{
	SpaceAgent space;
	Context context;
	JComponent create;
	Vector<Path> leafNames;
	JLabel currentAverage = new JLabel( "null");
	Path currentPath;
	
	public ConductorInterface( SpaceAgent space, Context context )
	{
		this.space = space;
		this.context = context;
		currentPath = context.getPiece().getFirstPath();
		setupLeafNames();
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS));
		create = getCreationPanel();
		add( create );
		add( new JPanel() );
		
	}
	
	JComponent getCreationPanel()
	{
		Box panel = new Box( BoxLayout.Y_AXIS );
		Box specific = new Box( BoxLayout.X_AXIS );
		final JComboBox path = new JComboBox( leafNames );
		Action a = new AbstractAction( "Add Specific")
		{
			public void actionPerformed( ActionEvent e )
			{
				space.createNewAgent( path.getSelectedItem().toString() );
			}
		};
		Action b = new AbstractAction( "Add Average")
		{
			public void actionPerformed( ActionEvent e )
			{
				space.createNewAgent( currentPath.toString() );
			}
		};
		specific.add( new JButton(a));
		specific.add( path );
		specific.setBorder( BorderFactory.createTitledBorder( "New path"));
		panel.add( specific );
		Box average = new Box( BoxLayout.X_AXIS );
		average.add( new JButton(b));
		average.add( currentAverage );
		panel.add( average );
		
		return panel;
	}
	
	void setupLeafNames()
	{
		leafNames = new Vector<Path>( context.getPiece().getLeafPaths() );
	}
	
	public void updatePath()
	{
		GroupNumericFeature paths = (GroupNumericFeature)context.getFeature( "Path");
		int pathIndex = (int)paths.getAverageValue();
		if( pathIndex > 0 && pathIndex < leafNames.size() )
			currentPath = leafNames.get( pathIndex );
		currentAverage.setText( currentPath.toString() );
		currentAverage.repaint();
	}

}
