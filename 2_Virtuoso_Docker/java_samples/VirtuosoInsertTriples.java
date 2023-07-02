import java.sql.*;

import java.io.Reader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.Statement;

public class VirtuosoInsertTriples {

    public static void main(String[] args) {

        // Connection properties
        String url = "jdbc:virtuoso://localhost:1111";
        String user = "dba";
        String password = "SecretPassword";

        // File properties
        String fileName = "../data/city_sample_inferred.ttl";
        String graphUri = "http://example.com/city";

        try {
            // Load the Virtuoso JDBC driver
            Class.forName("virtuoso.jdbc4.Driver");

            // Open a connection to Virtuoso
            Connection conn = DriverManager.getConnection(url, user, password);

            // Prepare the SPARQL statement to create the new graph
            // NOTE => Comment here if the graph already exists
            String createGraphQuery = String.format("SPARQL CREATE GRAPH <%s>", graphUri);
            PreparedStatement createGraphStmt = conn.prepareStatement(createGraphQuery);
            createGraphStmt.execute();

            // Read the Turtle file into a byte stream
            InputStream in = new FileInputStream(fileName);

            // Decode the byte stream using UTF-8 encoding
            Reader r = new InputStreamReader(in, StandardCharsets.UTF_8);

            // Create a Jena model from the Turtle string
            Model model = ModelFactory.createDefaultModel();
            model.read(r, null, "TURTLE");

            // Close the reader
            r.close();

            // Insert each triple from the model into the Virtuoso graph
            StmtIterator iter = model.listStatements();
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();
                String s = stmt.getSubject().toString();
                String p = stmt.getPredicate().toString();
                RDFNode o = stmt.getObject();
                String oStr = null;
                if (o.isResource()) {
                    oStr = o.asResource().toString();
                } else if (o.isLiteral()) {
                    oStr = o.asLiteral().toString();
                } else if (o.isAnon()) {
                    oStr = "[]";
                }
                String insertQuery = String.format("SPARQL INSERT DATA { GRAPH <%s> { <%s> <%s> <%s> } }", graphUri, s, p, oStr);
                // System.out.println(insertQuery);
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.execute();
            }

            // Close the connection
            conn.close();

            System.out.println("Turtle file inserted successfully.");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
