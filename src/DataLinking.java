/**
 * this is the main class of the program computing the	alignment and evaluation
 * */
import java.io.*;
import java.util.*;

import javax.management.Query;
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.SAXException;

import com.wcohen.ss.AbstractStringDistance;

//change
public class DataLinking {

	/**Store the resource that are found to be objects to avoid looking for their matching in another ontology*/
	public static List<String> notToUse = new ArrayList<>();
	
	/**Precision of the result*/
	public static double precision=0;
	
	/**Recall of the result*/
	public static double recall=0;
	

	
/**This method converts the RDF graph to a hashmap mapping a resource name (String) to an object of the class Resource  
 * By querying the dataset using Sparql, each resource (subject) are mapped to literals (objects) that are related to it  
 * If the object is not a literal, the method 'getResourceRelations()' (see description) gets the literals linked to that resource and append it to the subject's list of objects 
 * */
	public static Map<String, Resource> graphToMap(String onto, Dataset d) {
		String sparqlQueryString = " PREFIX " + onto + " SELECT * WHERE { ?s ?p ?o . }";
		Map<String, Resource> elements = new HashMap<>();
		
		System.out.println(sparqlQueryString);
		org.apache.jena.query.Query query = QueryFactory.create(sparqlQueryString);
		//QueryExecution qexec = QueryExecutionFactory.create(query, d);

		try(QueryExecution qexec = QueryExecutionFactory.create(query, d);) {	

			ResultSet results = qexec.execSelect();

			while (results.hasNext()) {

				QuerySolution sol = results.next();

				Map<String, RDFNode> solt = new HashMap<>();

				for (Iterator<String> names = sol.varNames(); names.hasNext();) {

					final String name = names.next();
					solt.put(name, sol.get(name));

				}

				RDFNode property = solt.get("p");
				RDFNode object = solt.get("o");
				RDFNode subject = solt.get("s");

				if (!notToUse.contains(subject.toString())) {  
					if (!elements.containsKey(subject.toString())) {
						Resource resource = new Resource();
						resource.setName(subject.toString());
		 				if (object.isResource()) {
							Map<String, String> prop_val = getResourceRelations(object, d, onto);
							for (String n : prop_val.keySet())
								resource.setRelatValue(prop_val.get(n));
						} else {
							resource.setRelatValue(object.toString());
							elements.put(subject.toString(), resource);
						}
					} else {

						Resource instance = elements.get(subject.toString());

						if (object.isResource()) {
							Map<String, String> prop_val = getResourceRelations(object, d, onto);

							for (String n : prop_val.keySet())
								instance.setRelatValue(prop_val.get(n));
						} else {
							instance.setRelatValue( object.toString());
							elements.put(subject.toString(), instance);
						}

						elements.remove(subject);
						elements.put(subject.toString(), instance);
					}

				}
			}
		} 

		return elements;

	}

	/**This method gets the relation's name without the ontology URI  **/
	public static String getProperty(String relation) {

		String re = relation.substring(relation.indexOf("#") + 1, relation.length());
		return re;
	}

	/**It gets all objects(literals) and relations linked to a resource that is object of another resource.
	 * It calls itself whenever it finds another resource as object and returns the list of relations mapped to their literals.
	 */
	private static Map<String, String> getResourceRelations(RDFNode subject, Dataset d, String onto) {
		String sparqlQueryString = " PREFIX " + onto + " SELECT * WHERE {<" + subject.toString() + "> ?p ?o . }";
		Map<String, String> elements = new HashMap<>();
		notToUse.add(subject.toString());
		org.apache.jena.query.Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, d);
		
		try {

			ResultSet results = qexec.execSelect();

			while (results.hasNext()) {

				QuerySolution sol = results.next();

				Map<String, RDFNode> solt = new HashMap<>();

				for (Iterator<String> names = sol.varNames(); names.hasNext();) {

					final String name = names.next();
					solt.put(name, sol.get(name));

				}

				RDFNode property = solt.get("p");
				RDFNode object = solt.get("o");

				// System.out.println(getProperty(property.toString())+ " "+object.toString());

				if (!elements.containsKey(property.toString())) {
					if (object.isResource()) {

						Map<String, String> prop_val = getResourceRelations(object, d, onto);

						for (String n : prop_val.keySet()) {
							elements.put(getProperty(n), prop_val.get(n));
						}
					} else {

						elements.put(getProperty(property.toString()), object.toString());

					}
				}

			}

		} finally {
			qexec.close();
		}

		return elements;

	}


