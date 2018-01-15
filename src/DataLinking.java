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

		Dataset d = TDBFactory.createDataset("D://Informatiion integration//PR-1/person1/db_person1\"");
		// Dataset d= data.d1;
		Dataset d2 = TDBFactory.createDataset("D://Informatiion integration//PR-1/person1/db_person2\"");

		// Read database from location
		/*
		 * d.begin(ReadWrite.READ); try { Iterator<Quad> iter =
		 * d.asDatasetGraph().find(); int i = 0; System.out.println("begin "); while
		 * (iter.hasNext() && i < 20) { Quad quad = iter.next();
		 * System.out.println("iteration " + i); System.out.println(quad); i++; } }
		 * finally { d.end(); } d.close(); System.out.println("finish1 ...");
		 * 
		 * 
		 * //Read database from location d2.begin(ReadWrite.READ); try { Iterator<Quad>
		 * iter = d2.asDatasetGraph().find(); int i = 0; System.out.println("begin ");
		 * while (iter.hasNext() && i < 20) { Quad quad = iter.next();
		 * System.out.println("iteration " + i); System.out.println(quad); i++; } }
		 * finally { d2.end(); } d2.close(); System.out.println("finish2 ...");
		 */

		String sparqlQueryString1 = "PREFIX humans: <http://www.inria.fr/2007/09/11/humans.rdfs#> PREFIX inst: <http://www.inria.fr/2007/09/11/humans.rdfs-instances#> SELECT ?x WHERE {?x humans:hasSpouse inst:Catherine .}";

		String sparqlQueryString = " PREFIX p1: <http://www.okkam.org/ontology_person1.owl#>"
				+ " PREFIX p2: <http://www.okkam.org/ontology_person2.owl#>" +
				" SELECT * WHERE { ?X  p2:soc_sec_id  ?o ."
				+ "}";

		// String sparqlQueryString2=" PREFIX inst:
		// <http://www.okkam.org/ontology_person2.owl#> SELECT * WHERE { ?p
		// inst:soc_sec_id ?secu .}";
		// See http://incubator.apache.org/jena/documentation/query/app_api.html
		org.apache.jena.query.Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, d);
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {

				QuerySolution sol = results.next();

				System.out.println("Solution := " + sol);
				for (Iterator<String> names = sol.varNames(); names.hasNext();) {

					final String name = names.next();

					String second_onto = new String();
					//if (name.toLowerCase().contains("o")) {
						// System.out.println("\t"+name+" := "+sol.get(name));
						System.out.println("\t" + sol.get(name));

						String ssn = sol.get(name).toString();
						

						//String sparqlQueryString2 = " PREFIX inst: <http://www.okkam.org/ontology_person2.owl#> SELECT * WHERE { ?p inst:soc_sec_id "
							//	+ ssn + "  .}";
						
					//	System.out.println(sparqlQueryString2);

						/*org.apache.jena.query.Query query2 = QueryFactory.create(sparqlQueryString2);
						QueryExecution qexec2 = QueryExecutionFactory.create(query2, d2);
						try {
							ResultSet results2 = qexec2.execSelect();
							while (results2.hasNext()) {

								QuerySolution sol2 = results2.next();

								for (Iterator<String> names2 = sol2.varNames(); names2.hasNext();) {

									final String name2 = names2.next();

									if (name.toLowerCase().contains("x")) {
										// System.out.println("\t"+name+" := "+sol.get(name));
										ssn = sol.get(name2).toString();
									}
								}
							}
						} finally {
							qexec2.close();
						}
						// Close the dataset
						
					}*/
					//else
						 System.out.println("\t ontology1"+name+" := "+sol.get(name)+"   ontology2:="+second_onto);
				}
			}
		} finally {
			qexec.close();
		}
		// Close the dataset
		//d2.close();
		d.close();

	}
}
