package com.moseph.mra.agent.reasoning;

import jade.tools.logging.ontology.SetLevel;

import java.util.*;

import javax.swing.*;

import com.moseph.mra.CurveContainer;
import com.moseph.mra.acts.*;
import com.moseph.mra.agent.*;
import com.moseph.mra.agent.analyser.*;
import com.moseph.mra.agent.attribute.*;
import com.moseph.mra.symbolic.*;

public class ValueReasoner extends GuiReasoner
{

	protected ActionExtractor extractor;
	protected LatticeManager latticeManager;
	protected Map<Aspect,LinkedList<ActRelation>> actionHistories = new HashMap<Aspect,LinkedList<ActRelation>>();
	Map<String,Feature> currentFeatureValues = new HashMap<String, Feature>();
	Map<Aspect,Numeriser> numerisers = new HashMap<Aspect, Numeriser>();
	Map<Aspect,Value> currentValues = new HashMap<Aspect, Value>();
	Map<Aspect,JLabel> displays = new HashMap<Aspect, JLabel>();
	boolean startAtDefaults = true;
	
	public ValueReasoner( Context c, StructuralDecider decider )
	{
		super( c, decider );
		latticeManager = c.getLatticeManager();
		//System.out.println( "Starting sequence manager: " + sequences );
		
		//set up the extractor, and get a handle on the act queues
		setUpExtractor();
		setUpNumerisers();
		setUpDefaults();
	}
	
	void setUpDefaults()
	{
		for( Aspect aspect : Aspect.values() )
		{
			if( aspect.isPattern ) continue;
			ValueLattice l = latticeManager.getLattice( aspect.feature );
			setValue( aspect, l.getAny() );
		}
	}

	public void setUpExtractor()
	{
		extractor = new ActionExtractor( context, latticeManager );
		extractor.initialise();
	}

	protected void setUpNumerisers()
	{
		for( Aspect aspect : Aspect.values() )
		{
			//System.out.println( "Adding numeriser for " + aspect );
			NumericTreeLattice lat = latticeManager.getLattice( aspect.feature );
			if( aspect.isPattern ) numerisers.put( aspect, new PatternNumeriser( context, lat ) );
			else numerisers.put( aspect, new NumericNumeriser( lat ) );
		}
	}

	/**
	 * This is the main bulk of the work
	 */
	public void fillPlan( BasicRenderPlan plan )
	{
		FeatureMusicianMap<MusicalAction> currentActs = getCurrentActs();
		//For each aspect of the plan to be filled
		for( Aspect aspect : Aspect.values() )
		{
			Value value = null;
			String feature = aspect.feature;
			//Look to see if a new act occured this turn; if not, simply use our current value
			if( currentActs.getSize( feature ) == 0 )
			{
				value = context.getMyCurrentValue( feature );
			}
			else
			{
				//This means we only respond to the first other player...
				MusicalAction actToRespondTo = currentActs.getFeatureValues( feature ).get( 0 );
				System.out.println( "Responding on aspect " + aspect + ", action: " + actToRespondTo );
				
				//Find the values which can be used with the current next symbol
				//Select a value
				value = getValueInResponseTo( aspect, actToRespondTo );
			}
			if( value != null ) setValue( aspect, value );
		}
		addValuesToPlan( plan );
		setDisplays();
	}
	
	void setValue( Aspect aspect, Value value )
	{
		currentValues.put( aspect, value );
		context.setMyCurrentValue( aspect.feature, value );
		if( displays.containsKey( aspect ) ) displays.get( aspect ).setText( value + "" );
	}
	
	void setDisplays()
	{
		for( Aspect aspect : Aspect.values() )
		{
			if( currentValues.containsKey( aspect ) && displays.containsKey( aspect ))
			{
				displays.get( aspect ).setText( currentValues.get( aspect ) + "" );
				displays.get( aspect ).revalidate();
			}
		}
	}
	
	Value getValueInResponseTo( Aspect aspect, MusicalAction act )
	{
		return act.getValue();
	}
	
	public FeatureMusicianMap<MusicalAction> getCurrentActs()
	{
		return extractor.update();
	}

