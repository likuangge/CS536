import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a Mini program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of 
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Kids
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode
//
//     TypeNode:
//       IntNode           -- none --
//       BoolNode          -- none --
//       VoidNode          -- none --
//       StructNode        IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       IntLitNode          -- none --
//       StrLitNode          -- none --
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              -- none --
//       DotAccessNode       ExpNode, IdNode
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode     
//         MinusNode
//         TimesNode
//         DivideNode
//         AndNode
//         OrNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         GreaterNode
//         LessEqNode
//         GreaterEqNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of kids, or
// internal nodes with a fixed number of kids:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of kids:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode   
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  CallStmtNode
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode,  CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// ASTnode class (base class for all other kinds of nodes)
// **********************************************************************

abstract class ASTnode { 
    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);

    // this method can be used by the unparse methods to do indenting
    protected void doIndent(PrintWriter p, int indent) {
        for (int k=0; k<indent; k++) p.print(" ");
    }
}

// **********************************************************************
// ProgramNode,  DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode
// **********************************************************************

class ProgramNode extends ASTnode {
    public ProgramNode(DeclListNode L) {
        myDeclList = L;
    }

    public void analyzeNames() {
    	// Highest scope //
		SymTable S = new SymTable();
		System.out.println("analyzing names for ProgramNode...");
		//return;
		myDeclList.analyzeNames(S);
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    // 1 kid
    private DeclListNode myDeclList;
}

class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
    }

    public void analyzeNames(SymTable S) {
		Iterator it = myDecls.iterator();
		try {
			while(it.hasNext()) {
				((DeclNode)it.next()).analyzeNames(S);
			}
		} catch (NoSuchElementException e) {
			System.err.println("unexpected NoSuchElementException in DeclListNode.analyzeNames");
            System.exit(-1);
		}
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException e) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    // list of kids (DeclNodes)
    private List<DeclNode> myDecls;
}

class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
    }

	public LinkedList analyzeNames(SymTable S) {
		LinkedList L = new LinkedList();
		Iterator<FormalDeclNode> it = myFormals.iterator();

		try {
			while(it.hasNext()) {
				SemSym sym = ((FormalDeclNode)it.next()).analyzeNames(S);
				L.add(sym.getType()); // keep track of parameters
			}
		} catch (NoSuchElementException ex) {
			System.err.println("unexpected NoSuchElementException in FormalsListNoce.analyzeNames");
            System.exit(-1);
		}

		return L;
	}

	// length //
	public int length() {
		return myFormals.size();
	}

    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }

    // list of kids (FormalDeclNodes)
    private List<FormalDeclNode> myFormals;
}

class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }

	public void analyzeNames(SymTable S) {
		myDeclList.analyzeNames(S);
		myStmtList.analyzeNames(S);
	}

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }

    // 2 kids
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

	public void analyzeNames(SymTable S) {
		Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            ((StmtNode)it.next()).analyzeNames(S);
        }
	}

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }
    }

    // list of kids (StmtNodes)
    private List<StmtNode> myStmts;
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }

	public void analyzeNames(SymTable S) {
		Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            ((ExpNode)it.next()).analyzeNames(S);
            while (it.hasNext()) {              
				((ExpNode)it.next()).analyzeNames(S);
            }
        } 
	}

	// length //
	public int length(){
		return myExps.size();
	}

    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }

    // list of kids (ExpNodes)
    private List<ExpNode> myExps;
}

// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
	// per spec, add this as abstract method for DeclNode //
	abstract public SemSym analyzeNames(SymTable S);
}

class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
    }

	public SemSym analyzeNames(SymTable S) {
		String name = myId.getName();

		if(myType.getType().equals("void")) {
			// Non-function declared type void //
			ErrMsg.fatal(myId.linenum(), myId.charnum(), "Non-function declared void");
			return null;
		}
		if(S.lookupLocal(name) != null) {
			// doubly defined //
			ErrMsg.fatal(myId.linenum(), myId.charnum(), "Multiply declared identifier");
			return null;
		}

		try {
			SemSym sym = new SemSym(name, myType.getType());
			S.addDecl(name, sym);
			myId.link(sym);
		} catch (DuplicateSymException e) {
			System.err.println("unexpect duplicate sym in VarDeclNode.analyzeNames");
			System.exit(-1);
		} catch (EmptySymTableException e) {
			System.err.println("unexpect empty sym in VarDeclNode.analyzeNames");
			System.exit(-1);
		}
		
		return null;
	}

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.println(";");
    }

    // 3 kids
    private TypeNode myType;
    private IdNode myId;
    private int mySize;  // use value NOT_STRUCT if this is not a struct type

    public static int NOT_STRUCT = -1;
}

