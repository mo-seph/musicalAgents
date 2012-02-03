/**
 * 
 */
package com.moseph.mra;

import java.util.List;

import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.logic.*;

/**
 * @author s0239182
 *
 */
public class MRAFactory
{


	Piece piece;
	public SymbolTable<Section> sections = new SymbolTable<Section>();
	SymbolCreator<Section> sectionCreator;
	public SymbolTable<Action> actions = new SymbolTable<Action>();
	SymbolCreator<Action> actionCreator;
	public SymbolTable<Behaviour> behaviours = new SymbolTable<Behaviour>();
	SymbolCreator<Behaviour> behaviourCreator;
	public SymbolTable<Decision> decisions = new SymbolTable<Decision>();
	SymbolCreator<Decision> decisionCreator;
	public SymbolTable<Term> terms = new SymbolTable<Term>();
	SymbolCreator<Term> termCreator;
	

	
	public MRAFactory( Piece p )
	{
		this.piece = p;
		initialise();
	}
	
	public MRAFactory( String name )
	{
		piece = createPiece( name );
		initialise();
	}
	
	void initialise()
	{
		sectionCreator = new SymbolCreator<Section>( sections, this )
		{	Section doCreate( String name ) { return new Section( name ); }	};
		actionCreator = new SymbolCreator<Action>( actions, this )
		{	Action doCreate( String name ) { return new Action( name ); }	};
		behaviourCreator = new SymbolCreator<Behaviour>( behaviours, this )
		{	Behaviour doCreate( String name ) { return new Behaviour( name ); }	};
		decisionCreator = new SymbolCreator<Decision>( decisions, this )
		{	Decision doCreate( String name ) { return new Decision( name ); }	};
		termCreator = new SymbolCreator<Term>( terms, this )
		{	Term doCreate( String name ) { return new Term( name ); }	};
	}
	
	public Piece getPiece()
	{
		return piece;
	}
	public Section getSection( String name )
	{
		return sectionCreator.get( name );
	}
	
	public Section createSection( String name )
	{
		return sectionCreator.create( name );
	}


	
	public Term getTerm( String name )
	{
		return termCreator.get( name );
	}
	
	public Behaviour createBehaviour( String name, String actionName, String decisionName )
	{
		Behaviour b = behaviourCreator.create( name );
		b.setAction( getAction( actionName ) );
		b.setDecision( getDecision( decisionName ) );
		return b;
	}
	
	public Behaviour createBehaviour( String name, String actionName, ExpressionTerm expression )
	{
		//Action a = getAction( actionName );
		Behaviour b = behaviourCreator.create( name );
		b.setAction( getAction( actionName ) );
		b.setExpression( expression );
		return b;
	}

	public Behaviour createBehaviour( String name, Action a, ExpressionTerm e )
	{
		Behaviour b = behaviourCreator.create( name );
		b.setAction( a );
		b.setExpression( e );
		return b;
	}
	public Behaviour createBehaviour( String name, Action a, Decision d )
	{
		Behaviour b = behaviourCreator.create( name );
		b.setAction( a );
		b.setDecision( d );
		return b;
	}
	
	public Behaviour createBehaviour( String name )
	{
		return behaviourCreator.create( name );
	}
		

	public Behaviour getBehaviour( String name )
	{
		return behaviourCreator.get( name );
	}
	
	public Behaviour createAnonymousBehaviour( String actionName, String decisionName )
	{
		Action a = getAction( actionName );
		Decision d = getDecision( decisionName );
		return new Behaviour( "Anon", a, d );
	}
	
	public Behaviour createAnonymousBehaviour( String actionName, ExpressionTerm expression )
	{
		Action a = getAction( actionName );
		return new Behaviour( "Anon", a, expression );
	}

	public Behaviour createAnonymousBehaviour( Action a, ExpressionTerm e )
	{
		return new Behaviour( "Anon", a, e );
	}

	public  Action createAction( String name )
	{
		return actionCreator.create( name );
	}
	
	public Action getAction( String name )
	{
		return actionCreator.get( name );
	}

	public Decision createDecision( String name, ExpressionTerm expression )
	{
		Decision d = decisionCreator.create( name );
		d.setExpression( expression );
		return d;
	}
	
	public Decision getDecision( String name )
	{
		return decisionCreator.get( name );
	}

