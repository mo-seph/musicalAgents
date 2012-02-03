package com.moseph.mra.midi;

import java.util.*;

import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;

public class MidiPatchbay
{
	static MidiPatchbay instance;
	Map<String,List<OutputWrapper>> outputs = new HashMap<String, List<OutputWrapper>>();
	Map<String,List<InputWrapper>> inputs = new HashMap<String, List<InputWrapper>>();
	OutputWrapper defaultOutput = null;
	DeviceWrapper defaultInput = null;
	
	public static void main( String[] args )
	{
		MidiPatchbay patchbay = getInstance();
		List<TeeReceiver>trl = new Vector<TeeReceiver>();
		for( InputWrapper wrap : patchbay.getInputs() )
		{
			System.out.println( "Creating tee for" + wrap );
			TeeReceiver tr = new TeeReceiver( null, wrap.toString() );
			wrap.setReceiver( tr );
			trl.add( tr );
		}
		patchbay.patch( "XMidi2X2", 0, "XMidi2X2", 0 );
		patchbay.patch( "XMidi2X2", 1, "XMidi2X2", 1 );
		patchbay.patch( "amt", 0, "amt", 1 );
		//patchbay.patch( "USB Audio Device [2]", 0, "USB", 0 );
		//patchbay.patch( "USB", 0, "USB Audio Device [2]",0 );
		//patchbay.patch( "USB Audio Device [2]", 0, "USB Audio Device [2]", 0 );
	}
	
	private MidiPatchbay()
	{
		initialise();
	}
	
	public static MidiPatchbay getInstance()
	{
		if( instance == null ) instance = new MidiPatchbay();
		return instance;
	}
	
	public void patch( String inName, int inIndex, String outName, int outIndex )
	{
		Transmitter input = getInput( inName, inIndex );
		Receiver output = getOutput( outName, outIndex );
		if( input != null && output != null ) 
		{
			System.out.println( "Patched... " + inName + ":" + inIndex + " -> " + outName + ":" + outIndex );
			System.out.println( "... " + input + "->" + output );
			input.setReceiver( output );
		}
		else System.err.println( "Could not patch " + inName + ":" + inIndex + " -> " + outName + ":" + outIndex );
	}
	
	public InputWrapper getInput( String name, int index )
	{
		System.out.println( "Patching " + name + ":" + index );
		List<InputWrapper> ipList = null;
		for( String ipName : inputs.keySet() )
		{
			if( ipName.toLowerCase().equals( name.toLowerCase() ) )
			{
				ipList = inputs.get( ipName );
				break;
			}
			else System.out.println( ">> No match from: " + ipName );
		}
		if( ipList == null )
		{
			for( String ipName : inputs.keySet() )
			{
				if( ipName.toLowerCase().startsWith( name.toLowerCase() ) )
				{
					ipList = inputs.get( ipName );
					break;
				}
				else System.out.println( ">> No match from: " + ipName );
			}
		}
		if( ipList == null ) return null;
		if( ipList.size() < index - 1 ) return null;
		return ipList.get( index );
	}
	public OutputWrapper getOutput( String name, int index )
	{
		List<OutputWrapper> opList = null;
		for( String opName : outputs.keySet() )
			if( opName.toLowerCase().equals( name.toLowerCase() ) )
			{
				opList = outputs.get( opName );
				break;
			}
		if( opList == null )
			for( String opName : outputs.keySet() )
				if( opName.toLowerCase().startsWith( name.toLowerCase() ) )
				{
					opList = outputs.get( opName );
					break;
				}
		if( opList == null ) return null;
		if( opList.size() < index - 1 ) return null;
		return opList.get( index );
	}
	
	public List<InputWrapper> getInputs()
	{
		List<InputWrapper> inList = new Vector<InputWrapper>();
		for( List<InputWrapper> iL : inputs.values() )
			inList.addAll( iL );
		return inList;
	}
	
	public List<OutputWrapper> getOutputs()
	{
		List<OutputWrapper> outList = new Vector<OutputWrapper>();
		for( List<OutputWrapper> oL : outputs.values() )
			outList.addAll( oL );
		return outList;
	}
	
	void initialise()
	{
		System.out.println( "Initialising patchbay!!");
		for( MidiDevice dev : MidiUtilities.getOutputDevices() )
		{
			Info inf = dev.getDeviceInfo();
			System.out.println( "Output Device: " + inf.getName() );
			try 
			{
				if( outputs.containsKey( inf.getName() ))
				{
					 outputs.get(  inf.getName() ).add( new OutputWrapper( dev, inf.getName() + ":" + outputs.get( inf.getName() ).size() ) );
				}
				else
				{
					List<OutputWrapper> list = new ArrayList<OutputWrapper>( 30 );
					OutputWrapper wrap = new OutputWrapper( dev, inf.getName() );
					list.add( wrap );
					if( defaultOutput == null ) defaultOutput = wrap;
					outputs.put( inf.getName(), list );
				}
			} catch( MidiUnavailableException e  )
			{ System.err.println( "Could not open output device: " + inf.getName() );
			}
		}
		for( MidiDevice dev : MidiUtilities.getInputDevices() )
		{
			Info inf = dev.getDeviceInfo();
			System.out.println( "Input Device: " + inf.getName() );
			try {
				if( inputs.containsKey( inf.getName() ))
				{
					 inputs.get(  inf.getName() ).add( new InputWrapper( dev, inf.getName() + ":" + inputs.get( inf.getName() ).size() ) );
				}
				else
				{
					List<InputWrapper> list = new ArrayList<InputWrapper>( 30 );
					InputWrapper wrap = new InputWrapper( dev, inf.getName() );
					list.add( wrap );
					if( defaultInput == null ) defaultInput = wrap;
					inputs.put( inf.getName(), list );
				}
			} catch( MidiUnavailableException e  )
			{ System.err.println( "Could not open input device: " + inf.getName() );
			}
		}
		
	}

	public DeviceWrapper getDefaultInput()
	{
		return defaultInput;
	}

	public OutputWrapper getDefaultOutput()
	{
		return defaultOutput;
	}

	public void setDefaultInput( DeviceWrapper defaultInput )
	{
		this.defaultInput = defaultInput;
	}

	public void setDefaultOutput( OutputWrapper defaultOutput )
	{
		this.defaultOutput = defaultOutput;
	}
	
	public void setDefaultInput( String name, int index )
	{
		setDefaultInput( getInput( name, index ) );
	}
	public void setDefaultOutput( String name, int index )
	{
		setDefaultOutput( getOutput( name, index ) );
	}
	

}
