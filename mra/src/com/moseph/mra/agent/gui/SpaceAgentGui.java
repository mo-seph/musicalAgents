package com.moseph.mra.agent.gui;


import jade.core.AID;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.*;

import static com.moseph.mra.MRAConstants.*;
import com.moseph.mra.Fragment;
import com.moseph.mra.Instrument;
import com.moseph.mra.Musician;
import com.moseph.mra.PartIndex;
import com.moseph.mra.Path;
import com.moseph.mra.Piece;
import com.moseph.mra.Score;
import com.moseph.mra.agent.*;
import com.moseph.mra.blob.BlobParams;

public class SpaceAgentGui extends JFrame
{
	SpaceAgent myAgent;
	List<PartIndex> musicians = new Vector<PartIndex>();
	Map<String,MusicianInformation> information = new HashMap<String,MusicianInformation>();
	int personWidth = 50;
	int personHeight = 30;
	int fullWidth = 0;
	int fullHeight = 0;
	int offset = 30;
	BufferedImage bi = null;
	Piece piece;
	double average = 0.0;
	double maxSpread = 1.0;
	boolean mouseDown = false;
	int mouseX, mouseY;
	MusicianInformation movingAgent = null;
	Context context;
	VirtualSpace space;
	//ConductorInterface controls;
	
	public SpaceAgentGui( SpaceAgent s, Context context )
	{
		super( "Agent System: " + context.getPiece().getName() );
		myAgent = s;
		this.context = context;
		space = new VirtualSpace(s, context);
		setLayout( new BorderLayout() );
		add( space, BorderLayout.CENTER );
		setSize( 1000, 800 );
		//controls = new ConductorInterface( s, context );
		//add( controls, BorderLayout.EAST );
	}
	

	
	public void updateScore( Score s  )
	{
		space.updateScore( s );
	}
	
	public void updatePath( AID aid, String name, Path p )
	{
		space.updatePath( aid, name, p );
		//controls.updatePath();
	}
	
	public void removeAgent( AID aid )
	{
		space.removeAgent( aid );
	}
}
