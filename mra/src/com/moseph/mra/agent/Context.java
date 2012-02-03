
package com.moseph.mra.agent;

import jade.core.AID;

import java.util.*;

import com.moseph.mra.*;
import com.moseph.mra.agent.analyser.GroupFeature;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.agent.reasoning.sequences.SequenceManager;
import com.moseph.mra.symbolic.*;

public class Context
{
	String partname = "Notes";
	Piece piece;
	List<Attribute> attributes = new Vector<Attribute>();
	Map<String,String> attsByName = new HashMap<String, String>();
	Map<String,Object> objectAttsByName = new HashMap<String, Object>();
	Map<String,MusicianInformation> byName = new HashMap<String,MusicianInformation>();
	Section currentSection;
	MusicianInformation id;
	FeatureSet features;
	double basicSectionSkipProbability = 0.01;
	double maxSectionWeight = 4.0;
	double quantisation = 4.0;
	List<Double> patternPoints = new Vector<Double>();
	double patternOffset = 0.0;
	double heardMusicTill = 0.0;
	Map<String,Object> attributeDefaults = new HashMap<String, Object>();
	MusicalContext musicalContext;
	Map<String,Value> currentValues = new HashMap<String, Value>();
	Map<String,Map<MusicianInformation,Value>> othersValues = new HashMap<String, Map<MusicianInformation,Value>>();
	LatticeManager latticeManager;
	SequenceManager sequenceManager;
	

	public Context( MusicianInformation id )
	{
		this.id = id;
		features = new FeatureSet( this );
		if( id != null ) byName.put( id.getName(), id );
		initAttributes();
		musicalContext = new MusicalContext( this );
	}
	
	public void setAttributes( List<Attribute> atts )
	{
		for( Attribute a : atts )
		{
			attributes.add( a );
			setAttribute( a.getName(), a.getValue() );
		}
	}
	
	public String getAttributeString( String attName )
	{
		return attsByName.get( attName );
	}
	
	public Object getAttributeObject( String attName )
	{
		return objectAttsByName.get( attName );
	}
	
	public void setAttribute( String attName, Object attVal )
	{
		if( ! attributeDefaults.containsKey( attName ))
			System.err.println( "Unknown attribute set in context! :" + attName +":" + attVal );
		if( attName == null ) return;
		if( attVal == null ) attsByName.put( attName, null );
		else attsByName.put( attName, attVal.toString() );
		objectAttsByName.put( attName, attVal );
	}
	
	public String getAllAttsAsString()
	{
		String ret = "";
		for( String att : attsByName.keySet() )
			ret += att + ": " + attsByName.get( att ) + "\n";
		return ret;
	}
	
	public String getPartname()
	{
		return partname;
	}
	public void setPartname( String partname )
	{
		this.partname = partname;
	}
	public Piece getPiece()
	{
		return piece;
	}
	public void setPiece( Piece piece )
	{
		this.piece = piece;
		setQuantisationLevel();
	}
	
	void setQuantisationLevel()
	{
		if( piece == null ) return;
		Attribute level = piece.getAttributeMap().get( "Quantisation" );
		if (level != null)
			quantisation = Double.parseDouble( level.getValue() + "" );
	}

	
	public MusicianInformation getMusicianInformation( Fragment f )
	{
		return getMusicianInformation( f.getMusician().getName() );
	}
	public MusicianInformation getMusicianInformation( AID aid )
	{
		MusicianInformation i = getMusicianInformation( aid.getLocalName() );
		i.setAid( aid );
		return i;
	}
	
	public MusicianInformation getMusicianInformation( String name )
	{
		MusicianInformation i = byName.get( name );
		if( i == null )
		{
			i = new MusicianInformation();
			i.setName( name );
			byName.put( name, i );
		}
		return i;
	}
	
	public MusicianInformation getMusicianInformation( MusicianInformation name )
	{
		if( byName.containsKey( name.getName() )) return getMusicianInformation( name.getName() );
		byName.put( name.getName(), name );
		return name;
	}
	
	public void retireMusician( AID i )
	{
		retireMusician( i.getLocalName() );
	}
	