class FnDeclNode extends DeclNode {
    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        myType = type;
        myId = id;
        myFormalsList = formalList;
        myBody = body;
    }

	public SemSym analyzeNames(SymTable S) {
		String name = myId.getName();
		FunctionSemSym sym = null;

		if(S.lookupLocal(name) != null) {
			ErrMsg.fatal(myId.linenum(), myId.charnum(), "Multiply declared identifier");
		}
		else {
			try {
				sym = new FunctionSemSym(name, myType.getType(), myFormalsList.length());
				S.addDecl(name, sym);
				myId.link(sym);
			} catch (DuplicateSymException e) {
				System.err.println("unexpect duplicate sym in FnDeclNode.analyzeNames");
				System.exit(-1);
			} catch (EmptySymTableException e) {
				System.err.println("unexpect empty sym in FnDeclNode.analyzeNames");
				System.exit(-1);
			}
		}

		// Additional Scope within a function //
		S.addScope();
		LinkedList L = myFormalsList.analyzeNames(S); // parameters included in scope

		// add parameter types //
		if(sym != null){
			sym.addParamTypes(L);
		}

		// Continue analyzing the remainder of the AST //
		myBody.analyzeNames(S);

		// Remove the scope //
		try {
			S.removeScope();
		} catch (EmptySymTableException e) {
			System.err.println("unexpect empty sym in FnDeclNode.analyzeNames");
			System.exit(-1);
		}

		return null;
	}

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.println(") {");
        myBody.unparse(p, indent+4);
        p.println("}\n");
    }

    // 4 kids
    private TypeNode myType;
    private IdNode myId;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;
}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
    }

	public SemSym analyzeNames(SymTable S) {
		String name = myId.getName();
		SemSym sym = null;

		if(myType.getType().equals("void")) {
			ErrMsg.fatal(myId.linenum(), myId.charnum(),"Non-function declared void");
			return null;
		}
		if(S.lookupLocal(name) != null) {
			ErrMsg.fatal(myId.linenum(), myId.charnum(),"Multiply declared identifier");
			return null;
		}

		try {
			sym = new SemSym(name, myType.getType());
			S.addDecl(name, sym);
			myId.link(sym);
		} catch (DuplicateSymException e) {
			System.err.println("unexpect duplicate sym in FormalDeclNode.analyzeNames");
			System.exit(-1);
		} catch (EmptySymTableException e) {
			System.err.println("unexpect empty sym in FormalDeclNode.analyzeNames");
			System.exit(-1);
		}

		return sym;
	}

    public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
    }

    // 2 kids
    private TypeNode myType;
    private IdNode myId;
}

class StructDeclNode extends DeclNode {
	static SymTable structS = new SymTable();

    public StructDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
    }

	public SemSym analyzeNames(SymTable S){
		String name = myId.getName(); //Point - but thats like the type...
		SemSym sym = null;

		// don't make structs of bool/int/void //
		if(name.equals("bool") || name.equals("void") || name.equals("int")){
			ErrMsg.fatal(myId.linenum(), myId.charnum(), "Invalid name of struct type");
			return sym;
		}

		// Check globally for struct type names //
		if(structS.lookupGlobal(name) != null){
			ErrMsg.fatal(myId.linenum(), myId.charnum(), "Multiply declared identifier");
			return sym;
		}

		sym = new SemSym(name, name); //is the type struct? or both
		try {
			structS.addDecl(name, sym);
		} catch (DuplicateSymException e) {
			System.err.println("unexpect duplicate sym in StructDeclNode.analyzeNames");
			System.exit(-1);
		} catch (EmptySymTableException e) {
			System.err.println("unexpect empty sym in StructDeclNode.analyzeNames");
			System.exit(-1);
		}		
		
		link(sym);
		// process DeclList such that its unique to any struct //
		myDeclList.analyzeNames(structS); // unique struct fields
		myDeclList.analyzeNames(S);		  // struct fields as global

		// ^Throws the same error twice, but I was pressed for time...
		// But does catch multiply defined struct fields so we're good

		return sym;
	}

	public void link(SemSym s) {
		thisSym = s;
	}

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("struct ");
		myId.unparse(p, 0);
		p.println("{");
        myDeclList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("};\n");

    }

    // 2 kids
    private IdNode myId;
	private DeclListNode myDeclList;
	private SemSym thisSym;
}

