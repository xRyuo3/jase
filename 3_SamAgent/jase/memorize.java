package jase;
// Jason package
import jason.*;
import jason.asSyntax.*;
import jason.asSemantics.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.*;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;


//  ontoKB.memorize(R); or  ontoKB.memorize(R,"TURTLE");
//  asl: +knownResource(R) + true
//  if already know R -> false 

public class memorize extends DefaultInternalAction{
    private static final Map<String, String> serializationFormats = new HashMap<>();
    static {
        serializationFormats.put("RDFXML", "RDF/XML");
        serializationFormats.put("TURTLE", "TURTLE");
        serializationFormats.put("NTRIPLES", "N-TRIPLES");
        serializationFormats.put("JSONLD", "JSON-LD");
    }
    
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {	
        String sourceIRI = null;
        String serializationFormat="TURTLE";
        
        jason.asSyntax.Literal knownResource= null;
        if (args.length < 1 || args.length > 2) {
            throw new JasonException("Invalid number of arguments for 'memorize' internal action.");
        }
        if (args[0].isGround()) {
            sourceIRI = ((StringTerm)args[0]).getString();
            knownResource=jason.asSyntax.Literal.parseLiteral("knownResource(\""+((StringTerm)args[0]).getString()+"\")");
        }
        if (VirtuosoConnector.isKnownOResource(sourceIRI)) return false;

        if (args.length==2&& args[1].isGround()) {
            String formatArg = ((StringTerm) args[1]).getString().toUpperCase();
            if (serializationFormats.containsKey(formatArg)) { serializationFormat = serializationFormats.get(formatArg); }
            else { throw new JasonException("Invalid serialization format: " + formatArg); }
        }    
        // onto model with no reasoning at all
        OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM);
        model.read(sourceIRI,serializationFormat);
        VirtuosoConnector.insertOnto(sourceIRI, model);
        ts.getAg().addBel(knownResource);
        return true;
    }
  
}
