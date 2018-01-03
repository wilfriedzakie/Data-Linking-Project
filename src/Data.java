import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;

import java.io.InputStream;
import java.util.*;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphListener;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.Quad;

public class Data {
	public static Dataset d;
	public Dataset createDataset(String file, String directory) {
		try {
			Dataset dataset = TDBFactory.createDataset(directory);
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
	
	public static void main(String args[])
	{
			//String file = args[0];
				//String directory = args[1];
				String directory="";
				String file="";
				d = TDBFactory.createDataset(directory);
				Model model = d.getDefaultModel();
	}

}
