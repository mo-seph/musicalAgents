package com.moseph.mra.agent.gui;


import jade.core.AID;

import static java.lang.Math.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Timer;

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
import com.moseph.mra.agent.analyser.GroupNumericFeature;
import com.moseph.mra.agent.attribute.PathFeature;

public class VirtualSpace extends JPanel implements Runnable
{
	boolean DOUBLE_BUFFER = false;
	Object ANTI_ALIAS = RenderingHints.VALUE_ANTIALIAS_ON;
	SpaceAgent myAgent;
	List<PartIndex> musicians = new Vector<PartIndex>();
	//Map<String,MusicianInformation> information = new HashMap<String,MusicianInformation>();
	public static final int personWidth = 60;
	public static final int personHeight = 60;
	int fullWidth = 0;
	int fullHeight = 0;
	int offset = 30;
	BufferedImage bi = null;
	//double average = 0.0;
	double maxSpread = 3.0;
	boolean mouseDown = false;
	int mouseX, mouseY;
	MusicianInformation movingAgent = null;
	Context context;
	MusicianInformation menuAgent = null;
	JPopupMenu musMenu;
	JPopupMenu backMenu;
	double linewidth = 8.0;
	Stroke thick = new BasicStroke( (float)linewidth );
	Stroke medium = new BasicStroke( (float)linewidth / 2 );
	Stroke thin;
	int barWidth = 10;
	int barHeight = 40;
	List<Path> leafNames;
	Path currentAveragePath;
	JMenuItem addCurrent;
	public static final int MARGIN = personWidth + 10 ;
	List<MusicianInformation> inactive = new Vector<MusicianInformation>();
	boolean dirty = false;
	
	public VirtualSpace( SpaceAgent s, Context context )
	{
		this.context = context;
		myAgent = s;
		setBackground( Color.black );
		setDoubleBuffered( true );
		addMouseListener( getMouseListener() );
		addMouseMotionListener( getMouseMotionListener() );
		leafNames = new Vector<Path>( context.getPiece().getLeafPaths() );
		initAgentPopup();
		initBackgroundPopup();
		Thread th = new Thread( this );
		th.start();
	}
	
	public void run()
	{
		TimerTask t = new TimerTask()
		{
			public void run()
			{
				if( dirty )
				{
					repaint();
					dirty = false;
				}
			}
		};
		Timer timer = new Timer();
		timer.schedule( t, 1300, 300 );
	}
	
	void initAgentPopup()
	{
		musMenu = new JPopupMenu( "Agent Actions");
		Action kill = new AbstractAction( "Kill")
		{
			public void actionPerformed( ActionEvent e )
			{
				myAgent.killAgent( menuAgent.getAid() );
			}
		};
		Action startBreak = new AbstractAction( "Send on break")
		{
			public void actionPerformed( ActionEvent e )
			{
				myAgent.setOnBreak( menuAgent.getAid(), true );
			}
		};
		Action endBreak = new AbstractAction( "Recall from break")
		{
			public void actionPerformed( ActionEvent e )
			{
				myAgent.setOnBreak( menuAgent.getAid(), false );
			}
		};
		Action bump = new AbstractAction( "Bump!")
		{
			public void actionPerformed( ActionEvent e )
			{
				myAgent.bumpAgent( menuAgent.getAid());
			}
		};
		musMenu.add( kill );
		musMenu.add( startBreak );
		musMenu.add( endBreak );
		musMenu.add( bump );
	}
	
	void initBackgroundPopup()
	{
		backMenu = new JPopupMenu( "Create agents");
		JMenu pathAdding = new JMenu( "Start agent at...");
		for( final Path p : context.getPiece().getLeafPaths( ) )
		{
			Action a = new AbstractAction( p.trimmedPath() )
			{
				public void actionPerformed( ActionEvent e )
				{
					myAgent.createNewAgent( p.toString() );
				}
			};
			pathAdding.add( a );
			
		}
		Action b = new AbstractAction( "Add Average")
		{
			public void actionPerformed( ActionEvent e )
			{
				if( currentAveragePath != null )
					myAgent.createNewAgent( currentAveragePath.toString() );
				else
					myAgent.createNewAgent( leafNames.get( 0 ).toString() );
			}
		};
		addCurrent = new JMenuItem( b );
		backMenu.add( pathAdding );
		backMenu.add( addCurrent );
		Action allBreak = new AbstractAction( "Send Everyone on break" )
		{
			public void actionPerformed( ActionEvent e )
			{
				myAgent.sendAllOnBreak();
			}
		};
		backMenu.add( new JMenuItem( allBreak ) );
		
		Action allReturn = new AbstractAction( "Recall everyone" )
		{
			public void actionPerformed( ActionEvent e )
			{
				myAgent.recallAll();
			}
		};
		backMenu.add( new JMenuItem( allReturn ) );
		
}
	
