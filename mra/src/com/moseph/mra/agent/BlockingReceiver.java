package com.moseph.mra.agent;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

/**
A {@link Behaviour} which waits until it gets a certain message and then does something.
This is an abstract class - the <code>receivedMessage( ACLMessage message)</code>
method must be overidden to add the action taken when a message is
received. The <code>BlockingReceiver( Agent a )</code> constructor
provides a Behaviour which matches any message, or a
{@link MessageTemplate} may be passed in to specify a message
template
@author David Murray-Rust
@version $Revision$, $Date$
*/
public abstract class BlockingReceiver extends SimpleBehaviour
{

	MessageTemplate msgTemplate;
	String name = "Unknown";

	/**
	Full constructor
	@param a the parent {@link Agent}
	@param m the kind of message to match
	*/
	public BlockingReceiver( Agent a, MessageTemplate m )
	{
		super( a );
		msgTemplate = m;
	}

	/**
	Matches any message
	@param a
	*/
	public BlockingReceiver( Agent a )
	{
		super( a );
		msgTemplate = MessageTemplate.MatchAll();
	}


	public void action()
	{
		ACLMessage message = myAgent.receive( msgTemplate );
		if( message != null )
		{
			receivedMessage( message );
		}
		else
		{
			block();
			return;
		}
	}

	public boolean done()
	{
		return false;
	}

	public void setName( String s )
	{
		name = s;
	}

	/**
	called when a message is received with the apropriate message
	@param message;
	*/
	abstract void receivedMessage( ACLMessage message );
}

