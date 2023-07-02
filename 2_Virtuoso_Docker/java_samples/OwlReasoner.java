import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.reasoner.ValidityReport.Report;
import org.apache.jena.rdf.model.InfModel;

public class OwlReasoner {

  public static final String INP_FILE_PATH = "../data/city_sample.ttl";
  public static final String OUT_FILE_PATH = "../data/city_sample_inferred.ttl";

  public static void main(String[] args) throws IOException {
    // Load the turtle file into a Jena model
    Model model = ModelFactory.createDefaultModel();
    model.read(INP_FILE_PATH, "TURTLE");

    // Create a reasoner using the built-in OWL reasoner
    Reasoner reasoner = ReasonerRegistry.getOWLReasoner();

    // Apply the reasoner to the model
    reasoner = reasoner.bindSchema(model);
    Model inferredModel = ModelFactory.createInfModel(reasoner, model);

    // Check if the inferred model is valid
    if (inferredModel instanceof InfModel) {
      InfModel infModel = (InfModel) inferredModel;
      ValidityReport report = infModel.validate();
      if (report.isValid()) {
        System.out.println("Inferred model is valid!");

        // Write the inferred triples to a turtle file
        try (FileWriter out = new FileWriter(OUT_FILE_PATH)) {
          inferredModel.write(out, "TURTLE");
          System.out.println("Inferred triples saved!");
        }
      } else {
        System.out.println("Inferred model is not valid:");
        Iterator<Report> iter = report.getReports();
        while (iter.hasNext()) {
          System.out.println(iter.next().toString());
        }
      }
    }
  }
}
