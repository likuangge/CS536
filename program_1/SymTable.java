////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Title:            P1
// Semester:         CS 536 - Fall 2015
//
// Author:           Dan Wortmann
// Email:            dwortmann@wisc.edu
// CS Login:         dwortman
// Lecturer's Name:  Aws Albarghouthi
//

// imports //
import java.util.*;

// notes //

public class SymTable
{
	// Class Variables //
	private LinkedList<HashMap<String,Sym>> symbolTable;
	
	/*
	 *	Class constructor.
	 */
	public SymTable()
	{
		symbolTable = new LinkedList<HashMap<String,Sym>>();
		symbolTable.addFirst(new HashMap<String,Sym>());
	}

	/*
	 *	Add the given name and sym to the first HashMap in the list
	 *
	 *	@param name - key name
	 *	@param sym	- sym type mapped to the given name
	 *	@throws DuplicateSymException - first HashMap in the list already 
	 *		contains the given name as a key
	 *	@throws EmptySymTableException - SymTable list is empty
	 */
	public void addDecl(String name, Sym sym) throws
		DuplicateSymException,
		EmptySymTableException
	{
		// Check if empty //
		if(symbolTable.size() <= 0)
		{
			throw new EmptySymTableException();
		}
		// Check parameters //
		if(name == null || sym == null)
		{	
			throw new NullPointerException();
		}
		// Check duplicate Sym
		if(symbolTable.peek().containsKey(name))
		{
			throw new DuplicateSymException();
		}
		// Otherwise add new entry to the first HashMap //
		symbolTable.peek().put(name, sym);
	}

	/*
	 *	Add a new empty hashmap to the front of the list
	 */
	public void addScope()
	{
		symbolTable.addFirst(new HashMap<String,Sym>());
	}

	/*
	 *	Return the associated Sym to the given name from the first HashMap
	 *	if it exists.
	 *
	 *	@param name - name of the key for query
	 *	@return Sym - Sym object corresponding to the given key name.
	 *	@throws EmptySymTableException - when list is empty
	 */
	public Sym lookupLocal(String name) throws EmptySymTableException
	{
		if(symbolTable.size() <= 0)
			throw new EmptySymTableException();
		// search only the head of the symbol table //
		return symbolTable.peek().get(name);
	}

	/*
	 *	Return the associated Sym to the given name from any HashMap within
	 *	the SymTable list. (Closest index first)
	 *
	 *	@param name - name of the key for query
	 *	@return Sym - Sym object corresponding to the given key name.
	 */
	public Sym lookupGlobal(String name) throws EmptySymTableException
	{
		Iterator<HashMap<String,Sym>> iter = symbolTable.iterator();

		while(iter.hasNext())
		{
			Sym lookup = iter.next().get(name);
			if(lookup != null)
			{
				return lookup;
			}
		}

		return null;
	}

	/*
	 *	Remove the HashMap from the front of the list.
	 *
	 *	@throws EmptySymTableException - when no HashMaps to remove
	 */
	public void removeScope() throws
		EmptySymTableException
	{
		if(symbolTable.size() <= 0)
			throw new EmptySymTableException();

		symbolTable.remove();
	}

	/*
	 *	Print out the Sym Table.
	 */
	public void print()
	{
		String symTableText = "";
		
		// Construct return string of SymTable //
		symTableText += "\nSym Table\n";

		// iterate SymTable list //
		Iterator<HashMap<String,Sym>> iter = symbolTable.iterator();

		while(iter.hasNext())
		{
			symTableText += (iter.next().toString() + "\n");
		}
		
		System.out.print(symTableText);
	}
}
