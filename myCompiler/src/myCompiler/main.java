package myCompiler;
import ADT.SymbolTable;
import ADT.Lexical;
import ADT.*;

/**
 *
 * @author abrouill SPRING 2022
 */
public class main {
    public static void main(String[] args) {
    	
    	System.out.println("Drake Novak,6995,CS4100,SPRING 2022,Eclipse");
    	
    	
    	String myFilePathStart = "C:\\Users\\djnvk\\eclipse-workspace\\myCompiler\\src\\myCompiler\\Current inputs\\";
    	
    	String myFilePathEnd = "CodeGenBASICsp22.txt";
    	//String myFilePathEnd = "CodeGenFULL-SP22.txt";
    	
    	String filePath = myFilePathStart + myFilePathEnd;
    	
        System.out.println("Parsing "+filePath);
        boolean traceon = false; //true; //false;
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        
        System.out.println("Done.");
        
    }
}