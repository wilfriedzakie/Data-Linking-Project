

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

/**
 * This class is the main class for linking data. 
 * It's launched after the data class who is responsible of creating the RDF dataset
 * It computes the entities mapping and at the end evaluates the ouptut with respect to a gold standard
 * The entities mapping  result as well as the evaluation metrics are stored in text files in the output folder specified in the input file.
 * */
public class DataLinking {

	public static List<String> notToUse = new ArrayList<>();
	


	/**This method converts the RDF graph to a hashmap mapping a resource name (String) to an object of the class Resource  
	 * By querying the dataset using Sparql, each resource (subject) are mapped to literals (objects) that are related to it  
	 * If the object is not a literal, the method 'getResourceRelations()' (see description) gets the literals linked to that resource and append it to the subject's list of objects 
	 * @return Map<String, Resource>
	 * */
	public static Map<String, Resource> graphToMap(String onto, Dataset d) {
		String sparqlQueryString = " PREFIX " + onto + " SELECT * WHERE { ?s ?p ?o . }";
		Map<String, Resource> elements = new HashMap<>();
		
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
				RDFNode subject = solt.get("s");

				if (!notToUse.contains(subject.toString())) {
					if (!elements.containsKey(subject.toString())) {
						Resource instance = new Resource();
						instance.setName(subject.toString());
						if (object.isResource()) {
							// to do get resources element in an hashmap
							Map<String, String> prop_val = getResourceRelations(object, d, onto);
							for (String n : prop_val.keySet())
								instance.setRelatValue( prop_val.get(n));
						} else {
							instance.setRelatValue(object.toString());
							elements.put(subject.toString(), instance);
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
		} finally {
			qexec.close();
		}

		return elements;

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

				

				if (!elements.containsKey(property.toString())) {
					if (object.isResource()) {

						Map<String, String> prop_val = getResourceRelations(object, d, onto);

						for (String n : prop_val.keySet()) {
							elements.put(getPropertyLabel(n), prop_val.get(n));
						}
					} else {

						elements.put(getPropertyLabel(property.toString()), object.toString());

					}
				}

			}

		} finally {
			qexec.close();
		}

		return elements;

	}

	
	/**
	 * Get the label associated to a relation from the relation URI
	 * @return String*/
	public static String getPropertyLabel(String relation) {

		String re = relation.substring(relation.indexOf("#") + 1, relation.length());
		return re;
	}
	
	/** This methods finds the matching of each element of the first ontology in the second one
	 * For each resource in the first ontology, its objects list is compared to the objects list of all resources in the second ontology
	 * The comparison is done using by giving some scores if two objects are equal and if not calculate their similarity using a string similarity method.
	 * The aggregate score of the objects of the resources of the second ontology are recorded
	 * and the resource with the highest score is stored as the match of the first ontology resource.
	 * This method returns a map with keys the resources of the first ontology and values their corresponding resources in the second one.  
	 **/
public static Map<String, String> mapping(Map<String, Resource> onto1_elements,Map<String, Resource> onto2_elements) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		SimilarityAlgo simi = new SimilarityAlgo();
		Map<String,String> mapping=new HashMap<>();
		
			for (String i1 : onto1_elements.keySet()) {
		
			Resource inst1 = onto1_elements.get(i1);
			List<String> a1 = inst1.getValue();
			Map<Resource, Double> scores = new HashMap<>();
			//Map<String, String> rel_val = inst1.getRelatValue();
			Resource entity2=null;
			for (String i2 : onto2_elements.keySet())
			{
				double normalizeScore=0;
				double score=0;
				Resource inst2 = onto2_elements.get(i2);
				List<String> a2 = inst2.getValue();
				for(String val1:a1) {
					
					for(String val2:a2) {
						
					if (val2.equals(val1))
						score += 1;
					else {
						
						double simvalue1 = simi.getScore(val1, val2, "JAROWINKLER", true);
						double simvalue2 = simi.getScore(val1, val2, "JACCARD", true);

						score += (simvalue1+simvalue2*2)/3;
						
					}			
				}	
			}
				normalizeScore=score/(a2.size());
			
				if(normalizeScore>0.8) {
					scores.put(inst2, score);
					
				}
					
		}
		if(!scores.isEmpty())	
		{
			entity2 = Collections.max(scores.entrySet(), Map.Entry.comparingByValue()).getKey();
			mapping.put(inst1.getName(), entity2.getName());
		}
		
			}
			return mapping;
	}

/**
 * Write result in a file whose path is determined by the output path defined in the 
 *  **/
	public static void writeOutput(ArrayList<String> map, String path) throws IOException {
		try (Writer out = new OutputStreamWriter(new FileOutputStream(path), "UTF-8")) {
			for(String m:map) {
				out.write(m+"\n");
				System.out.println(m+"\n");
			}	
		}
		
	}
	
	/**The main method receives a text file as input, and output the result of the matching
	 * It also outputs the 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * */

	public static void main(String[] args) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {


		String file = "D:\\Informatiion integration\\PR-1\\Inputfiles/inputFile1.txt";

		PreProcessData p = new PreProcessData(file);

		String pathResult=p.getOutputPath();
		
		String pathDataset1=p.getDataset1();
		String pathDataset2=p.getDataset2();
		
		String onto1 = p.getOnto1();
		String onto2 = p.getOnto2();
		String pathGS=p.getGoldStandardPath();
		
		Dataset d1 = TDBFactory.createDataset(pathDataset1);
		Dataset d2 = TDBFactory.createDataset(pathDataset2);
		
		
		Map<String, Resource> onto1_elements = graphToMap(onto1, d1);
		Map<String, Resource> onto2_elements = graphToMap(onto2, d2);

		
		Map<String,String> mapping=new HashMap<>();
		
		for(String ent:notToUse) {
			if(onto1_elements.containsKey(ent))
				onto1_elements.remove(ent);
			if(onto2_elements.containsKey(ent))
				onto2_elements.remove(ent);
		}
		
		for (String i1 : onto1_elements.keySet()) {
			
			Resource inst1 = onto1_elements.get(i1);
			
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
				
				String output1=pathResult+"output1.txt";
				
				

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
			
	
			String result=pathResult+"result.txt";
			
			writeOutput(map3,result);
			
			Evaluator e=new Evaluator();
			
			Map<String, Double>measure3=e.eval(result, pathGS);
			
			double precision3=measure3.get("precision");
			
			double precision=0;
			double recall=0;
			
			precision=precision3;
			recall=measure3.get("recall");
			
			System.out.println(precision);
			System.out.println(recall);
			
			
			try (Writer out = new OutputStreamWriter(new FileOutputStream(pathResult+"evaluation.txt"), "UTF-8")) {
					out.write("Precision: "+precision+"\n");
					out.write("       Recall: "+recall+"\n");		
			}
			
	}
}