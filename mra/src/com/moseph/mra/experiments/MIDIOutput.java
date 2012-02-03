package com.moseph.mra.experiments;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;

import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;
import javax.swing.*;
import javax.swing.Action;

import com.moseph.mra.*;
import com.moseph.mra.midi.*;

public class MIDIOutput extends JPanel
{
	List<Info> devices = new Vector<Info>();
	JComboBox b = new JComboBox();
	
	public static void main( String[] args )
	{
		// Obtain information about all the installed synthesizers.
		MIDIOutput op = new MIDIOutput();
		op.chooseDevice();
		JFrame f = new JFrame( "Midi Output Test");
		f.add( op );
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.setVisible( true );
	}
	
	public MIDIOutput ()
	{
		for( Info i : MidiSystem.getMidiDeviceInfo() )
		{
			try
			{
				MidiSystem.getMidiDevice( i ).getReceiver();
				b.addItem( i );
				devices.add( i );
			}
			catch( Exception e ) {};
		}
	}
	
	public void chooseDevice()
	{
		removeAll();
		setLayout( new BorderLayout() );
		b.setPreferredSize( new Dimension( 400, 20 ));
		add( b, BorderLayout.SOUTH );
		add( new JPanel(), BorderLayout.NORTH );
		Action start = new AbstractAction( "Start" )
		{
			public void actionPerformed( ActionEvent e )
			{
				playMIDI();
			}
		};
		add( new JButton( start ), BorderLayout.EAST );
	}
	
	public void playMIDI()
	{
		Fragment f = new Fragment();
		for( int i = 0; i < 200; i++ )
			f.addNote( i * 0.5, 60 + (i%20)*2, 0.7, 1.0 );
		try
		{
			MidiUtilities util = new MidiUtilities();
			Info i = devices.get( b.getSelectedIndex() );
			MidiDevice device = MidiSystem.getMidiDevice(  i );
			device.open();
			Receiver recv = device.getReceiver();
			System.out.println( "Using device: " + device.getDeviceInfo() );
			Sequencer seq = MidiSystem.getSequencer(false);
			System.out.println( "Sequencer: " + seq.getClass() );
			Sequence sequence = new Sequence( Sequence.PPQ, 384, 1 );
			Transmitter t = seq.getTransmitter();
			seq.getTransmitter().setReceiver( recv );
			t.setReceiver( recv );
			SongInfo info = new SongInfo( new TimeSignature( 4, 4 ), 240, 384 );
			seq.setSequence( sequence );
			seq.getTransmitter().setReceiver( recv );
			seq.open();
			Track systemTrack = seq.getSequence().getTracks()[ 0 ];
			FragmentToTrack out = new FragmentToTrack( systemTrack, info );
			out.addToTrack( f, 0 );
			seq.getTransmitter().setReceiver( recv );
			System.out.println( recv + " " + t + ", " + t.getReceiver()  );
			seq.start();
			seq.getTransmitter().setReceiver( recv );
		}
		catch( Exception e )
		{
			System.out.println( "Problem!: " + e  );
			e.printStackTrace();
		}
		
	}

}
