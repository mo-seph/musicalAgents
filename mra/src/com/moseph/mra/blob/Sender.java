package com.moseph.mra.blob;

import java.net.*;

import com.illposed.osc.*;


public class Sender
{
	OSCPortOut sender;
	
	public static void main( String[] args )
	{
		System.err.println( "Main" );
		System.out.println( "Main" );
		String hostname = "localhost";
		if( args.length > 0 ) hostname = args[0];
		System.out.println( "Making sender" );
		Sender s = new Sender( hostname );
		System.out.println( "Sending" );
		s.send();
	}
	
	public Sender( String host )
	{
		System.out.println( "Sender Starting" );
		try
		{
			System.out.println( "Connecting to " + host );
			sender = new OSCPortOut( InetAddress.getByName( host), 15000 );
			System.out.println( "OK" );
		}
		catch (Exception e1)
		{
			System.out.println( "Failed" );
			e1.printStackTrace();
		}
	}
	
	public void send()
	{
		Object args[] = new Object[2];
		args[0] = new Integer(3);
		args[1] = "hello";
		OSCMessage msg = new OSCMessage("/sayhello", args);
		try 
		{
			sender.send(msg);
		} 
		catch (Exception e) 
		{
			System.out.println("Couldn't send: " + e );
		}
	
	}

}
