package com.moseph.mra.ontology;

import jade.content.abs.*;
import jade.content.onto.*;
import jade.content.schema.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;


/**
 *  Generated Java class for ontology mra.
 */
public class MraOntology	extends jade.content.onto.Ontology
{
	//-------- constants --------

	/** The name of the ontology. */
	public static final String	ONTOLOGY_NAME	= "mra";

	//-------- vocabulary ----------

	public static final String	CHANNEL	= "Channel";

	public static final String	FRAGMENT	= "Fragment";

	public static final String	PATH	= "Path";
	public static final String	PATH_PATHSPEC	= "pathSpec";

	public static final String	PIECE	= "Piece";

	public static final String	PLAY	= "Play";
	public static final String	PLAY_PLAYER	= "player";
	public static final String	PLAY_PATH	= "path";

	public static final String	SCORE	= "Score";

	public static final String	SECTION	= "Section";

	public static final String	SPANCONTAINER	= "SpanContainer";

	public static final String	UNIT	= "Unit";
	public static final String	UNIT_NAME	= "name";

	//-------- static part --------

	/** The names of get methods. */
	protected static Map	getternames	= new HashMap();

	/** The names of set methods. */
	protected static Map	setternames	= new HashMap();

	/** The base ontologies. */
	protected static Ontology[]	base;

	static
	{
		List	bases	= new ArrayList();
		base	= (Ontology[])bases.toArray(new Ontology[bases.size()]);
	}

	// The singleton instance of this ontology
	private static Ontology	instance	= new MraOntology();

	/**
	 *  Get the singleton instance of this ontology.
	 */
	public static Ontology	getInstance()
	{
		return instance;
	}

	//-------- constructors --------

