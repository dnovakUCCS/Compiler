package project5;

//import ADT.SymbolTable;
//import ADT.Lexical;
import ADT.*;

/**
 *
 * @author abrouill FALL 2021
 */
public class main {

    public static void main(String[] args) {
        String filePath = "d:\\CodeGenPartial.txt";
        System.out.println("Parsing "+filePath);
        boolean traceon = false; //true; //false;
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        
        System.out.println("Done.");
    }

}
