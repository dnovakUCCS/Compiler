
/*
 The following code is provided by the instructor for the completion of PHASE 2 
of the compiler project for CS4100.
STUDENTS ARE TO PROVIDE THE FOLLOWING CODE FOR THE COMPLETION OF THE ASSIGNMENT:
1) Initialize the 2 reserve tables, which are fields in the Lexical class,
    named reserveWords and mnemonics.  Create the following functions,
    whose calls are in the lexical constructor:
        initReserveWords(reserveWords);
        initMnemonics(mnemonics);
2) 
 */
package ADT;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
/**
 *
 * @author abrouill
 */
import java.io.*;
public class Lexical {
    private File file;                        //File to be read for input
    private FileReader filereader;            //Reader, Java reqd
    private BufferedReader bufferedreader;    //Buffered, Java reqd
    private String line;                      //Current line of input from file   
    private int linePos;                      //Current character position
    //  in the current line
    private SymbolTable saveSymbols;          //SymbolTable used in Lexical
    //  sent as parameter to construct
    private boolean EOF;                      //End Of File indicator
    private boolean echo;                     //true means echo each input line
    private boolean printToken;               //true to print found tokens here
    private int lineCount;                    //line #in file, for echo-ing
    private boolean needLine;                 //track when to read a new line
    //Tables to hold the reserve words and the mnemonics for token codes
    private ReserveTable reserveWords = new ReserveTable(50); //a few more than # reserves
    private ReserveTable mnemonics = new ReserveTable(50); //a few more than # reserves
//global char
    char currCh;
    //constructor
    public Lexical(String filename, SymbolTable symbols, boolean echoOn) {
        saveSymbols = symbols;  //map the initialized parameter to the local ST
        echo = echoOn;          //store echo status
        lineCount = 0;          //start the line number count
        line = "";              //line starts empty
        needLine = true;        //need to read a line
        printToken = false;     //default OFF, do not print tokesn here
        //  within GetNextToken; call setPrintToken to
        //  change it publicly.
        linePos = -1;           //no chars read yet
        //call initializations of tables
        initReserveWords(reserveWords);
        initMnemonics(mnemonics);
        //set up the file access, get first character, line retrieved 1st time
        try {
            file = new File(filename);    //creates a new file instance  
            filereader = new FileReader(file);   //reads the file  
            bufferedreader = new BufferedReader(filereader);  //creates a buffering character input stream  
            EOF = false;
            currCh = GetNextChar();
        } catch (IOException e) {
            EOF = true;
            e.printStackTrace();
        }
    }
    // token class is declared here, no accessors needed
    public class token {
        public String lexeme;
        public int code;
        public String mnemonic;
        token() {
            lexeme = "";
            code = 0;
            mnemonic = "";
        }
    }
    private token dummyGet() {
        token result = new token();
        result.lexeme = "" + currCh; //have the first char
        currCh = GetNextChar();
        result.code = 0;
        result.mnemonic = "DUMMY";
        return result;
    }
    private final int UNKNOWN_CHAR = 99;
    public final int _GRTR = 38;
    public final int _LESS = 39;
    public final int _GREQ = 40;
    public final int _LEEQ = 41;
    public final int _EQLS = 42;
    public final int _NEQL = 43;
    private final int IDENT_ID = 50;
    private final int INTEGER_ID = 51;
    private final int FLOAT_ID = 52;
    private final int STRING_ID = 53;
    //@@@ These are nice for syntax to call later 
    // given a mnemonic, find its token code value
    public int codeFor(String mnemonic) {
        return mnemonics.LookupName(mnemonic);
    }
    // given a mnemonic, return its reserve word
    public String reserveFor(String mnemonic) {
        return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
    }
    
