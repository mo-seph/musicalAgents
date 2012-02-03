package com.moseph.mra.midi;
import static java.util.logging.Level.INFO;
import static javax.sound.midi.ShortMessage.*;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;

import com.moseph.mra.*;
/**
General utilities for dealing with MIDI data.
Mostly used to help turn MIDI into NBF notation, and probably the
other way round.

The MIDI messages recognised are:
<table border=1>
<tr><td>Status Byte</td><td>Fixed bytes</td><td>Information</td><td>Description</td></tr>
<tr><td>FF</td><td>00 02 </td><td></td>                 <td>Sequence Number</td></tr>
<tr><td>FF</td><td>01 </td><td>len text</td>            <td>Text Event</td></tr>
<tr><td>FF</td><td>02 </td><td>len text</td>            <td>Copyright Notice</td></tr>
<tr><td>FF</td><td>03 </td><td>len text</td>            <td>Sequence/Track Name</td></tr>
<tr><td>FF</td><td>04 </td><td>len text</td>            <td>Instrument Name</td></tr>
<tr><td>FF</td><td>05 </td><td>len text</td>            <td>Lyric</td></tr>
<tr><td>FF</td><td>06 </td><td>len text</td>            <td>Marker</td></tr>
<tr><td>FF</td><td>07 </td><td>len text</td>            <td>Cue Point</td></tr>
<tr><td>FF</td><td>20 01</td><td> cc </td>              <td>MIDI Channeel Prefix (32,1)</td></tr>
<tr><td>FF</td><td>2F 00 </td> <td></td>                <td>End of Track (47,0)</td></tr>
<tr><td>FF</td><td>51 03 </td><td>tttttt</td>           <td>Set Tempo (81,03)</td></tr>
<tr><td>FF</td><td>54 05 </td><td>hr mn se fr ff </td>  <td>SMPTE Offset (84,05)</td></tr>
<tr><td>FF</td><td>58 04 </td><td>nn dd cc bb </td>     <td>Time Signature (88,04)</td></tr>
<tr><td>FF</td><td>59 02 </td><td>sf mi </td>           <td>Key Signature (89,02)</td></tr>
<tr><td>FF</td><td>7F </td><td>len data</td>            <td>Sequencer Specific Meta-Event</td></tr>
<tr><td>90-9f</td> <td></td><td>note vel</td>            <td>Note on (144-159)</td></tr>
<tr><td>80-8f</td> <td></td><td>note vel</td>            <td>Note off (128-143)</td></tr>
</table>
	
@author David Murray-Rust
@version $Revision$, $Date$
*/
public class MidiUtilities
{
	public final static String[] MajorKeys = { "Cb", "Gb", "Db", "Ab", "Eb", "Bb", "F", "C", "G", "D", "A", "E", "B", "F#", "C#" };
	public final static String[] MinorKeys = { "Ab", "Eb", "Bb", "F", "C", "G", "D", "A", "E", "B", "F#", "C#", "G#", "D#", "A#" };
	public final static int SUBDIVISION = 48;
	protected static Logger log = MRAUtilities.getLogger();
	public static final int PAN = 10;
	public static final int DISTANCE = 11;
	public static final int ALL_NOTES_OFF = 123;


	public Fragment trackToFragment( Track t, SongInfo s )
	{
		//TrackConverter tc = new TrackConverter( t, s );
		return new TrackConverter( t, s ).toFragment();
	}

	class TrackConverter
	{
		Map<Integer,Vector<Note>> openNotes = new HashMap<Integer,Vector<Note>>();
		Track track;
		SongInfo songInfo;
		Fragment f = new Fragment();

		TrackConverter( Track t, SongInfo s )
		{
			track = t;
			songInfo = s;
		}

		Fragment toFragment()
		{
			for( int i = 0; i < track.size();  i++ )
			{
				MidiEvent e = track.get( i );
				MidiMessage m = e.getMessage();
				if( MidiUtilities.isNoteOnMessage( m ) )
				{
					//ShortMessage sm = (ShortMessage) m;
					addNoteEvent( e );
				}
				else if( MidiUtilities.isNoteOffMessage( m ) )
				{
					closeNotes( e );
				}
			}
			return f;
		}

