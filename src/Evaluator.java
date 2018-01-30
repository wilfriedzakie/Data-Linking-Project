

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.*;


public class Evaluator {
	
	public static void main(String[] args) throws IOException {
		args = new String[] {
				"C:\\Users\\zakie\\eclipse-workspace\\Jeana\\results2.txt",
				"D:\\Informatiion integration\\PR-1\\restaurants/goldStand.txt"};

		Map<String, Set<String>> output = new HashMap<>();
		//Files.lines(Paths.get(args[0])).map(s -> s.split("--->")).forEach(s -> output.put(s[0], s[1]));
		ArrayList<String> outList=new ArrayList<>();
		
		int numOutput = 0;
		try(BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	outList.add(line);
		    	numOutput++;
		        String match1=line.split("--->")[0];
		        String match2=line.split("--->")[1];
		        
		        if(!output.containsKey(match1)) {
		        	Set<String> s=new HashSet<>();
		        	s.add(match2);
		        	output.put(match1, s);
		        }
		        else {
		        	Set<String> s=output.get(match1);
		        	s.add(match2);
		        	output.remove(match1);
		        	output.put(match1, s);
		        }  
		        
		    }
		}
		
		
		Map<String, Set<String>> goldstandard = new HashMap<>();
		int countGdLine=0;
		
		ArrayList<String> gDs=new ArrayList<>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(args[1]))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	gDs.add(line);
		    	countGdLine++;
		        String gd1=line.split("--->")[0];
		        String gd2=line.split("--->")[1];
		        
		        
		        if(!goldstandard.containsKey(gd1)) {
		        	Set<String> s=new HashSet<>();
		        	s.add(gd2);
		        	goldstandard.put(gd1, s);
		        	
		        }
		        else {
		        	Set<String> s=goldstandard.get(gd1);
		        	s.add(gd2);
		        	goldstandard.remove(gd1);
		        	goldstandard.put(gd1, s);
		        }
		    }
		}

		
		int numCorrect = 0;
		
		
		for(String o:outList) {
			for(String a :gDs)
			if(a.equals(o))
				numCorrect++;
		}
		
		System.out.println(outList.size());
		/*for (String o : output.keySet()) {
			if (goldstandard.get(o) != null) {
				Set<String> out=output.get(o);
				Set<String> gd=goldstandard.get(o);
				
				for(String o1:out)
				{
					if(gd.contains(o1))
						numCorrect++;	
				}
			}
		}*/
		
		
		System.out.println("Precison: " + 100.0 * numCorrect / outList.size() + "%");
		
		System.out.println("Recall: " + 100.0 * numCorrect/ gDs.size() + "%");
		
	}
	
	public Map<String, Double> eval(String outpt, String goldStand) throws FileNotFoundException, IOException{
		
		Map<String, Set<String>> output = new HashMap<>();
		//Files.lines(Paths.get(args[0])).map(s -> s.split("--->")).forEach(s -> output.put(s[0], s[1]));
		ArrayList<String> outList=new ArrayList<>();
		
		Map<String, Double>measures= new HashMap<>();
		
		int numOutput = 0;
		try(BufferedReader br = new BufferedReader(new FileReader(outpt))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	outList.add(line);
		 		    }
		}
		
		
		Map<String, Set<String>> goldstandard = new HashMap<>();
		int countGdLine=0;
		
		ArrayList<String> gDs=new ArrayList<>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(goldStand))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	gDs.add(line);
		    }
		}

		
		int numCorrect = 0;
		
		
		for(String o:outList) {
			for(String a :gDs)
			if(a.equals(o))
				numCorrect++;
		}
		
		
		Double precision=100.0 * numCorrect / outList.size();
		Double recall=100.0 * numCorrect/ gDs.size();
		
		System.out.println("Correct predicted: " + numCorrect + " over "+outList.size()+" with Gold Standard= "+gDs.size());
		
		measures.put("precision", precision);
		measures.put("recall", recall);
		
		
		
		return measures;
	}
	
}
