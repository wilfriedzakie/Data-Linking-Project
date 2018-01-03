import org.apache.jena.tdb.TDBFactory;
import java.io.InputStream;
import java.util.*;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphListener;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.util.graph.GraphListenerBase;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.store.DatasetGraphTDB;
import org.apache.jena.tdb.sys.TDBInternal;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.rdf.model.*;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class Dataset {

	public org.apache.jena.query.Dataset createDataset(String file, String directory) {
		try {
			org.apache.jena.query.Dataset dataset = TDBFactory.createDataset(directory);
			dataset.begin(ReadWrite.WRITE);
			Model model = dataset.getDefaultModel();
			TDBLoader.loadModel(model, file);
			dataset.commit();
			dataset.end();
			return dataset;
			
		} catch (Exception ex) {
			System.out.println("##### Error Fonction: createDataset #####");
			System.out.println(ex.getMessage());
			return null;
		}

	}

}
