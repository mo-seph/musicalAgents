/**
 * 
 */
package com.moseph.mra;

import java.text.DecimalFormat;
import java.util.*;

import com.moseph.mra.logic.Function;
import com.moseph.mra.logic.Term;

/**
 * @author s0239182
 *
 */
public class Section extends Unit
{
	public static final double UNKNOWN_LENGTH = -1.0;
	List<Behaviour> activeBehaviours;
	List<Section> children = new Vector<Section>();
	ChannelSet channels;
	Section superSection;
	Section parent;
	double length;
	Section nextSection;
	SectionDecisionRule decisionRule;
	boolean mustRepeat = false;
	TimeSignature timeSignature = new TimeSignature( "4/4");
	double bpm = 120.0;
	int numRepeats = 0;
	


	public Section( String name )
	{
		super( name);
		activeBehaviours = new Vector<Behaviour>();
		channels = new ChannelSet();
	}

	public void addBehaviour( Behaviour d )
	{
		activeBehaviours.add( d );
	}
	
	public List<Behaviour> getActiveBehaviours()
	{
		if ( superSection == null ) return activeBehaviours;
		HashSet<Behaviour> dirs = new HashSet<Behaviour>( superSection .getActiveBehaviours() );
		dirs.addAll( activeBehaviours );
		return new Vector<Behaviour>( dirs );
	}
	
	public void addChannel( Channel c )
	{
		channels.add( c );
		c.use();
		if( length < c.getLength() ) length = c.getLength();
		if( c.getLength() < length ) c.setLength( length );
	}
	
	public Channel getChannel( String name )
	{
		return channels.get( name );
	}
	
	public List<Channel> getChannels()
	{
		if( superSection == null ) return channels.getChannels();
		return ChannelSet.combine( superSection.channels, channels ).getChannels();
	}
	
	public void setDerivedFrom( String parentName )
	{
		//System.out.println( name + " derives from " + parentName );
		superSection = factory.getSection( parentName );
	}
	
	public void setDerivedFrom( Section superSec )
	{
		superSection = superSec;
	}

	/**
	 * @see com.moseph.mra.Unit#addAttribute(com.moseph.mra.Attribute)
	 */
	@Override
	public void addAttribute( Attribute a )
	{
		if( a.name.equals( "Derives" ) ) setDerivedFrom( a.stringValue() );
		else if( a.name.equals( "Length" ) ) setLength( parseDoubleFor( a.stringValue(), "Length" ) );
		else if( a.name.equals( "NextSection")) setNextSection( a.stringValue() );
		else if( a.name.equals( "Ordering")) setOrdering( a );
		else if( a.name.equals( "TimeSignature")) setTimeSignature( a.stringValue() );
		else if( a.name.equals( "BPM")) setBPM( parseDoubleFor( a.stringValue(), "BPM") );
		else if( a.name.equals( "Repeats")) setNumRepeats( parseIntFor( a.stringValue(), "Repeats") );
		
		else
		{
			//System.out.println( "Adding " + a.name );
			super.addAttribute( a );
		}
		
	}
	
	public void setNextSection( String name )
	{
		nextSection = factory.getSection( name );
	}
	
	public TimeSignature getTimeSignature() { return timeSignature; }
	public void setTimeSignature( TimeSignature timeSignature ) { this.timeSignature = timeSignature; }
	public void setTimeSignature( String a ) 
	{ 
		//System.out.println( "Found time signature: " + a );
		timeSignature = new TimeSignature( a ); 
	}
	
	public double getBPM() { return bpm; }
	public void setBPM( double a ) { bpm = a; }
	
	public void setOrdering( Attribute a )
	{
		Object v = a.getValue();
		if( v instanceof Function )
		{
			Function f = (Function)v;
			decisionRule = factory.getDecisionRule( this, f.getName(), f.getParameters() );
		}
		else if( v instanceof Term )
		{
			decisionRule = factory.getDecisionRule( this, ((Term)v).getName() );
		}
		else if( v instanceof String )
		{
			decisionRule = factory.getDecisionRule( this, v.toString() );
		}
		else
		{
			System.err.println( "Trying to set a decision rule with a " + v.getClass() );
		}
	}
	
	public void addChild( Section child )
	{
		length = UNKNOWN_LENGTH;
		children.add( child );
		if( decisionRule == null ) setDecisionRule( new SequentialDecisionRule( this ) );

	}
	
	public int getNumChildren()
	{
		return children.size();
	}
	
	public List<Section> getChildren()
	{
		return children;
	}
	
	public int getSubsectionIndex( Section s )
	{
		return children.indexOf( s );
	}
	
	public Path getPath()
	{
		String path = getName();
		Section p = parent;
		while( p != null && ! ( p instanceof Piece ) )
		{
			path = p.getName() + "/" + path;
			p = p.parent;
		}
		return new Path( path );
	}
	
	public void setDecisionRule( SectionDecisionRule s )
	{
		decisionRule = s;
	}
	
	public SectionDecisionRule getDecisionRule()
	{
		return decisionRule;
	}

	public Section getSuperSection()
	{
		return superSection;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}
	
