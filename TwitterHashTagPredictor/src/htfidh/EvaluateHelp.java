package htfidh;

import java.util.ArrayList;
import java.util.List;

import predictor.HashTagPrediction;

public class EvaluateHelp {

	public static boolean getSinglePrecisionApproach2(
			List<HashTagPrediction> predictedHashTags,
			ArrayList<String> actualHashTags) {
		System.out.println(predictedHashTags);
		System.out.println(actualHashTags);
		for (HashTagPrediction predicted : predictedHashTags) {
			if (actualHashTags.contains(predicted.hashtag)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Can probably be used for precision too
	 * 
	 * @param predictedHashTags
	 * @param actualHashTags
	 * @return
	 */
	public static int getSingleRecallApproach2(
			ArrayList<String> predictedHashTags,
			ArrayList<String> actualHashTags) {
		int count = 0;
		for (String predicted : predictedHashTags) {
			if (actualHashTags.contains(predicted)) {
				count++;
			}
		}
		return count;
	}

	public static double precision(int correct, int incorrect) {
		double precision = correct * 1.0 / (correct + incorrect);
		return precision;
	}

}
