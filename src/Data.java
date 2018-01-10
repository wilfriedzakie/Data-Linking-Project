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
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.store.DatasetGraphTDB;
import org.apache.jena.tdb.sys.TDBInternal;
import
org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.rdf.model.*;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.tdb.base.file.Location;

public class Data{

	static Dataset d1;
	static Dataset d2;

public static Dataset createDataset(String file, String directory)
{
	try{
		Dataset dataset= TDBFactory.createDataset(directory);
		dataset.begin(ReadWrite.WRITE) ; 
		Model model= dataset.getDefaultModel();
		TDBLoader.loadModel(model, file);
		dataset.commit();
		System.out.println("Done");
		dataset.end();
		
		return dataset; 
		}
		catch(Exception ex)
		{
			System.out.println("##### Error Fonction: createDataset #####");
			System.out.println(ex.getMessage());
			return null;
		}
	}

	public static void main(String args[])
	{
			
			//String file = args[0];
			//String directory = args[1];
			String file1="D://Informatiion integration//PR-1/person1/person11.rdf";
			String directory1="D://Informatiion integration//PR-1/person1/db_person1";
			d1 = createDataset(file1,directory1);
			String file2="D://Informatiion integration//PR-1/person1/person12.rdf";
			String directory2="D://Informatiion integration//PR-1/person1/db_person2";
			d2 = createDataset(file2,directory2);
			
			//just to check
			
	}
}