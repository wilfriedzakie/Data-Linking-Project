

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.*;


public class Evaluator {
	
	
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
