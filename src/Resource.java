/**This class stores resources information such as their name and the literals related to them 
 * */
import java.util.*;

import org.apache.jena.rdf.model.RDFNode;

public class Resource {
	
	/**Resource name*/
	private String name;
	
	/**Resource RDF node*/
	private RDFNode node;
	
	
	/**Get Resource name*/
	public String getName() {
		return name;
	}

	/***Set resource name*/
	public void setName(String name) {
		this.name = name;
	}

	/**Get Jena RDF node */
	public RDFNode getNode() {
		return node;
	}

	/**Set Jena RDF node */
	public void setNode(RDFNode node) {
		this.node = node;
	}

	/**List of literal related to the resource*/
	private List <String> value=new ArrayList<>();
	
	/**Get List of literal related to the resource*/
	public List<String> getValue() {
		return value;
	}

	/**Set List of literal related to the resource*/
	public void setRelatValue(String objects) {
		value.add(objects);
	}
	
}
