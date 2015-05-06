package test;

import io.TweetHashTagTuple;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import predictor.HashTagPrediction;
import predictor.HashTagPredictor;
import test.Evaluator.EvaluatorOptions;

public class TenFoldEvaluation {
	/**
	 * 
	 * @param totalList
	 * @param numPredictionsList
	 * @param pw
	 *            PrintWrtier to write out to
	 * @param option
	 *            Evaluator Option [NAIVE, etc]. @see test.Evaluator
	 * @throws IOException
	 */
	public void tenFoldEvaluateAndWrite(HashTagPredictor predictor,
			List<TweetHashTagTuple> totalList,
			List<Integer> numPredictionsList, PrintWriter pw,
			EvaluatorOptions option) throws IOException {
		int TEN = 10;

		System.out.println("tenFoldEvaluation with set of " + totalList.size()
				+ " tweets");
		System.out.println("using prediction numbers " + numPredictionsList);
		List<List<TweetHashTagTuple>> tenLists = splitIntoTenRandomLists(totalList);
		assert (tenLists.size() == TEN);

		// initialize the accuracyTotals for averaging later
		List<Double> accuracyTotals = new ArrayList<Double>();
		for (int i = 0; i < numPredictionsList.size(); i++) {
			accuracyTotals.add(0.0);
		}
		for (int i = 0; i < TEN; i++) {

			System.out.println("Starting iteration " + i);
			// choose the 9 (excluding ith) as the training set
			List<TweetHashTagTuple> trainingList = new ArrayList<>();
			for (int k = 0; k < TEN; k++) {
				if (k != i)
					trainingList.addAll(tenLists.get(k));
			}

			// make the ith as the test set
			List<TweetHashTagTuple> testingList = new ArrayList<>(
					tenLists.get(i));

			// train the pos prediction model
			predictor.trainModel(trainingList);

			// test the the model
			List<Double> accuracyEvaluations = testPredictor(testingList,
					predictor, numPredictionsList, option);

			assert (accuracyEvaluations.size() == accuracyTotals.size());
			for (int j = 0; j < accuracyEvaluations.size(); j++) {
				Double currentTotal = accuracyTotals.get(j);
				accuracyTotals
						.set(j, currentTotal + accuracyEvaluations.get(j));
			}
		}

		// find the average accuracy over the 10 runs
		List<Double> finalAccuracy = new ArrayList<Double>();
		for (int i = 0; i < accuracyTotals.size(); i++) {
			finalAccuracy.add(accuracyTotals.get(i) / TEN);
		}

		System.out.println("Printing to file");
		// print to file in the format [numPredictions], [accuracy]
		for (int i = 0; i < finalAccuracy.size(); i++) {
			pw.println(String.format("%d, %.4f", numPredictionsList.get(i),
					finalAccuracy.get(i)));
		}
		System.out.println("DONE!");
	}

	/**
	 * 
	 * @param totalList
	 *            A big list of input
	 * @return Ten separate lists that are composed of the big list
	 */
	private List<List<TweetHashTagTuple>> splitIntoTenRandomLists(
			List<TweetHashTagTuple> totalList) {
		List<List<TweetHashTagTuple>> listOfLists = new ArrayList<List<TweetHashTagTuple>>();
		// randomize the list
		Collections.shuffle(totalList);
		int numElementsPerList = totalList.size() / 10;
		System.out
				.println("Splitting into lists of size " + numElementsPerList);
		for (int i = 0; i < 9; i++) {
			List<TweetHashTagTuple> currentList = totalList.subList(i
					* numElementsPerList, (i + 1) * numElementsPerList);
			listOfLists.add(currentList);
		}
		listOfLists.add(totalList.subList((9 * numElementsPerList),
				totalList.size()));

		return listOfLists;
	}

	public List<Double> testPredictor(List<TweetHashTagTuple> testingList,
			HashTagPredictor predictor, List<Integer> numPredictionsList,
			EvaluatorOptions option) {
		List<Double> numberCorrectList = new ArrayList<Double>(
				numPredictionsList.size());
		// initialize the list with all 0.0
		for (int i = 0; i < numPredictionsList.size(); i++)
			numberCorrectList.add(0.0);

		double totalNumTweets = 0.0;

		for (TweetHashTagTuple tuple : testingList) {
			List<HashTagPrediction> predictedHashTags = predictor
					.predictTopKHashTagsForTweet(tuple.text, -1);

			// extract the predictions
			List<String> predictions = new ArrayList<String>();
			for (HashTagPrediction pred : predictedHashTags) {
				predictions.add(pred.hashtag);
			}

			// tuple.hashTags is the actual hash tag set
			List<Double> evaluations = Evaluator
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
