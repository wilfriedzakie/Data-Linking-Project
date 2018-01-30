import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import davi_withoutclusters.newDiscoverNonKeys5Almost;

public class PreProcessData { 
	
	String onto1=new String();
	String onto2=new String();

	String outputPath=new String();
	String goldStandardPath=new String();
	
	String dataset1=new String();
	String dataset2=new String();
	
	ArrayList<String> keys=new ArrayList<String>();
	
	public  PreProcessData (String file) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
		
		List<String> list=new ArrayList<String>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {  	
		    	list.add(line);
		    }
		}
		
		onto1="onto: "+list.get(1);
		
		onto2="onto: "+list.get(4);
		
		goldStandardPath=list.get(7);
		outputPath=list.get(10);
		
		//Parse goldStandard and get new 
		goldStandardPath= convertXML(goldStandardPath, outputPath);
		
		dataset1=outputPath+"db1/";
		dataset2=outputPath+"db2/";	
			
	}
	
	
	
	/**
	 *  get path to dataset of the first ontology*/
	public String getDataset1() {
		return dataset1+"/";
	}

	/**
	 * get path to dataset of the second ontology*/
	public String getDataset2() {
		return dataset2+"/";
	}

	/**
	 * get path to Gold Standard*/
	public String getGoldStandardPath() {
		return goldStandardPath;
	}

	/** 
	 * get path to output folder*/
	public String getOutputPath() {
		return outputPath;
	}
	
	/**
	 *  Get first ontology OWL URI*/
	public String getOnto1() {
		return onto1;
	}

	/**  
	 * Get second ontology OWL URI*/
	public String getOnto2() {
		return onto2;
	}


	
	
	
/**Transform the XML gold standard to a .txt file in the form:
	 * entity1 ----> entity2
*
* */
public String convertXML(String filePath,String outputpath) throws UnsupportedEncodingException, FileNotFoundException, IOException, ParserConfigurationException, SAXException {

		
		ArrayList<String> ent1 = new ArrayList<String>();
		ArrayList<String> ent2 = new ArrayList<String>();
		
		String outPath=outputpath+"/goldStand.txt";

		try (Writer out = new OutputStreamWriter(new FileOutputStream(outPath), "UTF-8")) {

			File fXmlFile = new File(filePath);
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

					if (count == nList.getLength()
							&& count2 == nList.getLength()) {
						finished = true;
						break;
					}

					String s = "";
					if (n.getNodeName() == "entity1") {

						count++;

						Node entity1 = doc.getElementsByTagName("entity1")
								.item(i);

						NamedNodeMap attr = entity1.getAttributes();
						Node nodeAttr = attr.getNamedItem("rdf:resource");
					
						String[] entKey = nodeAttr.toString().split("=");

						s = entKey[1].trim().replaceAll("\"", "");
						//System.out.println(s);
						ent1.add(s);

					}
					String s2 = "";
					if (n.getNodeName() == "entity2") {

						count2++;
						Node entity2 = doc.getElementsByTagName("entity2")
								.item(i);

						NamedNodeMap attr2 = entity2.getAttributes();
						Node nodeAttr2 = attr2.getNamedItem("rdf:resource");
					

						String[] entKey2 = nodeAttr2.toString().split("=");

						s2 = entKey2[1].trim().replaceAll("\"", "");
						//System.out.println(s2);
						ent2.add(s2);
					}
				}
			}
			
			for (int j =0; j<ent1.size(); j++)
			//System.out.println(ent1.get(j) + "--->"+ ent2.get(j));
				out.write(ent1.get(j) + " ---> "+ ent2.get(j) + "\n");

		}
		return outPath;
	}
	
}
