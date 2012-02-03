/**
 * 
 */
package com.moseph.mra;

import java.util.HashMap;
import java.util.Map;

/**
 * @author s0239182
 * Stores named objects of type <T>. Objects can be used (or not), and initialised (or not)
 */
public class SymbolTable<T extends NamedSymbol>
{

	Map<String,T> symbolMap;
	
	public SymbolTable()
	{
		symbolMap = new HashMap<String,T>();
	}
	
	public T get( String name )
	{
		return symbolMap.get( name );
	}
	
	public boolean isInitialised( String name )
	{
		T ent = symbolMap.get( name );
		if( ent == null ) return false;
		return ent.isDefined();
	}
	
	public boolean isInitialised( T object )
	{
		return object.isDefined();	
	}
	
	public boolean isUsed( String name )
	{
		T ent = symbolMap.get( name );
		if( ent == null ) return true;
		return ent.isUsed();
	}
	
	public boolean isUsed( T object )
	{
		return object.isUsed();
	}
	
	public void set( T obj, boolean initialised, boolean used )
	{
		symbolMap.put( obj.getName(), obj );
		if( initialised ) obj.define();
		if( used ) obj.use();
	}
	
	public void setUsed( String name )
	{
		T ent = symbolMap.get( name );
		if( ent != null ) ent.use();
	}
	public void setInitialised( String name )
	{
		T ent = symbolMap.get( name );
		if( ent != null ) ent.define();
	}
}
