/**
 * *This class create graph dataset from 2 RDF files
 * **/

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.rdf.model.*;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.tdb.base.file.Location;

public class Data {

	static Dataset d1;
	static Dataset d2;

	/**
	 * * Use Jena API built in function to create RDF graph **/
	private static Dataset dataset_creation(String file, String directory) {
		try {
			Dataset dataset = TDBFactory.createDataset(directory);
			dataset.begin(ReadWrite.WRITE);
			Model model = dataset.getDefaultModel();
			TDBLoader.loadModel(model, file);
			System.out.println(model);
			dataset.commit();

			dataset.end();

			return dataset;
		} catch (Exception ex) {
			System.out.println("##### Error Fonction: createDataset #####");
			System.out.println(ex.getMessage());
			return null;
		}
	}

	
	 public static void main(String args[]) throws FileNotFoundException, IOException {
		 
		 String file=args[0];
		 //String file="D:\\Informatiion integration\\PR-1/inputFile.txt";
		 ArrayList<String> list=new ArrayList<>();
		 
		 try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			    String line;
			    while ((line = br.readLine()) != null) {  	
			    	
					list.add(line);
			    }
			}
		 
		 String outputfile=list.get(7);
		 String file1=list.get(2);
		 String file2=list.get(2);
		 	 

		 String directory1=outputfile+"/db_1";
		 d1 =dataset_creation(file1,directory1); 
		 
		 String directory2=outputfile+"/db_2"; 
		 d2 = dataset_creation(file2,directory2);
	     
	 }
	/** 
	 * 
	 * Create 2 dataset for each rdf file */
	 public void createDataset(String file1, String file2, String directory1, String directory2) {
		d1 = dataset_creation(file1, directory1);
		d2 = dataset_creation(file2, directory2);
	}
}