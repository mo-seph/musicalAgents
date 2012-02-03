package com.moseph.mra;

import jade.content.*;
import jade.core.AID;

public class Play implements AgentAction
{
	Path path;
	public Path getPath()
	{
		return path;
	}
	public void setPath( Path path )
	{
		this.path = path;
	}
}
