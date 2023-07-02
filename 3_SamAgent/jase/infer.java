package jase;
// Jason package
import jason.*;
import jason.asSyntax.*;
import jason.asSemantics.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;



// infer(resourceIRI,reasoner(opetional));
// default reasoner OWL_MEM_TRANS_INF
// This internal action retrive an iri an infer knowledge then memorize in 
// inferredResource(inf+reasoner)
// se esiste gia inferita con lo stesso iri e reasoner l'internal action non 'e applicabile
public class infer extends DefaultInternalAction{
    private static final Map<String, OntModelSpec> supportedReasoners = new HashMap<>();
    static {
        supportedReasoners.put("OWL_MEM", OntModelSpec.OWL_MEM);
        supportedReasoners.put("OWL_MEM_RDFS_INF", OntModelSpec.OWL_MEM_RDFS_INF);
        supportedReasoners.put("OWL_MEM_TRANS_INF", OntModelSpec.OWL_MEM_TRANS_INF);
        supportedReasoners.put("OWL_MEM_RULE_INF", OntModelSpec.OWL_MEM_RULE_INF);
        supportedReasoners.put("OWL_MEM_MICRO_RULE_INF", OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        supportedReasoners.put("OWL_MEM_MINI_RULE_INF", OntModelSpec.OWL_MEM_MINI_RULE_INF);
        supportedReasoners.put("OWL_DL_MEM", OntModelSpec.OWL_DL_MEM);
        supportedReasoners.put("OWL_DL_MEM_RDFS_INF", OntModelSpec.OWL_DL_MEM_RDFS_INF);
        supportedReasoners.put("OWL_DL_MEM_TRANS_INF", OntModelSpec.OWL_DL_MEM_TRANS_INF);
        supportedReasoners.put("OWL_DL_MEM_RULE_INF", OntModelSpec.OWL_DL_MEM_RULE_INF);
        supportedReasoners.put("OWL_LITE_MEM", OntModelSpec.OWL_LITE_MEM);
        supportedReasoners.put("OWL_LITE_MEM_TRANS_INF", OntModelSpec.OWL_LITE_MEM_TRANS_INF);
        supportedReasoners.put("OWL_LITE_MEM_RDFS_INF", OntModelSpec.OWL_LITE_MEM_RDFS_INF);
        supportedReasoners.put("OWL_LITE_MEM_RULES_INF", OntModelSpec.OWL_LITE_MEM_RULES_INF);
        supportedReasoners.put("RDFS_MEM", OntModelSpec.RDFS_MEM);
        supportedReasoners.put("RDFS_MEM_TRANS_INF", OntModelSpec.RDFS_MEM_TRANS_INF);
        supportedReasoners.put("RDFS_MEM_RDFS_INF", OntModelSpec.RDFS_MEM_RDFS_INF);
    }

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        String sourceIRI=null;
        String infIRI=null;
        jason.asSyntax.Literal inferredResource=null;
        String defaultResoner="OWL_MEM_TRANS_INF";
        OntModelSpec reasoner=null;

        if (args.length < 1 || args.length > 3) {
            throw new JasonException("Invalid number of arguments for 'infer' internal action.");
        }
      
        if (args[0].isGround()) {
            sourceIRI = ((StringTerm)args[0]).getString();
            reasoner= supportedReasoners.get(defaultResoner);
            infIRI=insertStringAfterProtocol(((StringTerm)args[0]).getString(),"inf_"+defaultResoner+'/');
        }
        if(VirtuosoConnector.isKnownOResource(infIRI)) return false;
       
        // check if reasoner is supported
        if (args.length==2&& args[1].isGround()) {
            String chosenReasoner = ((StringTerm) args[1]).getString().toUpperCase();
            if (supportedReasoners.containsKey(chosenReasoner)) { reasoner = supportedReasoners.get(chosenReasoner); 
            infIRI=insertStringAfterProtocol(((StringTerm)args[0]).getString(),"inf_"+chosenReasoner+'/');
        }else { throw new JasonException("Not supported reasoner: " + chosenReasoner); }}
   
        inferredResource=jason.asSyntax.Literal.parseLiteral("inferredFrom(\""+infIRI+"\",\""+sourceIRI +"\")");
        // Infer
        OntModel model = VirtuosoConnector.retrieveReasonedModel(sourceIRI,reasoner);
        VirtuosoConnector.insertOnto(infIRI,model);
        ts.getAg().addBel(inferredResource);
        return true;	
    }
    
    private static String insertStringAfterProtocol(String url, String insertString) {
        int protocolIndex = url.indexOf("://");
        if (protocolIndex != -1) {
            return url.substring(0, protocolIndex + 3) + insertString + url.substring(protocolIndex + 3);
        }
        return url;
    }
}

