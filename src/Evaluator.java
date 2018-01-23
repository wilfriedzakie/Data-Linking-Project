package dataLink;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Evaluator {
	
	public static void main(String[] args) throws IOException {
		args = new String[] {
				"C:/Users/dell/workspace/DataLinking/results.txt",
				"C:/Users/dell/Downloads/PR-1/goldStand.tsv" };

		Map<String, String> output = new HashMap<>();
		Files.lines(Paths.get(args[0])).map(s -> s.split("--->"))
				.forEach(s -> output.put(s[0], s[1]));
		Map<String, String> goldstandard = new HashMap<>();
		Files.lines(Paths.get(args[1])).map(s -> s.split("--->"))
				.forEach(s -> goldstandard.put(s[0], s[1]));
		int numCorrect = 0;
		int numOutput = 0;
		for (String o : output.keySet()) {
			if (goldstandard.get(o) != null) {
				numOutput++;
				if (goldstandard.get(o).equals(output.get(o)))
					numCorrect++;
			}
		}
		System.out.println("Precison: " + 100.0 * numCorrect / numOutput + "%");
		System.out.println("Recall: " + 100.0 * numCorrect
				/ goldstandard.size() + "%");
	}
}
