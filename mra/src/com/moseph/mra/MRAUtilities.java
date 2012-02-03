package com.moseph.mra;

import java.lang.reflect.Array;

import java.awt.Container;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
provides utilitites to aid dealing with MAXML Notation
@author David Murray-Rust
@version $Revision$, $Date$
*/
public class MRAUtilities
{
	public final static Logger log = Logger.getLogger( "com.moseph.music.representation" );
	private static boolean logInitialised = false;
	public static final double FUZZY_COMPARISON_THRESHOLD = 0.005;

	public static Logger getLogger()
	{
		if( ! logInitialised )
		{
			Handler h = new ConsoleHandler();
			log.addHandler( h );
			h.setLevel( Level.ALL );
			log.setLevel( Level.INFO );
			logInitialised = true;
		}
		return log;
	}
	/**
	gets an XML document whose root note is correct for storing MAXML data.
	THe document will have a single root element in it, of type nfb_data
	@return the specified document
	*/
	public static Document getMAXMLXMLDocument()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try 
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();  
			Element root = document.createElement("nfb_data"); 
			document.appendChild( root );
			return document;
		}
		catch( Exception e )
		{
			System.out.println( "Could not build MAXML XML Document: " + e );
			return null;
		}
	}

	/**
	Writes an XML Document object to standard out
	@param document
	*/
	public static void printMAXMLXMLDocument( Document document )
	{
		StreamResult stream = new StreamResult( System.out );
		streamXMLDocument( document, stream );
		System.out.println( "" );
	}

	/**
	Writes an XML Document object to a file
	@param document
	@param filename
	*/
	public static void writeMAXMLXMLDocument( Document document, String filename )
	{
		StreamResult stream = new StreamResult( new File( filename ) );
		streamXMLDocument( document, stream );
	}

	/**
	sends an XML Document object to a stream
	@param document
	@param stream
	*/
	static void streamXMLDocument( Document document, StreamResult stream )
	{
		try
		{
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty( "indent", "yes" );
			t.setOutputProperty( "method", "HTML" );
			DOMSource source = new DOMSource( document );
			t.transform( source, stream );
		}
		catch( Exception e )
		{
			System.err.println( "Could not stream XML document: " + e );
		}
	}

	/**
	outputs an XML representation of the given component on standard out
	@param comp the component to be output
	*/
	static void printMAXMLComponent( MAXMLComponent comp )
	{
		Document d = MRAUtilities.getMAXMLXMLDocument();
		Element e = comp.getXMLElement( d );
		Element root = d.getDocumentElement();
		root.appendChild( e );
		MRAUtilities.printMAXMLXMLDocument( d );
	}

	public static boolean fuzzyCompare( double a, double b, double thresh )
	{
		return Math.abs( a - b ) < thresh;
	}
	public static boolean fuzzyCompare( double a, double b )
	{
		return fuzzyCompare( a, b, FUZZY_COMPARISON_THRESHOLD );
	}
	
	public static int fuzzyCmp( double a, double b )
	{
		double cmp = a - b;
		if( cmp > FUZZY_COMPARISON_THRESHOLD ) return 1;
		if( cmp < 0.0 - FUZZY_COMPARISON_THRESHOLD ) return -1;
		return 0;
	}
	public static String formatBeat( double beat )
	{
		return MRAUtilities.beatFormat.format( beat );
	}
	static DecimalFormat beatFormat = new DecimalFormat( "0.000");
	
	public static void warn( Object o )
	{
		log.log( Level.WARNING, o.toString() );
	}
	
	public static String join( List<?> l)
	{
		String ret = "";
		if( l.size() > 0 ) ret = l.get(0).toString();
		for( int i = 1; i < l.size(); i++ )
		{
			ret += ", " + l.get(i).toString();
		}
		return ret;
	}
	
	public static String collectionToString( Object a )
	{
		return collectionToString( a, 0 );
	}
	
	public static String collectionToString( Object a, int level )
	{
		String ret = "";
		String prefix = "";
		for( int i = 0; i < level; i++ ) prefix += "\t";
		if( a instanceof Map )
		{
			Map map = (Map)a;
			ret += prefix + "{\n";
			for( Object o : map.keySet() ) ret += prefix + "\t" + o + " =>\t" + collectionToString( map.get( o ), level + 1 ) + "\n";
			ret += prefix + "}\n";
		}
		else if( a instanceof Container )
		{
			Collection l = (Collection)a;
			ret += prefix + "[\n";
			for( Object o : l ) ret += prefix + "\t" + collectionToString( o, level + 1 ) + "\n";
			ret += prefix + "]\n";
		}
		else ret += a;
		return ret;
	}
	
	public static <T> List<List<T>> powerList( List<List<T>> in )
	{
		int[] positions = new int[ in.size() ];
		List<List<T>> ret = new Vector<List<T>>();
		Arrays.fill( positions, 0 );
		ret.add( getSublist( positions, in ) );
		while( ! incrementArray( positions, in ))
		{
			//System.out.println( collectionToString( getSublist( positions, in ) ) );
			ret.add( getSublist( positions, in ) );
		}
		return ret;
	}
	
	static <T> List<T> getSublist( int[] array, List<List<T>> lists )
	{
		List<T> ret = new Vector<T>();
		for( int i = 0; i < array.length; i++ )
			if( lists.get(i).size() > 0 )
				ret.add( lists.get( i ).get( array[i] ));
			else
				ret.add( null );
		return ret;
	}
	
	static <T> boolean incrementArray( int[] array, List<List<T>> lists )
	{
		for( int i = array.length - 1; i >= 0; i-- )
		{
			array[i]++;
			if( array[i] == lists.get(i).size() || lists.get(i).size() == 0 ) array[i] = 0;
			else return false;
		}
		return true;
	}
	
	public static double sigmoidalProbability( double weight, double p0, double wBound )
	{
		return sigmoidalProbability( weight, p0, wBound, 0.95 );
	}
	
	public static double sigmoidalProbability( double weight, double p0, double wBound, double pBound )
	{
		double b = Math.log( p0 / ( 1 - p0 ));
		double kB = Math.log( pBound / ( 1 - pBound ) );
		double a  = ( kB - b ) / wBound;
		double inv = 1.0 + Math.exp( -(a * weight + b ));
		//System.out.printf( "Weight: %f, p0: %f, wBound: %f, pBound: %f, a: %f, b: %f\n", weight, p0, wBound, pBound, a, b );
		return 1.0 / inv;
	}
	
	public static double smooth( double oldVal, double newVal, double smoothing )
	{
		return ( smoothing * oldVal ) + (1-smoothing) * newVal ;
	}
}