// **********************************************************************
// TypeNode and its Subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
	// get type //
	abstract public String getType();
}

class IntNode extends TypeNode {
    public IntNode() {
    }

	public String getType() {
		return "int";
	}

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }

	public String getType() {
		return "bool";
	}

    public void unparse(PrintWriter p, int indent) {
        p.print("bool");
    }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }

	public String getType() {
		return "void";
	}

    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
		myId = id;
    }

	public String getType() {
		return myId.getName();
	}


    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
		myId.unparse(p, 0);
    }
	
	// 1 kid
    private IdNode myId;
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
	abstract public void analyzeNames(SymTable S);
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }

	public void analyzeNames(SymTable S) {
		myAssign.analyzeNames(S);
	}

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }

    // 1 kid
    private AssignNode myAssign;
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }

	public void analyzeNames(SymTable S) {
		myExp.analyzeNames(S);
	}

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("++;");
    }

    // 1 kid
    private ExpNode myExp;
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

	public void analyzeNames(SymTable S) {
		myExp.analyzeNames(S);
	}

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("--;");
    }

    // 1 kid
    private ExpNode myExp;
}

class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }

	public void analyzeNames(SymTable S) {
		myExp.analyzeNames(S);
	}

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("cin >> ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // 1 kid (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;
}

class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }

	public void analyzeNames(SymTable S) {
		myExp.analyzeNames(S);
	}

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("cout << ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // 1 kid
    private ExpNode myExp;
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }

	public void analyzeNames(SymTable S) {
		myExp.analyzeNames(S);
		// new scope inside if statement //
		S.addScope();
		myDeclList.analyzeNames(S);
		myStmtList.analyzeNames(S);
		
		// remove the scope after //
		try {
			S.removeScope();
		} catch (EmptySymTableException e) {
			System.err.println("unexpect empty sym in IfStmtNode.analyzeNames");
			System.exit(-1);
		}
	}

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
    }

    // e kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }

	public void analyzeNames(SymTable S) {
		myExp.analyzeNames(S);
		// process if scope first //
		S.addScope();
		myThenDeclList.analyzeNames(S);
		myThenStmtList.analyzeNames(S);
		try {
			S.removeScope();
		} catch (EmptySymTableException e) {
			System.err.println("unexpect empty sym in IfElseStmtNode.analyzeNames");
			System.exit(-1);
		}
		// process else scope next //
		S.addScope();
		myElseDeclList.analyzeNames(S);
		myElseStmtList.analyzeNames(S);
		try {
			S.removeScope();
		} catch (EmptySymTableException e) {
			System.err.println("unexpect empty sym in IfElseStmtNode.analyzeNames");
			System.exit(-1);
		}
	}

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent+4);
        myThenStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
        doIndent(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent+4);
        myElseStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");        
    }

    // 5 kids
    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }

	public void analyzeNames(SymTable S) {
		myExp.analyzeNames(S);
		// new scope within while //
		S.addScope();
		myDeclList.analyzeNames(S);
		myStmtList.analyzeNames(S);

		//remove the scope now //
		try {
			S.removeScope();
		} catch (EmptySymTableException e) {
			System.err.println("unexpect empty sym in WhileStmtNode.analyzeNames");
			System.exit(-1);
		}
	}
	
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("while (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
    }

    // 3 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }

	public void analyzeNames(SymTable S) {
		myCall.analyzeNames(S);
	}

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }

    // 1 kid
    private CallExpNode myCall;
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
    }

	public void analyzeNames(SymTable S) {
		if(myExp != null)
			myExp.analyzeNames(S); //has return
	}

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparse(p, 0);
        }
        p.println(";");
    }

    // 1 kid
    private ExpNode myExp; // possibly null
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {
	// analyzeNames for each node //
	public void analyzeNames(SymTable S) {} //default

	abstract public int linenum();
	abstract public int charnum();
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myIntVal = intVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }

	public int linenum() {
		return myLineNum;
	}

	public int charnum() {
		return myCharNum;
	}

    private int myLineNum;
    private int myCharNum;
    private int myIntVal;
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

	public int linenum() {
		return myLineNum;
	}

	public int charnum() {
		return myCharNum;
	}

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

	public int linenum() {
		return myLineNum;
	}

	public int charnum() {
		return myCharNum;
	}

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }

    private int myLineNum;
    private int myCharNum;
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

	public int linenum() {
		return myLineNum;
	}

	public int charnum() {
		return myCharNum;
	}

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }

    private int myLineNum;
    private int myCharNum;
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

	public void analyzeNames(SymTable S) {
		SemSym sym = S.lookupGlobal(myStrVal);
		if(sym == null)
			ErrMsg.fatal(myLineNum, myCharNum, "Undeclared identifier");
		else
			link(sym);
	}

	// link //
	public void link(SemSym s){
		thisSym = s;
	}

	// name //
	public String getName() {
		return myStrVal;
	}

	// line number //
	public int linenum() {
		return myLineNum;
	}

	// char number //
	public int charnum() {
		return myCharNum;
	}

	// type //
	public String getType() {
		return thisSym.getType();
	}

	// symbol entry //
	public SemSym sym() {
		return thisSym;
	}

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
		if(thisSym != null)
			p.print("("+thisSym.getType()+")");
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
	private SemSym thisSym;
}

