import java.util.HashMap;
import java.util.Map;

import com.wcohen.ss.AbstractStringDistance;
import com.wcohen.ss.api.StringWrapper;

public class SimilarityAlgo {
	/**
	 *La liste des algorithmes de calcul de similarite que <br/>
	 * nous considerons (cf. Cohen, String distance)
	 */
	private static enum Algorithms {
		JARO, JAROWINKLER, JAROWINKLERTFIDF, LEVENSTEIN, LEVEL2JAROWINKLER, SOFTTFIDF, JACCARD, GEOSIM
	};

	private Map<String, AbstractStringDistance> algorithmInstances;

	public SimilarityAlgo() {
		algorithmInstances = new HashMap<String, AbstractStringDistance>();
	}

	 public AbstractStringDistance getAlgorithm(String algoName) {
		if (!algorithmInstances.containsKey(algoName)) {
			AbstractStringDistance algo;
			switch (Algorithms.valueOf(algoName)) {
			case JACCARD: {
				algo = new com.wcohen.ss.Jaccard();
				break;
			}
			case JARO: {
				algo = new com.wcohen.ss.Jaro();
				break;
			}
			case JAROWINKLER: {
				algo = new com.wcohen.ss.JaroWinkler();
				break;
			}
			case JAROWINKLERTFIDF: {
				algo = new com.wcohen.ss.JaroWinklerTFIDF();
				break;
			}
			case LEVENSTEIN: {
				algo = new com.wcohen.ss.Levenstein();
				break;
			}
			case LEVEL2JAROWINKLER: {
				algo = new com.wcohen.ss.Level2JaroWinkler();
				break;
			}
			case SOFTTFIDF: {
				com.wcohen.ss.AbstractStringDistance algoSim = new com.wcohen.ss.JaroWinkler();
				algo = new com.wcohen.ss.SoftTFIDF(algoSim, 0.90);
				break;
			}
			default: {
				algo = new com.wcohen.ss.Jaccard();
				// System.out.println("This algorithm does not exist");
			}
			}
			algorithmInstances.put(algoName, algo);
		}
		return algorithmInstances.get(algoName);
	}

	private double normalize(double value, String algorithm, String s, String t) {
		double normalizedValue = value;
		switch (Algorithms.valueOf(algorithm)) {
		case JACCARD: {
			normalizedValue = value;
			break;
		}
		case JARO: {
			break;
		}
		case JAROWINKLER: {
			break;
		}
		case JAROWINKLERTFIDF: {
			break;
		}
		case LEVENSTEIN: {
			int size = (s.length() > t.length()) ? s.length() : t.length();
			if (size == 0) {
				System.out.println(
						"***Warning::Both of the strings are empty (SimilarityAlgorith.java). Please check this fact.");
				normalizedValue = 0;
			} else {
				normalizedValue = (size - Math.abs(value)) / size;
			}
			break;
		}
		case LEVEL2JAROWINKLER: {
			break;
		}
		case SOFTTFIDF: {
			normalizedValue = value;
			break;
		}
		default: {
			normalizedValue = value;
			break;
		}
		}

		return normalizedValue;
	}

	public double getScore(String s, String t, String algorithm, boolean normalize) {
		AbstractStringDistance algo = this.getAlgorithm(algorithm);
		double score = algo.score(s, t);
		if (normalize) {
			score = this.normalize(score, algorithm, s, t);
		}
		return score;
	}

}
