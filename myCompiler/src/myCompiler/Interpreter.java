package myCompiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import ADT.*;
/*
 * 
 * This program will simulate the running of a program
 * using the quad, symbol, and reserve table to step through 
 * a program and be able to print the trace for debugging 
 */
public class Interpreter {

	
	int sizeOfReserveTable = 20; //max size of reserve table
	
	ReserveTable opTable; //reserve table pointer
	
	//constructor to set up reserve table no input or output
	public Interpreter() {
		
		opTable = new ReserveTable(sizeOfReserveTable);
		
		fillReserveTable();
		
	}
	
	//initializes reserve table with hard coded reserve words no input or output
	private void fillReserveTable() {
		
		//fill reserve with reserved words
		opTable.Add("STOP", 0);
	    opTable.Add("DIV", 1);
	    opTable.Add("MUL", 2);
	    opTable.Add("SUB", 3);
	    opTable.Add("ADD", 4);
	    opTable.Add("MOV", 5);
	    opTable.Add("PRINT", 6);
	    opTable.Add("READ", 7);
	    opTable.Add("JMP", 8);
	    opTable.Add("JZ", 9);
	    opTable.Add("JP", 10);
	    opTable.Add("JN", 11);
	    opTable.Add("JNZ", 12);
	    opTable.Add("JNP", 13);
	    opTable.Add("JNN", 14);
	    opTable.Add("JINDR", 15);
		//rTable.Add(string name, int code);
		
	}
	
