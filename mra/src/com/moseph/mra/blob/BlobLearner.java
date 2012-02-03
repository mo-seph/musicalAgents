package com.moseph.mra.blob;


import java.awt.*;
import java.awt.color.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import static java.lang.Math.*;

import javax.swing.*;

import com.illposed.osc.*;

public class BlobLearner
{
	Set<BlobParams> blobs = new HashSet<BlobParams>();
	OSCPortIn input;
	OSCPortOut output;
	double MIN_DIST = 0.05;
	public static double MIN_COLSEP = 0.4;
	double ALLOWED_DIFFERENCE = 1.5;
	BlobLearnerPanel blp;
	boolean learningPhase = true;
	public static final int LEARNING_LIFETIME = 2;
	public static final int PLAYING_LIFETIME = 2;
	public static final double UPDATE_DISTANCE = 0.02;
	public static final long UPDATE_PERIOD = 300;
	public static final double MOVEMENT_DECAY = 0.5;
	public static final double BUMP_MAX = 0.15;
	public static final double BUMP_MIN = 0.01;
	public static final int BUMP_INDEX_THRESHOLD = 5;
	
	Map<BlobParams,BlobParams> lastPosition = new HashMap<BlobParams,BlobParams>();
	Map<BlobParams,String> playerNames = new HashMap<BlobParams,String>();
	int destPort = 15001;
	static String destHost = "localhost";
	public static final int TEMP_INACTIVE_LEVEL = BlobParams.MAX_VITALITY / 5;
	double redScale = 1.0;
	double greenScale = 1.0;
	double blueScale = 1.0;
	List<BlobPair> collisions = new Vector<BlobPair>();
	BlobLearnerUI ui;
	JFrame window;
	
	public static void main( String[] args )
	{
		if( args.length > 0 ) destHost = args[0];
		BlobLearner b = new BlobLearner( 15000, true );
	}
	
	public BlobLearner( int port, boolean showGUI )
	{
		this( port );
		if( showGUI ) setupGUI();
	}
	public BlobLearner( int port )
	{
		try { input = new OSCPortIn( port ); }
		catch (SocketException e) { e.printStackTrace(); }
		OSCListener listen = new OSCListener()
		{
			public void acceptMessage( Date arg0, OSCMessage arg1 )
			{
				if( arg1.getArguments().length > 0 ) 
					parseMessageString( arg1.getArguments()[0] + "");
			}
		};
		
		input.addListener( "/blobParams", listen );
		input.startListening();
	}
	
	public void setupGUI()
	{
		window = new JFrame( "Blob Learner");
		window.setLayout( new BorderLayout() );
		blp = new BlobLearnerPanel( this );
		ui = new BlobLearnerUI( this );
		window.add( blp, BorderLayout.CENTER );
		window.add( ui, BorderLayout.EAST );
		window.setVisible( true );
		window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}
	
	public void parseMessageString( String msg )
	{
		String[] parts = msg.split( ":");
		try
		{
			String validity = parts[5];
			if( validity.equalsIgnoreCase( "Invalid")) return;
			
			double x = Double.parseDouble( parts[0]);
			double y = Double.parseDouble( parts[1]);
			double red = Double.parseDouble( parts[2]);
			double green = Double.parseDouble( parts[3]);
			double blue = Double.parseDouble( parts[4]);
			red = exp( log( red ) * getRedScale() );
			blue = exp( log( blue ) * blueScale );
			green = exp( log( green ) * greenScale );
			red = min( red, 1.0 );
			blue = min( blue, 1.0 );
			green = min( green, 1.0 );
			updateBlob( x, y, red, green, blue );
		}
		catch( Exception e )
		{
			System.err.println( "Bad osc message: " + msg + "\n" + e );
			return;
		}
}
	
