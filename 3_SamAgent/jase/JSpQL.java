package jase;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.asSyntax.VarTerm;
import jason.bb.BeliefBase;

public class JSpQL{

    static String getSpQLFilter(String subj_str, Term object) {
        String objAdj="";
        if( object.toString().contains("_")){
            objAdj=object.toString().replaceAll("_",".*").toLowerCase();
        }
        if( objAdj.toString().contains(" ")){
            objAdj=objAdj.toString().replaceAll(" ",".*").toLowerCase();
        }
        else{
            objAdj="\"^(".concat(objAdj.toString().replaceAll("\"","")).concat(")\"")  ; 
        }

        // if (objAdj.toString())
        // "\"^(".concat(object.toString().replaceAll("\"","")).concat(")\"")  ;
        return String.format("Filter regex(%s,%s,\"i\").\n",subj_str,objAdj);
    }

    static boolean checkSpQLFilter(String pred) {
        return pred.equals("like");
    }

    static String term2SpQLVar(Term t){
        return "?"+((VarTerm)t).toString();
    }

    static String atom2SpQLRes(BeliefBase bb,Unifier un,Term t) throws JasonException{
        return JSpQL.concatPrefix(bb, un,  ((Atom)t));
    }
/*----
 * un oggetto puó essere:
 * 1. atom => bisogna concatenargli il prefisso e normalizzarla
 * 2. string/( number ) se il pred é un filtro bisogna scoportla in una regex il piú generale possibile
 * 3. variable => metto il ? davanti   
 */
/*----------
 * un soggetto puó essere:
 * atom=> bisogna concatenargli il prefisso e normalizzarla
 * variabile => metto il ? davanti 
 */
    static String getSpQLBody(BeliefBase bb,Unifier un,Term[] args) throws JasonException{
        String queryBody="";
        String queryOrderBy="";
        // System.out.println(args.length);
        Literal TriplePattern=Literal.parseLiteral("P(S,O)");
        for (Term triple:args) {
            if(un.clone().unifies(TriplePattern, triple)&&!triple.isList()){
    
                String pred_str=JSpQL.handleTriplePredicate(bb, un, triple);
                if(JSpQL.checkSpQLQptional(pred_str)){  System.out.println(((Structure) triple).getTerms());queryBody+=JSpQL.getOptionalClause(bb, un,((Literal) triple).getTermsArray());continue;}
                if(JSpQL.checkSpQLOrderBy(pred_str)){ queryOrderBy=JSpQL.getOrederByClause(((Literal)triple).getTerm(0));continue;}
                Term subject=((Literal) triple).getTerm(0);
                Term object=((Literal) triple).getTerm(1);
                String subj_str="";
                String obj_str="";
    
                if (subject.isVar())   subj_str=term2SpQLVar(subject);
    
                if (object.isVar())    obj_str=term2SpQLVar(object);
        
    
                if (subject.isAtom())  subj_str=atom2SpQLRes(bb, un, subject);
        
    
                if (object.isAtom())  obj_str=atom2SpQLRes(bb,un,object);
    
                if (object.isNumeric()) obj_str=object.toString();
        
    
                if (subject.isString()) throw new JasonException("Subject could be only a resource or a variable");  
    
                
                if (checkSpQLFilter(pred_str)){ queryBody+=getSpQLFilter(subj_str,object);  continue;}
                // if (object.isString() && checkSpQLFilter(pred_str)){ queryBody+=getSpQLFilter(subj_str,object);  continue;}
    
                queryBody+=String.format(" %s %s %s . ",subj_str,pred_str,obj_str);
    
            }
        }
        return String.format("{%s} %s",queryBody,queryOrderBy);
    }

    static String getOrederByClause(Term term) {
        System.out.println(term);
        return String.format("ORDER BY %s",JSpQL.getSpQLHeader(term));
    }

    static boolean checkSpQLOrderBy(String pred) {
        return pred.equals("order_by");
    }

    static String getOptionalClause(BeliefBase bb, Unifier un, Term[] triples) throws JasonException {
         
        // System.out.println(tripleArray);
        String a= String.format("OPTIONAL %s. ",getSpQLBody(bb, un, triples));
        System.out.println(a);
        return a;
    }

    static boolean checkSpQLQptional(String pred) {
        return pred.equals("optional");
    }

    static String concatPrefix(BeliefBase bb,Unifier un,Atom s) throws JasonException{
        // String stran=solveJasonAtom2SpQLResource(s.toString());
        return JSpQL.concatPrefix(bb, un, s.toString()).replaceFirst(":.*", ":" + JSpQL.solveJasonAtom2SpQLResource(s.toString()));
    }

