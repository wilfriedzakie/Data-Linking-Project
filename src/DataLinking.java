import java.util.Iterator;

import javax.management.Query;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.tdb.TDBFactory;

//change
public class DataLinking {

	public static void main(String[] args) {
		
		Dataset d=TDBFactory.createDataset("D://Informatiion integration//PR-1/person1/db_person1\"");
		//Dataset d= data.d1;
		Dataset d2=TDBFactory.createDataset("D://Informatiion integration//PR-1/person1/db_person2\"");
		
		d.begin(ReadWrite.READ);
		try {
			Iterator<Quad> iter = d.asDatasetGraph().find();
			int i = 0;
			System.out.println("begin ");
			while (iter.hasNext() && i < 20) {
				Quad quad = iter.next();
				System.out.println("iteration " + i);
				System.out.println(quad);
				i++;
			}
		} finally {
			d.end();
		}
		d.close();
		System.out.println("finish1 ...");
		
		d2.begin(ReadWrite.READ);
		try {
			Iterator<Quad> iter = d2.asDatasetGraph().find();
			int i = 0;
			System.out.println("begin ");
			while (iter.hasNext() && i < 20) {
				Quad quad = iter.next();
				System.out.println("iteration " + i);
				System.out.println(quad);
				i++;
			}
		} finally {
			d2.end();
		}
		d2.close();
		System.out.println("finish2 ...");
		
		/*String sparqlQueryString="PREFIX humans: <http://www.inria.fr/2007/09/11/humans.rdfs#> PREFIX inst: <http://www.inria.fr/2007/09/11/humans.rdfs-instances#> SELECT ?x WHERE {?x humans:hasSpouse inst:Catherine .}";
		// See http://incubator.apache.org/jena/documentation/query/app_api.html
		org.apache.jena.query.Query query= QueryFactory.create(sparqlQueryString) ;
		QueryExecution qexec= QueryExecutionFactory.create(query, d) ;
		try
		{
		ResultSet results= qexec.execSelect() ;
		while(results.hasNext()) {
			
		QuerySolution sol= results.next();

		System.out.println("Solution := "+sol);
		for(Iterator<String> names= sol.varNames(); names.hasNext(); ) {

		final String name= names.next();
		System.out.println("\t"+name+" := "+sol.get(name));
		}
	}	} 
		finally
		{ qexec.close() ; }
		// Close the dataset
		d.close();*/

	}
}