	public static  Channel createChannel( String type )
	{
		if( type.startsWith( "Notes"))
		{
			String name = type;
			if( !name.equals( "Notes")) name = name.replaceFirst( "Notes\\s*", "");
			return new Fragment( name );
		}
		if( type.equals( "Chords"))
		{
			return new SpanContainer<Span<Chord>>( "Chords" );
		}
		if( type.equals( "Accents"))
		{
			return new Channel<TemporalEvent<Accent>>( "Accents");
		}
		return new Channel( type );
	}
	
	public static TemporalEvent createTemporalEvent( String type )
	{
		if( type.equals( "Note"))
		{
			return new Note();
		}
		if( type.equals( "Gracenote"))
		{
			return new Gracenote();
		}
		if( type.equals( "Chord"))
		{
			return new Span<Chord>( new Chord() );
		}
		if( type.equals( "Accent"))
		{
			return new TemporalEvent<Accent>( new Accent() );
		}
		TemporalEvent e = new TemporalEvent();
		e.setName( type );
		return e;
	}
	
	public static  Unit createNamedUnit( String name )
	{
		return new Unit( name );
	}

	public static  Unit createTypedUnit( String type )
	{
		if( type.equals( "Note" )  )
			return new Note();
		else if( type.equals( "Gracenote") )
		{
			return new Gracenote();
		}
		//System.out.println( "Type: " + type );
		return new Unit( type );
	}
	
	public Function createFunction( String type, List<ExpressionTerm> parameters )
	{
		if( type.equals( "SomeFunction" ) ) {}
		return new FunctionPlaceholder( type, (ExpressionTerm[])parameters.toArray( new ExpressionTerm[parameters.size()] ) );
	}
	
	public ExpressionTerm createNegatedExpression( ExpressionTerm t )
	{
		return new NegatedExpression( t );
	}

	public ExpressionTerm createConnective( String type, List<ExpressionTerm> terms )
	{
		if( type.equals( "and")) { return new ConnectiveAnd( terms ); }
		if( type.equals( "or")) { return new ConnectiveOr( terms ); }
		return new Term( "", 0.0 );
	}
	
	Piece createPiece( String name )
	{
		return new Piece( name );
	}
	
	public SectionDecisionRule getDecisionRule( Section s, String name, List<ExpressionTerm> params )
	{
		SectionDecisionRule r = getDecisionRule( s, name );
		r.setParameters( params );
		return r;
	}
	public SectionDecisionRule getDecisionRule( Section s, String name )
	{
		if( name.equals( "Random")) return new RandomDecisionRule( s );
		return new NullDecisionRule(s);
	}

	public abstract class SymbolCreator<T extends NamedSymbol>
	{
		SymbolTable<T> symbols;
		MRAFactory factory;
		
		SymbolCreator( SymbolTable<T> symbols, MRAFactory factory )
		{
			this.symbols = symbols;
			this.factory = factory;
		}
		
		abstract T doCreate( String name );
		T createNew( String name )
		{
			T o = doCreate( name );
			//System.out.println( "Setting factory for " + name + " to " + factory );
			if( o instanceof Unit ) ((Unit)o).setFactory( factory );
			return o;
		}
		public T get( String name )
		{
			T existing = symbols.get( name );
			if( existing != null )
			{
				//System.out.println( "Found section " + name + " already defined");
				symbols.setUsed( name );
			}
			else
			{
				//System.out.println( "Creating placeholder for section " + name );
				existing = createNew( name );
				symbols.set( existing, false, true );
			}
			if( existing == null ) System.err.println( "Nothing found for " + name );
			return existing;
		}
		
		public  T create( String name )
		{
			T existing = symbols.get( name );
			if( existing == null )
			{
				//System.out.println( "Filling in new section for " + name );
				existing = createNew( name );
				symbols.set( existing, true, false );

				if( existing == null ) System.err.println( "Nothing created for " + name );
				return existing;
			}
			else
			{
				if( ! symbols.isInitialised( name ))
				{
					//System.out.println( "Filling in placeholder for " + name );
					existing.define();
					return existing;
				}
				else
				{
					//System.out.println( "Problem: " + name + " already initialised");
				}
			}
			//return createNew( name + "Broken!" );
			return createNew( name );
		}

	}
}
