package com.moseph.mra.visualise;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.moseph.mra.Channel;
import com.moseph.mra.MRAFactory;
import com.moseph.mra.Section;
import com.moseph.mra.SectionDecisionRule;

import static com.moseph.mra.MRAUtilities.*;

public class SectionVisualiser extends UnitVisualiser
{
	public SectionVisualiser()
	{
		super();
	}
	void dataFromObject( )
	{
		layoutSection();
		
	}
	
	void layoutSection()
	{
		
		if( data == null || !( data instanceof Section ) )return;
		Section section = (Section)data;
		//setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ));
		//System.out.println( "Laying out section");
		Box top = new Box( BoxLayout.X_AXIS );
		top.add( getSettingsPanel(section) );
		top.add( getListPanel( section.getAttributes(), "Attributes" ) );
		top.add( getListPanel( section.getActiveBehaviours(), "Behaviours" ) );
		//add( top );
		//System.out.println( "Visualising Channels...");
		setLayout( new BorderLayout() );
		JComponent thingies = null;
		if( section.isLeaf() )
		{
			List<JComponent> channels = new Vector<JComponent>();
			for( Channel c : section.getChannels() )
			{
				ChannelVisualiser vis = (ChannelVisualiser)MRAVisualiser.getVisualiser( c );
				vis.setLength( section.getLength() );
				channels.add( vis );
			}
			thingies = getListPanel( channels, "Channels", BoxLayout.Y_AXIS );
		}
		else
		{
			JTabbedPane children = new JTabbedPane();
			for( Section s: section.getChildren() )
			{
				children.addTab( s.getName(), MRAVisualiser.getVisualiser(s));
			}
			thingies = children;
		}
		add( new JSplitPane( JSplitPane.VERTICAL_SPLIT, top, thingies), BorderLayout.CENTER );
	}
	
	JComponent getSettingsPanel( Section section )
	{
		Vector<Object> settings = new Vector<Object>();
		Section par = section.getSuperSection();
		JLabel der = new JLabel( "Derived from: " + ( par == null ? "none" : par.getName() ) );
		
		if( par != null)
		{
			if( par.isDefined( )) der.setForeground( Color.green.darker().darker() );
			else	der.setForeground( Color.red.darker().darker() );
		}
		settings.add( der );
		settings.add( new JLabel( "Length: " + formatBeat( section.getLength())));
		SectionDecisionRule rule = section.getDecisionRule();
		if( rule != null ) settings.add( new JLabel( "Ordering: " + rule.getName() ));
		
		JComponent b = getListPanel( settings, "Settings", BoxLayout.Y_AXIS );
		
		return b;
	}
	@Override
	void setupBorder() {
	}
	
	
}
