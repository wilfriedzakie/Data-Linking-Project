import java.io.*;
import java.util.*;

import javax.management.Query;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.tdb.TDBFactory;

//change
public class DataLinking {

	public static RDFNode alignment(String sparqlQueryString2) {
		Dataset d2 = TDBFactory.createDataset("D://Informatiion integration//PR-1/person1/db_person2/");
		// String sparqlQueryString2 = " PREFIX inst:
		// <http://www.okkam.org/ontology_person2.owl#> SELECT * WHERE { ?p
		// inst:soc_sec_id '"
		// + data + "' .}";
		org.apache.jena.query.Query query2 = QueryFactory.create(sparqlQueryString2);
		QueryExecution qexec2 = QueryExecutionFactory.create(query2, d2);
		// System.out.println(sparqlQueryString2);

		RDFNode person2 = null;
		try {
			ResultSet results2 = qexec2.execSelect();
			while (results2.hasNext()) {

				QuerySolution sol2 = results2.next();
				// System.out.println(sol2);

				for (Iterator<String> names2 = sol2.varNames(); names2.hasNext();) {

					final String name2 = names2.next();

					if (sol2.get(name2).isResource()) {
						// System.out.println("\t" + name2 + " := " + sol2.get(name2));
						person2 = sol2.get(name2);
					}
				}
			}
		} finally {
			qexec2.close();
		}
		// Close the dataset
		d2.close();

		return person2;
	}

	public static String keyToQuery(String onto, List<String> keys) {

		List<String> toQuery = new ArrayList<String>();

		int kSize = keys.size();

		List<Character> cha = new ArrayList<>();
		char c1;
		Random r1 = new Random();
		c1 = (char) (r1.nextInt(26) + 'a');
		cha.add(c1);
		for (String k : keys) {

			char c2;

			// generate random character for the query

			do {
				Random r2 = new Random();
				c2 = (char) (r2.nextInt(26) + 'a');
				if (cha.contains(c2) || c1 == c2)
					continue;

				break;
			} while (true);

			cha.add(c2);

			String toQue = "?" + c1 + " onto:" + k + " ?" + c2 + " .";
			toQuery.add(toQue);

		}

		String sparqlQueryString = " PREFIX " + onto + " SELECT * WHERE { ";

		for (String s : toQuery)
			sparqlQueryString = sparqlQueryString + " " + s;

		sparqlQueryString = sparqlQueryString + "}";

		System.out.println(sparqlQueryString);

		return sparqlQueryString;
	}

	public static String secondQuery() {

		return null;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {

		Dataset d = TDBFactory.createDataset("D://Informatiion integration//PR-1/person1/db_person1/");
		// Dataset d= data.d1;

		String file = "D://Informatiion integration//PR-1//person1/keys.txt";

		Load_files l = new Load_files(file);

		String onto1 = l.getOnto1();
		String onto2 = l.getOnto2();

		List<String> keys = l.getKeys();

		// String sparqlQueryString = "PREFIX inst:
		// <http://www.okkam.org/ontology_person1.owl#> SELECT * WHERE { ?p
		// inst:soc_sec_id ?o .}";

		String sparqlQueryString = keyToQuery(onto1, keys);

		// System.out.println(sparqlQueryString2);
		org.apache.jena.query.Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, d);
		try {
			ResultSet results = qexec.execSelect();
			try (Writer out = new OutputStreamWriter(new FileOutputStream("results.txt"), "UTF-8")) {
			while (results.hasNext()) {

				String sparqlQueryString2 = " PREFIX " + onto2 + " SELECT * WHERE "
						+ sparqlQueryString.substring(sparqlQueryString.indexOf("{"));

				QuerySolution sol = results.next();
				RDFNode person2 = null;
				RDFNode person1 = null;
				// System.out.println("Solution := " + sol);
				// System.out.println("\n"+sparqlQueryString);

				for (Iterator<String> names = sol.varNames(); names.hasNext();) {

					final String name = names.next();

					String second_onto = new String();
					// if (name.toLowerCase().contains("o")) {

					// System.out.println("\t" + name + " := " + sol.get(name));

					if (sol.get(name).isResource()) {
						person1 = sol.get(name);
					}

					else {
						// System.out.println(name);
						String str = "?" + name;
						String newstr = "'" + sol.get(name).toString() + "'";
						sparqlQueryString2 = sparqlQueryString2.replace(str, newstr);

					}

				}
				// System.out.println(sparqlQueryString2);
				person2 = alignment(sparqlQueryString2);
				if (person1 != null && person2 != null) {
					System.out.println("Person1:  " + person1.toString() + "		Person2:  " + person2.toString());
					
						out.write("\nPerson1:  " + person1.toString() + "		Person2:  " + person2.toString());
					}
				}
			}
		} finally {
			qexec.close();
		}
		// Close the dataset

		d.close();

	}
}