	/**
	 *  Create a new MraOntology.
	 */
	private MraOntology()
	{
		super(ONTOLOGY_NAME, base, new MraOntologyIntrospector());

		try
		{
			// Add ontology classes.
			ConceptSchema oChannelSchema	= new ConceptSchema(CHANNEL);
			add(oChannelSchema, com.moseph.mra.Channel.class);
			ConceptSchema oFragmentSchema	= new ConceptSchema(FRAGMENT);
			add(oFragmentSchema, com.moseph.mra.Fragment.class);
			ConceptSchema oPathSchema	= new ConceptSchema(PATH);
			add(oPathSchema, com.moseph.mra.Path.class);
			ConceptSchema oPieceSchema	= new ConceptSchema(PIECE);
			add(oPieceSchema, com.moseph.mra.Piece.class);
			AgentActionSchema oPlaySchema	= new AgentActionSchema(PLAY);
			add(oPlaySchema, com.moseph.mra.Play.class);
			ConceptSchema oScoreSchema	= new ConceptSchema(SCORE);
			add(oScoreSchema, com.moseph.mra.Score.class);
			ConceptSchema oSectionSchema	= new ConceptSchema(SECTION);
			add(oSectionSchema, com.moseph.mra.Section.class);
			ConceptSchema oSpanContainerSchema	= new ConceptSchema(SPANCONTAINER);
			add(oSpanContainerSchema, com.moseph.mra.SpanContainer.class);
			ConceptSchema oUnitSchema	= new ConceptSchema(UNIT);
			add(oUnitSchema, com.moseph.mra.Unit.class);

			// Add slots to classes.


			oPathSchema.add(PATH_PATHSPEC, (PrimitiveSchema)getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);


			//oPlaySchema.add(PLAY_PLAYER, (ConceptSchema)getSchema("AID"), ObjectSchema.MANDATORY);
			oPlaySchema.add(PLAY_PATH, (ConceptSchema)getSchema(PATH), ObjectSchema.MANDATORY);




			oUnitSchema.add(UNIT_NAME, (PrimitiveSchema)getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);

			// Add inheritance relations.
			oChannelSchema.addSuperSchema((ConceptSchema)getSchema("Unit"));
			oFragmentSchema.addSuperSchema((ConceptSchema)getSchema("SpanContainer"));
			oPieceSchema.addSuperSchema((ConceptSchema)getSchema("Section"));
			oScoreSchema.addSuperSchema((ConceptSchema)getSchema("Unit"));
			oSectionSchema.addSuperSchema((ConceptSchema)getSchema("Unit"));
			oSpanContainerSchema.addSuperSchema((ConceptSchema)getSchema("Channel"));

			// Store names for getter/setter methods.
			// Todo: only store modified names...
			Map	oChannelGetter	= new HashMap();
			Map	oChannelSetter	= new HashMap();
			getternames.put(oChannelSchema, oChannelGetter);
			setternames.put(oChannelSchema, oChannelSetter);
			oChannelGetter.put("name", "getName");
			oChannelSetter.put("name", "setName");

			Map	oFragmentGetter	= new HashMap();
			Map	oFragmentSetter	= new HashMap();
			getternames.put(oFragmentSchema, oFragmentGetter);
			setternames.put(oFragmentSchema, oFragmentSetter);
			oFragmentGetter.put("name", "getName");
			oFragmentSetter.put("name", "setName");

			Map	oPathGetter	= new HashMap();
			Map	oPathSetter	= new HashMap();
			getternames.put(oPathSchema, oPathGetter);
			setternames.put(oPathSchema, oPathSetter);
			oPathGetter.put("pathSpec", "getPathSpec");
			oPathSetter.put("pathSpec", "setPathSpec");

			Map	oPieceGetter	= new HashMap();
			Map	oPieceSetter	= new HashMap();
			getternames.put(oPieceSchema, oPieceGetter);
			setternames.put(oPieceSchema, oPieceSetter);
			oPieceGetter.put("name", "getName");
			oPieceSetter.put("name", "setName");

			Map	oPlayGetter	= new HashMap();
			Map	oPlaySetter	= new HashMap();
			getternames.put(oPlaySchema, oPlayGetter);
			setternames.put(oPlaySchema, oPlaySetter);
			oPlayGetter.put("player", "getPlayer");
			oPlaySetter.put("player", "setPlayer");
			oPlayGetter.put("path", "getPath");
			oPlaySetter.put("path", "setPath");

			Map	oScoreGetter	= new HashMap();
			Map	oScoreSetter	= new HashMap();
			getternames.put(oScoreSchema, oScoreGetter);
			setternames.put(oScoreSchema, oScoreSetter);
			oScoreGetter.put("name", "getName");
			oScoreSetter.put("name", "setName");

			Map	oSectionGetter	= new HashMap();
			Map	oSectionSetter	= new HashMap();
			getternames.put(oSectionSchema, oSectionGetter);
			setternames.put(oSectionSchema, oSectionSetter);
			oSectionGetter.put("name", "getName");
			oSectionSetter.put("name", "setName");

			Map	oSpanContainerGetter	= new HashMap();
			Map	oSpanContainerSetter	= new HashMap();
			getternames.put(oSpanContainerSchema, oSpanContainerGetter);
			setternames.put(oSpanContainerSchema, oSpanContainerSetter);
			oSpanContainerGetter.put("name", "getName");
			oSpanContainerSetter.put("name", "setName");

			Map	oUnitGetter	= new HashMap();
			Map	oUnitSetter	= new HashMap();
			getternames.put(oUnitSchema, oUnitGetter);
			setternames.put(oUnitSchema, oUnitSetter);
			oUnitGetter.put("name", "getName");
			oUnitSetter.put("name", "setName");

		}
		catch(OntologyException e)
		{
			// Shouldn't happen?
			throw new RuntimeException(e.toString());
		}
	}
}

//-------- helper classes --------

/**
 *  Introspector to convert java objects to and from abstract descriptors.
 */
class MraOntologyIntrospector	implements Introspector
{
	//-------- attributes --------

	/** Getter method cache (schema->(slot->method)). */
	protected static Map gettercache	= Collections.synchronizedMap(new HashMap());

	/** Setter method cache (schema->(slot->method)). */
	protected static Map settercache	= Collections.synchronizedMap(new HashMap());

	//-------- Introspector interface --------

