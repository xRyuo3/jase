import java.sql.*;

public class ExecQuery {

    public static void main(String[] args) {
        try {
            Class.forName("virtuoso.jdbc4.Driver");
            String url = "jdbc:virtuoso://localhost:1111";
            Connection conn = DriverManager.getConnection(url, "dba", "SecretPassword");

            String query = "SPARQL PREFIX ex: <http://example.org/> SELECT ?city WHERE { ?city a ex:City } LIMIT 10";
            exec_query(conn, query);

        } catch (Exception e){
            System.out.println(e);
            e.printStackTrace();
        }

    }

   public static void exec_query(Connection conn, String sql) {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            while(rs.next()) {

                for (int x = 1; x <= rsmd.getColumnCount(); x++) {
                    Object o = rs.getObject(x);
                    if (o == null)
                        System.out.print("[NULL]");
                    else
                        System.out.print(o + "=" + o.getClass());
                    System.out.print(" | ");
                }
                System.out.println();
            }
            rs.close();

            System.out.println("Done");
        } catch (Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }

}