	public void learningFinished()
	{
		input.stopListening();
		int index = 1;
		for( BlobParams blob : blobs )
		{
			blob.revitalise();
			BlobParams last = new BlobParams( blob );
			last.inactive = false;
			lastPosition.put( blob, last );
			playerNames.put( blob, "player" + index );
			index++;
		}
		learningPhase = false;
		input.startListening();
		try
		{
			startSending();
		}
		catch (Exception e)
		{
			System.out.println( "Problem sending agent params: " + e );
			e.printStackTrace();
		}
		if( ui != null ) window.remove( ui );
		resetCollisions();
		window.validate();
		window.repaint();
	}
	
	public void resetCollisions()
	{
		for( BlobParams blob : blobs ) blob.collision = false;
			collisions.clear();
	}
	
	void startSending() throws Exception
	{
		System.out.println( "--- Sending out agent params");
		InetAddress dest;
		dest = InetAddress.getByName( destHost );
		output = new OSCPortOut( dest, destPort );
		for( BlobParams blob : blobs )
		{
			String bS = playerNames.get(blob) + ":" + 
						blob.xpos + ":" +
						blob.ypos + ":" +
						blob.red + ":" +
						blob.green + ":" +
						blob.blue + ":" ;
			
			System.out.println( "- " + bS );
			OSCMessage m = new OSCMessage( "/agentParam", new Object[] { bS } );
			output.send( m );
		}
		OSCMessage done = new OSCMessage( "/start" );
		output.send( done);
		
		System.out.println( "Starting timer");
		TimerTask sendUpdate = new TimerTask()
		{
			public void run()
			{
				sendOutUpdates();
				updateVitalityPlaying();
			}
		};
		Timer timer = new Timer();
		timer.schedule( sendUpdate, UPDATE_PERIOD * 2, UPDATE_PERIOD );
	}
	
	void sendOutUpdates()
	{
		for( BlobParams blob : blobs )
		{
			blobDecay( blob );
			if( hasChanged( blob ) )
			{
				lastPosition.put( blob, new BlobParams( blob ) );
				sendMessage( blob );
			}
		}
	}
	
	boolean hasChanged( BlobParams blob )
	{
		//return true;
		BlobParams last = lastPosition.get( blob );
		if( blob.inactive != last.inactive ) return true;
		if( blob.inactive ) return false;
		if( blob.getPhysicalDistance( last ) > UPDATE_DISTANCE && ! blob.inactive ) return true;
		return false;
	}
	
	void blobDecay( BlobParams blob )
	{
		blob.messaged--;
		blob.bumped--;
		if( blob.bumpIndex >= BUMP_INDEX_THRESHOLD  )
		{
			blob.bumped = 3;
			blob.bumpIndex = 0;
		}
		blob.movement *= MOVEMENT_DECAY;
	}
	
	void sendMessage( BlobParams blob )
	{
		blob.messaged = 2;
		String name = playerNames.get( blob );
		String activity = blob.inactive ? "inactive" : blob.movement + "";
		if( blob.bumped > 1 ) activity = "bump";
		String message = blob.xpos + ":" + blob.ypos + ":" + activity;
		//System.out.println( ">>> Sending update for " + name + ": " + message );
		try
		{
			output.send( new OSCMessage( "/" + name, new Object[] { message }));
		}
		catch( Exception e )
		{
			System.err.println( "Couldn't send message: " + e );
		}
	}
	
	void addBlob( BlobParams p ) 
	{ 
		blobs.add( p ); 
		updateCollisions( p );
	}
	
	public void updateBlob( double x, double y, double red, double green, double blue )
	{
		updateBlob( new BlobParams( x, y, red, green, blue ));
	}
	
	public void updateBlob( BlobParams blob )
	{
		if( learningPhase )
		{
			learnBlob( blob );
			updateVitalityLearning();
		}
		else 
		{
			blobMessage( blob );
		}
	}
	
	void learnBlob( BlobParams newBlob )
	{
		for( BlobParams blob : blobs )
		{
			if( blob.getPhysicalDistance( newBlob ) < MIN_DIST ) 
			{
				mergeTo( newBlob, blob );
				return;
			}
		}
		addBlob( newBlob );
	}
	