	/**
	 *  Convert a java object into an abstract descriptor.
	 */
	public AbsObject	externalise(Object obj, ObjectSchema schema,
		Class clazz, Ontology onto)
		throws OntologyException
	{
		try
		{
			// Create abstract descriptor.
			AbsObject    ret = schema.newInstance();            

			// Iterate over slots.
			String[]     slots = schema.getNames();
			for(int i=0; i<slots.length; i++)
			{
				// Try to invoke getter method.
				Method	getter	= getGetterMethod(schema, slots[i], onto);
				Object	value	= getter.invoke(obj, new Object[0]);

				// Set value of slot.
				if(value!=null)
				{
					// Fill in values of multi slot.
					if(schema.getSchema(slots[i]) instanceof AggregateSchema)
					{
						AbsAggregate	absvalue	= new AbsAggregate(schema.getSchema(slots[i]).getTypeName());
						for(Iterator it=getIterator(value); it.hasNext(); )
						{
							absvalue.add((AbsTerm)onto.fromObject(it.next()));
						}
						AbsHelper.setAttribute(ret, slots[i], absvalue);
					}

					// Set scalar slot.
					else
					{
						AbsObject	absvalue	= onto.fromObject(value);
						AbsHelper.setAttribute(ret, slots[i], absvalue);
					}
				}
			}

			return ret;
		}
		catch(InvocationTargetException e)
		{
			if(e.getTargetException() instanceof RuntimeException)
			{
				throw (RuntimeException)e.getTargetException();
			}
			else
			{
				StringWriter	sw = new StringWriter();
				e.getTargetException().printStackTrace(new PrintWriter(sw));
				throw new RuntimeException(""+sw);
			}
		} 
		catch(IllegalAccessException e)
		{
			StringWriter	sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			throw new RuntimeException(""+sw);
		} 
	}

	/**
	 *  Convert an abstract descriptor into a java object.
	 */
	public Object	internalise(AbsObject abs, ObjectSchema schema,
		Class clazz, Ontology onto) 
		throws UngroundedException, OntologyException
	{
		try
		{
			// Create Java object.
			Object	ret	= clazz.newInstance();

			// Iterate over slots.
			String[]     slots = schema.getNames();
			for(int i=0; i<slots.length; i++)
			{
				// Get slot value.
				AbsObject	absvalue	= abs.getAbsObject(slots[i]);
				if(absvalue!=null)
				{
					Object	value	= onto.toObject(absvalue);

					// Try to invoke setter method.
					Method	setter	= getSetterMethod(schema, slots[i], onto);
					Class	param	= setter.getParameterTypes()[0];

					// Convert list to array. Hack???
					if(value instanceof jade.util.leap.List && param.isArray())
					{
						jade.util.leap.Iterator	it	= ((jade.util.leap.List)value).iterator();
						value	= Array.newInstance(param.getComponentType(),
							((jade.util.leap.List)value).size());
						for(int j=0; it.hasNext(); j++)
						{
							Array.set(value, j, it.next());
						}
					}

					// Convert Long to Integer. Hack???
					else if(value instanceof Long && (param==Integer.class || param==int.class))
					{
						value	= new Integer(((Number)value).intValue());
					}

					// Convert Double to Float. Hack???
					else if(value instanceof Double && (param==Float.class || param==float.class))
					{
						value	= new Float(((Number)value).floatValue());
					}

					setter.invoke(ret, new Object[]{value});
				}
			}

			return ret;
		}
		catch(InvocationTargetException e)
		{
			if(e.getTargetException() instanceof RuntimeException)
			{
				throw (RuntimeException)e.getTargetException();
			}
			else
			{
				StringWriter	sw = new StringWriter();
				e.getTargetException().printStackTrace(new PrintWriter(sw));
				throw new RuntimeException(""+sw);
			}
		} 
		catch(IllegalAccessException e)
		{
			StringWriter	sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			throw new RuntimeException(""+sw);
		} 
		catch(InstantiationException e)
		{
			StringWriter	sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			throw new RuntimeException(""+sw);
		} 
	}
					