	public void updateScore( Score s  )
	{
		for( Fragment f : s.fragments() )
		{
			String name = f.getPartIndex().getMusician().getName();
			
			MusicianInformation inf = context.getMusicianInformation( name );
			inf.setMusician( f.getPartIndex().getMusician() );
			inf.setInstrument( f.getPartIndex().getInstrument() );
		}
		dirty = true;
	}
	
	public void updatePath( AID aid, String name, Path p )
	{
		updateAverageCreation();
		if( isInactivePath( p ) ) setInactive( context.getMusicianInformation( aid ));
		else setActive( context.getMusicianInformation( aid ));
	}
	
	boolean isInactivePath( Path p )
	{
		if( p == null ) return true;
		if( p.toString().equals( "/break")) return true;
		if( p.getIndex( context.getPiece() ) < 0 ) return true;
		return false;
	}
	
	void updateAverageCreation()
	{
		GroupNumericFeature paths = (GroupNumericFeature)context.getFeature( "Path");
		paths.setSilent( true );
		int pathIndex = (int)paths.getAverageValue();
		if( pathIndex > 0 && pathIndex < leafNames.size() )
			currentAveragePath = leafNames.get( pathIndex );
		if( addCurrent != null && currentAveragePath != null )
			addCurrent.setText( "Create agent playing " + currentAveragePath.toString() );
	}
	
	public void removeAgent( AID mus )
	{
		//context.retireMusician( mus );
		//repaint();
		dirty = true;
	}
	
	public void paint( Graphics g )
	{
		Dimension d = getSize();
		Graphics2D bg = (Graphics2D)g;
		if( DOUBLE_BUFFER )
		{
			if( bi == null || d.width != fullWidth || d.height != fullHeight )
				bi = (BufferedImage)createImage( d.width, d.height );
			bg = (Graphics2D)bi.createGraphics();
		}
		fullWidth = d.width;
		fullHeight = d.height;
		if( thin == null ) thin = bg.getStroke();
		
		//Start with black
		bg.setColor( Color.black );
		bg.fillRect( 0, 0, fullWidth, fullHeight );
		
		bg.setRenderingHint( RenderingHints.KEY_ANTIALIASING, ANTI_ALIAS );
		
		drawBoard( bg );
		for( MusicianInformation p : context.getActiveMusicians() )
		{
			if( movingAgent != null && movingAgent == p  ) drawMusicianShell( bg, p );
			else drawMusician( bg, p );
		}
		for( MusicianInformation p : context.getActiveMusicians() )
		{
			if( movingAgent != null && movingAgent == p  ) continue;
			drawMusicianInfo( bg, p );
		}
		drawMovingMusician( bg );
		if( DOUBLE_BUFFER ) g.drawImage( bi, 0, 0, this );
	}
	
	void drawBoard( Graphics2D g )
	{
		Dimension d = getSize();
		Dimension board = getBoardSize();
		g.setColor( Color.darkGray );
		g.fillRect( board.width, 0, d.width - board.width, d.height);
	}
	
	void drawMusicianInfo( Graphics2D g, MusicianInformation info )
	{
		g.setColor( Color.green );
		g.setStroke( thin );
		Dimension loc = getMusicianCoords( info );
		int ind = 1;
		int x = loc.width - personWidth / 2;
		int y = loc.height + personWidth / 2;
		for( String s : info.details() )
		{
			g.drawString( s, x, y + ( 15 * ind++));
		}
		
	}
	
