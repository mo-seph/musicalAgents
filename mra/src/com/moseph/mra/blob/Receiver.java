package com.moseph.mra.blob;

import java.net.SocketException;

import com.illposed.osc.*;

public class Receiver
{
	OSCPortIn receiver;
	
	public static void main( String[] args )
	{
		System.out.println( "Reciever!");
		@SuppressWarnings("unused") Receiver r = new Receiver();
	}
	
	public Receiver()
	{
		try
		{
			receiver = new OSCPortIn(15000);
			OSCListener listener = new OSCListener() 
			{
				public void acceptMessage(java.util.Date time, OSCMessage message) 
				{
					System.out.println("Message received!");
					for( Object s : message.getArguments() ) System.out.println( s + "");
				}
			};
			receiver.addListener("/sayhello", listener);
			receiver.startListening();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
	}
}
