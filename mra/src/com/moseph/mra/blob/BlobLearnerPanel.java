package com.moseph.mra.blob;

import java.awt.*;
import java.util.*;
import static java.lang.Math.*;

import javax.swing.JPanel;

import com.moseph.mra.blob.BlobLearner.BlobPair;

public class BlobLearnerPanel extends JPanel
{
	BlobLearner learner;
	int blobSize = 60;
	double xsize;
	double ysize;
	int inactiveIndex = 0;
	int linewidth = 4;
	Stroke wideStroke = new BasicStroke( (float)linewidth);
	Stroke thinStroke = new BasicStroke( (float)2);
	
	public BlobLearnerPanel( BlobLearner learner )
	{
		this.learner = learner;
		TimerTask redraw = new TimerTask()
		{
			public void run()
			{
				repaint();
			}
		};
		Timer timer = new Timer();
		timer.schedule( redraw, 1000, 500 );
		
	}
	
	public void paint( Graphics g )
	{
		Graphics2D g2 = (Graphics2D)g;
		inactiveIndex = 0;
		g2.setColor( Color.black );
		g2.fillRect( 0, 0, getWidth(), getHeight() );
		xsize = (double)( getWidth() - blobSize * 2 );
		ysize = (double)( getHeight() - blobSize * 2 );
		
		paintInfo( g2 );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		//try
		//{
			for( BlobPair collision : new Vector<BlobPair>( learner.getCollisions() ) ) paintCollision( collision, g2 );
		//}
		//catch( ConcurrentModificationException e ) { }//Array was cleared while we're iterating 
		for( BlobParams blob : learner.getBlobs() ) paintBlob( blob, g2, learner.getPlayerNames() );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
		g2.setStroke( new BasicStroke( (float)linewidth ) );
		g.setColor( Color.GRAY );
		g.drawLine( getWidth() - blobSize, 0, getWidth() - blobSize, getHeight() );
		
	}
	
	void paintInfo( Graphics2D g )
	{
		g.setColor( Color.green );
		String colString = 
			"Red: " + learner.getRedScale() + 
			", Green: " + learner.getGreenScale() +
			", Blue: " + learner.getBlueScale() +
			", MinSep: " + learner.MIN_COLSEP;
		g.drawString( colString, 0, getHeight() );
	}
	
	void paintCollision( BlobPair coll, Graphics2D g )
	{
		int[] pos1 = blobToPixel( coll.b1, blobSize / 2 );
		int[] pos2 = blobToPixel( coll.b2, blobSize / 2 );
		if( coll.b1.getColorDistance( coll.b2 ) < BlobLearner.MIN_COLSEP / 2 )
			g.setStroke( wideStroke );
		else
			g.setStroke( thinStroke );
		g.setColor( Color.red );
		g.setColor( coll.b2.getColor() );
		g.drawLine( pos1[0], pos1[1], pos2[0], pos2[1]);
	}
	
	int[] blobToPixel( BlobParams b )
	{
		return blobToPixel( b, 0 );
	}
	
	int[] blobToPixel( BlobParams b, int offset )
	{
		int x = (int)( b.xpos * xsize ) + offset;
		int y = (int)( b.ypos * ysize ) + offset;
		return new int[] { x, y };
	}


	void paintBlob( BlobParams blob, Graphics2D g, Map<BlobParams,String> names )
	{
		Stroke defaultStroke = g.getStroke();
		int textHeight = g.getFontMetrics().getHeight();
		int lw = linewidth;
		//float[] rgb = hsvToRGB( blob.red, blob.blue );
		//System.out.printf( "Color: %f, %f, %f\n", rgb[0], rgb[1], rgb[2]);
		float vital = (float)( blob.vitality + 200 ) / (float) BlobParams.MAX_VITALITY;
		vital = (float)min( 1.0, (double)vital );
		//Color c = new Color( rgb[0], rgb[1], rgb[2], vital );
		Color c = new Color( (float)blob.red, (float)blob.green, (float)blob.blue );
		g.setColor( c );
		int[] pos = blobToPixel( blob );
		int x = pos[0];
		int y = pos[1];
		if( blob.inactive )
		{
			x = (int)( xsize + blobSize );
			y = blobSize * inactiveIndex++;
			g.fillOval( x, y, blobSize, blobSize );
			if( names.containsKey( blob ))
			{
				g.setStroke( defaultStroke );
				
				g.setColor( Color.black );
				g.fillRect( x + 5, y + blobSize/2 - textHeight/2, blobSize - 10, textHeight + 2 );
				g.setColor( Color.green );
				g.drawString( names.get( blob ), x + 6, y + blobSize / 2 + (int)(textHeight * 0.5) );
			}
			return;
		}
		g.setStroke( wideStroke );
		g.drawOval( x, y, blobSize, blobSize );
		int inSize = (int)((double)blobSize * vital );
		int xi = x + ( blobSize - inSize + 1) / 2;
		int yi = y + ( blobSize - inSize + 1 ) / 2;
		g.setStroke( defaultStroke );
		g.fillOval( xi, yi, inSize, inSize );
		if( blob.collision )
		{
			g.setStroke( wideStroke );
			g.setColor( new Color( 1.0f, 0.0f, 0.0f, vital ) );
			g.drawRect( x - lw, y - lw, blobSize + 2 * lw, blobSize  + 2 * lw);
		}
		if( blob.tempInactive )
		{
			g.setStroke( wideStroke );
			g.setColor( Color.red );
			g.drawLine( x, y, x + blobSize, y + blobSize );
			g.drawLine( x + blobSize, y, x, y + blobSize );
		}
		if( names.containsKey( blob ))
		{
			g.setStroke( defaultStroke );
			g.setColor( Color.green );
			g.drawString( names.get( blob ), x, y + blobSize + textHeight );
		}
		if( blob.messaged > 0 )
		{
			g.setStroke( wideStroke );
			g.setColor( Color.GREEN );
			g.drawRect( x + 5, y + 5, blobSize - 10, blobSize - 10 );
			blob.messaged--;
		}
		if( blob.movement > 0 )
		{
			g.setStroke( wideStroke );
			g.setColor( Color.ORANGE );
			g.drawArc( x-lw, y-lw, blobSize+2*lw, blobSize+2*lw, 180, (int)(blob.movement * -360 ) );
		}
		
		if( blob.bumped > 0 )
		{
			g.setStroke( wideStroke );
			g.setColor( Color.GREEN );
			g.drawOval( x + 10, y + 10, blobSize - 20, blobSize - 20 );
		}
		if( blob.bumpIndex > 0 )
		{
			g.setStroke( defaultStroke );
			g.setColor( Color.GREEN );
			for( int i = 0; i < blob.bumpIndex; i++ )
				g.fillOval( x + i * 10, y, 10, 10 );
		}
	}

}	