    // Public access to the current End Of File status
    public boolean EOF() {
        return EOF;
    }
    // DEBUG enabler, turns on token printing inside of GetNextToken
    public void setPrintToken(boolean on) {
        printToken = on;
    }
    /* @@@ */
    private void initReserveWords(ReserveTable reserveWords) {
        reserveWords.Add("GO_TO", 0);
        reserveWords.Add("INTEGER", 1);
        reserveWords.Add("TO", 2);
        reserveWords.Add("DO", 3);
        reserveWords.Add("IF", 4);
        reserveWords.Add("THEN", 5);
        reserveWords.Add("ELSE", 6);
        reserveWords.Add("FOR", 7);
        reserveWords.Add("OF", 8);
        reserveWords.Add("PRINTLN", 9);
        reserveWords.Add("READLN", 10);
        reserveWords.Add("BEGIN", 11);
        reserveWords.Add("END", 12);
        reserveWords.Add("VAR", 13);
        reserveWords.Add("DOWHILE", 14);
        reserveWords.Add("PROGRAM", 15);
        reserveWords.Add("LABEL", 16);
        reserveWords.Add("REPEAT", 17);
        reserveWords.Add("UNTIL", 18);
        reserveWords.Add("PROCEDURE", 19);
        reserveWords.Add("DOWNTO", 20);
        reserveWords.Add("FUNCTION", 21);
        reserveWords.Add("RETURN", 22);
        reserveWords.Add("FLOAT", 23);
        reserveWords.Add("STRING", 24);
        reserveWords.Add("ARRAY", 25);
        // add the rest of the reserve words here
        // add 1 and 2-char tokens here
        reserveWords.Add("/", 30);
        reserveWords.Add("*", 31);
        reserveWords.Add("+", 32);
        reserveWords.Add("-", 33);
        reserveWords.Add("(", 34);
        reserveWords.Add(")", 35);
        reserveWords.Add(";", 36);
        reserveWords.Add(":=", 37);
        reserveWords.Add(">", 38);
        reserveWords.Add("<", 39);
        reserveWords.Add(">=", 40);
        reserveWords.Add("<=", 41);
        reserveWords.Add("=", 42);
        reserveWords.Add("<>", 43);
        reserveWords.Add(",", 44);
        reserveWords.Add("[", 45);
        reserveWords.Add("]", 46);
        reserveWords.Add(":", 47);
        reserveWords.Add(".", 48);
        reserveWords.Add("?", 99);
    }
    /* @@@ */
    private void initMnemonics(ReserveTable mnemonics) {
// add 5-character student created mnemonics corresponding to reserve values
        mnemonics.Add("GOTO_", 0);
        mnemonics.Add("INTGE", 1);
        
        mnemonics.Add("_TO__", 2);
        mnemonics.Add("_DO__", 3);
        mnemonics.Add("_IF__", 4);
        mnemonics.Add("THEN_", 5);
        mnemonics.Add("ELSE_", 6);
        mnemonics.Add("_FOR_", 7);
        mnemonics.Add("_OF__", 8);
        mnemonics.Add("PRNTL", 9);
        mnemonics.Add("READL", 10);
        mnemonics.Add("BEGIN", 11);
        mnemonics.Add("_END_", 12);
        mnemonics.Add("_VAR_", 13);
        mnemonics.Add("DOWHL", 14);
        mnemonics.Add("PRGRM", 15);
        mnemonics.Add("LABEL", 16);
        mnemonics.Add("RPEAT", 17);
        mnemonics.Add("UNTIL", 18);
        mnemonics.Add("PRCED", 19);
        mnemonics.Add("DOWNT", 20);
        mnemonics.Add("FNCTN", 21);
        mnemonics.Add("RETUR", 22);
        mnemonics.Add("FLOAT", 23);
        mnemonics.Add("STRNG", 24);
        mnemonics.Add("ARRAY", 25);
        // add the rest of the reserve words here
        // add 1 and 2-char tokens here
        mnemonics.Add("DIVID", 30);
        mnemonics.Add("MULTP", 31);
        mnemonics.Add("PLUS_", 32);
        mnemonics.Add("MINUS", 33);
        mnemonics.Add("LPTHS", 34);
        mnemonics.Add("RPTHS", 35);
        mnemonics.Add("SCOLN", 36);
        mnemonics.Add("ASIGN", 37);
        mnemonics.Add("GRETR", 38);
        mnemonics.Add("LESSR", 39);
        mnemonics.Add("GRTEQ", 40);
        mnemonics.Add("LESEQ", 41);
        mnemonics.Add("COMPR", 42);
        mnemonics.Add("NOTEQ", 43);
        mnemonics.Add("COMA_", 44);
        mnemonics.Add("LSQBR", 45);
        mnemonics.Add("RSQBR", 46);
        mnemonics.Add("COLON", 47);
        mnemonics.Add("PERID", 48);  
        
        mnemonics.Add("IDENT", 50);  
        mnemonics.Add("INTID", 51);
        mnemonics.Add("FLTID", 52);
        mnemonics.Add("STRID", 53);
        
        
        mnemonics.Add("UNKWN", 99);
    }
    // Character category for alphabetic chars, upper and lower case
    private boolean isLetter(char ch) {
        return (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')));
    }
    // Character category for 0..9 
    private boolean isDigit(char ch) {
        return ((ch >= '0') && (ch <= '9'));
    }
    // Category for any whitespace to be skipped over
    // space, tab, and newline
    private boolean isWhitespace(char ch) {
        return ((ch == ' ') || (ch == '\t') || (ch == '\n'));
    }
    // Returns the VALUE of the next character without removing it from the
    //    input line.  Useful for checking 2-character tokens that start with
    //    a 1-character token.
    private char PeekNextChar() {
        char result = ' ';
        if ((needLine) || (EOF)) {
            result = ' '; //at end of line, so nothing
        } else // 
        {
            if ((linePos + 1) < line.length()) { //have a char to peek
                result = line.charAt(linePos + 1);
            }
        }
        return result;
    }
    // Called by GetNextChar when the characters in the current line
    // buffer string (line) are used up.
    private void GetNextLine() {
        try {
            line = bufferedreader.readLine();  //returns a null string when EOF
            if ((line != null) && (echo)) {
                lineCount++;
                System.out.println(String.format("%04d", lineCount) + " " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line == null) {    // The readLine returns null at EOF, set flag
            EOF = true;
        }
        linePos = -1;      // reset vars for new line if we have one
        needLine = false;  // we have one, no need
        //the line is ready for the next call to get a char with GetNextChar
    }
    // Returns the next character in the input file, returning a 
    // /n newline character at the end of each input line or at EOF
    public char GetNextChar() {
        char result;
        if (needLine) //ran out last time we got a char, so get a new line
        {
            GetNextLine();
        }
        //try to get char from line buff
        // if EOF there is no new char, just return endofline, DONE
        if (EOF) {
            result = '\n';
            needLine = false;
        } else {
            // if there are more characters left in the input buffer
            if ((linePos < line.length() - 1)) { //have a character available
                linePos++;
                result = line.charAt(linePos);
            } else {
                //need a new line, but want to return eoln on this call first
                result = '\n';
                needLine = true; //will read a new line on next GetNextChar call
            }
        }
        return result;
    }
    final char comment_start1 = '{';
    final char comment_end1 = '}';
    final char comment_start2 = '(';
    final char comment_startend = '*';
    final char comment_end2 = ')';
    // skips over any comment as if it were whitespace, so it is ignored
    public char skipComment(char curr) {
        // if this is the start of a comment...
        if (curr == comment_start1) {
            curr = GetNextChar();
            // loop until the end of comment or EOF is reached
            while ((curr != comment_end1) && (!EOF)) {
                curr = GetNextChar();
            }
            // if the file ended before the comment terminated
            if (EOF) {
                System.out.println("Comment not terminated before End Of File");
            } else {
                // keep getting the next char
                curr = GetNextChar();
            }
        } else {
            // this is for the 2-character comment start, different start/end
            if ((curr == comment_start2) && (PeekNextChar() == comment_startend)) {
                curr = GetNextChar(); // get the second
                curr = GetNextChar(); // into comment or end of comment
                //while comment end is not reached
                while ((!((curr == comment_startend) && (PeekNextChar() == comment_end2))) && (!EOF)) {
                    curr = GetNextChar();
                }
                // EOF before comment end
                if (EOF) {
                    System.out.println("Comment not terminated before End Of File");
                } else {
                    curr = GetNextChar();          //must move past close
                    curr = GetNextChar();          //must get following
                }
            }
        }
        return (curr);
    }
    //reads past any white space, blank lines, comments
    public char skipWhiteSpace() {
        do {
            while ((isWhitespace(currCh)) && (!EOF)) {
                currCh = GetNextChar();
            }
            currCh = skipComment(currCh);
        } while (isWhitespace(currCh) && (!EOF));
        return currCh;
    }
    // returns TRUE if ch is a prefix to a 2-character token like := or <=
    private boolean isPrefix(char ch) {
        return ((ch == ':') || (ch == '<') || (ch == '>'));
    }
    // returns TRUE if ch is the string delmiter
    private boolean isStringStart(char ch) {
        return ch == '"';
    }
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// Student supplied methods
    
    final char kindv = 'V'; //constant for add symbol pass in for a variable
    final char kindc = 'C'; //constant for add symbol pass in for a constant
    final char identValue = 0; //value to pass in for the id

    //return is the token class, there are no pass in parameters
    //this function will parse an identifier out of the line
    private token getIdent() {
    	
    	token result = new token(); //creates local token
    	result.code = IDENT_ID; //we know its an ident if its here
    	
    	result.lexeme = "" + currCh; //adds first char to the token
    	currCh = GetNextChar(); //gets next char
    	
    	//check if the char is a letter, digit, money symbol, or underscore in a row till it hits a charicter that is not one then it stops
    	while((isLetter(currCh)) || (isDigit(currCh)) || (currCh == '$') || (currCh == '_')) { 
    		result.lexeme += currCh; //if condition is met adds to the lexeme string
        	currCh = GetNextChar(); //cycles to next char
    	}
    	
    	
    	 result = checkTruncate(result); //checks if it needs to truncate then does it if needed

    	 result.code = reserveWords.LookupName(result.lexeme); //checks the reserve word table for the lexeme
    	//if it didn't find one it then must be a symbol
    	if (result.code == ReserveTable.notFound) {
    		result.code = IDENT_ID; //change back to this value because you know its not in reserve table
    		saveSymbols.AddSymbol(result.lexeme, kindv, identValue);//checks adds into symbol if possible
    	}


        return result; //returns the token
    }
    
    //this function will parse a number out of the line
    //it will return the token with no input parameters
    private token getNumber() {
    	//parses a number out of the line

    	token result = new token(); //creates local token
    	
    	result.code = INTEGER_ID; //starts as an into till otherwise changed
    	
        result.lexeme = "" + currCh; //adds first char to lexeme
    	currCh = GetNextChar(); //gets next char in line
    	
    	//used to get the value of the string for the symbol table
		double lexemeValueDouble = 0.0;
		int lexemeValueInt = 0;
    	
    	while(isDigit(currCh)) { //if the char is a digit it adds it to the lexeme and moves to the next char to check again stops when it hits a char thats not a int
    		result.lexeme += currCh; //adds the char to the lexeme
    		currCh = GetNextChar(); //gets next char
    	}
    	
    	
    	//checks if the next char is a period to make the number a float if it does continues the logic if not then it is an int and thats the end the logic
    	if(currCh == '.') {
    		
    		result.lexeme += currCh; //adds the period to the lexeme
    		currCh = GetNextChar();//gets the next char in the string
    		result.code = FLOAT_ID; //we know now that it is a float
    		
    		//keeps adding the next char if it is a digit to the lexeme if there is one
    		while(isDigit(currCh)) {
    			
    			result.lexeme += currCh;//gets the next char in the string
        		currCh = GetNextChar();//we know now that it is a float
        		
        	}
    		
    		//checks if it is in scientific form
    		//if so we know the next char after the e is if it is positive or negative
    		//then the amount of decimals the e is for
    		if(currCh == 'E' || currCh == 'e') {
    			result.lexeme += currCh;//gets the next char in the string
        		currCh = GetNextChar();//we know now that it is a float
    			
        		//checks if it is negative
        		if(currCh == '-') {
        			result.lexeme += currCh;
            		currCh = GetNextChar();
        		}
        		//checks if it is positive
        		else if(currCh == '+') {
        			result.lexeme += currCh;
        			currCh = GetNextChar();
        		}
        		
        		//looks for digits after the e keeps going to it runs into a non digit char if it does't have at least one digit that is a problem and its solved by adding a zero
        		if(isDigit(currCh)) {
        			while(isDigit(currCh)) {
            			result.lexeme += currCh;
                		currCh = GetNextChar();
                	}
        		}
        		else {
        			result.lexeme += 0;
        			System.out.println("Warning no value after the e for scientific notation added a 0 for base case.");
        		}
        		
        		
        		
    		}
    		
    	}
    
    	result = checkTruncate(result);




    	int reserveWordReturn; //used to get reserved word return
    	
    	reserveWordReturn = reserveWords.LookupName(result.lexeme); //checks if in reserved word table and returns position if in or -1 if not
     	if (reserveWordReturn == ReserveTable.notFound) { //if it is in it will then change the code to the position of where if not doesnt
     		
     		if(result.code == FLOAT_ID) {
     			
     			//checks if the string can be turned into a value if so does it
     			if(doubleOK(result.lexeme)) {
     				lexemeValueDouble = Double.parseDouble(result.lexeme);
     			}
     			
     			//adds to symbol table
     			saveSymbols.AddSymbol(result.lexeme, kindc, lexemeValueDouble);
     		}
     		else if (result.code == INTEGER_ID) {
     			
     			//checks if the string can be turned into a value if so does it
     			if(integerOK(result.lexeme)) {
     				lexemeValueInt = Integer.parseInt(result.lexeme);
     			}
     			
     			saveSymbols.AddSymbol(result.lexeme, kindc, lexemeValueInt);
     		}
     		else {
     			saveSymbols.AddSymbol(result.lexeme, kindc, 0);
     		}
     		
     		
     	}
     	else {
     		result.code = reserveWordReturn; //changes code to location in reserved word table
     	}
        
        
        /* a number is:   <digit>+[.<digit>*[E[+|-]<digit>+]] */
        return result;
    }

    
    //this function parses out the string from the line
    // it takes no parameters and returns the a token class object
    private token getString() {
    	//this parses out a string 
    	

    	
    	boolean foundEOL = false; //to keep track if hit end of line to know error
    	boolean foundQuote = false; //check if its found the second quote
    	
    	token result = new token();//creates a local token
    	result.lexeme = ""; //inits the lexeme we know the first char is a quote so we don't add
    	
		result.code = STRING_ID; //must be a string so far
    	
    	currCh = GetNextChar(); //get next char in the line
    	
    	//if a end of line has not been found or a quote then it keeps adding chars to the lexeme
    	while(!(foundEOL) && !(foundQuote)) {
    		//checks if hit an end line char then will print warning and end while loop
    		if(currCh == '\n') {
    			foundEOL = true;
    			System.out.println("End of line found before comment terminated.");
    		}
    		//checks for quote if so then the while look can end
    		else if(currCh == '"') {
    			foundQuote = true;
    			
    		}
    		//if neither of the other two has happen then can safely add char to lexeme
    		else {
    			result.lexeme += currCh;
    		}
    		
        	currCh = GetNextChar();
    		
    	}


    	//if end of line not found then can check if its in reserve table
    	if(foundEOL == false) {
    		
        	result = checkTruncate(result);
    		
        	result.code = reserveWords.LookupName(result.lexeme);
        	if (result.code == ReserveTable.notFound) {
        		result.code = STRING_ID;
        		saveSymbols.AddSymbol(result.lexeme, kindc, result.lexeme);
        	}
    		return result;
    	}
    	else {
    		result.code = UNKNOWN_CHAR;
    	}
    		
    	
        return result;
    }
    
    //when reached here in logic all it can be is a unknown char or operator
    //takes the char from the line and checks if it is an operand if it is a two char operand case it checks that two.
    //it parses the char out of the line and labels it accordingly
    //not parameters and returns the token class object
    private token getOneTwoChar() {

    	token result = new token(); //creates local token object
    	result.lexeme = "" + currCh; //init it with the first char
    	char nextChar = ' '; //place holder for the next char in he line
    	
    	if(isPrefix(currCh)) { //checks if the current char is a a prefix for a two char operand 
    		nextChar = PeekNextChar(); //gets the next char to see if it fits the two char operand
    		//i can't think of a way to do this dynamically so i hard coded it
    		if((nextChar == '=') && ((currCh == ':') || (currCh == '>') || (currCh == '<'))) { //checks if the two chars are ":=", ">=", "<="
    	    	currCh = GetNextChar();
    			result.lexeme += currCh;
    		}
    		else if((nextChar == '>') && (currCh == '>')) {//checks if the two chars are "<>"
    	    	currCh = GetNextChar();
    			result.lexeme += currCh;
    		}
    	}
    	currCh = GetNextChar(); //gets next char to continue
    	
    	result.code = reserveWords.LookupName(result.lexeme); //checks reserve words for operands
    	if (result.code == ReserveTable.notFound) {
    		
        	result.code = UNKNOWN_CHAR;
        	
    	} 
        return result;
    }
    
    
    
    
    //the variables for easy changing of the length to truncate to zero for no truncating
    final int truncIdLength = 30;
    final int truncIntLength = 6;
    final int truncFloatLength = 12;
    final int truncStringLength = 0;
    
    
    //takes in the token class as a parameter and passes the class back out
    //this function checks if the lexeme needs to be truncated
    //it will determine the type the lexeme is and truncated for those rules
    public token checkTruncate(token result) {
        // truncate long lexemes, validate doubles and integers
        // handle appropriate types and add to symbol table as needed
    	
    	
    	int truncateLength = 0; //variable to use the switch statement to figure out type of truncation
    	String whatsBeingTruncated = ""; //string for system print out of what is being truncated
    	
    	//case statement to determine what we are truncating
        switch (result.code) {
            case IDENT_ID:
            	truncateLength = truncIdLength;
            	whatsBeingTruncated = "Identifier";
                break;
            case INTEGER_ID:
            	truncateLength = truncIntLength;
            	whatsBeingTruncated = "Integer";
                break;
            case FLOAT_ID:
            	truncateLength = truncFloatLength;
            	whatsBeingTruncated = "Float";
                break;
            case STRING_ID:
            	truncateLength = truncStringLength;
            	whatsBeingTruncated = "String";
                break;
            default:
                break; //don't add                           
        }
        
        if(truncateLength != 0) {
        	int length = result.lexeme.length(); //gets length of the lexeme to 
        	
        	if(length > truncateLength) {//checks if it needs truncating
        		
        		String truncatedString = ""; //the temp string to add the amount to be truncated to
        		
        		for(int i = 0; i < truncateLength; i++) { 
        			
        			truncatedString += result.lexeme.charAt(i); //adds each char till it hits the truncation cap
        			
        		}
        		//prints warning message
        		System.out.println(whatsBeingTruncated + " length > " + truncateLength + ",truncated " + result.lexeme + " to "  + truncatedString);
        		result.lexeme = truncatedString; //sets the token to the truncated string
        	} 
        }
        
        
        return result; //passes the token back
    }
// End of student supplied methods
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// numberic validation freebie code!
    // Checks to see if a string contains a valid DOUBLE 
    public boolean doubleOK(String stin) {
        boolean result;
        Double x;
        try {
            x = Double.parseDouble(stin);
            result = true;
        } catch (NumberFormatException ex) {
            result = false;
        }
        return result;
    }
    // Checks the input string for a valid INTEGER
    public boolean integerOK(String stin) {
        boolean result;
        int x;
        try {
            x = Integer.parseInt(stin);
            result = true;
        } catch (NumberFormatException ex) {
            result = false;
        }
        return result;
    }
// Main method of Lexical
    public token GetNextToken() {
        token result = new token();
        currCh = skipWhiteSpace();
        if (isLetter(currCh)) { //is ident
            result = getIdent();
        } else if (isDigit(currCh)) { //is numeric
            result = getNumber();
        } else if (isStringStart(currCh)) { //string literal
            result = getString();
        } else //default char checks
        {
            result = getOneTwoChar();
        }
        if ((result.lexeme.equals("")) || (EOF)) {
            result = null;
        }
//set the mnemonic
        if (result != null) {
        	result.mnemonic = 
        			
        	mnemonics.LookupCode(result.code);
            result = checkTruncate(result);
            if (printToken) {
                System.out.println("\t" + result.mnemonic + " | \t" + String.format("%04d", result.code) + " | \t" + result.lexeme);
            }
        }
        return result;
    }
}


