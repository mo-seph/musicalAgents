package com.moseph.mra.agent;

import jade.core.AID;

import javax.sound.midi.*;

import com.moseph.mra.SongInfo;
import com.moseph.mra.midi.*;

public class RecordAgentDetails
{
	boolean enabled = false;
	int channel = -1;
	String name = "Unnamed";
	String instrument = "Unknown";
	InputWrapper input;
	AID agentID;
	SongInfo info;
	FragmentAdaptor adaptor = null;
	double x = 0.0;
	double y = 0.0;
	int devNumber = -1;
	String SEPARATOR_CHAR = ";";
	Track track;
	boolean ignoreNoInputDevice = false;
	
	
	public RecordAgentDetails() 
	{
		
	}
	
	public RecordAgentDetails( String s )
	{
		this();
		String[] args = s.split( SEPARATOR_CHAR );
		System.err.println( "Making details from string'" + s + "'");
		if( args.length != 7 ) System.err.println( "Bad RecordAgentDetails String: " + s );
		else
		{
			init( args[0], args[1], args[2], args[3], args[4], args[5], args[6] );
		}
		
	}
	
	public RecordAgentDetails( String name, String instrument, String devName, String devNumber, String channel, String x, String y )
	{
		this();
		init( name, instrument, devName, devNumber, channel, x, y );
	}
	
	void init( String name, String instrument, String devName, String devNumber, String channel, String x, String y )
	{
		MidiPatchbay pb = MidiPatchbay.getInstance();
		this.name = name;
		this.instrument = instrument;
		this.devNumber = Integer.parseInt( devNumber );
		
		System.out.println( "Looking for device: " + devName );
		this.input = pb.getInput( devName, this.devNumber );
		if( input == null ) System.err.println( "Could not get device: " + devName + ":" + this.devNumber );
		this.channel = Integer.parseInt( channel );
		this.x = Double.parseDouble( x );
		this.y = Double.parseDouble( y );
	}
	
	public String toString()
	{
		String inputName = "unknown";
		if( input != null ) inputName = input.getDeviceInfo().getName();
		return name + SEPARATOR_CHAR + instrument + SEPARATOR_CHAR + inputName + SEPARATOR_CHAR + devNumber + SEPARATOR_CHAR + channel + SEPARATOR_CHAR + x + SEPARATOR_CHAR + y;
	}
	
	public static MidiDevice.Info getMidiDevice( String devName, int number )
	{
		System.out.println( "#####\n#####\n#####\n#####\nLooking for midi device: '" + devName + "'," + number);
		int index = 0;
		for( MidiDevice.Info i : MidiSystem.getMidiDeviceInfo() )
		{
			System.out.println( "Trying: " + i.getName() );
			if( i.getName().toLowerCase().startsWith( devName.toLowerCase() ) )
			{
				if( index >= number )
				{
					System.out.println( "OK!" );
					return i;
				}
				else
				{
					System.out.println( "Index " + index + " too low for: " + number );
					index++;
				}
			}
			else
			{
				System.out.println( "Wrong device: ;" + i.getName() + "'" );
			}
		}
		return null;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled( boolean enabled )
	{
		this.enabled = enabled;
	}

	public InputWrapper getInput()
	{
		return input;
	}

	public void setInput( InputWrapper input )
	{
		this.input = input;
	}

	public String getInstrument()
	{
		return instrument;
	}

	public void setInstrument( String instrument )
	{
		this.instrument = instrument;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public int getChannel()
	{
		return channel;
	}

	public void setChannel( int channel )
	{
		this.channel = channel;
	}

	public AID getAgentID()
	{
		return agentID;
	}

	public void setAgentID( AID agentID )
	{
		this.agentID = agentID;
	}

	public SongInfo getInfo()
	{
		return info;
	}

	public void setInfo( SongInfo info )
	{
		this.info = info;
	}

	public FragmentAdaptor getAdaptor()
	{
		return adaptor;
	}

	public void setAdaptor( FragmentAdaptor adaptor )
	{
		this.adaptor = adaptor;
	}

	public double getX()
	{
		return x;
	}

	public void setX( double x )
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY( double y )
	{
		this.y = y;
	}

	public Track getTrack()
	{
		return track;
	}

	public void setTrack( Track track )
	{
		this.track = track;
	}

	public boolean isIgnoreNoInputDevice()
	{
		return ignoreNoInputDevice;
	}

	public void setIgnoreNoInputDevice( boolean ignoreNoInputDevice )
	{
		this.ignoreNoInputDevice = ignoreNoInputDevice;
	}

}