		public boolean addNoteEvent( MidiEvent e )
		{
			double noteBeat = songInfo.getEventBeat( e );
			int noteNumber = ((ShortMessage)e.getMessage()).getData1();
			int noteVelocity = ((ShortMessage)e.getMessage()).getData2();
			//double velocity = ( (double)noteVelocity )/128;
			Note n = new Note( noteBeat, noteNumber, noteVelocity );
			n.setEvent( e );
			f.addNote( n );
			openNote( n );
			return true;
		}

		public void closeNotes( MidiEvent e )
		{
			ShortMessage sm = (ShortMessage)e.getMessage();
			Vector<Note> v = openNotes.get( sm.getData1() );
			if( v == null || v.size() == 0 )
			{
				log.log( INFO, "Attempted to close a midi note which was not open (Beat " + songInfo.getEventBeat( e ) + ")" );
				return;
			}
			for( Note n : v )
			{
				closeNoteWithEvent( n, e );
			}
			v.clear();
		}

		public void openNote( Note n )
		{
			int noteNumber = n.getPitchNumber();
			if( openNotes.get( noteNumber ) == null )
			{
				openNotes.put( noteNumber, new Vector<Note>() );
			}
			openNotes.get( noteNumber ).add( n );
		}

		void closeNoteWithEvent( Note n, MidiEvent e )
		{
			if( n.getStartEvent() != null )
			{
				double endTime = songInfo.getEventBeat( e );
				double duration = ( endTime - n.getOnset() );
				n.close( duration );
			}
		}

	}


	/**
	prints the event if it is relevant.
	@param e
	@param track
	*/
	public static void printMessage( MidiEvent e, int track )
	{
		String s = eventToString( e );
		if( s != null )
		{
			log.fine( "Track " + track + ": " + eventToString( e ) );
		}
	}

	/**
	gives a string representation of the given event. May be null for
	certain events
	@param e
	*/
	public static String eventToString( MidiEvent e )
	{
		return messageToString( e.getMessage() );
	}

	/**
	gives a string representation of the given message. May be null for
	certain messages
	@param m
	*/
	public static String messageToString( MidiMessage m )
	{
		if( m instanceof MetaMessage )
		{
			MetaMessage mm = (MetaMessage)m;
			if( isTempo( mm ) )
			{
				return "Tempo: " + getBPM( mm ) + " bpm";
			}
			else if( isTrackName( mm ) )
			{
				return "Name:" + getDataAsString( mm );
			}
			else if( isTimeSignature( mm ) )
			{
				return "Time Signature: " + getTimeSignature( mm );
			}
			else if( isKeySignature( mm ) )
			{
				return "Key Signature: " + getKeySignature( mm );
			}
			else if( isSMPTEOffset( mm ) )
			{
				return "SMPTE Offset: " + getSMPTETime( mm );
			}

			else if( isEndOfTrack( mm ) || isIgnored( mm ) )
			{
				//Ignore it
				return null;
			}
			else
			{
				return ">>>: " + getByteString( mm );
			}
		}
		else
		{
			if( isNoteOnMessage( m ) || isNoteOffMessage( m ))
			{
				ShortMessage ms = (ShortMessage)m;
				String type = isNoteOnMessage( m ) ? " on " : " off ";
				return "Note " + type + "[" + ms.getChannel() + "]: " + getByteString( m );
				
			}
			else if( isNoteOffMessage( m ) )
			{
				return "Note: off: " + getByteString( m );
			}
			else
			{
				return getByteString( m );
			}
		}
	}


	public static float getBPM( MetaMessage m )
	{
		byte[] b = m.getData();
		int microseconds = b[0]*256*256 + b[1]*256 + b[0];
		float bpm = 60000000/microseconds;
		return bpm;
	}

	public static String getByteString( MidiMessage m )
	{
		String byteString = "";
		for( byte b : m.getMessage() ) byteString += ( b & 0xFF ) + " ";
		return byteString;
	}

	public static String getDataAsString( MetaMessage m )
	{
		String dataString  = "";
		for( byte b : m.getData() ) dataString += (char)b;
		return dataString;
	}