    static String concatPrefix(BeliefBase bb,Unifier un,String s) throws JasonException{
        // String stran=solveJasonAtom2SpQLResource(s.toString());
        String queryPrefix="";
        Literal pattern=Literal.parseLiteral("prefix(X,Y)");
        Iterator<Literal> i = bb.getCandidateBeliefs(pattern, un);
        if (i != null) {
            while (i.hasNext()) {
                Literal l = i.next();
                if (l.isStructure()) {
                    if (un.clone().unifies(pattern, l) && !l.getTerm(1).isStructure()&&s.equals(l.getTerm(1).toString())) {
                        // String predicate=l.getFunctor().toUpperCase();
                        Term prefix=l.getTerm(0);
                        Term resource= l.getTerm(1);
                        queryPrefix+=String.format("%s:%s",prefix,resource);
                    
                    // System.out.println("Concat Prefix: "+queryPrefix);
                    }
                }
            }
        }
        //si puo' aggiustare, permettendo una ricerca del prefisso in base alla stinga e mappandolo a un literal 
        if (queryPrefix=="") throw new JasonException("The belief base does not contain any prefix for string: "+s);
        return queryPrefix;
    }

    public static String solveJasonAtom2SpQLResource(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            
            if ((currentChar == '_') & (i > 0)) {
                result.deleteCharAt(result.length() - 1);
                result.append(Character.toUpperCase(input.charAt(i-1)));
            } else {
                result.append(currentChar);
            }
        }
    
        return result.toString();
    
    }

    public static String getSpQLPrefixFromBB(TransitionSystem ts,Unifier un){
        String queryPrefix="";
        Literal pattern=Literal.parseLiteral("prefix(X,ontology(Y))");
        Iterator<Literal> i = ts.getAg().getBB().getCandidateBeliefs(pattern, un);
        if (i != null) {
            while (i.hasNext()) {
                Literal l = i.next();
                if (l.isStructure()) {
                    if (un.clone().unifies(pattern, l)) {
                        String predicate=l.getFunctor().toUpperCase();
                        Term subject=l.getTerm(0);
                        Term object=((Literal) l.getTerm(1)).getTerm(0);
                        queryPrefix=queryPrefix+String.format("%s %s:<%s>\n",predicate,subject,object.toString().replaceAll("\"", ""));
                    }
                }
            }
        }
        return queryPrefix;
    }

    public static String handleTriplePredicate(BeliefBase bb, Unifier un, Term triple) throws JasonException{
            // 1)Predicato 'e una variabile 
                // 2)Predicato 'e un literal type label has 
                // 3)Predicato 'e un filtro like 
                // In jason in pred non puo' essere una stringa
    
        // 3)Predicato 'e un filtro like 
        String pred="";
        if((((Literal) triple).getFunctor().toString().equals("like"))||(((Literal) triple).getFunctor().toString().equals("optional"))||(((Literal) triple).getFunctor().toString().equals("order_by"))) {
        // System.out.println("Predicato 'e un filtro");    
            pred= ((Literal) triple).getFunctor();
        }
        // 1)Predicato 'e una variabile , convenzionalmente in owl i predicati iniziano con la lettera minuscola, se voglio considerarlo come variabile
        //il secondo carattere deve essere un _
        
        // In jason in pred non puo' essere una stringa
        else if ( ((Literal) triple).getFunctor().toString().length()>=2 && ((Literal) triple).getFunctor().toString().charAt(1)=='_' ){
            // System.out.println("Predicato 'e una variabile");
            pred="?"+solveJasonAtom2SpQLResource(((Literal) triple).getFunctor().toString());
        }
        else{
            // System.out.println("Predicato 'e un atom");
            pred=concatPrefix(bb,un,((Literal) triple).getFunctor());
        }
        // 2)Predicato 'e un atom type label has deve essere nella belif base del agente altrimenti solleva eccezione
        return  pred;
    }

    public static String getSpQLHeader(Term triple){
        String input=((ListTermImpl) triple).toString();
        input = input.substring(1, input.length()-1);
        String[] elements = input.split(",");
    
        List<String> processedElements = new ArrayList<>();
        for (String element : elements) {
            if (element.length() > 2 && element.charAt(1) == '_') {
                String processedElement = element.substring(0, 1).toUpperCase() +
                        element.substring(2).replace("_", "");
                processedElements.add(processedElement);
            } else {
                processedElements.add(element);
            }
        }
    
        return "?"+String.join(" ?", processedElements) ;
        // String pattern = "._(.*?)(?=,)";
        // TO DO: gestire il caso in cui tra gli elementi ho  una variabile predicato
        
        // return((ListTermImpl) triple).toString().replaceAll("\\[", "?").replaceAll("\\]","").replaceAll(","," ?");
    }
    
}