package pLib;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import jason.*;
import jason.asSyntax.*;
import jason.asSemantics.*;



public class save_plan extends DefaultInternalAction{
    
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {	
        
        if (args.length <= 1 || args.length > 2 || !args[0].isGround() || !args[1].isGround() ) {
            throw new JasonException("Invalid number of arguments for 'inspect' internal action or not bounded arguments.");
        }
        appendToFile(((StringTerm)args[0]).getString());
        return true;

    }
    
    public static void appendToFile(String text) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("samPlans.asl", true))) {
            writer.write(text);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("An error occurred while appending to the file: " + e.getMessage());
        }
    }
}
