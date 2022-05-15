package ADT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
 	DRAKE NOVAK 
 	2025Sp CS 4100- Compiler Foundations- Part 1, Assignment 2: Symbol and Quad Tables Copy 
 	The symbol table conceptually is a fixed index list (i.e., once a row of data has been added, its index must
	not change during the run of the program) consisting of an indexed list of entries, with each entry (a
	single row of data) containing all of the following: a name field (String type); a kind field (char to hold a
	label, variable, or constant indicator, represented by the characters ‘L’, ‘V’, or ‘C’); a data_type field
	specifying the data as an integer, float, or string (char, represented ‘I’, ‘F’, or ‘S’); and a set of 3 value
	fields of the appropriate type (integerValue, floatValue, stringValue) to hold one of these: an integer, a
	double, or a String, as selected by the contents of the data_type field. (Later, it will be clear why labels
	will always store their data in the integerValue field, while variables and constants may use any one of the
	integerValue, floatValue, stringValue storage areas based on their data_type field).  The interface to the
	SymbolTable must implement the following methods (in addition to a constructor, as shown):
 */




public class SymbolTable {
	
	
	public final char constantkind = 'C';
	
	final static int notFound = -1; //variable to symbolize not found error
	
	static int printCount = 0; //used for formating the print to file
	
	private class symbol{
		
		public
		
			
		
			String name; //the symbol
			char kind; //is it a L = label, V = variable, or C = constant
			char data_type; //is it an I = integer, F = float or S = string
			
			//holds one of these based on the data type 
			int intValue; 
			double floatValue;
			String stringValue;
			
	}
	
	
	
	int size; //keeps track of the overall size of the table
	int currentIndex = 0; //keeps track of the next open index in the list
	symbol[] mySymbol; //pointer to the array of objects

	
	
	

	//Initializes the SymbolTable to hold maxSize rows of data. Input argument is an int maxsize
	public SymbolTable(int maxSize){
		
		size = maxSize; //saves the size of the array
		mySymbol = new symbol[maxSize]; //initializing the space for the pointers to point to the objects
		
	}
	
	

	
	
	/*
	Three overloaded methods to add symbol with given kind and value to the end
	of the symbol table, automatically setting the correct data_type, and returns the
	index where the symbol was located.  If the symbol is already in the table
	according to a non-case-sensitive comparison [“Total” matches “total” as well
	as “ToTaL”] with all the existing strings in the table, no change or verification
	is made, and this just returns the row index where the symbol was found. 
	These methods only FAIL, and return -1, when the table already contains maxSize
	rows, and adding a new row would exceed this size limit. This should not happen.
	*/
	

	/*
	 * For the three AddSymbols they take three inputs a string,a char, and the last one is overloaded of either(int, double, char).
	 * it firstly will lookup the symbol in the table to see if it is in the table if not it will then check if the table is full or not.
	 * if the table it full it returns a -1 if not it allows the addition of a new symbol and returns its index location
	 * lastly if the lookup does find it returns the index location.
	 * 
	 *
	 */
	public int AddSymbol(String symbol, char kind, int value) {
		
		int Lookup = LookupSymbol(symbol); //checks if symbol is in table yet
		
		/*
		 * I chose to check the look up before checking the size of the table because if it is in the table you don't even care about the size so it makes more sense to know if it is in the table first
		 */
		if(Lookup == -1) {
			if(currentIndex < size) {
				
				mySymbol[currentIndex] = new symbol(); //init the space for this instance of the object 
				mySymbol[currentIndex].name = symbol;	//assigning variable
				mySymbol[currentIndex].kind = kind;		//assigning variable
				mySymbol[currentIndex].data_type = 'i';	//assigning variable
				mySymbol[currentIndex].intValue = value;//assigning variable
				
				currentIndex++;
				return (currentIndex - 1);//returns index location of the just added symbol
				
			}
			else
				return notFound; //returns -1 if not found in table and table is full
		}
		else
			return Lookup; //returns index location if found in the table already
		
	}
	
	public int AddSymbol(String symbol, char kind, double value) {
		
		int Lookup = LookupSymbol(symbol);
		if(Lookup == -1) {
			if(currentIndex < size) {
				
				mySymbol[currentIndex] = new symbol();
				mySymbol[currentIndex].name = symbol;
				mySymbol[currentIndex].kind = kind;
				mySymbol[currentIndex].data_type = 'f';
				mySymbol[currentIndex].floatValue = value;
				
				currentIndex++;
				return (currentIndex - 1);
				
			}
			else
				return notFound;
		}
		else
			return Lookup;
		
	}
	
