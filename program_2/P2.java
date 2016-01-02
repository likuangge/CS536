import java.util.*;
import java.io.*;
import java_cup.runtime.*;  // defines Symbol

/**
 * This program is to be used to test the Scanner.
 * This version is set up to test all tokens, but more code is needed to test 
 * other aspects of the scanner (e.g., input that causes errors, character 
 * numbers, values associated with tokens)
 */
public class P2 {
    public static void main(String[] args) throws IOException {
        // exception may be thrown by yylex
        // test all tokens
        testAllTokens();
        CharNum.num = 1;
    
        // Test valid code/line numbers/char numbers
		myP2Testing(100000);

		// test bad strings and numbers out of range... //
		testBadTokens();
		
    }

    /**
     * testAllTokens
     *
     * Open and read from file allTokens.txt
     * For each token read, write the corresponding string to allTokens.out
     * If the input file contains all tokens, one per line, we can verify
     * correctness of the scanner by comparing the input and output files
     * (e.g., using a 'diff' command).
     */
    private static void myP2Testing(int numTokens) throws IOException {
        // Array of available testing tokens //
		// roughly 60% are valid tokens //
		String[] TOKENS = {
			"    ","++ ","/ ","== "," ", "> ","(","\t",
			"\"include in string\"\n","x1 ",
			"int "," ","< ","- ","<< ",")","         ", "   ", "\n",
			"\"include double quote \\\"\"\n"," ", "id1 ","thisID ",
			"! ",";","    ","&&","||","bool ", "        \n", " ",
			"\"include backslash \\\\\"\n","            ","   \n",
			","," ","+ ","= ","<= ","if ", "     ", " ", "      \n",
			"\"include a quote \\\"\"\n","a ", "\n","             ",
			".","-- "," ","return ","true ","{", "    \n","       ",
			"\"single quote \\'\"\n","    ","   \n","         "," ",
			"cin ","void ","}","*","else ", " ", "       "," ", " ",
			"\"\\n denotes a newline\"\n"," ",
			"cout ",">> ","!= ","while ",">= ", "\"&!88\"\n","abc123 ",
			"false ","struct "," ", "\t", "\"\"\n",
			"\"symbols str !@#$%^&*()-_+<>\"\n",
			"_validID ","_anotherValidID ",
			"__this__asWell ","\t"," "
		};
		
		// open input and output files
        PrintWriter autoIn = null;
		PrintWriter myOut = null;
		PrintWriter goldOut = null;

        try {
            autoIn = new PrintWriter(new FileWriter("autoInput.in"));
            myOut = new PrintWriter(new FileWriter("myOutput.out"));
			goldOut = new PrintWriter(new FileWriter("expectedOutput.out"));
        }         
		catch (IOException ex) {
            System.err.println("myOutput.out cannot be opened.");
            System.exit(-1);
        }

		// Random generate input file //
		Random randomGenerator = new Random();
		int randomToken = 0;
		int lineNum = 1;
		int charNum = 1;

		for(int i = 0; i < numTokens; i++)
		{
			randomToken = randomGenerator.nextInt(TOKENS.length);
			String buf = TOKENS[randomToken];
			autoIn.print(buf);

			// expected output //
			// <token/literal>:<line>:<char>
			if(!buf.replaceAll("[\n\r\040\t]","").equals(""))
				goldOut.println(buf.replaceAll("[\n]","").trim()+" : "+lineNum+" : "+charNum);

			charNum += buf.length();

			// increment lines //
			if(buf.contains("\n"))
			{
				lineNum++;
				charNum = 1;
			}
			else if(i % 7 == 0)
			{
				autoIn.println(" ");
				lineNum++;
				charNum = 1;
			}

			// force spaces //
			if(i % 2 == 0)
			{
				autoIn.print(" ");
				charNum++;
			}
		}

		// close autoInput and open as readable file //
		autoIn.close();
		goldOut.close();
		FileReader inFile = null;
        try {
            inFile = new FileReader("autoInput.in");
		} catch (FileNotFoundException ex) {
            System.err.println("File autoIn.in not found.");
            System.exit(-1);
        } 

        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();

		String literal = null;
		int line = 0;
		int c = 0;
		while (token.sym != sym.EOF) {
            switch (token.sym) {
            case sym.BOOL:
				literal = "bool";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;
                break;
			case sym.INT:
				literal = "int";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.VOID:
				literal = "void";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.TRUE:
				literal = "true";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.FALSE:
				literal = "false";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.STRUCT:
				literal = "struct";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.CIN:
				literal = "cin";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.COUT:
				literal = "cout";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;				
            case sym.IF:
				literal = "if";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.ELSE:
				literal = "else";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

               	break;
            case sym.WHILE:
				literal = "while";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.RETURN:
				literal = "return";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.ID:
				literal = ((IdTokenVal)token.value).idVal;
				line = ((IdTokenVal)token.value).linenum;
				c = ((IdTokenVal)token.value).charnum;

                break;
            case sym.INTLITERAL:
				literal = Integer.toString(
					((IntLitTokenVal)token.value).intVal
					);
				line = ((IntLitTokenVal)token.value).linenum;
				c = ((IntLitTokenVal)token.value).charnum;

               	break;
            case sym.STRINGLITERAL:
				literal = ((StrLitTokenVal)token.value).strVal;
				line = ((StrLitTokenVal)token.value).linenum;
				c = ((StrLitTokenVal)token.value).charnum;

                break;    
            case sym.LCURLY:
				literal = "{";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.RCURLY:
				literal = "}";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.LPAREN:
				literal = "(";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.RPAREN:
				literal = ")";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.SEMICOLON:
				literal = ";";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.COMMA:
				literal = ",";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.DOT:
				literal = ".";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.WRITE:
				literal = "<<";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.READ:
				literal = ">>";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;				
            case sym.PLUSPLUS:
				literal = "++";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.MINUSMINUS:
				literal = "--";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;	
            case sym.PLUS:
				literal = "+";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.MINUS:
				literal = "-";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.TIMES:
				literal = "*";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.DIVIDE:
				literal = "/";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.NOT:
				literal = "!";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.AND:
				literal = "&&";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.OR:
				literal = "||";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.EQUALS:
				literal = "==";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.NOTEQUALS:
				literal = "!=";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.LESS:
				literal = "<";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.GREATER:
				literal = ">";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.LESSEQ:
				literal = "<=";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.GREATEREQ:
				literal = ">=";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
			case sym.ASSIGN:
				literal = "=";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
			default:
				//outFile.println("UNKNOWN TOKEN");
			
            } // end switch
			myOut.println(literal+" : "+line+" : "+c);
            token = scanner.next_token();
        } // end while
	myOut.close();
}

