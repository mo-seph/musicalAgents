package com.moseph.mra.agent.gui;

import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;

import javax.sound.midi.*;
import javax.swing.*;
import javax.sound.midi.MidiDevice.Info;

import com.moseph.mra.agent.RecordAgentDetails;
import com.moseph.mra.midi.*;
import static com.moseph.mra.midi.MidiUtilities.*;

public abstract class MIDISelector extends JPanel
{
	final JComboBox outputBox = new JComboBox();
	final JComboBox sequencerBox = new JComboBox();
	final List<InputWrapper> inputList = new Vector<InputWrapper>();
	final List<RecordAgentDetails> agentList = new Vector<RecordAgentDetails>();
	RecordAgentSelectBox recSelect;
	MidiPatchbay patchbay = MidiPatchbay.getInstance();
	
	public MIDISelector()
	{
		
		populateBoxes();
		initInterface();
			setPreferredSize( new Dimension( 500, 300 ) );
	}

	void populateBoxes()
	{
		for( OutputWrapper out : patchbay.getOutputs() )
			outputBox.addItem( out );
		for( InputWrapper in : patchbay.getInputs() )
			inputList.add( in );
		for( MidiDevice seq : getSequencerDevices() )
			sequencerBox.addItem( seq.getDeviceInfo() );
	}

	void initInterface()
	{
		Action a = new AbstractAction( "Start" )
		{
			public void actionPerformed( ActionEvent e )
			{
				deviceSelection();
			}
		};
		Box b = new Box( BoxLayout.Y_AXIS );
		outputBox.setBorder( BorderFactory.createTitledBorder( "System Output" ) );
		sequencerBox.setBorder( BorderFactory.createTitledBorder( "Sequencer" ) );
		b.setBorder( BorderFactory.createTitledBorder( "Choose MIDI Setup" ) );
		b.add( outputBox );
		b.add( sequencerBox );
		recSelect = new RecordAgentSelectBox( agentList );
		b.add( recSelect );
		b.add( new JButton( a ) );
		add( b );
	}
	
	void deviceSelection()
	{
		OutputWrapper output = (OutputWrapper) outputBox.getSelectedItem();
		Info seqInfo = (Info) sequencerBox.getSelectedItem();
		try
		{
			Sequencer sequencer = (Sequencer)MidiSystem.getMidiDevice( seqInfo );
			System.out.println( "Output: " + output.getClass() + ", Sequencer: "
					+ sequencer.getClass() );
			devicesSelected( output, sequencer );
		}
		catch (MidiUnavailableException e1)
		{
			System.err.println( "Couldn't get midi device: " + e1 );
			e1.printStackTrace();
		}
	}
	
	public List<RecordAgentDetails> getRecordAgentDetails()
	{
		return new Vector<RecordAgentDetails>( agentList );
	}

	public abstract void devicesSelected( OutputWrapper output, Sequencer sequencer );
	
	class RecordAgentSelectBox extends Box
	{
		List<RecordAgentDetails> agents;
		public RecordAgentSelectBox(  final List<RecordAgentDetails> agents )
		{
			super( BoxLayout.Y_AXIS );
			this.agents = agents;
			setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Input Config" ) );
			next( null );
		}
		
		
		public void next( RecordAgentDetails caller )
		{
			if( caller != null ) agents.add( caller );
			add( new RecordAgentDisplay( this ));
			revalidate();
		}
		
	}
	
	class RecordAgentDisplay extends Box implements KeyListener
	{
		final RecordAgentDetails details;
		JTextField nameField = new JTextField( "Name", 30 );
		JComboBox channelField = new JComboBox();
		boolean nameOK = false;
		JButton completeButton;
		final RecordAgentSelectBox parent;
		JComboBox inputSelect = new JComboBox();
		
		public RecordAgentDisplay( RecordAgentSelectBox parent )
		{
			super( BoxLayout.X_AXIS );
			this.parent = parent;
			details = new RecordAgentDetails();
			for( int i = 0; i < 16; i++ ) channelField.addItem( new Integer( i ) );
			
			for( InputWrapper dev :  patchbay.getInputs() )
				inputSelect.addItem( dev );
			
			add( inputSelect );
			add( nameField );
			add( channelField );
			
			final RecordAgentSelectBox par = parent;
			Action a = new AbstractAction( "OK") {
				public void actionPerformed( ActionEvent arg0 ) {
					complete();
					par.next( details );
				}
			};
			completeButton = new JButton( a );
			add( completeButton );
			completeButton.setEnabled( true );
			nameField.addKeyListener( this );
		}

		public void keyPressed( KeyEvent arg0 ) { }

		public void keyReleased( KeyEvent arg0 ) { }

		public void keyTyped( KeyEvent arg0 )
		{
			nameOK = true;
			updateButton();
		}
		
		void updateButton()
		{
			completeButton.setEnabled( nameOK  );
		}
		
		void complete()
		{
			channelField.setEnabled( false );
			nameField.setEnabled( false );
			details.setEnabled( true );
			details.setName( nameField.getText() );
			details.setChannel( ((Integer)channelField.getSelectedItem() ).intValue() );
			details.setInput( (InputWrapper )inputSelect.getSelectedItem() );
			completeButton.setEnabled( false );
		}

		public RecordAgentDetails getDetails()
		{
			return details;
		}
		
	}

}