	public static String getSMPTETime( MetaMessage m )
	{
		byte[] data = m.getData();
		String smpte = data[0]+"";
		for( byte b : data ) smpte += ":" + b;
		return smpte + " (hh:mm:ss:ff:pf)";
	}

	public static TimeSignature getTimeSignature( MetaMessage m )
	{
		byte[] b = m.getData();
		return new TimeSignature( b[ 0 ], (int)Math.pow( 2, b[1] ) );
	}

	public static MetaMessage createTimeSignature( TimeSignature t )
	{
		byte numerator = (byte)t.beats;
		byte denominator = (byte)( (int)( Math.log( t.type ) / Math.log( 2 ) ) );

		byte[] data = new byte[ 4 ];
		data[ 0 ] = numerator;
		data[ 1 ] = denominator;
		data[ 2 ] = 0x24;
		data[ 3 ] = 0x08;
		MetaMessage ts = new MetaMessage();
		try
		{
			ts.setMessage( 88, data, 4 );
		}
		catch( Exception e )
		{
			log.log( INFO, "Bad time signature: " + t );
		}
		return ts;
	}

	public static String getKeySignature( MetaMessage m )
	{
		byte[] b = m.getData();
		if( b[1] == 0 )
		{
			return MajorKeys[ b[0] + 7 ] + " Major";
		}
		else
		{
			return MinorKeys[ b[0] + 7 ] + " Minor";
		}
	}

	/* Message Type Functions:
	//FF 00 02                     Sequence Number
	//FF 01 len text               Text Event
	//FF 02 len text               Copyright Notice
	//FF 03 len text               Sequence/Track Name
	//FF 04 len text               Instrument Name
	//FF 05 len text               Lyric
	//FF 06 len text               Marker
	//FF 07 len text               Cue Point
	//FF 20 01 cc                  MIDI Channeel Prefix (32,1)
	//FF 2F 00                     End of Track (47,0)
	//FF 51 03 tttttt              Set Tempo (81,03)
	//FF 54 05 hr mn se fr ff      SMPTE Offset (84,05)
	//FF 58 04 nn dd cc bb         Time Signature (88,04)
	//FF 59 02 sf mi               Key Signature (89,02)
	//FF 7F len data               Sequencer Specific Meta-Event
	//90-9f note vel               Note on (144-159)
	//80-8f note vel               Note off (128-143)
	*/
	
	//90-9f note vel               Note on (144-159)
	public static boolean isNoteOnMessage( MidiMessage m )
	{
		if( ! ( m instanceof ShortMessage ) )
		{
			return false;
		}
		ShortMessage sm = (ShortMessage)m;
		return ( sm.getStatus() >= 144 && sm.getStatus() <= 159 && sm.getData2() != 0 );
	}

	//80-8f note vel               Note off (128-143)
	public static boolean isNoteOffMessage( MidiMessage m )
	{
		if( ! ( m instanceof ShortMessage ) )
		{
			return false;
		}
		ShortMessage sm = (ShortMessage)m;
		return ( ( sm.getStatus() >= 128 && sm.getStatus() <= 143 ) || ( sm.getStatus() >= 144 && sm.getStatus() <= 159 && sm.getData2() == 0 ) );
	}

	//FF 51 03 tttttt              Set Tempo (81,03)
	public static boolean isTempo( MidiMessage m )
	{
		if( ! ( m instanceof MetaMessage ) )
		{
			return false;
		}
		byte[] b = m.getMessage();
		return ( b[1] == 81 && b[2] == 3 );
	}

	//FF 03 len text               Sequence/Track Name
	public static boolean isTrackName( MidiMessage m )
	{
		if( ! ( m instanceof MetaMessage ) )
		{
			return false;
		}
		byte[] b = m.getMessage();
		return ( b[1] == 3 );
	}

	//FF 2F 00                     End of Track (47,0)
	public static boolean isEndOfTrack( MidiMessage m )
	{
		if( ! ( m instanceof MetaMessage ) )
		{
			return false;
		}
		byte[] b = m.getMessage();
		return ( b[1] == 47 && b[2] == 0 );
	}

