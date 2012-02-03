package com.moseph.mra.midi.analysis;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import com.moseph.mra.*;
import com.moseph.mra.acts.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.midi.*;
import com.moseph.mra.symbolic.*;
import com.moseph.mra.visualise.*;
import static com.moseph.mra.MRAUtilities.*;

public class TwoFragmentVisualAnalyser extends TwoFragmentAnalyser
{
	BasicFragmentVisualiser bfv;
	JComponent bfd;
	JFrame frame = new JFrame( "Two Fragment Analysis");
	JTabbedPane pane = new JTabbedPane();
	JComponent display = new Box( BoxLayout.Y_AXIS );
	JComponent display1 = new Box( BoxLayout.Y_AXIS );
	JComponent display2 = new Box( BoxLayout.Y_AXIS );
	FragmentDisplay frag1Display;
	FragmentDisplay frag2Display;
	protected JTabbedPane actQueues = new JTabbedPane( JTabbedPane.LEFT );
	double displayScale = 10.0;
	Map<String, Map<String,MusicalActionQueueVisualiser>> visualisers = new HashMap<String, Map<String,MusicalActionQueueVisualiser>>();
	int numVisualisers = 0;
	List<MusicalActionQueueVisualiser> visList = new Vector<MusicalActionQueueVisualiser>();
	
	public static void main( String[] args )
	{
		if( args.length == 0 ) 
		{
			System.err.println( "Need a filename");
			System.exit( 1 );
		}
		TwoFragmentAnalyser tfa = new TwoFragmentVisualAnalyser( args[0]);
		while( tfa.getPosition() < 400 )
		{
			tfa.skip();
		}
		while( tfa.hasMoreEvents() && tfa.getPosition() < 600.0 )
		{
			tfa.nextChunk();
		}
		tfa.done();
		if( args.length > 1 ) tfa.writeSequences( args[1] );
	}
	
	public TwoFragmentVisualAnalyser( String filename )
	{
		super( filename );
		initDisplay();
		nextChunk();
		frame.setVisible( true );
	}
	
	
	void initDisplay()
	{
		visualisers.put( a.getName(), new HashMap<String, MusicalActionQueueVisualiser>() );
		visualisers.put( b.getName(), new HashMap<String, MusicalActionQueueVisualiser>() );
		bfv = new BasicFragmentVisualiser();
		bfv.setData( frag1 );
		bfd = bfv.getDisplay();
		//display1.add( bfv );
		frag1Display = new FragmentDisplay( frag1 );
		frag2Display = new FragmentDisplay( frag2 );
		
		//display1.add( frag1Display );
		display2.add( frag2Display );
		frag1Display.setAlignmentX( 0.0f );
		frag2Display.setAlignmentX( 0.0f );
		display1.setBorder( BorderFactory.createTitledBorder( "Track 1" ) );
		display2.setBorder( BorderFactory.createTitledBorder( "Track 2" ) );
		
		JScrollPane jps = new JScrollPane( display, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
		display.add( bfd );
		bfd.setBorder( BorderFactory.createMatteBorder( 2, 2, 2, 2, Color.green ) );
		bfd.setBackground( Color.pink );
		display.add( frag1Display );
		display.add( actQueues );
		display.add( frag2Display );
		
		frame.add( jps );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		
	}
	
	public void nextChunk()
	{
		super.nextChunk();
		FeatureMusicianMap<ActQueue> queues = extractor.getQueues();
		for( String featureName : queues.features() )
		{
			for( String musName : queues.musicians( featureName ) )
			{
				MusicalActionQueueVisualiser visual = getVisualiser( musName, featureName );
				visual.update();
				visual.fitToWindow();
			}
		}
		setSizes( pos, displayScale );
		//System.out.println( collectionToString( context.getFeatures() ));
		//System.out.println( collectionToString( queues ));
		display.revalidate();
	}
	
	MusicalActionQueueVisualiser getVisualiser( String musName, String featureName )
	{
		return getVisualiser( context.getMusicianInformation( musName ), featureName );
	}
	MusicalActionQueueVisualiser getVisualiser( MusicianInformation inf, String featureName )
	{
		String musName = inf.getName();
		if( ! visualisers.containsKey( musName )) 
			visualisers.put( musName, new HashMap<String, MusicalActionQueueVisualiser>() );
		Map<String,MusicalActionQueueVisualiser> musicianVis = visualisers.get( musName );
		MusicalActionQueueVisualiser vis = musicianVis.get( featureName );
		if( vis == null )
		{
			vis = new MusicalActionQueueVisualiser();
			vis.setName( "Feature: " + featureName );
			vis.setPreferredSize( new Dimension( 30000, 100 ) );
			vis.setMinimumSize( new Dimension( 3000, 100 ) );
			vis.setData( extractor.getQueueForMusician( inf, featureName ) );
			vis.setAlignmentX( 0.0f );
			musicianVis.put( featureName, vis );
			addVisualiser( inf, featureName, vis );
		}
		return vis;
	}
	
	void addVisualiser( MusicianInformation inf, String featureName, MusicalActionQueueVisualiser vis )
	{
		visList.add( vis );
		int tabIndex = actQueues.indexOfTab( featureName );
		if( tabIndex < 0 )
		{
			Box b = new Box( BoxLayout.Y_AXIS );
			b.add( vis );
			actQueues.add( featureName, b );
		}
		else
		{
			Box comp = (Box)actQueues.getComponentAt( tabIndex );
			comp.revalidate();
			comp.add( vis );
		}
		actQueues.revalidate();
		
	}
	
	void setSizes( double beats, double scaleFactor )
	{
		int pixelSize = (int)( beats * scaleFactor );
		bfd.setMinimumSize( new Dimension( pixelSize + 400, 200 ) );
		frag1Display.setMinimumSize( new Dimension( pixelSize + 400, 200 ) );
		frag2Display.setMinimumSize( new Dimension( pixelSize + 400, 200 ) );
		bfd.setPreferredSize( new Dimension( pixelSize, 200 ) );
		bfd.revalidate();
		bfd.revalidate();
		frag1Display.setPreferredSize( new Dimension( pixelSize, 200 ) );
		frag2Display.setPreferredSize( new Dimension( pixelSize, 200 ) );
		display.setMinimumSize( new Dimension(pixelSize,200) );
		//display.setPreferredSize( new Dimension(pixelSize,400 + visList.size() * 100 ) );
		display1.setPreferredSize( new Dimension(pixelSize,200 + visList.size() * 100) );
		display2.setPreferredSize( new Dimension(pixelSize,200 + visList.size() * 100) );
		for( MusicalActionQueueVisualiser v : visList )
		{
			v.setPreferredSize( new Dimension( pixelSize, 100 ) );
			v.setMinimumSize( new Dimension( pixelSize, 100 ) );
			v.setMaximumSize( new Dimension( pixelSize * 100, 100 ) );
			v.fitToWindow();
			v.setLength( beats );
		}
	}
	
	protected void done()
	{
		super.done();
		for( Map<String,MusicalActionQueueVisualiser> m : visualisers.values() )
		{
			for( MusicalActionQueueVisualiser v : m.values() )
			{
				v.getQueue().addEvent( new MusicalAction( me, new NumericTreeValue("", 0.0 ), Relationship.MU, Relationship.MU, pos ) );
			}
		}
		setSizes( pos, displayScale );
	}

}