	void drawMusician( Graphics2D g, MusicianInformation info )
	{
		//Setup
		boolean inactive = isInactive( info );
		g.setStroke( thick);
		g.setColor( getColor( info ));
		Dimension loc = getMusicianCoords( info );
		int x =loc.width - personWidth/2;
		int y =loc.height - personHeight/2;
		
		//Draw main shell
		if( inactive )
			g.drawOval( x, y, personWidth, personHeight );
		else
			g.fillOval( x, y, personWidth, personHeight );
		
		//Draw path and pull bar
		if( ! inactive )
		{
			
			double pathProp = getPathDifference( info );
			g.setColor( getPathColor( pathProp ));
			int lw = (int)(2*linewidth);
			pathProp = min( 1.0, max( -1.0, pathProp ));
			g.drawArc( x-lw, y-lw, personWidth+2*lw, personHeight+2*lw, 90, (int)( -pathProp * 90 ));
			
			double pull = info.getMusician().getPull();
			g.setColor( getPathColor( pull ));
			g.setStroke( medium );
			//pull = min( 1.0, max( -1.0, pull ));
			int delta = (int)(linewidth);
			g.drawArc( x-delta, y-delta, personWidth+2*delta, personHeight+2*delta, 90, (int)( -pull * 90 ));
		}		
		
		//Draw dynamics and density markers
		if( ! inactive )
		{
			double[] dandd = getDensityAndDynamic( info );
			if( dandd[0] > 0.01 ) drawBar( g, x, y + personHeight, dandd[0], Color.red );
			if( dandd[1] > 0.01) drawBar( g, x + barWidth, y + personHeight, dandd[1], Color.ORANGE );
		}
}
	
	void drawBar( Graphics2D g, int x, int y, double proportion, Color c )
	{
		int barLength = (int)( barHeight * proportion );
		g.setColor( Color.BLACK );
		g.fillRect( x - barWidth, y - barLength - 2, barWidth, barLength + 2 );
		g.setColor( c );
		g.fillRect( x - barWidth, y - barLength, barWidth, barLength );
	}
	
	void drawMusicianShell( Graphics2D g, MusicianInformation info )
	{
			g.setColor( Color.lightGray );
			g.setStroke( thick );
			Dimension loc = getMusicianCoords( info );
			g.drawOval( loc.width - personWidth/2, loc.height- personWidth / 2, personWidth, personHeight );
	}
	
	void drawMovingMusician( Graphics g  )
	{
		g.setColor( new Color( 200, 200, 200, 100));
		if( movingAgent != null )
		{
			g.fillOval( mouseX - personWidth / 2, mouseY - personHeight / 2, personWidth, personHeight  );
		}
	}
	
	MusicianInformation getMusicianForPoint( int x, int y )
	{
		for( MusicianInformation inf : context.getActiveMusicians() )
		{
			Dimension d = getMusicianCoords( inf );
			if( d.width - personWidth / 2 < x && 
					d.width + personWidth / 2 > x &&
					d.height - personHeight / 2 < y &&
					d.height + personHeight / 2 > y  )
				return inf;
		}
		return null;
	}
	
	Color getColor( MusicianInformation info )
	{
		if( info.getColour() != null ) return info.getColour();
		try
		{
			double pathProp = getPathDifference( info );
			return  getPathColor( 2 * pathProp );
		}
		catch( Exception e ) { e.printStackTrace();		}
		return Color.CYAN;
	}
	
	/**
	 * Returns the path difference, where -1 means maxSpread behind and
	 * +1 means maxSpread ahead
	 * @param info
	 * @return
	 */
	double getPathDifference( MusicianInformation info )
	{
		GroupNumericFeature paths = (GroupNumericFeature)context.getFeature( "Path" );
		double pathIndex = paths.getNumericValue( info );
		PathFeature pf = ((PathFeature)paths.getValue( info ));
		Path p = null;
		if( pf != null ) p = pf.getPath();
		if( p == null ) return Double.MIN_VALUE;
		if( p.toString().equals( "/break")) return Double.MIN_VALUE;
		if( pathIndex < 0 ) return Double.MIN_VALUE;
		double difference = ( (double) pathIndex - paths.getAverageValue() )/maxSpread;
		return difference;
	}
	
