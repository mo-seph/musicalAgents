package com.moseph.mra;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ChannelSet
{
	Map<String,Channel> channels;
	
	public ChannelSet()
	{
		channels = new Hashtable<String,Channel>();
	}
	
	public void add( Channel c )
	{
		if( channels.containsKey( c.getName() ) )
		{
			channels.get( c.getName() ).merge( c );
		}
		else
		{
			channels.put( c.getName(), c );
		}
	}
	
	public void merge( ChannelSet c )
	{
		for( Channel chan : c.getChannels() ) { add( chan ); }
	}
	
	public List<Channel> getChannels()
	{
		return new Vector<Channel>( channels.values() );
	}
	
	public static ChannelSet combine( ChannelSet a, ChannelSet b )
	{
		ChannelSet r = new ChannelSet();
		r.merge( a );
		r.merge( b );
		return r;
	}

	public Channel get( String name )
	{
			return channels.get( name );
	}
}
