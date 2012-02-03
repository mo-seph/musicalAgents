package com.moseph.mra.visualise;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import com.moseph.mra.Piece;
import com.moseph.mra.Section;

public class PieceVisualiser extends UnitVisualiser
{	
	public PieceVisualiser()
	{
		super();
	}

	
	void dataFromObject( )
	{
		System.out.println( "Piece Visualiser visualising " + data );
		layoutPiece();
	}
	
	void layoutPiece()
	{
		if( data == null || !( data instanceof Piece ))return;
		Piece piece = (Piece)data;
		setLayout( new BorderLayout() );
		JTabbedPane pane = new JTabbedPane();
		JTabbedPane sections = new JTabbedPane();
		for( Section s : piece.getSections() )
		{
			System.out.println( "Adding section" + s.getName() );
			sections.add( s.getName(), MRAVisualiser.getVisualiser(s) );
		}
		pane.add( "Sections", sections );
		pane.add( "Behaviours", getListPanel( piece.getBehaviours(), null ) );
		pane.add( "Decisions", getListPanel( piece.getDecisions(), null ) );
		pane.add( "Actions", getListPanel( piece.getActions(), null ) );
		add( pane, BorderLayout.CENTER );
	}
	
}
