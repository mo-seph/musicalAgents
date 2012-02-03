package com.moseph.mra.visualise;

import com.moseph.mra.MRAUtilities;
import com.moseph.mra.NamedSymbol;
import com.moseph.mra.Unit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.moseph.mra.Section;
import com.moseph.mra.logic.ExpressionTerm;
import com.moseph.mra.parser.MRAParser;
import com.moseph.mra.parser.ParseException;
public class MRAVisualiser extends JPanel
{
	Object dat;
	static Map<Class, Class> classLookup = new HashMap<Class,Class>();
	protected boolean packVertically = true;
	protected boolean packHorizontally = false;
	protected static Logger log = MRAUtilities.getLogger();
	
	public static void main(String args[]) throws ParseException 
	{
		String filename = "examples/Canto Ostinato.mra";
		//BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println( "Opening " + filename );
		try
		{
			FileInputStream file = new FileInputStream( filename );
			MRAParser parser = new MRAParser( file );
			Section s = parser.runParser();
			MRAVisualiser.setupUI();
			MRAVisualiser viz = getVisualiser( s );
			JFrame f = new JFrame( "InC" );
			f.add( viz );
			f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			f.setSize( new Dimension( 1200, 800 ));
			f.setVisible( true );
			
		}
		catch( Exception e )
		{
			System.err.println( "Could not open file: " + e );
		}
	}
	
	public MRAVisualiser()
	{

	}
	
	public MRAVisualiser( Object o )
	{
		this();
		setData( o );
	}
	
	/**
	 * @param o
	 * @return
	 */
	/**
	 * @param o
	 * @return
	 */
	public static MRAVisualiser getVisualiser( Object o )
	{
		if( o == null )
		{
			return new MRAVisualiser();
		}
		Class c = o.getClass();
		if( o instanceof ExpressionTerm )
		{
			MRAVisualiser ret = new ExpressionTermVisualiser();
			ret.setData( o );
			return ret;
		}
		if( classLookup.get( c ) != null )
		{
			try
			{
				MRAVisualiser ret = (MRAVisualiser)classLookup.get( c ).newInstance();
				//System.out.println( "Found class " + classLookup.get( c ) + " for " + o );
				ret.setData( o );
				return ret;
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		Class oc = o.getClass();
		while( c != null )
		{
			try
			{
				
				String targetClass = "com.moseph.mra.visualise" + c.getName().replaceAll( c.getPackage().getName(), "" ) + "Visualiser";
				//System.out.println( "Looking for visualiser for: " + c.getName() + " - trying " + targetClass );
				Class vis = Class.forName( targetClass );
				//System.out.println( "Success!");
				MRAVisualiser ret = (MRAVisualiser)vis.newInstance();
				ret.setData( o );
				classLookup.put( oc, vis );
				return ret;
			}
			catch( Exception e )
			{
				c = c.getSuperclass();
			}
		}
		return new MRAVisualiser( o );
		
		
	}
	
	public void paint( Graphics g )
	{
		((Graphics2D)g).setRenderingHint
		  (RenderingHints.KEY_ANTIALIASING,
		   RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint( g );
	}
	
	void setData( Object o )
	{
		dat = o;
		removeAll();
		dataFromObject( );
		setupBorder();
		Dimension d = getPreferredSize();
		if( ! packVertically ) { d.height = 2000; }
		if( ! packHorizontally ) { d.width = 2000; }
		setMaximumSize( d );
	}
	
	void dataFromObject( )
	{
		JLabel lab = new JLabel( dat + "" );
		add( lab );		
	}
	
	void setupBorder()
	{
		setBorder( BorderFactory.createEtchedBorder() );	
	}
	
	JComponent getListPanel( List objects, String title )
	{
		return getListPanel(objects, title, BoxLayout.Y_AXIS );
	}
	
	JComponent getListPanel( List objects, String title, int direction )
	{
		Box b = new Box( direction );
		
		if( title != null )
		{
			b.setBorder( BorderFactory.createTitledBorder( 
					BorderFactory.createMatteBorder( 2, 2, 2, 2, Color.lightGray ),
					title, TitledBorder.CENTER, TitledBorder.TOP, getFont().deriveFont(Font.BOLD )));
		}
		if( objects == null ) return b;
		Box bb = new Box( direction );
		JScrollPane p = new JScrollPane( bb );
		if( direction == BoxLayout.Y_AXIS )
		{
			p.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
			p.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
		}
		else
		{
			p.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
			p.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
		}
		for( Object att : objects )
		{
			if( att instanceof String )
			{
				bb.add( new JLabel( att.toString() ));
			}
			else if ( att instanceof JComponent )
			{
				bb.add( (JComponent)att );
			}
			else
			{
				bb.add( MRAVisualiser.getVisualiser( att ));
			}
		}
		bb.add( Box.createGlue() );
		b.add( p );
		return b;

	}
	
	JComponent getSettingsLabel( String label, String value )
	{
		return new JLabel( label + ": " + value );
	}
	
	public static void setupUI()
	{
		System.out.println( "Setting up UI");
		setUIFont (new javax.swing.plaf.FontUIResource("Sans",Font.PLAIN,12));
	}
	
	public static void setUIFont (javax.swing.plaf.FontUIResource f){
	    //
	    // sets the default font for all Swing components.
	    // ex. 
	    //  setUIFont (new javax.swing.plaf.FontUIResource("Serif",Font.ITALIC,12));
	    //
	    java.util.Enumeration keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements()) {
	      Object key = keys.nextElement();
	      Object value = UIManager.get (key);
	      if (value instanceof javax.swing.plaf.FontUIResource)
	        UIManager.put (key, f);
	      }
	    }    

	public static Color getColorFor( NamedSymbol sym )
	{
		if( sym == null ) return Color.BLACK;
		if( sym.isDefined() && sym.isUsed() ) { return Color.green; }
		else if ( sym.isUsed() ) { return Color.RED ; }
		else if ( sym.isDefined() ) { return Color.ORANGE ; }
		else { return Color.cyan ; }
	}
}