	public void retireMusician( MusicianInformation i )
	{
		retireMusician( i.getName() );
	}
	
	public void retireMusician( String name )
	{
		System.out.println( "Retiring: " + name );
		MusicianInformation mi = byName.get( name );
		if( mi == null )
		{
			String names = "";
			for( String n : byName.keySet() ) names += n + ", ";
			System.out.println( name + " not found in " + names );
			return;
		}
		mi.setActive( false );
		features.retireMusician( mi );
	}
	

	
	public Section getCurrentSection()
	{
		return currentSection;
	}
	public void setCurrentSection( Section currentSection )
	{
		this.currentSection = currentSection;
	}
	
	/**
	 * Sets the given value for the given musician and the given feature
	 * @param feature
	 * @param name
	 * @param value
	 */
	public void setFeature(  String feature, MusicianInformation name, Feature value )
	{
		//Make sure we always use the same musician information
		features.setFeature( feature, getMusicianInformation( name ), value );
	}
	public void setFeature(  String feature, AID name, Feature value )
	{
		features.setFeature( feature, getMusicianInformation( name ), value );
	}
	
	/**
	 * Wrapper for easy Path feature setting
	 * @param name
	 * @param path
	 */
	public void setPath( AID name, Path path )
	{
		setFeature( "Path", name, new PathFeature( path, piece ));
	}
	
	/**
	 * Wrapper for easy Path feature setting (primarily for testing where no AIDs are available)
	 * @param name
	 * @param path
	 */
	public void setPath( MusicianInformation name, Path path )
	{
		setFeature( "Path", name, new PathFeature( path, piece ));
	}
	
	public GroupFeature getFeature( String name )
	{
		return features.getOrCreate( name );
	}

	public double getBasicSectionSkipProbability()
	{
		return basicSectionSkipProbability;
	}

	public void setBasicSectionSkipProbability( double basicSectionSkipProbability )
	{
		this.basicSectionSkipProbability = basicSectionSkipProbability;
	}

	public double getMaxSectionWeight()
	{
		return maxSectionWeight;
	}

	public void setMaxSectionWeight( double maxSectionWeight )
	{
		this.maxSectionWeight = maxSectionWeight;
	}
	
	public MusicianInformation getMyID()
	{
		return id;
	}

	public FeatureSet getFeatures()
	{
		return features;
	}
	
	public Collection<MusicianInformation> getMusicians()
	{
		return byName.values();
	}
	
	public List<MusicianInformation> getActiveMusicians()
	{
		Vector<MusicianInformation>mi = new Vector<MusicianInformation>();
		for( MusicianInformation m : byName.values() )
			if( m.isActive() ) mi.add( m );
		return mi;
	}
	
	public MusicianInformation getClosestMusician() { return getClosestMusician( false ); }
	public MusicianInformation getClosestActiveMusician() { return getClosestMusician( true ); }
	public MusicianInformation getClosestMusician( boolean requireValidPath )
	{
		double closestDist = Double.MAX_VALUE;
		MusicianInformation closest = null;
		Set<MusicianInformation> others = getFeature( "Path").getOtherMusicians();
		for( MusicianInformation m : others )
		{
			if( m.equals( id )) continue;
			if( requireValidPath && ! hasValidPath( m )) continue;
			double dist = id.getSquaredDistance( m );
			if( dist < closestDist ) 
			{
				closest = m;
				closestDist = dist;
			}
		}
		return closest;
	}
	
	boolean hasValidPath( MusicianInformation m )
	{
		Path p = getPath( m );
		//System.out.println( p + " has index " + p.getIndex( piece ));
		if( p.getIndex( piece ) >= 0 ) return true;
		return false;
	}
	
	public Path getPath( MusicianInformation m )
	{
		PathFeature pf = (PathFeature)getFeature( "Path").getValue( m );
		Path path = null;
		if( pf != null ) path = pf.getPath();	
		return path;
	}
	
	public void updateLocationSensitiveTotals()
	{
		features.updateLocationSensitiveTotals();
	}