	public int AddSymbol(String symbol, char kind, String value) {
		

		int Lookup = LookupSymbol(symbol);
		if(Lookup == -1) {
			if(currentIndex < size) {
				
				mySymbol[currentIndex] = new symbol();
				mySymbol[currentIndex].name = symbol;
				mySymbol[currentIndex].kind = kind;
				mySymbol[currentIndex].data_type = 's';
				mySymbol[currentIndex].stringValue = value;
				
				currentIndex++;
				return (currentIndex - 1);
				
			}
			else
				return notFound;
		}
		else
			return Lookup;
	}
	
	
	
	/*
	 * accepts the string for the symbol to compare to other symbols in the table.
	Returns the index where symbol is found, or -1 if not in the table. 
	 */
	
	public int LookupSymbol(String symbol) {
		
		for(int i = 0; i < currentIndex; i++) { //loops through amount current filled in table
			
			if((symbol.compareToIgnoreCase(mySymbol[i].name)) == 0) { //check if already in table non-case-sensitive
				
				return i; //returns index location if found
				
			}
			
		}
		
		return notFound; //returns -1 if not found
	}
	
	/*
	 * accepts an integer index to specify which object to 
	return the various values currently stored at index.
	*/
	public String GetSymbol(int index) {
		
		return mySymbol[index].name;
		
	}
	public char GetKind(int index) {
		
		return mySymbol[index].kind;
		
	}
	public char GetDataType(int index) {
		
		return mySymbol[index].data_type;
		
	}
	public String GetString(int index) {
		
		return mySymbol[index].stringValue;
		
	}
	public int GetInteger(int index) {
		
		return mySymbol[index].intValue;
		
	}
	public double GetFloat(int index) {
		
		return mySymbol[index].floatValue;
		
	}
	
	
	
	/*
	 * Overloaded methods, these set the kind and value fields at the slot indicated by
	 * index.
	 */
	
	//accepts and int index, char kind, and a (int value, or double value or String value) to update the current value for the kind and value in the desired object indicated by the index. does not have a return
	public void UpdateSymbol(int index, char kind, int value){ 
		
		mySymbol[index].kind = kind;
		mySymbol[index].intValue = value;
		
	}
	public void UpdateSymbol(int index, char kind, double value){
		
		mySymbol[index].kind = kind;
		mySymbol[index].floatValue = value;
		
	}
	public void UpdateSymbol(int index, char kind, String value){
		
		mySymbol[index].kind = kind;
		mySymbol[index].stringValue = value;
		
	}
	
	
	/*
	Prints to a plain text file all the data in only the occupied rows of the symbol
	table.  Must be in neat tabular format, 1 text line per row, selectively showing only
	the used value field (stringValue, integerValue, or floatValue) which is active for
	that row based on the dataType for that row.
	*/
	
	//accepts a string for the filename and returns nothing but prints out the contents of the table to the file stated
	
	public void PrintSymbolTable(String filename){
		
		String line = ""; //buffer for output
		
		try {
			
			File myfile = new File(filename);
			
			if(!myfile.exists()){
				myfile.createNewFile();
			}
			
			
			FileWriter myFileWriter = new FileWriter(myfile);
			
			myFileWriter.write("Novak Drake SP2022"+ "\n");
			
			
			for(int i = 0; i < currentIndex; i++) {
				
				line = getPrintableLine(mySymbol[i]);
				
				
				myFileWriter.write(line); 
			}
			
			
			myFileWriter.close();
			
			
		} catch (IOException e) {
			
			System.out.println("Writing failed");
		}
		
		
		
		
	}
	
	//accepts an object and returns a string "theLine" it puts together the line in the table and sorts through the different data values to make it easy to print to a file
	private String getPrintableLine(symbol currentSymbol) {
		
		String count = Integer.toString(printCount);
		String theLine = "";
		
		//gets space before 1-9 so the | matches with double digit numbers
		if(printCount < 10) {
			//theLine = " " + count + "| " + currentSymbol.name;
			theLine =currentSymbol.name;
		}
		else {
			//theLine = count + "| " + currentSymbol.name;
			theLine =currentSymbol.name;
		}
		
		
//		//gets the bar to match the example file give for next area
//		if(currentSymbol.name.length() < 13) {
//			for (int k = currentSymbol.name.length(); k< 13; k++) {
//				theLine += " ";
//			}
//		}
//		
		
		theLine += " " + "|" + currentSymbol.kind + "|";
		
		
		if(currentSymbol.data_type == 'f') {
			
			theLine += "f|" + currentSymbol.floatValue + "\n";
			
		}
		else if (currentSymbol.data_type == 'i'){
			
			theLine += "i|" + currentSymbol.intValue + "\n";
			
		}
		else
			theLine += "s|" + currentSymbol.stringValue + "\n";
		
		printCount++;
		
		return theLine;
	}
	
	
	
	
	
	
	
}
