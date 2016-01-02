// Dan Wortmann - CS536

// Imports //

// notes //

public class P1
{
	public static void main(String[] args)
	{
		System.out.println("==================================");
		System.out.println("   Starting Symbol Table testing  ");
		System.out.println("==================================");

		// Test Sym constructor //
		String type0 = "String";
		Sym sym0 = new Sym(type0);

		// Sym getType() //
		String typeRet = sym0.getType();
		if(!typeRet.equals(type0))
		{
			System.out.println("Sym did not construct with proper name");
			System.exit(-1);
		}

		// Sym toString() //
		typeRet = sym0.toString();
		if(!typeRet.equals(type0))
		{
			System.out.println("Sym toString() method returned wrong name");
			System.exit(-1);
		}		
		// Test SymTable constructor //-------------------------------------
		SymTable symTable0 = new SymTable();

		// SymTable addDecl() //--------------------------------------------

		// Check null parameters //
		boolean passed = false;
		try
		{
			SymTable testNull1 = new SymTable();
			testNull1.addDecl(null,null);
		} 
		catch (NullPointerException e) {
			passed = true;
		}
		catch (Exception e) {
			passed = false;
		}

		if(!passed)
		{
			System.out.print("addDecl() Test 1 failed!");
			System.exit(-1);
		}
		//
		passed = false;
		try
		{
			SymTable testNull2 = new SymTable();
			testNull2.addDecl("okay",null);
		} 
		catch (NullPointerException e) {
			passed = true;
		}
		catch (Exception e) {
			passed = false;
		}

		if(!passed)
		{
			System.out.print("addDecl() Test 2 failed!");
			System.exit(-1);
		}
		//
		passed = false;
		try
		{
			SymTable testNull3 = new SymTable();
			testNull3.addDecl(null,new Sym("okay"));
		} 
		catch (NullPointerException e) {
			passed = true;
		}
		catch (Exception e) {
			passed = false;
		}

		if(!passed)
		{
			System.out.println("addDecl() Test 3 failed!");
			System.exit(-1);
		}

		// Check duplicate //-----------------------------------------------
		passed = false;	
		try
		{
			SymTable testDuplicate = new SymTable();
			testDuplicate.addDecl("double", new Sym("equal"));
			testDuplicate.addDecl("double", new Sym("equal"));
		}
		catch (DuplicateSymException e) {
			passed = true;
		}
		catch (Exception e) {
			passed = false;
		}

		if(!passed)
		{
			System.out.println("addDecl() duplicate test failed!");
			System.exit(-1);
		}

		// Check empty symTable (removeScope()) //--------------------------
		

		// Add a few entries and print() //---------------------------------
		try
		{				
			Sym sym1 = new Sym("Integer");
			Sym sym2 = new Sym("Double");
			Sym sym3 = new Sym("Float");
			Sym sym4 = new Sym("Char");
			symTable0.addDecl("test0", sym0);
			symTable0.addDecl("test1", sym1);
			symTable0.addDecl("test2", sym2);
			symTable0.addDecl("test3", sym3);
			symTable0.addDecl("scope0", sym4);

			symTable0.print();
		}
		catch (EmptySymTableException e) {
			System.out.println("The SymTable is empty currently");
		}
		catch (NullPointerException e) {
			System.out.println("At least one addDecl() parameter is null");
		}
		catch (DuplicateSymException e) {
			System.out.println("Key already exists in the head HashMap");
		}

		// Local lookup //
		passed = false;
		try
		{
			Sym r0 = symTable0.lookupLocal("String");
			Sym r1 = symTable0.lookupLocal("test0");
			Sym r2 = symTable0.lookupLocal("Double");
			Sym r3 = symTable0.lookupLocal("test2");
			Sym r4 = symTable0.lookupLocal("Char");
			Sym r5 = symTable0.lookupLocal("Garbage");

			if(	r5 == null &&
		   		r4 == null && 
		   		r3 != null && 
		   		r2 == null && 
		   		r1 != null && 
		   		r0 == null)
			{
				passed = true;
			}
			else
			{
				System.out.println(r5);
				System.out.println(r4);
				System.out.println(r3);
				System.out.println(r2);
				System.out.println(r1);
				System.out.println(r0);
			}
		}
		catch (EmptySymTableException e) {
			passed = false;
		}

		if(!passed)
		{
			System.out.println("local lookup failed!");
		}

		
		// addScope() //
		passed = false;

		try
		{
			Sym s0 = symTable0.lookupLocal("scope0");
			symTable0.addScope();
			symTable0.addDecl("scope1",new Sym("int"));
			Sym s1 = symTable0.lookupLocal("scope1");
			symTable0.addScope();
			symTable0.addDecl("scope2",new Sym("double"));
			Sym s2 = symTable0.lookupLocal("scope2");
			Sym s20 = symTable0.lookupLocal("scope0");

			if(s0 != null && s1 != null && s2 != null && s20 == null)
			{
				passed = true;
			}
		}
		catch (EmptySymTableException e) {
			System.out.println("The SymTable is empty currently");
			passed = false;

		}
		catch (NullPointerException e) {
			System.out.println("At least one addDecl() parameter is null");
			passed = false;
		}
		catch (DuplicateSymException e) {
			System.out.println("Key already exists in the head HashMap");
			passed = false;
		}

		if(!passed)
		{
			System.out.println("The Scopes were not added properly");
		}


		// lookupGlobal() //
		passed = false;
		try
		{
			Sym g0 = symTable0.lookupGlobal("scope0");
			Sym g1 = symTable0.lookupGlobal("scope1");
			Sym g2 = symTable0.lookupGlobal("scope2");
		
			if(g0 != null && g1 != null && g2 != null)
			{
				passed = true;
			}
			else
			{
				System.out.println("Global lookup has failed!");
			}
		}
		catch (EmptySymTableException e)
		{
			passed = false;
			System.out.println("Global lookup has failed!");

		}
		
		// removeScope() //
		try
		{
			symTable0.removeScope();
			Sym removed2 = symTable0.lookupGlobal("scope2");
			if(removed2 != null)
				System.out.println("removing the scope failed!");
		}
		catch (EmptySymTableException e) {
			System.out.println("removing from scope failed!");
		}
		// print() //
		symTable0.print();

		///////////////////////////////////////////////////////////////////
		// More Exhaustive testing
		//
		SymTable test = new SymTable();
		// add 25 declarations
		for(int i = 0; i < 25; i++)
		{
			try
			{
				test.addDecl("Decl" + Integer.toString(i), new Sym("int"));
			}
			catch  (DuplicateSymException e){
				System.exit(-1);
			}
			catch (EmptySymTableException e){
				System.exit(-1);
			}
		}
		test.print();
		//local lookup all declarations
		for(int i = 0; i < 25; i++)
		{
			try
			{
				Sym ret = test.lookupLocal("Decl" + Integer.toString(i));
				if(ret == null)
					System.exit(-1);
			}
			catch (EmptySymTableException e){
				System.exit(-1);
			}
		}
		// change scope
		test.addScope();

		try
		{
			if(test.lookupLocal("Decl0") != null)
				System.exit(-1);
		}
		catch (EmptySymTableException e){
			System.exit(-1);
		}
		// global lookup
		for(int i = 0; i < 25; i++)
		{
			try
			{
				Sym ret = test.lookupGlobal("Decl" + Integer.toString(i));
				if(ret == null)
					System.exit(-1);
			}
			catch (EmptySymTableException e){
				System.exit(-1);
			}
		}
		// remove many scopes //
		for(int j = 0; j < 10; j++)
		{
			try
			{
				test.removeScope();
			}
			catch (EmptySymTableException e) {
				if(j < 2)
					System.exit(-1);
			}
		}

		test.print();

		System.out.println("==================================");
		System.out.println("  Symbol Table testing finished!  ");
		System.out.println("==================================");
	}
}