	public static boolean isIgnored( MidiMessage m )
	{
		if( ! ( m instanceof MetaMessage ) )
		{
			return false;
		}
		byte[] b = m.getMessage();
		return ( 
			b[1] == 33 ||  // FF 21 01 pp: MIDI Port (Used to select which port on an interface is used)
			b[1] == 127 || //FF 7F len data: Sequencer Specific Meta-Event (cannot handle)
			false);
	}

	//FF 54 05 hr mn se fr ff      SMPTE Offset (84,05)
	public static boolean isSMPTEOffset( MidiMessage m )
	{
		if( ! ( m instanceof MetaMessage ) )
		{
			return false;
		}
		byte[] b = m.getMessage();
		return ( b[1] == 84 && b[2] == 5 );
	}

	//FF 58 04 nn dd cc bb         Time Signature (88,04)
	public static boolean isTimeSignature( MidiMessage m )
	{
		if( ! ( m instanceof MetaMessage ) )
		{
			return false;
		}
		byte[] b = m.getMessage();
		return ( b[1] == 88 && b[2] == 4 );
	}

	//FF 59 02 sf mi               Key Signature (89,02)
	public static boolean isKeySignature( MidiMessage m )
	{
		if( ! ( m instanceof MetaMessage ) )
		{
			return false;
		}
		byte[] b = m.getMessage();
		return ( b[1] == 89 && b[2] == 2 );
	}


	public static ShortMessage noteOff( int pitch, int vel )
	{
		return getMessage( NOTE_OFF, pitch, vel );
	}

	public static ShortMessage noteOff( int channel, int pitch, int vel )
	{
		return getMessage( NOTE_OFF, channel, pitch, vel );
	}

	public static ShortMessage noteOn( int pitch, int vel )
	{
		return getMessage( NOTE_ON, pitch, vel );
	}
	
	public static ShortMessage noteOn( int channel, int pitch, int vel )
	{
		return getMessage( NOTE_ON, channel, pitch, vel );
	}
	
	public static ShortMessage programChange( int channel, int value )
	{
		return getMessage( PROGRAM_CHANGE, channel, value, value );
	}
	
	public static ShortMessage pan( int channel, int value )
	{
		return getMessage( CONTROL_CHANGE, channel, PAN, value );
	}
	public static ShortMessage distance( int channel, int value )
	{
		return getMessage( CONTROL_CHANGE, channel, 11, value );
	}
	
