package test;

import io.TweetHashTagTuple;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import predictor.HashTagPredictor;
import test.EvaluationMethods.EvaluatorOptions;

public class TenFoldEvaluation extends Evaluator {
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

}
