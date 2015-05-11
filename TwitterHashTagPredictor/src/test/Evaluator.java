package test;

import io.TweetHashTagTuple;

import java.util.ArrayList;
import java.util.List;

import predictor.HashTagPrediction;
import predictor.HashTagPredictor;
import test.EvaluationMethods.EvaluatorOptions;

public class Evaluator {
	public List<Double> testPredictor(List<TweetHashTagTuple> testingList,
			HashTagPredictor predictor, List<Integer> numPredictionsList,
			EvaluatorOptions option) {
		List<Double> numberCorrectList = new ArrayList<Double>(
				numPredictionsList.size());
		// initialize the list with all 0.0
		for (int i = 0; i < numPredictionsList.size(); i++)
			numberCorrectList.add(0.0);

		double totalNumTweets = 0.0;

		// find the largest number of predictions
		int maxPredictions = 1;
		for (int num : numPredictionsList)
			maxPredictions = Math.max(num, maxPredictions);

		for (TweetHashTagTuple tuple : testingList) {
			List<HashTagPrediction> predictedHashTags = predictor
					.predictTopKHashTagsForTweet(tuple.text, maxPredictions);

			// extract the predictions
			List<String> predictions = new ArrayList<String>();
			for (HashTagPrediction pred : predictedHashTags) {
				predictions.add(pred.hashtag);
			}

			// tuple.hashTags is the actual hash tag set
			List<Double> evaluations = EvaluationMethods
					.evaluateCorrectnessWithKPredictions(predictions,
							tuple.hashTags, numPredictionsList, option);

			assert (evaluations.size() == numPredictionsList.size());
			// if correct, add it to the count
			for (int i = 0; i < numPredictionsList.size(); i++) {
				numberCorrectList.set(i,
						numberCorrectList.get(i) + evaluations.get(i));
			}

			// increment the total number of tweets
			totalNumTweets += 1;
		}

		// similar to the numberCorrectList, but we find the accuracy by
		// dividing by the total number of tweets
		List<Double> accuracyList = new ArrayList<Double>(
				numPredictionsList.size());
		for (int i = 0; i < numberCorrectList.size(); i++) {
			accuracyList.add(numberCorrectList.get(i) / totalNumTweets);
		}
		return accuracyList;
	}
}