	public static ShortMessage allNotesOff( int channel )
	{
		return getMessage( CONTROL_CHANGE, channel, ALL_NOTES_OFF, 0 );
	}
	
	
	public static ShortMessage getMessage( int type, int channel, int data1, int data2 )
	{
		try
		{
			ShortMessage msg = new ShortMessage();
			msg.setMessage( type, channel, data1, data2 );
			return msg;
		}
		catch( Exception e )
		{
			log.log( INFO, "Could not create short message [" + type + "," + data1 + "," + data2 + ",c" + channel + "] " + e ); 
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static ShortMessage getMessage( int type, int pitch, int vel )
	{
		try
		{
			ShortMessage msg = new ShortMessage();
			msg.setMessage( type, pitch, vel );
			return msg;
		}
		catch( Exception e )
		{
			log.log( INFO, "Could not create short message [" + type + "," + pitch + "," + vel + "] " + e ); 
			e.printStackTrace();
		}
		return null;
	}
		
	public static String byteToString( byte[] b )
	{
		String op = "[ ";
		for( byte i : b )
			op += i + " ";
		return op + "]";
	}
	
	public static List<MidiDevice> getOutputDevices()
	{
		List<MidiDevice> outputs = new Vector<MidiDevice>();
		for (Info i : MidiSystem.getMidiDeviceInfo())
		{
			MidiDevice dev;
			try
			{
				dev = MidiSystem.getMidiDevice( i );
				int recv = dev.getMaxReceivers();
				int trans = dev.getMaxTransmitters();
				// A reciever but no transmitters == output
				if (recv != 0 && trans == 0)
				{
					outputs.add( dev );
				}
			}
			catch (MidiUnavailableException e)
			{
				System.err.println( "Bad Device: " + i );
			}
		}
		return outputs;
	}
	public static List<MidiDevice> getInputDevices()
	{
		List<MidiDevice> outputs = new Vector<MidiDevice>();
		for (Info i : MidiSystem.getMidiDeviceInfo())
		{
			MidiDevice dev;
			try
			{
				dev = MidiSystem.getMidiDevice( i );
				int recv = dev.getMaxReceivers();
				int trans = dev.getMaxTransmitters();
				// A transmitter but no recievers == input
				if (recv == 0 && trans != 0)
				{
					outputs.add( dev );
				}
			}
			catch (MidiUnavailableException e)
			{
				System.err.println( "Bad Device: " + i );
			}
		}
		return outputs;
	}
	
	public static List<MidiDevice> getSequencerDevices()
	{
		List<MidiDevice> outputs = new Vector<MidiDevice>();
		for (Info i : MidiSystem.getMidiDeviceInfo())
		{
			MidiDevice dev;
			try
			{
				dev = MidiSystem.getMidiDevice( i );
				int recv = dev.getMaxReceivers();
				int trans = dev.getMaxTransmitters();
				if (recv != 0 && trans != 0)
				{
					outputs.add( dev );
				}
			}
			catch (MidiUnavailableException e)
			{
				System.err.println( "Bad Device: " + i );
			}
		}
		return outputs;
	}
	
	public static void waitForEnter()
	{
		BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
		try
		{
			in.readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static MidiDevice.Info getMidiDeviceFromList( String devName, int number, Info...devices )
	{
		System.out.println( "#####\n#####\n#####\n#####\nLooking for midi device: '" + devName + "'," + number);
		int index = 0;
		for( MidiDevice.Info i : devices )
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
	
	public static MidiDevice.Info getMidiDevice( String devName, int number )
	{
		return getMidiDeviceFromList( devName, number, MidiSystem.getMidiDeviceInfo() );
	}
	
	public static Info getOutputMidiDevice( String devName, int number )
	{
		System.out.println( "Getting output device: "+ devName + ", " + number );
		return getMidiDevice( devName, number, getOutputDevices() );
	}
	
	public static Info getInputMidiDevice( String devName, int number )
	{
		System.out.println( "Getting input device: "+ devName + ", " + number );
		return getMidiDevice( devName, number, getInputDevices() );
	}
		
	public static Info getMidiDevice( String devName, int number, List<MidiDevice> devs )
	{
		Info[] infos = new Info[ devs.size() ];
		for( int i = 0; i < devs.size(); i++ )
		{
			infos[i] = devs.get( i ).getDeviceInfo() ;
			System.out.println( "List contains " + infos[i].getName() + "; r:" + devs.get(i).getMaxReceivers() + ",t:" + devs.get(i).getMaxTransmitters()  );
		}
		return getMidiDeviceFromList( devName, number, infos );
	}
	
	public static void patchCable( String inName, int inNumber, String outName, int outNumber )
	{
		Info inputInfo = getInputMidiDevice( inName, inNumber );
		Info outputInfo = getOutputMidiDevice( outName, outNumber );
		try
		{
			System.out.println( "Opening input");
			MidiDevice input = MidiSystem.getMidiDevice( inputInfo );
			try {
				input.open();
			} catch (MidiUnavailableException e)
			{ System.out.println( "Input already open!" ); }
				
			
			System.out.println( "Opening output");
			MidiDevice output = MidiSystem.getMidiDevice( outputInfo );
			try {
				output.open();
			} catch (MidiUnavailableException e)
			{ System.out.println( "Output already open!" ); }
			
			System.out.println( "input; recv: " + input.getMaxReceivers() + ", tran: " + input.getMaxTransmitters() );
			System.out.println( "Output; recv: " + output.getMaxReceivers() + ", tran: " + output.getMaxTransmitters() );
			
			System.out.println( "Getting Transmitter");
			Transmitter t= input.getTransmitter();
			System.out.println( "Getting Receiver");
			Receiver r = output.getReceiver();
			//TeeReceiver tr = new TeeReceiver( r, "> " + inName + ":" + inNumber + " -> " + outName + ":" + outNumber );
			//t.setReceiver( tr );
			t.setReceiver( r );
		} catch (MidiUnavailableException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