/** This methods finds the matching of each element of the first ontology in the second one
 * For each resource in the first ontology, its objects list is compared to the objects list of all resources in the second ontology
 * The comparison is done using by giving some scores if two objects are equal and if not calculate their similarity using a string similarity method.
 * The aggregate score of the objects of the resources of the second ontology are recorded
 * and the resource with the highest score is stored as the match of the first ontology resource.
 * This method returns a map with keys the resources of the first ontology and values their corresponding resources in the second one.  
 **/
	public static Map<String, String> mapping(Map<String, Resource> onto1_elements, Map<String, Resource> onto2_elements) {
		SimilarityAlgo simi = new SimilarityAlgo();
		Map<String, String> mapping = new HashMap<>();
		for (String i1 : onto1_elements.keySet()) {

			Resource inst1 = onto1_elements.get(i1);
			List<String> a1 = inst1.getValue();
			Map<Resource, Double> scores = new HashMap<>();
			
			Resource entity2 = null;
			for (String i2 : onto2_elements.keySet()) {
				double score = 0;
				Resource inst2 = onto2_elements.get(i2);
				List<String> a2 = inst2.getValue();
				for (String val1 : a1) {

					for (String val2 : a2) {

						if (val2.equals(val1))
							score += 3;
						else {

							double simvalue = simi.getScore(val1, val2, "JAROWINKLER", true);
							score += simvalue;
						}
					}
				}
				scores.put(inst2, score);
			}
			entity2 = Collections.max(scores.entrySet(), Map.Entry.comparingByValue()).getKey();
			mapping.put(inst1.getName(), entity2.getName());
		}
		
		return mapping;

	}

	
	/**The main method receives a text file as input, and output the result of the matching
	 * It also outputs the 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * */
	public static void main(String[] args) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
		
		//String file =args[0];
		
		//To remove
		String file = "D:\\Informatiion integration\\PR-1/inputFile.txt";
		
		PreProcessData p = new PreProcessData(file);
		
		String pathGS=p.getGoldStandardPath();
		String pathResult=p.getOutputPath();
		String pathDataset1=p.getDataset1();
		String pathDataset2=p.getDataset2();
		String onto1 = p.getOnto1();
		String onto2 = p.getOnto2();

		System.out.println("check");
		System.out.println(pathDataset1);
		Dataset d1 = TDBFactory.createDataset(pathDataset1);
		Dataset d2 = TDBFactory.createDataset(pathDataset2);
		
		
		Map<String, Resource> onto1_elements = graphToMap(onto1, d1);
		Map<String, Resource> onto2_elements = graphToMap(onto2, d2);

		
		//Remove from the datasets all resources that are found to be object in a relation 
		for (String ent : notToUse) {
			if (onto1_elements.containsKey(ent))
				onto1_elements.remove(ent);
			if (onto2_elements.containsKey(ent))
				onto2_elements.remove(ent);
		}

		Map<String, String> mapping1 = mapping(onto1_elements, onto2_elements);
		Map<String, String> mapping2 = mapping(onto2_elements, onto1_elements);

		ArrayList<String> map1 = new ArrayList<String>();
		ArrayList<String> map2 = new ArrayList<String>();
		ArrayList<String> map3 = new ArrayList<String>();

		// each elements in ontology 1 is mapped to many elements in ontogy 2
		for (String m : mapping1.keySet()) {
			String match1 = m + " ---> " + mapping1.get(m);
			map1.add(match1);
		}

		// each elements in ontology 2 is mapped to many elements in ontogy 1
		for (String m : mapping2.keySet()) {
			String match1 =mapping2.get(m) + " ---> " + m ;
			map2.add(match1);
		}

		// Map3: cross matching
		for (String m : mapping1.keySet()) {
			String match1 = m + " ---> " + mapping1.get(m);
			map3.add(match1);
		}
		for (String n : mapping2.keySet()) {
			String match2 = mapping2.get(n) + " ---> " + n;
			if (!map3.contains(match2)) {
				map3.add(match2);
		}
	}

			ArrayList<String> result = evaluator(map1, map2, map3,pathGS);
			System.out.println("Precison: " +precision + "%");
			System.out.println("Recall: " + recall + "%");
			

			try (Writer out = new OutputStreamWriter(new FileOutputStream(pathResult+"/results2.txt"), "UTF-8")) {

				for (String s : result) {
					
					out.write(s+"\n");
				}

			}
			

}
	

	/**Compute the precision and the recall for different matchings and return the matching with the highest precision as result
	 * */
private static ArrayList<String> evaluator(ArrayList<String> map1, ArrayList<String> map2, ArrayList<String> map3, String strGS) throws FileNotFoundException, IOException {

		//String strGS = "D://Informatiion integration//PR-1//person2/goldStand.tsv";
	

		ArrayList<String> gDs = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(strGS))) {
			for (String line; (line = br.readLine()) != null;) {
				gDs.add(line);
				}
		}
		
		
		int numCorrect1 = 0;

		for (String o : map1) {
			for (String a : gDs)
				if (a.equals(o))
					numCorrect1++;
		}
		
		
		int numCorrect2 = 0;
		
		for (String o : map2) {
			for (String a : gDs)
				if (a.equals(o))
					numCorrect2++;
		}
	
		
		int numCorrect3 = 0;
		for (String o : map3) {
			for (String a : gDs)
				if (a.equals(o))
					numCorrect3++;
		}				
		
		if(numCorrect1>numCorrect2 && numCorrect1>numCorrect3) {		
			System.out.println("1"+numCorrect1);
			precision=100.0 * numCorrect1 / map1.size();
			recall=100.0 * numCorrect1 / gDs.size();
			
			return map1;
		}
		
		else if(numCorrect2>=numCorrect1 && numCorrect2>=numCorrect3) {
			System.out.println("2"+numCorrect2);
			precision=100.0 * numCorrect2 / map2.size();
			recall=100.0 * numCorrect2 / gDs.size();
			
			return map2;
		}
		else if(numCorrect3>=numCorrect1 && numCorrect3>=numCorrect2) {
			System.out.println("3"+numCorrect3);
			System.out.println(numCorrect3);
			precision=100.0 * numCorrect3 / map3.size();
			recall=100.0 * numCorrect3 / gDs.size();
			
			return map3;		
		}
				
		return null;
	}
}
