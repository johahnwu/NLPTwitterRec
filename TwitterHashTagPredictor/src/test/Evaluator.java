package test;

import java.util.ArrayList;
import java.util.List;

public class Evaluator {

	public enum EvaluatorOptions {
		NAIVE, RECALL;
	}

	/**
	 * This evaluates for each k in numPredictionsList, if any of the top k hash
	 * tags are contained in the actual hash tag set. The returned list
	 * corresponds to the numPredictionsList. For example, [1, 2, 3] might
	 * return with [0.0, 0.0, 1.0], where it means that the third predicted hash
	 * tag is in the actual set but the first two are not. 1.0 means contained,
	 * 0.0 means the top k are not contained in the actual set.
	 * 
	 * @param predictions
	 *            List of all predicted hash tags, sorted by highest confidence
	 *            first
	 * @param actual
	 *            List of actual hash tags corresponding to Tweet text
	 * @param numPredictionsList
	 *            List of "k" predictions
	 * @return List where 1.0 means correctly tagged, 0.0 otherwise
	 */
	public static List<Double> evaluateCorrectnessWithKPredictions(
			List<String> predictions, List<String> actual,
			List<Integer> numPredictionsList, EvaluatorOptions option) {
		switch (option) {
		case NAIVE:
			return naiveEvaluateCorrectnessWithKPredictions(predictions,
					actual, numPredictionsList);
		case RECALL:
			return recallEvaluateCorrectnessWithKPredictions(predictions,
					actual, numPredictionsList);
		default:
			return naiveEvaluateCorrectnessWithKPredictions(predictions,
					actual, numPredictionsList);
		}
	}

	public static List<Double> naiveEvaluateCorrectnessWithKPredictions(
			List<String> predictions, List<String> actual,
			List<Integer> numPredictionsList) {
		List<Double> evaluationList = new ArrayList<Double>(
				numPredictionsList.size());
		// k corresponds to the top K predictions
		for (int k : numPredictionsList) {
			// Sometimes the number of predictions is less than the wanted
			// number
			int stopIndex = Math.min(k, predictions.size());
			boolean correctlyTagged = false;
			// iterate through the prediction list and see if it's in the actual
			// set
			for (int i = 0; i < stopIndex; i++) {
				if (actual.contains(predictions.get(i))) {
					correctlyTagged = true;
					break;
				}
			}
			if (correctlyTagged)
				evaluationList.add(1.0);
			else
				evaluationList.add(0.0);
		}
		return evaluationList;
	}

	public static List<Double> recallEvaluateCorrectnessWithKPredictions(
			List<String> predictions, List<String> actual,
			List<Integer> numPredictionsList) {
		List<Double> evaluationList = new ArrayList<Double>(
				numPredictionsList.size());
		// k corresponds to the top K predictions
		for (int k : numPredictionsList) {
			// Sometimes the number of predictions is less than the wanted
			// number
			int stopIndex = Math.min(k, predictions.size());
			// iterate through the prediction list and see if it's in the actual
			// set
			int numCorrect = 0;
			for (int i = 0; i < stopIndex; i++) {
				if (actual.contains(predictions.get(i))) {
					numCorrect += 1;
				}
			}
			// we use the min as the divisor because it'll be impossible to get
			// a full score if k is less than the actual hash tags set
			int divisor = Math.min(k, actual.size());
			// dividing by 0 is no good
			if (divisor <= 0)
				divisor = 1;
			double evaluationScore = numCorrect / divisor;
			if (evaluationScore > 1) {
				System.out.println("ERROR!");
				System.out.println("predicted" + predictions);
				System.out.println("actual" + actual);
				System.out.println(divisor + " " + numCorrect);
			}
			evaluationList.add(evaluationScore);
		}
		return evaluationList;
	}
}