	/**
	 * Sets the number of divisions per beat to quantise to
	 * @param quantisation
	 */
	public void setQuantisation( double quantisation )
	{
		this.quantisation = quantisation;
	}
	public double getQuantisation()
	{
		return quantisation;
	}
	
	public void addPatternPoint( double point )
	{
		patternPoints.add( point );
	}
	
	public double getPatternPointBefore( double point )
	{
		for( int i = patternPoints.size() - 1; i >= 0; i-- )
		{
			if( patternPoints.get( i ) < point ) return patternPoints.get( i );
		}
		return 0.0;
	}
	
	public String stringAttribute( String featureName )
	{
		if( ! attributeDefaults.containsKey( featureName ) )
		{
			System.err.println( "Unknown attribute: " + featureName );
			return null;
		}
		if( attsByName.containsKey( featureName ) ) return attsByName.get( featureName );
		return attributeDefaults.get( featureName ).toString();
	}
	
	public boolean booleanAttribute( String featureName )
	{
		String s = stringAttribute( featureName );
		if( s == null ) return false;
		String[] trueStrings = { "true", "1", "yes", "on" };
		for( String ts : trueStrings )
			if( s.equalsIgnoreCase( ts )) return true;
		return false;
	}
	
	public double doubleAttribute( String featureName )
	{
		String s = stringAttribute( featureName );
		if( s == null ) return Double.NaN;
		try
		{
			return Double.parseDouble( s );
		}
		catch( NumberFormatException e ) { System.err.println( "Bad Number for " + featureName + ": " + s );}
		return Double.NaN;
	}
	
	public int intAttribute( String featureName )
	{
		String s = stringAttribute( featureName );
		if( s == null ) return 0;
		try
		{
			return Integer.parseInt( s );
		}
		catch( NumberFormatException e ) { System.err.println( "Bad Number for " + featureName + ": " + s );}
		return 0;
	}
	
	void initAttributes()
	{
		attributeDefaults.put( "KeepLastInContext", "true" );
		attributeDefaults.put( "PatternLength", "1.0" );
		attributeDefaults.put( "Filename", null );
		attributeDefaults.put( "Instrument", "AcousticPiano" );
		attributeDefaults.put( "x", "0.0" );
		attributeDefaults.put( "y", "0.0" );
		attributeDefaults.put( "Partname", null );
		attributeDefaults.put( "Reasoner", "AcousticMirror" );
		attributeDefaults.put( "PatternSize", "4" );
	}

	public MusicalContext getMusicalContext()
	{
		return musicalContext;
	}

	public void setMusicalContext( MusicalContext musicalContext )
	{
		this.musicalContext = musicalContext;
	}

	public double getHeardMusicTill()
	{
		return heardMusicTill;
	}

	public void setHeardMusicTill( double heardMusicTill )
	{
		this.heardMusicTill = heardMusicTill;
	}
	
	public Value getMyCurrentValue( String name )
	{
		return currentValues.get( name );
	}
	
	public void setMyCurrentValue( String name, Value value )
	{
		currentValues.put( name, value );
	}
	
	public void setCurrentValue( String musicianName, String featureName, Value value )
	{
		if( ! othersValues.containsKey( featureName )) othersValues.put( featureName, new HashMap<MusicianInformation, Value>() );
		Map<MusicianInformation,Value>  featureMap = othersValues.get( featureName );
		featureMap.put( getMusicianInformation( musicianName ), value );
	}

	public Value getCurrentValue( MusicianInformation musician, String featureName )
	{
		if( ! othersValues.containsKey( featureName )) return null;
		Map<MusicianInformation,Value>  featureMap = othersValues.get( featureName );
		return featureMap.get( getMusicianInformation( musician) );
	}

	public Map<String, Map<MusicianInformation, Value>> getOthersValues()
	{
		return othersValues;
	}

	public LatticeManager getLatticeManager()
	{
		return latticeManager;
	}

	public void setLatticeManager( LatticeManager latticeManager )
	{
		this.latticeManager = latticeManager;
	}

	public SequenceManager getSequenceManager()
	{
		return sequenceManager;
	}

	public void setSequenceManager( SequenceManager sequenceManager )
	{
		this.sequenceManager = sequenceManager;
	}

}

