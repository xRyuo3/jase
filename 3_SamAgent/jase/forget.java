package jase;
import jason.*;
import jason.asSyntax.*;
import jason.asSemantics.*;

public class forget extends DefaultInternalAction{
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

        String sourceIRI=null;
        Literal bel2del=null;
      
        if (args.length!=1||!args[0].isGround()) {
            throw new JasonException("Invalid arguments or number of arguments for 'forget' internal action.");
        }
        sourceIRI = ((StringTerm)args[0]).getString();
        bel2del=ts.getAg().findBel(Literal.parseLiteral("B(\""+sourceIRI+"\")"),un);

        if(bel2del==null){return false;}
        VirtuosoConnector.deleteOnto(sourceIRI);
        ts.getAg().delBel(bel2del);
        return true;
    }
}
