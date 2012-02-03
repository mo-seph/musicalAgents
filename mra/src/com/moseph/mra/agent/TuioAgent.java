package com.moseph.mra.agent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.*;
import jade.wrapper.*;

import java.io.IOException;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.illposed.osc.*;
import com.moseph.mra.*;
import com.moseph.mra.agent.analyser.GroupNumericFeature;
import com.moseph.mra.agent.attribute.*;
import TUIO.*;

import static jade.lang.acl.ACLMessage.*;
import static java.lang.Math.random;
import static com.moseph.mra.MRAConstants.*;
import static com.moseph.mra.agent.AgentUtilities.*;

public class TuioAgent extends MusicallyAwareAgent implements TuioListener

{
	Map<Integer,AID> agents;
	TuioClient tuio;
	Map<AID,Long> lastSeenTimes = new ConcurrentHashMap<AID, Long>();
	Map<AID,Float[]> positions = new ConcurrentHashMap<AID,Float[]>();
	long delay = 1500;
	long lastRefresh = 0;
	long refreshInterval = 100;
	List<AID> comingOffBreak = new Vector<AID>();
	List<AID> killing = new Vector<AID>();
	
	
	public void initialise()
	{
		super.initialise();
		agents = new HashMap<Integer,AID>();
		tuio = new TuioClient( 3333 );
		tuio.addTuioListener( this );
		tuio.connect();
		addBehaviour( getUpdateFromMusicBehaviour() );
		addBehaviour( getFallthroughBehaviour() );
	}
	
	public void sendLocationUpdate( AID aid, String location )
	{
		//System.out.println( ">>>>> Sending location update to: " + aid );
		ACLMessage msg = new ACLMessage( INFORM );
		msg.addReceiver( aid );
		msg.addUserDefinedParameter( TYPE_PARAM, LOCATION_TYPE);
		msg.setContent( location );
		send( msg );
	}
	
	public String[] getServiceNames()
	{
		return new String[] { MUSICIAN_SERVICE };
	}


	public void addTuioObject( TuioObject object )
	{
		if( agents.containsKey( object.getSymbolID() ))
			setOnBreak( agents.get( object.getSymbolID() ), false );
		//else
			//addAgent( fiducial_id );
	}
	public void removeTuioObject( TuioObject  object )
	{
		//if( agents.containsKey( fiducial_id ))
			//setOnBreak( agents.get( fiducial_id), true );
		lastSeenTimes.put( agents.get( object.getSymbolID() ), System.currentTimeMillis() );
	}
	
	public void updateTuioObject( TuioObject object )
	{
		//if( agents.containsKey( fiducial_id ) && ! lastSeenTimes.containsKey( agents.get( fiducial_id )  ))
			//comingOffBreak.add( agents.get( fiducial_id ) );
		
		if( ! agents.containsKey( object.getSymbolID() ))
			addAgent( object.getSymbolID() );
		
		AID id = agents.get( object.getSymbolID() );
		lastSeenTimes.remove( id );
		positions.put( id, new Float[] { object.getX(), object.getY() } );
	}
	
	public void addTuioCursor( TuioCursor cursor ) {}
	public void updateTuioCursor( TuioCursor cursor ) {}
	public void removeTuioCursor( TuioCursor cursor ) {}

	public void refresh(TuioTime time ){}
	public void doUpdate()
	{
		long time = System.currentTimeMillis();
		if( time < lastRefresh + refreshInterval )
		{
			return;
		}
		//System.out.println( "******************\n*******************\nREFRESH\n\n");
		for( AID id : comingOffBreak )
		{
			System.out.println( "calling " + id + " off break\n");
			setOnBreak( id, false );
		}
		comingOffBreak.clear();
		for( AID id : lastSeenTimes.keySet() )
		{
			if( lastSeenTimes.get( id ) < time - delay )
			{
				System.out.println( "Sending " + id + " on break\n");
				killing.add( id );
			}
		}
		for( AID id : positions.keySet() )
		{
				Float[] loc = positions.get( id );
        		sendLocationUpdate( id, loc[0] + ":" + loc[1] );
		}
		positions.clear();
		for( AID id : killing )
		{
			setOnBreak( id, true );
			lastSeenTimes.remove( id );
		}
		killing.clear();
		
		lastRefresh = time;
	}

	public void addTuioCur( long session_id ) { }
	public void removeTuioCur( long session_id ) { 	}
	public void updateTuioCur( long session_id, float xpos, float ypos, float x_speed, float y_speed, float m_accel ) { }

		
	public void setOnBreak( AID aid, boolean onBreak )
	{
		String breakStr = onBreak ? BREAK_START : BREAK_END;
		System.out.println( ">>>>> Sending break request " + breakStr + " to: " + aid );
		ACLMessage msg = new ACLMessage( INFORM );
		if( aid != null )
			msg.addReceiver( aid );
		else
		{
			msg = getMessageToAllMusicians();
			msg.setPerformative( INFORM);
		}	
		msg.addUserDefinedParameter( TYPE_PARAM, BREAK_TYPE);
		msg.setContent( breakStr );
		send( msg );
	}

	void addAgent( int fiducial_id )
	{
		msg( "Adding agent");
		SpaceAgent sa = SpaceAgent.getInstance();
		if( sa != null )
		{
			AID id = sa.createNewAgent();
			if( id != null )
			{
				agents.put( fiducial_id, id );
    			System.out.println( "Agent created OK!");
			}
		}
	}
	
	void msg( String msg )
	{
		System.out.println( "****************************\n" + msg + "\n***********************'n");
	}
	
	Behaviour getUpdateFromMusicBehaviour()
	{
		return new BlockingReceiver( this, getScoreTemplate() )
		{
			void receivedMessage( ACLMessage m ) 
			{ 
				doUpdate();
			}
		};
	}
}
