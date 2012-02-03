package com.moseph.mra;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import static com.moseph.mra.MRAUtilities.*;

public class Unit implements NamedSymbol, Serializable
{

	protected List<Attribute> attributes;
	protected MRAFactory factory;
	protected String name;
	boolean defined = false;
	boolean used = false;
	protected static Logger log = MRAUtilities.getLogger();

	public Unit()
	{
		//attributes = new Vector<Attribute>(); 
	}
	
	public Unit( String name )
	{
		this();
		this.name = name;
	}
	
	void initAtts()
	{
		if( attributes == null ) attributes = new Vector<Attribute>();
	}
	public void setAttributes( List<String> vals )
	{
		initAtts();
		log.log( Level.WARNING, "setAttributes(List(vals)) not defined for " + getClass() );
		addAttribute( new Attribute( "Params", join( vals )));
	}
	public void addAttributes( List<String> attributes, List<String> values )
	{
		initAtts();
		log.log( Level.WARNING, "addAttributes(List(attributes),List(vals)) not defined for " + getClass() );
	}
	public void addAttribute( Attribute a )
	{
		initAtts();
		attributes.add( a );
	}

	public List<Attribute> getAttributes()
	{
		initAtts();
		return attributes;
	}

	protected void setFactory( MRAFactory f )
	{
		factory = f;
	}
	
	int parseIntFor( String value, String target )
	{
		int val = 0;
		try
		{
			val = Integer.parseInt( value );
		}
		catch( Exception e )
		{
			log.log( Level.WARNING, "Bad value given for " + target + " (in " + this.getClass() + "): " + value + "\n" + e);
		}
		return val;
		
	}
	
	double parseDoubleFor( String value, String target )
	{
		double val = 0;
		try
		{
			val = Double.parseDouble( value );
		}
		catch( Exception e )
		{
			log.log( Level.WARNING, "Bad value given for " + target + " (in " + this.getClass() + "): " + value );
		}
		return val;
		
	}

	protected int getAttributeAsInteger( Element el, String s )
	{
		String currentParam = el.getAttribute( s );
		return parseIntFor( currentParam, s );
	}
	protected double getAttributeAsDouble( Element el, String s )
	{
		String currentParam = el.getAttribute( s );
		double attr = 0.0;
		if( currentParam.length() > 0 )
		{
			try
			{
				attr = Double.parseDouble( currentParam );
			}
			catch( NumberFormatException e )
			{
				e.printStackTrace();
				System.err.println( "Bad " + s + " value for " + this.getClass() + "(" + currentParam + "): " + e );
			}
		}
		return attr;
	}

	/* (non-Javadoc)
	 * @see com.moseph.mra.NamedSymbol#getName()
	 */
	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.moseph.mra.NamedSymbol#define()
	 */
	public void define()
	{
		defined = true;
	}

	/* (non-Javadoc)
	 * @see com.moseph.mra.NamedSymbol#use()
	 */
	public void use()
	{
		used = true;
	}

	/* (non-Javadoc)
	 * @see com.moseph.mra.NamedSymbol#isDefined()
	 */
	public boolean isDefined()
	{
		return defined;
	}

	/* (non-Javadoc)
	 * @see com.moseph.mra.NamedSymbol#isUsed()
	 */
	public boolean isUsed()
	{
		return used;
	}
	
}
