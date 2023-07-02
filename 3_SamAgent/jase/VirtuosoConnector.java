package jase;
import java.sql.*;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.StmtIterator;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class VirtuosoConnector {
    
    private static final String URL = "jdbc:virtuoso://localhost:1111/";
    private static final String USERNAME = "dba";
    private static final String PASSWORD = "SecretPassword";

    static {
        try {
            Class.forName("virtuoso.jdbc4.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load Virtuoso JDBC driver", e);
        }
    }

    private static Connection dbConnect() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    // insert single statement
    public static void insert(String stmt) throws SQLException {
        Connection conn = dbConnect();
        PreparedStatement createGraphStmt = conn.prepareStatement(stmt);
        createGraphStmt.execute();
        conn.close();
    }
    // insert Jena model
    public static void insertOnto(String graphIRI, OntModel model){
        try{
            StmtIterator iter = model.listStatements();
            while (iter.hasNext()) {
                org.apache.jena.rdf.model.Statement stmt = iter.nextStatement();
                Node subject = stmt.getSubject().asNode();
                Node predicate = stmt.getPredicate().asNode();
                RDFNode object = stmt.getObject();
                String sparql;
                if (object.isLiteral()) {
                    Literal literal = object.asLiteral();
                    RDFDatatype datatype = literal.getDatatype();
                    String datatypeURI = datatype.getURI();
                    String literalValue = literal.getLexicalForm().replace("'", "''");
                    sparql = "SPARQL INSERT INTO GRAPH <" + graphIRI + "> { <" + subject + "> <" + predicate + "> ";
                    sparql += datatypeURI != null ? "\"\"\"" + literalValue + "\"\"\"^^<" + datatypeURI + "> }" : "'" + literalValue + "' }";
                } else {
                    sparql = "SPARQL INSERT INTO GRAPH <" + graphIRI + "> { <" + subject + "> <" + predicate + "> <" + object + "> }";
                }
                
                insert(sparql);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Load Jena Model from db given the IRI
    public static OntModel retrieveReasonedModel(String sourceIRI,OntModelSpec reasoner) {    
        OntModel model = ModelFactory.createOntologyModel( reasoner); 
        VirtGraph connString = new VirtGraph("jdbc:virtuoso://localhost:1111",USERNAME,PASSWORD);
        Query sparql = QueryFactory.create("SELECT * FROM <"+sourceIRI+"> WHERE { ?s ?p ?o }");     
        QueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, connString);
        org.apache.jena.query.ResultSet results = vqe.execSelect();
        while (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            RDFNode subjectNode = solution.getResource("?s");
            RDFNode predicateNode = solution.getResource("?p");
            RDFNode objectNode = solution.get("?o");
            if (subjectNode != null && predicateNode != null && objectNode != null) {
                model.add(subjectNode.asResource(), predicateNode.as(Property.class), objectNode);
            }
        }
        return model;
    }
    



    public static ResultSet executeQuery(String sql) throws SQLException {
        Connection conn = dbConnect();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        return rs;
    }
      // true if res is memo
    // false if not
    public static boolean isKnownOResource(String sourceIRI) throws SQLException{
        String queryString ="SPARQL ASK WHERE{ GRAPH <"+sourceIRI+">{?s ?p ?o}}";
        ResultSet answer= VirtuosoConnector.executeQuery(queryString);
        return answer.next()&answer.getBoolean(1);
    }
       
    public static void deleteOnto(String graphIRI) throws SQLException{
        String clearQuery = String.format("SPARQL CLEAR GRAPH  <%s> ", graphIRI);
        ResultSet rs =  VirtuosoConnector.executeQuery(clearQuery);
        System.out.println("Deleted graph successfully!");
    }

}
