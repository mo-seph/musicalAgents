package com.moseph.mra.test;

import com.moseph.mra.*;
import com.moseph.mra.logic.ConnectiveAnd;
import com.moseph.mra.logic.ConnectiveExpression;
import com.moseph.mra.logic.ConnectiveOr;
import com.moseph.mra.logic.Term;

import junit.framework.TestCase;

public class TermTest extends TestCase
{
	Term t = new Term( "T", 1.0 );
	Term f = new Term( "F", 0.0 );;
	Term t1 = new Term( "T", 1.0 );
	Term f1 = new Term( "F", 0.0 );;
	Term t2 = new Term( "T", 1.0 );
	Term f2 = new Term( "F", 0.0 );;
	ConnectiveExpression goodAnd = new ConnectiveAnd( t, t1, t2 );
	ConnectiveExpression badAnd = new ConnectiveAnd( t, t1, f2);
	ConnectiveExpression goodOr = new ConnectiveOr( t, f1, f2 );
	ConnectiveExpression badOr = new ConnectiveOr( f, f1, f2 );
	
	@Override
	protected void setUp() throws Exception
	{
	}

	/*
	 * Test method for 'com.moseph.mra.Term.isTrue()'
	 */
	public void testIsTrue()
	{
		assertTrue( "A term with 1.0 is not true", t.isTrue() );
		assertFalse( "A term with value 0.0 is not false", f.isTrue() );
	}
	
	public void testConditionals()
	{

		assertTrue( "And with three trues is true", goodAnd.isTrue() );
		assertFalse( "and with two trues and a false is false", badAnd.isTrue() );
		assertTrue( "Or with one true and two falses is true", goodAnd.isTrue() );
		assertFalse( "Or with three falses is false", badAnd.isTrue() );
	}
	
	public void testNestedConditionals()
	{
		assertTrue( "(And)Nested true ands are true", new ConnectiveAnd( goodAnd, goodAnd ).isTrue() );
		assertFalse( "(And)Nested opposite ands are false", new ConnectiveAnd( goodAnd, badAnd ).isTrue() );
		assertFalse( "(And)Nested false ands are false", new ConnectiveAnd( badAnd, badAnd ).isTrue() );
		assertTrue( "(And)Nested true and and true or is true", new ConnectiveAnd( goodAnd, goodOr ).isTrue() );
		assertFalse( "(And)Nested true and and false or is false", new ConnectiveAnd( goodAnd, badOr ).isTrue() );
		assertFalse( "(And)Nested false and and true or is false", new ConnectiveAnd( badAnd, goodOr ).isTrue() );
		assertFalse( "(And)Nested false and and false or is false", new ConnectiveAnd( badAnd, badOr ).isTrue() );
		
		assertTrue( "(Or)Nested true ands are true", new ConnectiveOr( goodAnd, goodAnd ).isTrue() );
		assertTrue( "(Or)Nested opposite ands are true", new ConnectiveOr( goodAnd, badAnd ).isTrue() );
		assertFalse( "(Or)Nested false ands are false", new ConnectiveOr( badAnd, badAnd ).isTrue() );
		assertTrue( "(Or)Nested true and and true or is true", new ConnectiveOr( goodAnd, goodOr ).isTrue() );
		assertTrue( "(Or)Nested true and and false or is true", new ConnectiveOr( goodAnd, badOr ).isTrue() );
		assertTrue( "(Or)Nested false and and true or is true", new ConnectiveOr( badAnd, goodOr ).isTrue() );
		assertFalse( "(Or)Nested false and and false or is false", new ConnectiveOr( badAnd, badOr ).isTrue() );
		
		
	}

}
