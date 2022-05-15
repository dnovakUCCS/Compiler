/*
STUDENTS MUST CHANGE THE 4-CHAR MNEMONICS HERE TO THEIR OWN 5-CHAR MNEMONICS
SAMPLE syntactic CODE FOR SP22 Compiler Class.
See TEMPLATE at end of this file for the framework to be used for
ALL non-terminal methods created.
Two methods shown below are added to LEXICAL, where reserve and mnemonic
tables are accessible:
Returns the integer token code for the given mnemonic 
public int codeFor(String mnemonic){
    return mnemonics.LookupName(mnemonic);
}
Returns the Reserve Word for the given mnemonic
public String reserveFor(String mnemonic){
    return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
}
Allows control of whether tokens are printed within Lexical or not
public void setPrintToken(boolean on){
    printToken = on;
}
Add 2 lines which prints each token found by GetNextToken:
            if (printToken) {
                System.out.println("\t" + result.mnemonic + " | \t" + 
String.format("%04d", result.code) + " | \t" + result.lexeme);
            }
 */
package ADT;
import myCompiler.Interpreter;
/**
 *
 * @author abrouill
 */
public class Syntactic {
    private String filein;              //The full file path to input file
    private SymbolTable symbolList;     //Symbol table storing ident/const
    private QuadTable quads; 			//Quad table
    private Interpreter interp;			//interpreter
    private Lexical lex;                //Lexical analyzer 
    private Lexical.token token;        //Next Token retrieved 
    private boolean traceon;            //Controls tracing mode 
    private int level = 0;              //Controls indent for trace mode
    private boolean anyErrors;          //Set TRUE if an error happens 
    private final int symbolSize = 250;
    private final int quadSize = 1500;
    private int Minus1Index;
    private int Plus1Index;
    
    //constructor
    public Syntactic(String filename, boolean traceOn) {
    	
        filein = filename; //inputfile
        traceon = traceOn;
        symbolList = new SymbolTable(symbolSize);
        
        Minus1Index = symbolList.AddSymbol("-1", symbolList.constantkind, -1);
        Plus1Index = symbolList.AddSymbol("1", symbolList.constantkind, 1);
        
        lex = new Lexical(filein, symbolList, true);
        
        quads = new QuadTable(quadSize);
        
        interp = new Interpreter();
        
        lex.setPrintToken(traceOn);
        anyErrors = false;
        
    }
//The interface to the syntax analyzer, initiates parsing
// Uses variable RECUR to get return values throughout the non-terminal methods    
  //Interface to the syntax analyzer, initiates parsing
    public void parse() {
    // make filename pattern for symbol table and quad table output later
    String filenameBase = filein.substring(0, filein.length() - 4);
    System.out.println(filenameBase);
    int recur = 0;
    // prime the pump, get first token
    token = lex.GetNextToken();
    // call PROGRAM
    recur = Program();
    // done, so add the final STOP quad
    quads.AddQuad(interp.opcodeFor("STOP"), 0, 0, 0);
    //print ST, QUAD before execute
    symbolList.PrintSymbolTable(filenameBase + "ST-before.txt");
    quads.PrintQuadTable(filenameBase + "QUADS.txt");
    //interpret
    if (!anyErrors) {
    	interp.InterpretQuads(quads, symbolList, traceon, filenameBase + "TRACE.txt");
    } 
    else {
    	System.out.println("Errors, unable to run program.");
    }
    
    
    	symbolList.PrintSymbolTable(filenameBase + "ST-after.txt");
    }
//Non Terminal     
    private int ProgIdentifier() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        // This non-term is used to uniquely mark the program identifier
        if (token.code == lex.codeFor("IDENT")) {
            // Because this is the progIdentifier, it will get a 'p' type to prevent re-use as a var
            symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'p', 0);
            //move on
            token = lex.GetNextToken();
        }
        return recur;
    }