    private static void testBadTokens() throws IOException {
		// open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("badTokens.in");
            outFile = new PrintWriter(new FileWriter("badTokens.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File badTokens.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("badTokens.out cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();

		// Verify output to hand made file //
		String literal = null;
		int line = 0;
		int c = 0;
		while (token.sym != sym.EOF) {
            switch (token.sym) {
            case sym.BOOL:
				literal = "bool";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;
                break;
			case sym.INT:
				literal = "int";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.VOID:
				literal = "void";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.TRUE:
				literal = "true";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.FALSE:
				literal = "false";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.STRUCT:
				literal = "struct";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.CIN:
				literal = "cin";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.COUT:
				literal = "cout";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;				
            case sym.IF:
				literal = "if";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.ELSE:
				literal = "else";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

               	break;
            case sym.WHILE:
				literal = "while";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.RETURN:
				literal = "return";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.ID:
				literal = ((IdTokenVal)token.value).idVal;
				line = ((IdTokenVal)token.value).linenum;
				c = ((IdTokenVal)token.value).charnum;

                break;
            case sym.INTLITERAL:
				literal = Integer.toString(
					((IntLitTokenVal)token.value).intVal
					);
				line = ((IntLitTokenVal)token.value).linenum;
				c = ((IntLitTokenVal)token.value).charnum;

               	break;
            case sym.STRINGLITERAL:
				literal = ((StrLitTokenVal)token.value).strVal;
				line = ((StrLitTokenVal)token.value).linenum;
				c = ((StrLitTokenVal)token.value).charnum;

                break;    
            case sym.LCURLY:
				literal = "{";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.RCURLY:
				literal = "}";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.LPAREN:
				literal = "(";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.RPAREN:
				literal = ")";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.SEMICOLON:
				literal = ";";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.COMMA:
				literal = ",";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.DOT:
				literal = ".";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.WRITE:
				literal = "<<";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.READ:
				literal = ">>";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;				
            case sym.PLUSPLUS:
				literal = "++";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.MINUSMINUS:
				literal = "--";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;	
            case sym.PLUS:
				literal = "+";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.MINUS:
				literal = "-";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.TIMES:
				literal = "*";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.DIVIDE:
				literal = "/";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.NOT:
				literal = "!";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.AND:
				literal = "&&";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.OR:
				literal = "||";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.EQUALS:
				literal = "==";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.NOTEQUALS:
				literal = "!=";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.LESS:
				literal = "<";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.GREATER:
				literal = ">";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.LESSEQ:
				literal = "<=";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
            case sym.GREATEREQ:
				literal = ">=";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
			case sym.ASSIGN:
				literal = "=";
				line = ((TokenVal)token.value).linenum;
				c = ((TokenVal)token.value).charnum;

                break;
			default:
				//outFile.println("UNKNOWN TOKEN");
			
            } // end switch
			outFile.println(literal+" : "+line+" : "+c);
            token = scanner.next_token();
        } // end while
		outFile.close();

	}

    /**
     * testAllTokens
     *
     * Open and read from file allTokens.txt
     * For each token read, write the corresponding string to allTokens.out
     * If the input file contains all tokens, one per line, we can verify
     * correctness of the scanner by comparing the input and output files
     * (e.g., using a 'diff' command).
     */
    private static void testAllTokens() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("allTokens.in");
            outFile = new PrintWriter(new FileWriter("allTokens.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File allTokens.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("allTokens.out cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        while (token.sym != sym.EOF) {
            switch (token.sym) {
            case sym.BOOL:
                outFile.println("bool" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum); 
                break;
			case sym.INT:
                outFile.println("int" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.VOID:
                outFile.println("void" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.TRUE:
                outFile.println("true" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum); 
                break;
            case sym.FALSE:
                outFile.println("false" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum); 
                break;
            case sym.STRUCT:
                outFile.println("struct" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum); 
                break;
            case sym.CIN:
                outFile.println("cin" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum); 
                break;
            case sym.COUT:
                outFile.println("cout" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;				
            case sym.IF:
                outFile.println("if" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.ELSE:
                outFile.println("else" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.WHILE:
                outFile.println("while" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.RETURN:
                outFile.println("return" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.ID:
                outFile.println(((IdTokenVal)token.value).idVal + "\t//line #: " + ((IdTokenVal)token.value).linenum + "\tchar#: " + ((IdTokenVal)token.value).charnum);
                break;
            case sym.INTLITERAL:  
                outFile.println(((IntLitTokenVal)token.value).intVal + "\t//line #: " + ((IntLitTokenVal)token.value).linenum + "\tchar#: " + ((IntLitTokenVal)token.value).charnum);
                break;
            case sym.STRINGLITERAL: 
                outFile.println(((StrLitTokenVal)token.value).strVal + "\t//line #: " + ((StrLitTokenVal)token.value).linenum + "\tchar#: " + ((StrLitTokenVal)token.value).charnum);
                break;    
            case sym.LCURLY:
                outFile.println("{" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.RCURLY:
                outFile.println("}" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.LPAREN:
                outFile.println("(" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.RPAREN:
                outFile.println(")" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.SEMICOLON:
                outFile.println(";" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.COMMA:
                outFile.println("," + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.DOT:
                outFile.println("." + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.WRITE:
                outFile.println("<<" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.READ:
                outFile.println(">>" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;				
            case sym.PLUSPLUS:
                outFile.println("++" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.MINUSMINUS:
                outFile.println("--" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;	
            case sym.PLUS:
                outFile.println("+" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.MINUS:
                outFile.println("-" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.TIMES:
                outFile.println("*" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.DIVIDE:
                outFile.println("/" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.NOT:
                outFile.println("!" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.AND:
                outFile.println("&&" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.OR:
                outFile.println("||" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.EQUALS:
                outFile.println("==" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.NOTEQUALS:
                outFile.println("!=" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.LESS:
                outFile.println("<" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.GREATER:
                outFile.println(">" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.LESSEQ:
                outFile.println("<=" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
            case sym.GREATEREQ:
                outFile.println(">=" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
			case sym.ASSIGN:
                outFile.println("=" + "\t//line #: " + ((TokenVal)token.value).linenum + "\tchar#: " + ((TokenVal)token.value).charnum);
                break;
			default:
				outFile.println("UNKNOWN TOKEN");
            } // end switch

            token = scanner.next_token();
        } // end while
        outFile.close();
    }
}

