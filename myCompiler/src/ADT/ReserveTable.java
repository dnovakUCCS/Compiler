

/*
 * Drake Novak 
 * Reserve table class for the compiler project
 * Objective is to make a table for reserved words in the compiler with corresponding opcodes for later use
 * 
 * 
 * */


package ADT;

import java.io.*;



public class ReserveTable {
	
	final static int notFound = -1; //represents code not being found
	
	int currentIndex = 0; //the next open slot
	ReservedWord[] myReserveTable; //the pointer to a array of objects
	
	public ReserveTable(int maxSize) {
		
		myReserveTable = new ReservedWord[maxSize]; //init the amount of objects
	
	}
	
	
	
private class ReservedWord { //my object to keep track of every reserved word 
		
		int Index; //need an index separate from the spot in the array because the index starts at 1 and the array starts at 0
		String Name; //the name of reserved word
		int Code; //the op code
		
		ReservedWord(int i, String n, int c){ //init the reserved word
			
			Index = (i+1);
			Name = n;
			Code = c;
			
		}
		
		
		//wasn't sure if there was a way to use the values without a get function so i created get functions
		
		public String getName() { 
			
			return Name;
			
		}
		public int getCode() {
			return Code;
		}
		public int getIndex() {
			
			return Index;
		}
		
	}
	
	
	
	
	
	//adds the reserved word and opcode to table
	public int Add(String name, int code) {
		
		myReserveTable[currentIndex] = new ReservedWord(currentIndex, name, code);
		currentIndex++;
		
		return currentIndex;
	}
	
	
	
	//compares the inputed string to all names in the table for a match returns op code
	public int LookupName(String name) {
		
		for(int i = 0; i<currentIndex; i++) {
			if((name.compareToIgnoreCase(myReserveTable[i].getName())) == 0) {
				return myReserveTable[i].getCode();
			}
		}
		
		return notFound;
	}
	
	//compares inputed code to all current codes in the table returns the reserved word
	public String LookupCode(int Code) {
		
		for(int i= 0; i < currentIndex; i++) {
			if(Code == myReserveTable[i].getCode()) {
				return myReserveTable[i].getName();
			}
		}
		
		return "";
	}
	
	
	//prints out the reserve table in a table format
	public void PrintReserveTable(String filename) {
		
		String line = "";
		
		try {
			
			File myfile = new File(filename);
			
			if(!myfile.exists()){
				myfile.createNewFile();
			}
			
			
			FileWriter myFileWriter = new FileWriter(myfile);
			
			myFileWriter.write("Novak Drake SP2022"+ "\n");
			
			
			for(int i = 0; i < currentIndex; i++) {
				
				//line = String.valueOf(myReserveTable[i].getIndex()) + " " + myReserveTable[i].getName() + " "+ myReserveTable[i].getCode(); //creates the line format
				line = String.valueOf(myReserveTable[i].getIndex()) + " " + myReserveTable[i].getName() + " "+ myReserveTable[i].getCode(); //creates the line format
				
				myFileWriter.write(line + "\n"); 
			}
			myFileWriter.close();
			
			
		} catch (IOException e) {
			
			System.out.println("Writing failed");
		}
		
		
		
	}
	
	
	
	
	
}