	/**
	 *  Check match between given object schema and Java class.
	 */
	public void	checkClass(ObjectSchema schema, Class clazz, Ontology onto)
		throws OntologyException
	{
		// Todo: Implement checks.
	}

	//-------- helper methods --------

	/**
	 *  Find the getter method for a slot.
	 */
	protected Method	getGetterMethod(ObjectSchema schema, String slot,
		Ontology onto)	throws OntologyException
	{
		// Try to find method in cache.
		Map	getters	= (Map)gettercache.get(schema);
		if(getters==null)
		{
			getters	= new HashMap();
			gettercache.put(schema, getters);
		}
		Method	getter	= (Method)getters.get(slot);

		if(getter==null)
		{
			String	gettername	= (String)((Map)MraOntology.getternames.get(schema)).get(slot);
			Class	clazz	= onto.getClassForElement(schema.getTypeName());
			getter	= getMethod(clazz, gettername, 0);
			if(getter==null)
			{
				throw new OntologyException("Getter method "+gettername
					+" for slot "+slot+" of class "+clazz+" not found.");
			}
			getters.put(slot, getter);
		}

		return getter;
	}

	/**
	 *  Find the setter method for a slot.
	 */
	protected Method	getSetterMethod(ObjectSchema schema, String slot,
		Ontology onto)	throws OntologyException
	{
		// Try to find method in cache.
		Map	setters	= (Map)settercache.get(schema);
		if(setters==null)
		{
			setters	= new HashMap();
			settercache.put(schema, setters);
		}
		Method	setter	= (Method)setters.get(slot);

		if(setter==null)
		{
			String	settername	= (String)((Map)MraOntology.setternames.get(schema)).get(slot);
			Class	clazz	= onto.getClassForElement(schema.getTypeName());
			setter	= getMethod(clazz, settername, 1);
			if(setter==null)
			{
				throw new OntologyException("Setter method "+settername
					+" of class "+clazz+" not found.");
			}
			setters.put(slot, setter);
		}

		return setter;
	}

	/**
	 *  Get a public method of a class by name.
	 *  @param clazz	The class to search.
	 *  @param name	The name of the method to search for.
	 *  @param args	The required number of arguments.
	 *  @return	The method, or null if not found.
	 */
	protected Method	getMethod(Class clazz, String name, int args)
	{
		Method[]	ms	= clazz.getMethods();
		for(int i=0; i<ms.length; i++)
		{
			if(ms[i].getName().equals(name)
				&& ms[i].getParameterTypes().length==args)
			{
				return ms[i];
			}
		}
		return null;
	}

	/**
	 *  Get an iterator for an arbitrary collection object.
	 *  Supports iterators, enumerations, java.util.Collections,
	 *  java.util.Maps, arrays.
	 *  @param collection	The collection object.
	 *  @return An iterator over the collection.
	 */
	protected static Iterator	getIterator(Object collection)
	{
		if(collection instanceof Iterator)
		{
			return (Iterator)collection;
		}
		else if(collection instanceof Enumeration)
		{
			// Return enumeration wrapper.
			final Enumeration eoc	= (Enumeration)collection;
			return new Iterator()
			{
				public boolean	hasNext()	{return eoc.hasMoreElements();}
				public Object	next()	{return eoc.nextElement();}
				public void	remove(){throw new UnsupportedOperationException(
					"remove() not supported for enumerations");}
			};
		}
		else if(collection instanceof Collection)
		{
			return ((Collection)collection).iterator();
		}
		else if(collection instanceof Map)
		{
			return ((Map)collection).values().iterator();
		}
		else if(collection!=null && collection.getClass().isArray())
		{
			// Return array wrapper.
			final Object array	= collection;
			return new Iterator()
			{
				int i=0;
				public boolean	hasNext()	{return i<Array.getLength(array);}
				public Object	next()	{return Array.get(array, i++);}
				public void	remove()	{throw new UnsupportedOperationException(
					"remove() not supported for arrays");}
			};
		}
		else
		{
			throw new RuntimeException("Cannot iterate over "+collection);
		}
	}
}

