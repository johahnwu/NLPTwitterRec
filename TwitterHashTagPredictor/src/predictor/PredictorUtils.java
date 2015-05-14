package predictor;

import java.util.List;
import java.util.Map;

public class PredictorUtils {
	/**
	 * This is for duplicates. If the hash tag already exists (with a diffrent
	 * confidence), add the confidence. Otherwise add the HashTagPrediction to
	 * the currentList
	 * 
	 * @param currentList
	 * @param listToAdd
	 * @param seenIndex
	 * @param counts
	 */
	public static void addInConfidences(List<HashTagPrediction> currentList,
			List<HashTagPrediction> listToAdd, Map<String, Integer> seenIndex,
			Map<String, Integer> counts) {
		for (HashTagPrediction prediction : listToAdd) {
			String ht = prediction.hashtag;
			if (seenIndex.keySet().contains(ht)) {
				int index = seenIndex.get(ht);
				currentList.get(index).confidence += prediction.confidence;
				counts.put(ht, counts.get(ht) + 1);
			} else {
				seenIndex.put(ht, currentList.size());
				currentList.add(prediction);
				counts.put(ht, 1);
			}
		}
	}

	/**
	 * Take the counth root of the confidences for each hashtag, depending on
	 * how many times the hashtag appears
	 * 
	 * @param list
	 * @param counts
	 */
	public static void takeCounthRoots(List<HashTagPrediction> list,
			Map<String, Integer> counts) {
		for (HashTagPrediction prediction : list) {
			int count = counts.get(prediction.hashtag);
			double exponent = 1.0 / (count);
			prediction.confidence = Math.pow(prediction.confidence, exponent);
		}
	}

	/**
	 * Divide all confidences by the highest confidence, so the max is 1
	 * 
	 * @param list
	 */
	public static void normalize(List<HashTagPrediction> list) {
		// make the max value 1
		double maxConfidence = 0.0;
		for (HashTagPrediction prediction : list) {
			maxConfidence = Math.max(maxConfidence, prediction.confidence);
		}
		for (HashTagPrediction prediction : list) {
			prediction.confidence = prediction.confidence / maxConfidence;
		}
	}
}