	void blobMessage( BlobParams input )
	{
		BlobParams closest = null;
		double distance = Double.MAX_VALUE;
		//Find the existing blob with the closest color
		for( BlobParams existing : blobs )
		{
			double thisDist = input.getColorDistance( existing );
			if( thisDist < distance )
			{
				distance = thisDist;
				closest = existing;
			}
		}
		//If this is close enough, set the params to the new blob + revitalise
		if( distance < MIN_COLSEP * ALLOWED_DIFFERENCE )
		{
			double distMoved = closest.getPhysicalDistance( input );
			closest.movement += distMoved;
			if( isBumpMove( distMoved ) ) closest.bumpIndex++;
			else closest.bumpIndex--;
			if( closest.bumpIndex < 0 ) closest.bumpIndex = 0;
			//System.out.println( distMoved + ": " + closest.bumpIndex  );
			closest.xpos = input.xpos;
			closest.ypos = input.ypos;
			closest.revitalise();
		}
		//Otherwise, discard
		else
		{
			System.out.println( "Unknown blob: " + input );
		}
	}
	
	boolean isBumpMove( double dist )
	{
		if( dist > BUMP_MAX ) return false;
		if( dist < BUMP_MIN ) return false;
		return true;
	}
	
	void mergeTo( BlobParams newBlob, BlobParams existing )
	{
		existing.averageWith( newBlob );
		updateCollisions( existing );
	}
	
	void updateVitalityLearning()
	{
		int diff = BlobParams.MAX_VITALITY / ( LEARNING_LIFETIME * blobs.size() );
		Vector<BlobParams> dead = new Vector<BlobParams>();
		for( BlobParams blob : blobs )
		{
			blob.vitality -= diff;
			if( blob.vitality < 0 ) dead.add( blob );
		}
		for( BlobParams b : dead ) blobs.remove( b );
		if( dead.size() > 0 ) recalcCollisions();
	}
	
	void updateVitalityPlaying()
	{
		//int diff = BlobParams.MAX_VITALITY / ( PLAYING_LIFETIME * blobs.size() );
		int diff = BlobParams.MAX_VITALITY / ( PLAYING_LIFETIME );
		for( BlobParams blob : blobs )
		{
			if( blob.vitality > 0 ) blob.vitality -= diff;
			if( blob.vitality <= 0 ) blob.inactive = true;
			if( blob.vitality < TEMP_INACTIVE_LEVEL ) blob.tempInactive = true;
		}
	}
	
	void updateCollisions( BlobParams p )
	{
		resetCollisions();
		for( BlobParams blob : blobs )
			checkCollision( p, blob );
	}
	
	void checkCollision( BlobParams a, BlobParams b )
	{
		if( a == b ) return;
		if( a.getColorDistance( b ) < MIN_COLSEP )
		{
			a.collision = true;
			b.collision = true;
			collisions.add( new BlobPair( a, b ));
		}
	}
	
	void recalcCollisions()
	{
		for( BlobParams b : blobs ) b.collision = false;
		Vector<BlobParams> blist = new Vector<BlobParams>( blobs );
		for( int i = 0; i < blist.size() - 1; i++ )
			for( int j = i + 1; j < blist.size(); j++ )
				checkCollision( blist.get( i ), blist.get(j));
	}

	public List<BlobParams> getBlobs()
	{
		return new Vector<BlobParams>( blobs );
	}

	public Map<BlobParams, String> getPlayerNames()
	{
		return playerNames;
	}

	public void setBlueScale( double blueScale )
	{
		this.blueScale = blueScale;
	}

	public void setGreenScale( double greenScale )
	{
		this.greenScale = greenScale;
	}

	public void setRedScale( double redScale )
	{
		System.out.println( "Setting red to " + redScale );
		this.redScale = redScale;
	}

	public double getRedScale()
	{
		return redScale;
	}

	public double getBlueScale()
	{
		return blueScale;
	}

	public double getGreenScale()
	{
		return greenScale;
	}
	
	public class BlobPair
	{
		BlobParams b1;
		BlobParams b2;
		public BlobPair( BlobParams b1, BlobParams b2 )
		{
			this.b1 = b1;
			this.b2 = b2;
		}
	}

	public List<BlobPair> getCollisions()
	{
		return collisions;
	}

}
