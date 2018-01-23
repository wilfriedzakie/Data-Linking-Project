package dataLink;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

public class Evaluator {

	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
		args = new String[] { "C:/Users/dell/Downloads/PR-1/person1/dataset11_dataset12_goldstandard_person.xml" };
		// "C:/Users/dell/Desktop/result" };

		Map<String, String> output = new HashMap<>();

		try (Writer out = new OutputStreamWriter(new FileOutputStream(
				"C:/Users/dell/Downloads/PR-1/results.tsv"), "UTF-8")) {

			// try {
			// Create a Document from a file map.xml
			File fXmlFile = new File(args[0]);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			DocumentTraversal traversal = (DocumentTraversal) doc;
			NodeIterator iterator = traversal.createNodeIterator(
					doc.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null,
					true);

			NodeList nList = doc.getElementsByTagName("entity1");

			boolean finished = false;

			int count = 0;
			int count2 = 0;
			

			for (Node n = iterator.nextNode(); n != null; n = iterator
					.nextNode()) {

				for (int i = 0; i < nList.getLength() && !finished; i++) {

					// output.put(entKey, entKey2);

					if (count == nList.getLength()
							&& count2 == nList.getLength()) {
						finished = true;
						break;
					}
					
					

					if (n.getNodeName() == "entity1") {

						count++;

						Node entity1 = doc.getElementsByTagName("entity1")
								.item(i);

						NamedNodeMap attr = entity1.getAttributes();
						Node nodeAttr = attr.getNamedItem("rdf:resource");
						//System.out.println("l'entity1 = " + nodeAttr);
						String entKey = nodeAttr.toString();
						
					}
					if (n.getNodeName() == "entity2") {

						count2++;
						Node entity2 = doc.getElementsByTagName("entity2")
								.item(i);

						NamedNodeMap attr2 = entity2.getAttributes();
						Node nodeAttr2 = attr2.getNamedItem("rdf:resource");
						//System.out.println("l'entity 2 = "
								//+ nodeAttr2.toString());

						String entKey2 = nodeAttr2.toString();
						

					}

				}

			}
			
			for(String o : output.keySet()){
				String value = output.get(o);
				System.out.println(value);
			}
			
			

			//out.write(entKey + "\t" + entKey2 + "\n");

			
		}

		

	}

}