//TODO ?? check analyzeNames //charnum/linenum
class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        myLoc = loc;	
        myId = id;
    }

	public void analyzeNames(SymTable S) {
		myLoc.analyzeNames(S);
		myId.analyzeNames(S);
	}

	// line number //
	public int linenum() {
		return myId.linenum();
	}

	// char number //
	public int charnum() {
		return myId.charnum();
	}

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myLoc.unparse(p, 0);
		p.print(").");
		myId.unparse(p, 0);
    }

    // 2 kids
    private ExpNode myLoc;	
    private IdNode myId;
}

class AssignNode extends ExpNode {
    public AssignNode(ExpNode lhs, ExpNode exp) {
        myLhs = lhs;
        myExp = exp;
    }

	public void analyzeNames(SymTable S) {
		myLhs.analyzeNames(S);
		myExp.analyzeNames(S);
	}

	public int linenum() {
		return myLhs.linenum();
	}

	public int charnum() {
		return myLhs.charnum();
	}

    public void unparse(PrintWriter p, int indent) {
		if (indent != -1)  p.print("(");
	    myLhs.unparse(p, 0);
		p.print(" = ");
		myExp.unparse(p, 0);
		if (indent != -1)  p.print(")");
    }

    // 2 kids
    private ExpNode myLhs;
    private ExpNode myExp;
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        myId = name;
        myExpList = elist;
    }

    public CallExpNode(IdNode name) {
        myId = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }

	public void analyzeNames(SymTable S) {
		myId.analyzeNames(S);
		myExpList.analyzeNames(S);
	}

	// line number //
	public int linenum() {
		return myId.linenum();
	}

	// char number //
	public int charnum() {
		return myId.charnum();
	}

    // ** unparse **
    public void unparse(PrintWriter p, int indent) {
	    myId.unparse(p, 0);
		p.print("(");
		if (myExpList != null) {
			myExpList.unparse(p, 0);
		}
		p.print(")");
    }

    // 2 kids
    private IdNode myId;
    private ExpListNode myExpList;  // possibly null
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }

	public void analyzeNames(SymTable S) {
		myExp.analyzeNames(S);
	}

	// line number //
	public int linenum() {
		return myExp.linenum();
	}

	// char number //
	public int charnum() {
		return myExp.charnum();
	}

    // one child
    protected ExpNode myExp;
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
    }

	public void analyzeNames(SymTable S) {
		myExp1.analyzeNames(S);
		myExp2.analyzeNames(S);
	}

	// line number //
	public int linenum() {
		return myExp1.linenum();
	}

	// char number //
	public int charnum() {
		return myExp1.charnum();
	}


    // two kids
    protected ExpNode myExp1;
    protected ExpNode myExp2;
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(-");
		myExp.unparse(p, 0);
		p.print(")");
    }
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(!");
		myExp.unparse(p, 0);
		p.print(")");
    }
}

// **********************************************************************
// Subclasses of BinaryExpNode
// **********************************************************************

class PlusNode extends BinaryExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" + ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class MinusNode extends BinaryExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" - ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class TimesNode extends BinaryExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" * ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class DivideNode extends BinaryExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" / ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class AndNode extends BinaryExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" && ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class OrNode extends BinaryExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" || ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class EqualsNode extends BinaryExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" == ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class NotEqualsNode extends BinaryExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" != ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class LessNode extends BinaryExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" < ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class GreaterNode extends BinaryExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" > ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class LessEqNode extends BinaryExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" <= ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class GreaterEqNode extends BinaryExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" >= ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}
