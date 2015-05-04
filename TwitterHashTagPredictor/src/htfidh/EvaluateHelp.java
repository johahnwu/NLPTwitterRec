package htfidh;

import java.util.ArrayList;

public class EvaluateHelp {

	public static boolean getSinglePrecisionApproach2(
			ArrayList<String> predictedHashTags,
			ArrayList<String> actualHashTags) {
		for (String predicted : predictedHashTags) {
			if (actualHashTags.contains(predicted)) {
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