//Non Terminals
    private int Program() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Program", true);
        if (token.code == lex.codeFor("PRGRM")) {
            token = lex.GetNextToken();
            recur = ProgIdentifier();
            if (token.code == lex.codeFor("SCOLN")) {
                token = lex.GetNextToken();
                recur = Block();
                if (token.code == lex.codeFor("PERID")) {
                    if (!anyErrors) {
                        System.out.println("Success.");
                    } 
                    else {
                        System.out.println("Compilation failed.");
                    }
                } 
                else {
                    error(lex.reserveFor("PERID"), token.lexeme);
                }
            } else {
                error(lex.reserveFor("SCOLN"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("PRGRM"), token.lexeme);
        }
        trace("Program", false);
        return recur;
    }
//Non Terminal    
    //starts at the block of code to then begin and signifies when the code ends
    //grabs the 
    private int Block() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Block", true);
        if (token.code == lex.codeFor("BEGIN")) {
            token = lex.GetNextToken();
            recur = Statement();
            while ((token.code == lex.codeFor("SCOLN")) && (!lex.EOF()) && (!anyErrors)) {
                token = lex.GetNextToken();
                recur = Statement();
            }
            if (token.code == lex.codeFor("_END_")) {
                token = lex.GetNextToken();
            } else {
                error(lex.reserveFor("_END_"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("BEGIN"), token.lexeme);
        }
        trace("Block", false);
        return recur;
    }
    
    
//Not a NT, but used to shorten Statement code body   
    //<variable> $COLON-EQUALS <simple expression>
    //starts decides the paths to go down right now only does the assignment operator
    //this function grabs the the operator then chooses what to do with it
    //then passes back to statement 
    private int handleAssignment() {
    	
    	int left, right;
    	
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("handleAssignment", true);
        //have ident already in order to get to here, handle as Variable
        left = Variable();  //Variable moves ahead, next token ready 
        if (token.code == lex.codeFor("ASIGN")) {
            token = lex.GetNextToken();
            right = SimpleExpression();
            
            quads.AddQuad(interp.opcodeFor("MOV"), right, 0, left);
            
        } else {
            error(lex.reserveFor("ASIGN"), token.lexeme);
        }
        

        
        trace("handleAssignment", false);
        return recur;
    }

    
    //<simple expression> -> [<sign>]  <term>  {<addop>  <term>}*
    //This function when reached will process the points that have simple expresions
    //This currently happens after the assignment variable
    //simple expressions can start with a '(' or a '-' or $letter
    //checks first for the sign
    //if there is no sign then there has to be a term
    //it then grabs the next token and sends it to term
    //when it gets back it will the only option is to continue the expression with and add or subtract
    //it goes add then it grabs a variable after because they have to come in pairs if not it breaks
    //it will repeat the add then term till the simple expressing is done
    private int SimpleExpression() {
    	int left, right, opcode, temp;
    	int signVar = 0;
        if (anyErrors) {
            return -1;
        }
        trace("SimpleExpression", true);
        
        //if negative then it will set signVar as -1 if not then it will set as 1
        if(token.code == lex.codeFor("PLUS_") || token.code == lex.codeFor("MINUS")) {
        	signVar = Sign();
        }
        
        left = Term(); //gets variable
        
        //adds the sign change operation to the quad
        if(signVar == -1) {
        	quads.AddQuad(Mulop(), left, Minus1Index, left);
        }
        //parses out the diferent maths operations and there order adding them properly to the quad
        while(token.code == lex.codeFor("PLUS_") || token.code == lex.codeFor("MINUS")) {
        	
            opcode = Addop();//add operation
            right = Term(); //gets variable
            temp = GenSymbol();
            quads.AddQuad(opcode, left, right, temp);
            left = temp;
        }
        

        trace("SimpleExpression", false);
        return left;
    }
//Non Terminal    
    private int Statement() {
        int recur = 0;
        
        if (anyErrors) {
            return -1;
        }
        trace("Statement", true);
        
        if (token.code == lex.codeFor("BEGIN")) {
            token = lex.GetNextToken();
            recur = Statement();
            while ((token.code == lex.codeFor("SCOLN")) && (!lex.EOF()) && (!anyErrors)) {
                token = lex.GetNextToken();
                recur = Statement();
            }
            if (token.code == lex.codeFor("_END_")) {
                token = lex.GetNextToken();
            } else {
                error(lex.reserveFor("_END_"), token.lexeme);
            }
        }
        else if (token.code == lex.codeFor("IDENT")) {  //must be an ASSIGNMENT

            recur = handleAssignment();
        }
        else if(token.code == lex.codeFor("_IF__")){              //must be an ASSUGNMENT
        	int branchQuad, patchElse;

            token = lex.GetNextToken(); // move past ‘if’
            branchQuad = relexpression(); //tells where branchTarget to be set 
            // to jump around TRUE part
            if (token.code == lex.codeFor("THEN_")){ //all ok, continue
                
            	token = lex.GetNextToken(); // move past ‘then’
                recur = Statement(); //all if body quads are genned
                if (token.code == lex.codeFor("ELSE_")){ //have to jump around to ??
                	  
                	token = lex.GetNextToken(); // move past ELSE
                	patchElse = quads.nextQuad(); //save backfill quad to jump around
                	// ELSE body, target is unknown now
                	quads.AddQuad(interp.opcodeFor("JMP"), 0, 0, 0); //jump_op
                	//backfill the FALSE IF branch jump 
                	quads.setQuadOp3(branchQuad,quads.nextQuad());//conditional jump 
                	recur = Statement(); // gen ELSE body quads
                	// fill in end of ELSE part
                	quads.setQuadOp3(patchElse, quads.nextQuad());
                }
                else    //no ELSE, so fix IF branch, fall thru
                	quads.setQuadOp3(branchQuad, quads.nextQuad());
                } //if the THEN was found
                else // error, no THEN
               	 	error("No Then", token.lexeme);  
            
        }
        else if(token.code == lex.codeFor("DOWHL")) {
        	int saveTop, branchQuad;
            // declare above int saveTop, branchQuad
        	token = lex.GetNextToken(); //move past this token
            saveTop = quads.nextQuad(); //Before generating code, save top of loop
            // where unconditional branch will jump
            branchQuad = relexpression(); //tells where branchTarget to be set 

              
            //token = lex.GetNextToken();
            
            recur = Statement();         //the loop body is processed
            quads.AddQuad(interp.opcodeFor("JMP"), 0, 0, saveTop);//jump to top of loop
            //backfill the forward branch
            //Quad function for ease- set 3rd op
            quads.setQuadOp3(branchQuad,quads.nextQuad());//conditional jumps nextQuad 

        	
        }//end of while structure
        else if(token.code == lex.codeFor("PRNTL")){
        	
        	recur = handlePrintln();
        	
        }
        else if(token.code == lex.codeFor("READL")){
        	
        	recur = handleReadln();
        	
        }
        else { // if/elses should look for the other possible statement starts...  
            //  but not until PART B
            
        	error("Statement start", token.lexeme);
        }
        
//        if(token.code == lex.codeFor("_END_")){
//        	token = lex.GetNextToken();
//        	if(token.code == lex.codeFor("SCOLN")) {
//        		token = lex.GetNextToken();
//        	}
//        	else {
//        		error("No SimiColon", token.lexeme);
//        	}
//        }
        
        trace("Statement", false);
        return recur;
    }
/**
 * *************************************************
*/
    /*     UTILITY FUNCTIONS USED THROUGHOUT THIS CLASS */
// error provides a simple way to print an error statement to standard output
//     and avoid reduncancy
    private void error(String wanted, String got) {
        anyErrors = true;
        System.out.println("ERROR: Expected " + wanted + " but found " + got);
    }
    // trace simply RETURNs if traceon is false; otherwise, it prints an
    // ENTERING or EXITING message using the proc string
    private void trace(String proc, boolean enter) {
        String tabs = "";
        if (!traceon) {
            return;
        }
        if (enter) {
            tabs = repeatChar(" ", level);
            System.out.print(tabs);
            System.out.println("--> Entering " + proc);
            level++;
        } else {
            if (level > 0) {
                level--;
            }
            tabs = repeatChar(" ", level);
            System.out.print(tabs);
            System.out.println("<-- Exiting " + proc);
        }
    }
// repeatChar returns a string containing x repetitions of string s; 
//    nice for making a varying indent format
    private String repeatChar(String s, int x) {
        int i;
        String result = "";
        for (i = 1; i <= x; i++) {
            result = result + s;
        }
        return result;
    }

    /*  Template for all the non-terminal method bodies
	private int exampleNonTerminal(){
	        int recur = 0;
	        if (anyErrors) {
	            return -1;
	        }
	        trace("NameOfThisMethod", true);
	// unique non-terminal stuff
	        trace("NameOfThisMethod", false);
	        return recur;
	}  
    
    */    
    
    //<variable> -> <identifier>
    //calls identifier function
    private int Variable(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Variable", true);
        // unique non-terminal stuff
        
        
        recur = Identifier();
        
        trace("Variable", false);
        return recur;
    }  
    
    //<factor> -> <unsigned constant> | <variable> | $LPAR    <simple expression>    $RPAR
    //splits off the different types constant, variable, parenthesis, catches improper syntax
    //sends consts to UnsignedConstant, variables to variable, and grabs the parenthesis and goes back to simple expression
    private int Factor(){
    	
    	
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Factor", true);
        
       
        if(token.code == lex.codeFor("INTID") || token.code == lex.codeFor("FLTID")) {
        	recur = UnsignedConstant();
        }
        else if(token.code == lex.codeFor("IDENT")) {
        	recur = Variable();
        }
        else if(token.code == lex.codeFor("LPTHS")) {
        	token = lex.GetNextToken();
        	recur = SimpleExpression();
        	
        	if(token.code == lex.codeFor("RPTHS")) {//gets the pairing right parenthesis on its way back if not errors
        		token = lex.GetNextToken();
        	}
        	else {
        		//System.out.println("ERROR: NO RIGHT PARENTHESIS!\n");
        		error("Right Parenthesis", token.lexeme);
        	}
        }
        else {
        	//System.out.println("ERROR: SYNTAX ERROR! \n");
        	error("Term", token.lexeme);
        }
        
        
        trace("Factor", false);
        return recur;
    }  
   
    //<term> -> <factor> {<mulop>  <factor> }*
    //We know the entering lexeme must be a factor and this could be on the left side of the simply expression
    //if the next token is a * or / then goes to mulop to get opcode
    //then it needs to get the token on the right side. 
    //generates a symbol to store it to for the later use because this is the right side of something else
    //adds to quad table
    private int Term(){
        int recur = 0;
        int left, right, opcode, temp;
        
        if (anyErrors) {
            return -1;
        }
        trace("Term", true);

        left = Factor();
        
        while(token.code == lex.codeFor("MULTP") || token.code == lex.codeFor("DIVID")) {
        	
        	opcode = Mulop(); //mulop returns opcode and for either * or /
        	
        	right = Factor(); //gets right term or more to the expression

            temp = GenSymbol();
            quads.AddQuad(opcode, left, right, temp);
            left = temp;
        }
        
        trace("Term", false);
        return left;
    } 	
    
    //<relexpression> -> <simple expression>  <relop>  <simple expression>
    //enters could be a simple expression to start going left to right
    //then there is a conditional
    //then there could be another simple expresion
    //generates a temp symbol to store it because it is the right of something
    //adds to quad table
    int relexpression() {
    	
    	int left, right, saveRelop, temp;
    	
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("relexpression", true);
    	

    	left = SimpleExpression(); //get the left operand, our ‘A’
    	saveRelop = relop();          //returns tokenCode of rel operator //getnexttoken inside
    	right = SimpleExpression();//right operand, our ‘B’
    	temp = GenSymbol();        //Create temp var in symbol table
    	quads.AddQuad(interp.opcodeFor("SUB"), left, right, temp); //compare
    	recur = quads.nextQuad();       //Quad index that needs branch fix
    	quads.AddQuad(relopToOpcode(saveRelop),temp,0,0);//target unset 
    	
    	
    	trace("relexpression", true);
    	
    	return recur;
    	
    }
    
    
    
    //calls unsigned number
    //<unsigned constant>-> <unsigned number>
    private int UnsignedConstant(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("UnsignedConstant", true);
		recur = UnsignedNumber();
        trace("UnsignedConstant", false);
        return recur;
    } 
    
    //    <unsigned number>-> $FLOAT | $INTEGER
    //    						**note: as defined for Lexical
    //checks if its a int or float
    //passes back the corresponding location in symbol list
    //gets next token
    private int UnsignedNumber(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("UnsignedNumber", true);
        	
        if(token.code == lex.codeFor("INTID")) { //$float $integer
        	
        	recur = symbolList.LookupSymbol(token.lexeme); //gets location in symbol list
        	
        }
        else if (token.code == lex.codeFor("FLTID")) {
        		
        	recur = symbolList.LookupSymbol(token.lexeme); //gets location in symbol list
        	
        }
        else {
        	error("UnsignedNumber", token.lexeme);
        }
        token = lex.GetNextToken(); 
        
        		
        trace("UnsignedNumber", false);
        return recur;
    } 
    
    //<mulop> -> $MULTIPLY | $DIVIDE
    //checks if multiplies; if so then gets the opcode for it
    //or if divides; if so then gets the opcode for it
    //if neither error 
    //Processed the token so it grabs next token
    private int Mulop(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Mulop", true);
        
        if(token.code == lex.codeFor("MULTP")) {
        	recur = interp.opcodeFor("MUL");
        }
        else if(token.code == lex.codeFor("DIVID")) {
        	recur = interp.opcodeFor("DIV");
        }
        else {
        	error("Mulop", token.lexeme);
        }
        
        token = lex.GetNextToken();
        
        trace("Mulop", false);
        return recur;
    } 

    //<addop> -> $PLUS | $MINUS
    //if add or sub then
    //returns corresponding opcode
    //gets next token
    private int Addop(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Addop", true);
		if(token.code == lex.codeFor("PLUS_")) {
			
			recur = interp.opcodeFor("ADD");
			
		}
		else if(token.code == lex.codeFor("MINUS")) {
			recur = interp.opcodeFor("SUB");
		}
		else {
			error("Addop", token.lexeme);
		}
		
		token = lex.GetNextToken();    
		
        trace("Addop", false);
        return recur;
    } 
    
    //<sign> -> $PLUS | $MINUS
    //if add negative or positive sign then 
    //returns corresponding opcode
    //gets next token
    private int Sign(){
        int recur = 0;
        
        trace("Sign", true);
        
        if(token.code == lex.codeFor("PLUS_")) {
			
			recur = 1; //Not negative
			
		}
        else if(token.code == lex.codeFor("MINUS")) {
        	
        	recur = -1; //negative
        	
        }
        else {
        	error("Sign", token.lexeme);
        }
        
        token = lex.GetNextToken();
        
        trace("Sign", false);
        return recur;
    }
    
    //the false branch
    //gets the opcode for the false branch 
    //based on the passed in correct one
    //passes back the corresponding relational operator
    private int relopToOpcode(int relop) {
    	int recur = 0;
    	
    	trace("RelopToOpcode", true);
    	
    	switch(relop)
    	{
    		case 42: //9
    			//jnz
    			recur = interp.opcodeFor("JNZ");
    			break;
    		case 43: //12
    			//jz
    			recur = interp.opcodeFor("JZ");
    			break;	
    		case 39: //11
    			//jnn
    			recur = interp.opcodeFor("JNN");
    			break;	
    		case 38: //10
    			//jnp
    			recur = interp.opcodeFor("JNP");
    			break;	
    		case 41: //13
    			//jp
    			recur = interp.opcodeFor("JP");
    			break;	
    		case 40: //14
    			//jn
    			recur = interp.opcodeFor("JN");
    			break;	
    		default:
    			error("RelopToOpcode", token.lexeme);
    			break;
    	
    	}
    	
    	
    	
    	trace("RelopToOpcode", false);
    	
    	return recur;
    }
    
    //<relop>    ->   $EQ | $LSS | $GTR | $NEQ | $LEQ | $GEQ
    //checks if its a valid comparator 
    //returns its token.code
	private int relop() {
    	 int recur = 0;
         
         trace("Relop", true);
         	
         if(token.code == lex.codeFor("COMPR")) {
        	 //interp.opcodeFor("JZ");
        	 recur = lex.codeFor("COMPR");
         }
         else if(token.code == lex.codeFor("NOTEQ")) {
        	//interp.opcodeFor("JNZ");
        	 recur = lex.codeFor("NOTEQ");
        }
		else if(token.code == lex.codeFor("LESSR")) {
			//interp.opcodeFor("JN");
			recur = lex.codeFor("LESSR");
		}
		else if(token.code == lex.codeFor("GRETR")) {
			//interp.opcodeFor("JP");
			recur = lex.codeFor("GRETR");
		}
		else if(token.code == lex.codeFor("LESEQ")) {
			//interp.opcodeFor("JNP");
			recur = lex.codeFor("LESEQ");
		}
		else if(token.code == lex.codeFor("GRTEQ")) {
			//interp.opcodeFor("JNN");
			recur = lex.codeFor("GRTEQ");
		}
		else {
			error("Relop", token.lexeme);
		}
         
         token = lex.GetNextToken();
         
         trace("Relp", false);
         return recur;
    }
    
    //if identifier; then gets location in symbol table
	//if not then error
	//Processed the identifier so grabs next token
    private int Identifier(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Identifier", true);
        if(token.code == lex.codeFor("IDENT")) {
        	
        	recur = symbolList.LookupSymbol(token.lexeme);
        	
        }
        else {
        	error("IDENT", token.mnemonic);
        }
        
        token = lex.GetNextToken();
        
        trace("Identifier", false);
        return recur;
    }  
    
    //handles the println operation
    //gets the location of the string in the symbol table and returns it
    private int handlePrintln() {
    	
    	int recur = 0;
    	int toprint = 0;
    	
    	if (anyErrors) {
    	return -1;
    	}
    	
    	trace("handlePrintln", true);
    	
    	//got here from a PRINT token, move past it...
    	token = lex.GetNextToken();
    	
    	//look for ( stringconst, ident, simpleexp )
    	if (token.code == lex.codeFor("LPTHS")) {
    		
    		//move on
    		token = lex.GetNextToken();
    		
    		if ((token.code == lex.codeFor("STRID"))) { // {
    			
    			// save index for string literal or identifier
    			toprint = symbolList.LookupSymbol(token.lexeme);
    			
    			//move on
    			token = lex.GetNextToken();
    			
    		} 
    		else {
    			toprint = SimpleExpression();
    		}
    		quads.AddQuad(interp.opcodeFor("PRINT"), 0, 0, toprint);
    		//now need right ")"
    		if (token.code == lex.codeFor("RPTHS")) {
    			//move on
    			token = lex.GetNextToken();
    		} 
    		else {
    			error(lex.reserveFor("RPTHS"), token.lexeme);
    		}
    	} 
    	else {
    		error(lex.reserveFor("LPTHS"), token.lexeme);
    	}
    	// end LPTHS group
    	
    	
    	trace("handlePrintln", false);
    	
    	return recur;
    }
    
    //handles the readln operation
    //gets the location of the string in the symbol table and returns it
    private int handleReadln() {
    	
    	int recur = 0;
    	int toprint = 0;
    	
    	if (anyErrors) {
    	return -1;
    	}
    	
    	trace("handleReadln", true);
    	
    	//got here from a PRINT token, move past it...
    	token = lex.GetNextToken();
    	
    	//look for ( stringconst, ident, simpleexp )
    	if (token.code == lex.codeFor("LPTHS")) {
    		
    		//move on
    		token = lex.GetNextToken();
    		
    		if ((token.code == lex.codeFor("IDENT"))) { //|| (token.code == lex.codeFor("IDNT"))) {
    			
    			// save index for string literal or identifier
    			toprint = symbolList.LookupSymbol(token.lexeme);
    			
    			//move on
    			token = lex.GetNextToken();
    			
    		} 
    		else {
    			error("IDENT", token.lexeme);
    		}
    		quads.AddQuad(interp.opcodeFor("READ"), 0, 0, toprint);
    		//now need right ")"
    		if (token.code == lex.codeFor("RPTHS")) {
    			//move on
    			token = lex.GetNextToken();
    		} 
    		else {
    			error(lex.reserveFor("RPTHS"), token.lexeme);
    		}
    	} 
    	else {
    		error(lex.reserveFor("LPTHS"), token.lexeme);
    	}
    	// end LPTHS group
    	
    	
    	trace("handleReadln", false);
    	
    	return recur;
    }
    


	//Adds a new, specially named temporary symbol to the symbol table
	int genCount = 0;
	private int GenSymbol() {
		String temp = "@" + Integer.toString(genCount);
		genCount++;
		symbolList.AddSymbol(temp, 'v', 0);
		return symbolList.LookupSymbol(temp);
	}

}