	public void recalculateLength()
	{
		for( Channel c : channels.getChannels() )
		{
			if( c.getLength() > length ) length = c.getLength();
		}
	}
	public boolean isLeaf()
	{
		return children.size() == 0;
	}
	
	public Section getSection( int section )
	{
		if( section < children.size() ) return children.get( section );
		return null;
	}
	
	public void setParent( Section s ) { parent = s; }
	public Section getNextSection()
	{
		//System.out.println( getName() + " looking for next section");
		Section next = null;
		//If we have a decision rule
		if( decisionRule != null ) 
		{
			//System.out.println( "Using decision rule: " + ( next == null ? "Failed" : "OK"));
			next = decisionRule.getNextSection();
		}
		
		//If we don't have one, or it has returned null
		//If we still have to repeat
		if( next == null && mustRepeat ) 
		{
			//System.out.println( "Repeating...");
			next = this;
		}
		//If we have a defined next section, go there
		if( next == null && nextSection != null )
		{
			//System.out.println( "Using next section");
			next = nextSection;
		}
		//Otherwise, it's parent time again...
		if( next == null && parent != null ) 
		{
			//System.out.println( "Using parent value");
			next = parent.getNextSection();
		}
		if( next != null )
		{
			//System.out.println( "Got " + next.getName() + " for next section");
			next.activate();
			if( ! next.isLeaf() ) 
			{
				//System.out.println( next.getName() + " is not a leaf, so asking it for next section");
				next = next.getNextSection();
			}
		}
		else
		{
			System.out.println( "Couldn't get next section");
		}
		return next;
	}
	
	public Path getFirstPath()
	{
		Section current = this;
		String s = new String();
		while( current.parent != null ) current = current.parent;
		while( current.children != null && current.children.size() > 0 )
		{
			current = current.children.get( 0);
			s += "/" + current.name;
		}
		return new Path( s );
		
	}
	
	public void activate()
	{
		if( decisionRule != null ) decisionRule.reset();
		for( Section s : children )
		{
			//System.out.println( "Setting parent of " + s.getName() + " to " + getName() );
			s.setParent( this );
		}
	}
	
	public String toString()
	{
		return printOutput( 0, false );
	}
	
	public String printAllNotes()
	{
		return printOutput( 0, true );
	}
	public String printOutput( int level, boolean printAll )
	{
		DecimalFormat df = new DecimalFormat( "0.00");
		String prefix = "";
		for( int i = 0; i < level; i++ ) prefix += "\t";
		String ret = prefix + "( " + getMRADeclaration() + " " + name + "\n";
		ret += getExtraMRAData();
		if( getLength() >0 ) ret += prefix + "\tLength:\t\t\t" + df.format( getLength() ) + "\n";
		if( getNumRepeats() != 0 ) ret += prefix + "\tNumRepeats:\t\t" + getNumRepeats() + "\n";
		for( Section s: children )
		{
			ret += s.printOutput( level + 1, printAll );
		}
		if( printAll )
		{
			for( Channel c : channels.getChannels() )
				if( c instanceof Fragment )
					ret += c.toMRAString( level + 1 );
		}
		else if( channels.get( "Notes") != null )ret += prefix + channels.get( "Notes").toString();
		ret += prefix + ")\n";
		return ret;
	}
	
	public int getNumLeaves()
	{
		if( isLeaf() ) return 1;
		int leaves = 0;
		for( Section s : children ) leaves += s.getNumLeaves();
		return leaves;
	}

	public int getNumRepeats()
	{
		return numRepeats;
	}

	public void setNumRepeats( int numRepeats )
	{
		//System.out.println( name + " setting repeats to " + numRepeats );
		this.numRepeats = numRepeats;
	}
	
	public String printStructure()
	{
		String ret = name;
		if( children.size() > 0 )
		{
			ret += "( ";
			for( Section c: children ) ret += c.printStructure() + " ";
			ret += ") ";
		}
		return ret;
	}

	/**
	 * Returns a map of attributes by att name. May be freely altered.
	 * Order of increasing overridingness is parent->derivedFrom->this
	 * @return
	 */
	public Map<String,Attribute> getAttributeMap()
	{
		initAtts();
		//Do we want derivedFrom to override parent or vice versa?
		Map<String,Attribute> attrs = new HashMap<String,Attribute>();
		if( parent != null ) attrs = parent.getAttributeMap();
		if( superSection != null )
			for( Attribute a : superSection.getAttributeMap().values() ) attrs.put( a.getName(), a );
		for( Attribute a : attributes ) attrs.put( a.getName(), a );
		return attrs;
	}
	
	public List<Section> getLeaves()
	{
		List<Section> ret = new Vector<Section>();
		if( isLeaf() )
			ret.add( this );
		else
			for( Section child : getChildren() ) ret.addAll( child.getLeaves() );
		return ret;
	}
	
	public List<Path> getLeafPaths()
	{
		activate();
		List<Path> ret = new Vector<Path>();
		if( isLeaf() )
			ret.add( this.getPath() );
		else
			for( Section child : getChildren() ) ret.addAll( child.getLeafPaths() );
		return ret;
	}
	
	String getMRADeclaration() { return "Section"; }
	String getExtraMRAData() { return ""; }
	
}
