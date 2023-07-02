package jase;
// Jason package
import jason.*;
import jason.asSyntax.*;
import jason.asSemantics.*;

import java.sql.ResultSet;



public class reason extends DefaultInternalAction{
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {	
        // Divido la sparql query in tre parti 
        // 1. import dei prefissi
        // 2. variabili della select, quindi header della query 
        // 3. query body che contiene tutte le triple body
        /*
         * %s        => 1. prefix import
         * SELECT  %s => 2. query header
         * WHERE {
         *  %s       => 3. query body
         * }
        */
        // Fare check che il primo argometo sia una listra di strutture
        // System.out.println(args[0].isa)
        Term[] triple=((ListTerm)args[0]).toArray(new Term[((ListTerm)args[0]).size()]);
        String queryPrefix=JSpQL.getSpQLPrefixFromBB(ts, un);
        String queryHead=JSpQL.getSpQLHeader(args[args.length-2]);
        String queryBody=JSpQL.getSpQLBody(ts.getAg().getBB(), un, triple);
        String query=
         String.format("""
            SPARQL
            %s
            SELECT DISTINCT %s 
            WHERE
                %s
            
            """,queryPrefix, queryHead, queryBody);
        System.out.println(
          query
        );
    
        ResultSet res=VirtuosoConnector.executeQuery(query);
        int colNum=res.getMetaData().getColumnCount();
        
        // cur positioned before the first row, next move the cursor and return false if there are no more rows
        ListTerm[] L= new ListTerm[colNum];
        for (int i = 0; i < colNum; i++) {
            L[i] = new ListTermImpl();
        }
        while(res.next()){
            for(int i=1;i<=colNum;i++){
                String termString = res.getObject(i).toString();
                L[i-1].add(new StringTermImpl(termString));
                
                // System.out.println((Term) ((ListTerm)args[args.length-1]).toArray()[i-1]);
                // un.unifies((Term) ((ListTerm)args[args.length-1]).toArray()[i-1],L[i-1]);

            }
        }
        // L.getAsJson();
        // System.out.println(L[]);
        ListTerm c= new ListTermImpl();
        for( Term e: L){
            c.append(e);
            //     // System.out.println(L[1]);
        // un.unifies(args[args.length-1],Literal.parseLiteral(L.toString()));
        }
        un.unifies(args[args.length-1],c);

        // System.out.println("ghhj"+c);

        return true;
    }
    
    


}

