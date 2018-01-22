import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import davi_withoutclusters.newDiscoverNonKeys5Almost;

public class Load_files { 
	
	String onto1=new String();
	String onto2=new String();
	ArrayList<String> keys=new ArrayList<String>();
	
	public  Load_files (String file) throws FileNotFoundException, IOException {
		
		List<String> list=new ArrayList<String>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {  	
		    	list.add(line);
		    }
		}
		
		onto1="onto: "+list.get(1);
		onto2="onto: "+list.get(4);
		
		String key=list.get(7).substring(6,list.get(7).length()-1);
		
		String[] ks=key.split(",");
		
		for(String k:ks) {
			keys.add(k.substring(1,k.length()-1));
		}
			
	}

	public String getOnto1() {
		return onto1;
	}

	public String getOnto2() {
		return onto2;
	}

	public ArrayList<String> getKeys() {
		return keys;
	}

}