	double[] getDensityAndDynamic( MusicianInformation info )
	{
		try
		{
		GroupNumericFeature density = (GroupNumericFeature)context.getFeature( "Density" );
		double densityVal = density.getNumericValue( info );
		GroupNumericFeature dynamic = (GroupNumericFeature)context.getFeature( "Dynamics" );
		double dynamicVal = dynamic.getNumericValue( info );
		return new double[] { densityVal, dynamicVal };
		}
		catch( Exception e )
		{
			System.err.println( "COuldn't get density and dynamics: " + e );
			return new double[] { 0.0, 0.0 };
		}
	}
	
	Color getPathColor( double pathDifference )
	{
		if( pathDifference == Double.MIN_VALUE ) return Color.MAGENTA;
		if( pathDifference == Double.MIN_VALUE ) return Color.PINK;
		//Now in the range 0-1 ( from -maxSpread to +maxSpread
		double diffFactor = ( pathDifference + 1 ) / 2;
		int diffColor = (int)(diffFactor * 510 );
		//System.out.println( "Diff factor: " + diffFactor + ", diff Colour: " + diffColor  );
		if( diffColor < 0 ) diffColor = 0;
		if( diffColor > 510 ) diffColor = 510;
		if( diffColor > 255 ) return new Color( 510 - diffColor, 255, 0 );
		return new Color( 255, diffColor, 0 );
	}
	
	boolean isInactive( MusicianInformation info )
	{
		return inactive.contains( info );
	}
	
	void setActive( MusicianInformation info )
	{
		inactive.remove( info );
	}
	void setInactive( MusicianInformation info )
	{
		inactive.add( info );
	}
	Dimension getMusicianCoords( MusicianInformation info )
	{
		Dimension d = getBoardSize();
		Musician m = info.getMusician();
		if( isInactive( info ) )
		{
			int index = inactive.indexOf( info );
			return new Dimension( d.width + offset, index * personHeight + offset );
		}
		int width = d.width - 2*offset;
		int height = d.height - 2*offset;
		int x = (int)(0.5 * width );
		int y = (int)(0.5 * width );
		//String inst = "unknown";
		if( m != null )
		{
			x = (int)(( m.getX() / ROOM_X + 1.0 )* 0.5  * width );
			y = (int)(( m.getY() / ROOM_Y + 1.0 ) * 0.5  * height );
		}			
		return new Dimension( x + offset, y + offset );
	}
	
	Dimension getBoardSize()
	{
		Dimension d = getSize();
		d.width -= MARGIN;
		return d;
	}
	
	double[] getSpaceCoords( int x, int y )
	{
		Dimension d = getBoardSize();
		int width = d.width - 2*offset;
		int height = d.height - 2*offset;
		double cx = ( (double)x - offset ) / width;
		double cy = ( (double)y - offset ) / height;
		return new double[] { cx, cy };
	}
	
	public MouseListener getMouseListener()
	{
		final VirtualSpace space = this;
		return new MouseAdapter()
		{
			public void mousePressed( MouseEvent e )
			{
				if( SwingUtilities.isLeftMouseButton( e ) )
				{
					if( ! mouseDown )
					{
						//Capture an agent if possible
						movingAgent = getMusicianForPoint( e.getX(), e.getY() );
					}			
					mouseX = e.getX();
					mouseY = e.getY();
					mouseDown = true;
				}
				else if( SwingUtilities.isRightMouseButton( e ) )
				{
					menuAgent =  getMusicianForPoint( e.getX(), e.getY() );
					if( menuAgent != null ) musMenu.show( space, e.getX(), e.getY() );
					else backMenu.show( space, e.getX(), e.getY() );
				}
			}
			
			public void mouseReleased( MouseEvent e )
			{
				if( movingAgent != null )
				{
					//Send a message to the agent
					
					//double[] location = getSpaceCoords( e.getX(), e.getY() );
					myAgent.sendLocationUpdate( movingAgent.getAid(), getSpaceCoords( e.getX(), e.getY() ));
					movingAgent = null;
				}
				mouseDown = false;
			}
		};
	}

	MouseMotionListener getMouseMotionListener()
	{
		return new MouseMotionAdapter()
		{
			public void mouseDragged( MouseEvent e )
			{
				
				mouseX = e.getX();
				mouseY = e.getY();
			}
			
		};
	}
}
