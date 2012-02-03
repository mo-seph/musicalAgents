package com.moseph.mra.agent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.*;

import java.io.IOException;
import java.net.SocketException;
import java.util.*;

import com.illposed.osc.*;
import com.moseph.mra.*;
import static jade.lang.acl.ACLMessage.*;
import static com.moseph.mra.MRAConstants.*;

public class OSCAgent extends MusicallyAwareAgent
{

	OSCPortIn receiver;
	Map<String,AID> others;

	public void initialise()
	{
		super.initialise();
		others = new HashMap<String, AID>();
		try
		{
			receiver = new OSCPortIn( 15001 );
			receiver.startListening();
		} catch (SocketException e)
		{
			e.printStackTrace();
		}
		addBehaviour( getUpdateFromMusicBehaviour() );
		addBehaviour( getFallthroughBehaviour() );
	}

	Behaviour getUpdateFromMusicBehaviour()
	{
		return new BlockingReceiver( this, getScoreTemplate() )
		{
			void receivedMessage( ACLMessage m )
			{
				Score s = getScoreFromMessage( m );
				for ( Fragment f : s.fragments() )
				{
					String name = f.getPartIndex().getMusician().getName();
					if( !others.containsKey( name ) )
					{
						addOther( name );
					}
				}
			}
		};
	}

	public List<MessageTemplate> getKnownMessages()
	{
		List<MessageTemplate> sup = super.getKnownMessages();
		sup.add( getScoreTemplate() );
		return sup;
	}

	void addOther( final String name )
	{
		System.out.println( "=== Adding listener for " + name );
		final AID other = new AID( name, false );
		others.put( name, other );
		OSCListener listener = new OSCListener()
		{
			public void acceptMessage( java.util.Date time, OSCMessage message )
			{
				//System.out.println("Message received for " + name );
				//for( Object s : message.getArguments() ) System.out.println( s + "");
				String msg = message.getArguments()[0] + "";
				String[] args = msg.split( ":" );
				if( args.length > 2 )
				{
					/*
					 * double xorig = Double.parseDouble( args[0] ); double
					 * yorig = Double.parseDouble( args[1] ); double x = ( xorig
					 * * 2.0 - 1.0 ) * ROOM_X; double y = ( yorig * 2.0 - 1.0 )
					 * * ROOM_Y; sendLocationUpdate( other, new double[] { x, y
					 * } );
					 */
					sendLocationUpdate( other, msg );
				} else
				{
					System.out.println( "Bad OSC message: " + msg );
				}
			}
		};
		receiver.addListener( "/" + name, listener );
	}

	/*
	 * public void sendLocationUpdate( AID aid, double[] coords ) {
	 * System.out.println( ">>>>> Sending location update to: " + aid );
	 * ACLMessage msg = new ACLMessage( INFORM ); msg.addReceiver( aid );
	 * msg.addUserDefinedParameter( TYPE_PARAM, LOCATION_TYPE); try {
	 * msg.setContentObject( coords ); send( msg ); } catch (IOException e) {
	 * System.err.println( "Could not serialise location: " + e ); } }
	 */
	public void sendLocationUpdate( AID aid, String location )
	{
		//System.out.println( ">>>>> Sending location update to: " + aid );
		ACLMessage msg = new ACLMessage( INFORM );
		msg.addReceiver( aid );
		msg.addUserDefinedParameter( TYPE_PARAM, LOCATION_TYPE );
		msg.setContent( location );
		send( msg );
	}

	public String[] getServiceNames()
	{
		return new String[] { MUSICIAN_SERVICE };
	}
}
