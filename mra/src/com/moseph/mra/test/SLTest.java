package com.moseph.mra.test;

import com.moseph.mra.*;

import jade.content.Concept;
import jade.content.abs.*;

import junit.framework.TestCase;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.core.AID;
import com.moseph.mra.ontology.*;

/*
import examples.content.musicShopOntology.MusicShopOntology;
import examples.content.ecommerceOntology.ECommerceOntology;

public class SLTest extends TestCase
{
	SLCodec codec = new SLCodec();
	//Ontology mraOntology = MraOntology.getInstance();
	Ontology mraOntology = MRAOntology.getInstance();
	Ontology msOntology = MusicShopOntology.getInstance();
	public void setup()
	{
		
	}
	
	public void testBasicParsing()
	{
		Section a = new Section( "a");
		Section b = new Section( "b");
		a.addChild( b );
		Play p = new Play();
		p.setPath( new Path( "a/b"));
		jade.content.onto.basic.Action act = new jade.content.onto.basic.Action( new AID( ), (Concept)new Path( "a/b"));
		//p.setPlayer( new AID("test"));
		try
		{
			String s = codec.encode( (AbsContentElement)mraOntology.fromObject( act ) );
			System.out.println( s );
			//String s = "((action (agent-identifier :name a1@a.b.c.d:1099/JADE) ( Play  (Path /a/b) ) ) )";
			//AbsContentElement ace = codec.decode( mraOntology, s );
			//Object o = mraOntology.toObject( ace);
			
			//String other = "((action (agent-identifier :name a1@iceflow.inf.ed.ac.uk:1099/JADE) (SELL :buyer (agent-identifier :name a1@iceflow.inf.ed.ac.uk:1099/JADE) :item (CD :serialID 123456 :title Synchronicity :tracks (sequence (TRACK :name Synchronicity) (TRACK :name \"Every breath you take\") (TRACK :name \"King of pain\" :duration 240))) :creditcard (CREDITCARD :type VISA :number 3378892003 :expirationdate 20060126T154656683Z))))";
			//AbsContentElement ace = codec.decode( msOntology, other);
			//Object o = msOntology.toObject( ace);
			//s = codec.encode( (AbsContentElement)mraOntology.fromObject( ace ) );
			//System.out.println( s );
		}
		catch (CodecException e)
		{
			e.printStackTrace();
		}
		catch (UngroundedException e)
		{
			e.printStackTrace();
		}
		catch (OntologyException e)
		{
			e.printStackTrace();
		}
	}

}
*/
