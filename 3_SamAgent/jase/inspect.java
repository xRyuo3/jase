package jase;
// Jason package
import jason.*;
import jason.asSyntax.*;
import jason.asSemantics.*;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.*;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;



/*
    controlla se la resuouce appartiene all ontologia con IRI torna ture se trova un risultato false altrimenti
    come terzo parametro abbiamo se vuoto lo binda con il jason literal 
    ontoKB.inspect(IRI,Resource,ResultAsJasonAtom)
    IRI: url di un ontologia
    Resource: una stringa oppute un jason atom 
    Result: non deve essere bindato a un valore e quello che viene restituito ´e l´equivalente in Jason di una risorsa che esiste a database,


    io voglio anche il processo contrario che se la resource é un Jason atom questo viene tradotto in quello che potrebbe essere una risorsa ontologica valida 
    e poi mi venga ritornata una risposta ossia si questo jason atom esiste in questa ontologia
*/
public class inspect extends DefaultInternalAction{
    private static final Map<String, String> serializationFormats = new HashMap<>();
    static {
        serializationFormats.put("RDFXML", "RDF/XML");
        serializationFormats.put("TURTLE", "TURTLE");
        serializationFormats.put("NTRIPLES", "N-TRIPLES");
        serializationFormats.put("JSONLD", "JSON-LD");
    }
    //si puo' aggiustare, permettendo una ricerca del prefisso in base alla stinga e mappandolo a un literal jasoniano
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {	
        
        if (args.length <= 1 || args.length > 3 || !args[0].isGround() || !args[1].isGround() ) {
            throw new JasonException("Invalid number of arguments for 'inspect' internal action or not bounded arguments.");
        }
        String ontologyIRI = ((StringTerm)args[0]).getString().endsWith("#") ? ((StringTerm)args[0]).getString().substring(0, ((StringTerm)args[0]).getString().length() - 1):((StringTerm)args[0]).getString();
        String query=String.format("""
            SPARQL
            Select ?r
            where
                {  ?s ?p ?o .
                    BIND(STRBEFORE(str(?s), \"#\") as ?prefix).
                    Filter(?prefix=\"%s\").
                    BIND(STRAFTER(str(?s), \"#\") as ?r).
                    %s
                }
                """,ontologyIRI,getSpQLFilter("?r",args[1]));
        ResultSet res=VirtuosoConnector.executeQuery(query);
        
        String termString="";
        while(res.next()){
            termString = res.getObject(1).toString();
        }
     
        if (!termString.equals("") && args.length==3 && !args[2].isGround()){       
            un.unifies(args[2], jason.asSyntax.Literal.parseLiteral(solveSpQLResource2JasonAtom(termString)));
        }
        
        return termString.equals("")? false:true;
    }
    private static String getSpQLFilter(String subj_str, Term object) {
        String objAdj=object.toString().contains("_")?"^(" + object.toString().replaceAll("\"", "").replace("_", ").*") + ".*".replaceAll("_",".*").toLowerCase() :"^(".concat(object.toString().replaceAll("\"","")).concat(")");       
        return String.format("Filter regex(%s,\"%s\",\"i\").\n",subj_str,objAdj);
    }
    public static String solveSpQLResource2JasonAtom(String input) {       
        return Character.isUpperCase(input.charAt(0))?input.substring(0, 1).toLowerCase()+"_" + input.substring(1):input;
    }

}