	Value getResponseValue( Aspect aspect, ActRelation newRelation, MusicalAction actToRespondTo )
	{
		String feature = aspect.feature;
		Value myValue = context.getMyCurrentValue( feature );
		Value otherValue = actToRespondTo.getValue();
		ValueLattice lattice = latticeManager.getLattice( feature );
		if( myValue == null ) myValue = lattice.getAny();
		
		List<Value> possible = lattice.getPossibleValues( myValue, newRelation.getRSelf(), otherValue, newRelation.getROther() );
		if( possible.size() == 0 ) return null;
		//For now, select first one every time
		return possible.get( 0 );
	}

	void addValuesToPlan( BasicRenderPlan plan )
	{
		for( Aspect aspect : currentValues.keySet() )
		{
			addToPlan( plan, aspect, currentValues.get( aspect ) );
		}
	}

	void addToPlan( BasicRenderPlan plan, Aspect aspect, Value value )
	{
		if( value == null ) 
		{
			System.err.println( "No value for SeqeuenceReasoner to put in plan for " + aspect );
			return;
		}
		Feature f = numerise( aspect, value );
		switch ( aspect )
		{
			case DYNAMICS:
				plan.setDynamicsCurve( calculateCurve( plan, aspect, (NumericFeature)f ) );
				break;
	
			case LENGTH:
				plan.setLengthCurve( calculateCurve( plan, aspect, (NumericFeature)f ) );
				break;
	
			case DISPLACEMENT:
				plan.setTimingCurve( calculateCurve( plan, aspect, (NumericFeature)f ) );
				break;
	
			case DYNAMIC_PATTERN:
				plan.setDynamicsPattern( (PatternAttribute)f );
				break;
	
			case LENGTH_PATTERN:
				plan.setDynamicsPattern( (PatternAttribute)f );
				break;
	
			case DISPLACEMENT_PATTERN:
				plan.setDynamicsPattern( (PatternAttribute)f );
				break;
	
			default:
				break;
		}
		currentFeatureValues.put( aspect.feature, f );
	}

	Feature numerise( Aspect aspect, Value value )
	{
		Numeriser n = numerisers.get( aspect );
		return n.numerise( value );
	}

	CurveContainer calculateCurve( BasicRenderPlan plan, Aspect aspect, NumericFeature feature )
	{
		double pv = feature.getValue();
		NumericFeature previousValue = (NumericFeature)currentFeatureValues.get( aspect.feature );
		if( previousValue != null ) pv = previousValue.getValue();
		return new CurveContainer( plan.length, pv, feature.getValue() );
	}

	protected List<String> getNecessaryAnalysers()
	{
		List<String> nec = super.getNecessaryAnalysers();
		nec.add( "DynamicPattern" );
		nec.add( "DynamicAverage" );
		nec.add( "DynamicChange" );
		nec.add( "LengthPattern" );
		nec.add( "LengthAverage" );
		nec.add( "LengthChange" );
		nec.add( "DisplacementPattern" );
		nec.add( "DisplacementAverage" );
		nec.add( "DisplacementChange" );
		return nec;
	}

	public enum Aspect
	{
		DYNAMICS( DynamicAverageAnalyser.featureName, false  ),
		DYNAMIC_PATTERN( DynamicPatternAnalyser.featureName, true  ),
		LENGTH( LengthAverageAnalyser.featureName, false  ),
		LENGTH_PATTERN( LengthPatternAnalyser.featureName, true  ),
		DISPLACEMENT( DisplacementAverageAnalyser.featureName, false  ),
		DISPLACEMENT_PATTERN(DisplacementPatternAnalyser.featureName, true  );
		
		public String feature;
		public boolean isPattern;
		Aspect( String f, boolean pat )
		{
			this.feature = f;
			this.isPattern = pat;
		}
	}

	public Map<Aspect, Value> getCurrentValues()
	{
		return currentValues;
	}

	public Map<String, Feature> getCurrentFeatureValues()
	{
		return currentFeatureValues;
	}

	@Override
	public JComponent getGUIComponent()
	{
		JComponent component = super.getGUIComponent();
		Box b = new Box( BoxLayout.X_AXIS );
		for( Aspect aspect : Aspect.values() )
		{
			Box box = new Box( BoxLayout.Y_AXIS );
			box.add( new JLabel( aspect + "" ));
			JLabel label = new JLabel( "Null");
			box.add( label );
			displays.put( aspect, label );
			b.add( box );
		}
		component.add( b );
		
		return component;
	}

}