	//sets up the factorial test code and symbols, input is symbol and quad table pointers, output boolean true/false
	boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable) {
		
		
		//fill quad table with factorial commands
		qtable.AddQuad(5, 3, 0, 2); //MOV
		qtable.AddQuad(5, 3, 0, 1); //MOV
		qtable.AddQuad(3, 1, 0, 4); //SUB
		qtable.AddQuad(10, 4, 0, 7); //JP
		qtable.AddQuad(2, 2, 1, 2); //MUL
		qtable.AddQuad(4, 1, 3, 1); //ADD
		qtable.AddQuad(8, 0, 0, 2); // JMP
		qtable.AddQuad(6, 0, 0, 2); //PRINT
		qtable.AddQuad(0, 0, 0, 0);//STOP
		
		
		//fill symbol table with initial symbols
		stable.AddSymbol("n", 'v', 10);
		stable.AddSymbol("i", 'v', 0);
		stable.AddSymbol("product", 'v', 0);
		stable.AddSymbol("1", 'c', 1);
		stable.AddSymbol("$temp", 'v', 0);
		
		
		return true;
	}
	
	
	
	
	//sets up the Summation test code and symbols, input is symbol and quad table pointers, output boolean true/false
	boolean initializeSummationTest(SymbolTable stable, QuadTable qtable) {
		
		
		//fill quad table with factorial commands
		qtable.AddQuad(5, 5, 0, 2); //MOV
		qtable.AddQuad(5, 3, 0, 1); //MOV
		qtable.AddQuad(3, 1, 0, 4); //SUB
		qtable.AddQuad(10, 4, 0, 7); //JP
		qtable.AddQuad(4, 2, 1, 2); //ADD
		qtable.AddQuad(4, 1, 3, 1); //ADD
		qtable.AddQuad(8, 0, 0, 2); // JMP
		qtable.AddQuad(6, 0, 0, 2); //PRINT
		qtable.AddQuad(0,0,0,0);//STOP
		
		
		//fill symbol table with initial symbols
		stable.AddSymbol("n", 'v', 10);
		stable.AddSymbol("i", 'v', 0);
		stable.AddSymbol("Sum", 'v', 0);
		stable.AddSymbol("1", 'c', 1);
		stable.AddSymbol("$temp", 'v', 0);
		stable.AddSymbol("0", 'c', 0);		
		
		
		return true;
	}
	
	//the interpreter will simulate the running of a program, through the use of the quad table and symbol table. 
	//input is quad and symbol table, boolean for option trace and a filename to print to
	//no output besides the printing to a file, file prints the optional trace
	public void InterpretQuads(QuadTable Q, SymbolTable S, boolean TraceOn, String filename) {
		
		int pc = 0000; //program counter starts at zero
		boolean programRun = true; //true for simulation to continue false for it to stop
		FileWriter myFileWriter = null; //file output pointer
		
		int opcode, op1, op2, op3; //declares opcode and operand variables
		
		
		if(TraceOn) { //opens the file to print to 
			
			myFileWriter = openFile(filename); //opens file
			
			
			System.out.println("Novak Drake SP2022");
			
			try {
				myFileWriter.write("Novak Drake SP2022"+ "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		while(programRun) {
			
			opcode 	= 	Q.GetQuad(pc, 0);//sets up opcode and operands 
			op1 	= 	Q.GetQuad(pc, 1);
			op2 	= 	Q.GetQuad(pc, 2);
			op3 	=	Q.GetQuad(pc, 3);
			
			if(TraceOn) { //prints the trace line
				
				System.out.println(makeTraceString(pc, opcode,op1,op2,op3));
				
				try {
					myFileWriter.write(makeTraceString(pc, opcode,op1,op2,op3)+ "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
			}
			
			
			
			switch(opcode){
				case 0: //STOP
					System.out.println("Execution terminated by program STOP.");
					programRun = false;
					break;
				case 1:	//DIV
					
					int div = S.GetInteger(op1) / S.GetInteger(op2);
					
					S.UpdateSymbol(op3, 'v', div);
					
					
					pc++;
					break;
				case 2:	//MUL
					//op1*op2 -> op3
					//SymbolTable(7).Value = SymbolTable(2).Value * SymbolTable(5).Value 
					
					int product = S.GetInteger(op1) * S.GetInteger(op2);
					
					S.UpdateSymbol(op3, 'v', product);
					
					
					pc++;
					break;
				case 3:	//SUB
					
					int difference = S.GetInteger(op1) - S.GetInteger(op2);
					
					S.UpdateSymbol(op3, 'v', difference);
					
					pc++;
					break;
				case 4:	//ADD
					
					int sum = S.GetInteger(op1) + S.GetInteger(op2);
					
					S.UpdateSymbol(op3, 'v', sum);
					
					pc++;
					break;
				case 5:	//MOV
					//SymbolTable(4).Value = SymbolTable(3).Value
					
					int transfer = S.GetInteger(op1);
					
					S.UpdateSymbol(op3, 'v', transfer);
					
					pc++;
					break;
				case 6:	//PRINT
					//write symbol table name and value to STD output
					if(S.GetInteger(op3) != 0) {
						System.out.println(S.GetInteger(op3));
					}
					else {
						System.out.println(S.GetSymbol(op3));
					}
					
					pc++;
					break;
				case 7:	//READ
					// Assume operand must be an integer
					 // Make a scanner to read from CONSOLE
					Scanner sc = new Scanner(System.in);
					 // Put out a prompt to the user
					System.out.print('>');
					// Read one integer only
					int readval = sc.nextInt();
					// Op3 has the ST index we need, update it
					 
					S.UpdateSymbol(op3,'i',readval);
					 // Deallocate the scanner
					sc = null;
					
					pc++;
					break;
				case 8:	//JMP
					//set pc to op3
					pc = op3;
					break;
				case 9:	//jz
					
					if(S.GetInteger(op1) == 0) {
						
						pc = op3;
						
					}
					else
						pc++;
					
				
					break;
				case 10://JP
					//if op1>0  set pc to op3
					
					if(S.GetInteger(op1)> 0) {
						
						pc = op3;
						
					}
					else
						pc++;
					
					break;
				case 11:	//JN
					if(S.GetInteger(op1)< 0) {
						
						pc = op3;
						
					}
					else
						pc++;
					
					
					
					break;
				case 12:	//JNZ
					if(S.GetInteger(op1)!= 0) {
						
						pc = op3;
						
					}
					else
						pc++;
					
					
					
					break;
				case 13:	//JNP
					if(S.GetInteger(op1)<= 0) {
						
						pc = op3;
						
					}
					else
						pc++;
					
					
					
					break;
				case 14:	//JNN
					if(S.GetInteger(op1)>= 0) {
						
						pc = op3;
						
					}
					else
						pc++;
					
					
				
					break;
				case 15:	//JINDR
				
						
					pc = op3;
					
					
					
					break;
				default:
					System.out.println("Undeclared opcode");
					break;
			}
			
		}
		
		if(TraceOn) { //closes the file printed to
			
			try {
				myFileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	//sets up the format for printing
	//input 5 ints, pc opcode and 3 operands
	private String makeTraceString(int pc, int opcode,int op1,int op2,int op3 ){
        String result = "";
        result = "PC = "+String.format("%04d", pc)+": "+(opTable.LookupCode(opcode)+"     ").substring(0,6)+String.format("%02d",op1)+", "+String.format("%02d",op2)+", "+String.format("%02d",op3);
        return result;
    }
	
	
	//opens the file to print to pass in filename
	//output file writing pointer
	private FileWriter openFile(String filename){
		
		FileWriter myFileWriter = null;
		
		try {
			
			File myfile = new File(filename);
			
			if(!myfile.exists()){
				myfile.createNewFile();
			}
			
			myFileWriter = new FileWriter(myfile);
			
			
		} catch (IOException e) {
			
			System.out.println("Writing failed");
		}
		
		return myFileWriter;
	}
	
	
	public int opcodeFor(String string) {
		
		return opTable.LookupName(string);
		
	}
	
}
