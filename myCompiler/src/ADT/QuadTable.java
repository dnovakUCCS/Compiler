package ADT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/*
  	DRAKE NOVAK 
 	2025Sp CS 4100- Compiler Foundations- Part 1, Assignment 2: Symbol and Quad Tables Copy 
 The QuadTable is different from the SymbolTable in its access and contents.  Each fixed indexed entry
	row consists of four int values representing an opcode and three operands.  This means it can be
	implemented simply with a 2-dimensional array of integers, maxSize x 4 in dimension.  The methods
	needed are:
 */


public class QuadTable {

	int size; //size of table
	Quad[] myQuad;  //pointer to list of objects
	int currentIndex = 0; //index space for newest addition

	
	private class Quad{
		
		public
			int opcode; 
			int operand1;
			int operand2;
			int operand3;
		
	}
	
	
	public int nextQuad() {
		
		if(myQuad == null) {
			return 0;
		}
		else {
			return currentIndex;
		}
		
		
	}
	
	
	
	/*
	Constructor creates a new, empty QuadTable ready for data to be added, with the
	specified maxumum number of rows (maxSize).  An array is suggested as the
	cleanest implementation of this, along with a private int nextAvailable counter to
	keep track of which rows have been used so far.
	
	
	I chose to do the array of classes because i thought it was easier
	 */
	public QuadTable(int maxSize){
		
		size = maxSize;
		
		myQuad = new Quad[maxSize]; //init the array of pointers 
		
		
	}
	
	
	
	/*
	Returns the int index of the next open slot in the QuadTable.  Very important
	during code generation, this must be implemented exactly as described, and must
	be the index where the next AddQuad call will put its data.
	 */
	public int NextQuad() {
		
		return currentIndex;
		
		
	}
	
	
	/*
	Expands the active length of the quad table by adding a new row at the
	NextQuad slot, with the parameters sent as the new contents, and increments
	the NextQuad counter to the next available (empty) index when done.
	 */
	public void AddQuad(int opcode,int op1,int op2,int op3) {
		
		myQuad[currentIndex] = new Quad();
		myQuad[currentIndex].opcode = opcode;
		myQuad[currentIndex].operand1 = op1;
		myQuad[currentIndex].operand2 = op2;
		myQuad[currentIndex].operand3 = op3;
		currentIndex++;
	}
	
	
	/*
	Returns the int data for the row and column specified at the two integer inputs index, and column.
	will return -1 if column asked for doesn't exist
	 */
	public int GetQuad(int index, int column) {
		
		
		switch(column) { //because im not using a matrix i have to figure out the "column" by checking which column they would of been in
		case 0:
			return myQuad[index].opcode;
		case 1:
			return myQuad[index].operand1;
		case 2:
			return myQuad[index].operand2;
		case 3:
			return myQuad[index].operand3;
		default:
			return -1;
		
		}
		
		
	}
	
	
	/*
	Changes the contents of the existing quad at index.  Used only when back filling 
	jump addresses later, during code generation, and very important.
	
	accepts 5 ints the index, opcode, operand1, operand2, and operand3, it does not have a return
	 */
	
	public void UpdateQuad(int index, int opcode, int op1, int op2, int op3) {
		
		myQuad[index].opcode = opcode;
		myQuad[index].operand1 = op1;
		myQuad[index].operand2 = op2;
		myQuad[index].operand3 = op3;
		
		
	}
	
	public void setQuadOp3(int index, int op3) {
		
		myQuad[index].operand3 = op3;
		
	}
	
	
	
	/*
	Prints to the named file only the currently used contents of the Quad table in neat
	tabular format, one row per output text line.
	
	accepts the file name and returns nothing
	 */
	public void PrintQuadTable(String filename){
		
		String line = "";
		
		try {
			
			File myfile = new File(filename);
			
			if(!myfile.exists()){
				myfile.createNewFile();
			}
			
			
			FileWriter myFileWriter = new FileWriter(myfile);
			
			myFileWriter.write("Novak Drake SP2022"+ "\n");
			
			
			for(int i = 0; i < currentIndex; i++) {
				
				line = myQuad[i].opcode + "|" + myQuad[i].operand1 + "|" + myQuad[i].operand2 + "|" + myQuad[i].operand3 + "|" + "\n"; //the format for outputting to the file
				
				
				myFileWriter.write(line); 
			}
			myFileWriter.close();
			
			
		} catch (IOException e) {
			
			System.out.println("Writing failed");
		}
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
