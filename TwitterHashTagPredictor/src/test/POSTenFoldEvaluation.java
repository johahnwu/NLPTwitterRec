package test;

import io.InputFileReader;
import io.TweetHashTagTuple;
import io.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pos.predictor.POSPredictionModel;
import pos.predictor.POSPredictionTrainer;
import pos.tagger.TwitterPOSTagger;
import predictor.HashTagPrediction;
import predictor.POSHashTagPredictor;

public class POSTenFoldEvaluation {
	private TwitterPOSTagger posTagger;

	public POSTenFoldEvaluation() throws IOException {
		posTagger = new TwitterPOSTagger();
	}

	/**
	 * 
	 * @param totalList
	 * @param numPredictionsList
	 * @param pw
	 *            PrintWrtier to write out to
	 * @throws IOException
	 */
	public void tenFoldEvaluateAndWrite(List<TweetHashTagTuple> totalList,
			List<Integer> numPredictionsList, PrintWriter pw)
			throws IOException {
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
			POSPredictionModel predictionModel = trainModel(trainingList);

			// create a predictor with said model
			POSHashTagPredictor hashTagPredictor = new POSHashTagPredictor(
					posTagger, predictionModel);

			// test the the model
			List<Double> accuracyEvaluations = testPredictor(testingList,
					hashTagPredictor, numPredictionsList);

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

	/**
	 * 
	 * @param trainingList
	 *            List of TweetHashTagTuples to train on
	 * @return POSPrediction Model that has been trained on the input
	 */
	public POSPredictionModel trainModel(List<TweetHashTagTuple> trainingList) {
		POSPredictionTrainer trainer = new POSPredictionTrainer(posTagger);
		return trainer.trainModel(trainingList);
	}

	public List<Double> testPredictor(List<TweetHashTagTuple> testingList,
			POSHashTagPredictor predictor, List<Integer> numPredictionsList) {
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
			List<Double> evaluations = evaluateCorrectnessWithKPredictions(
					predictions, tuple.hashTags, numPredictionsList);

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
	private List<Double> evaluateCorrectnessWithKPredictions(
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

	public static void main(String[] args) throws IOException {
		String inputFile = "data/randomized_tweets";
		String outputFile = "output/normal_output";

		int index = 0;
		while (index < args.length) {
			if (args[index].equalsIgnoreCase("--inputFile")) {
				inputFile = args[index + 1];
				index += 2;
			} else if (args[index].equalsIgnoreCase("--outputFile")) {
				outputFile = args[index + 1];
				index += 2;
			} else {
				index += 1;
			}
		}

		System.out.println("Loading file " + inputFile);
		InputFileReader reader = new InputFileReader(new File(inputFile));
		List<TweetHashTagTuple> totalList = new ArrayList<TweetHashTagTuple>();
		String tweet;
		while ((tweet = reader.getNextLine()) != null) {
			TweetHashTagTuple tuple = Utils.convertInputToHashTagTuple(tweet);
			if (tuple == null)
				continue;
			totalList.add(tuple);
		}

		System.out.println("File to write to " + outputFile);
		try (PrintWriter pw = new PrintWriter(new File(outputFile))) {
			POSTenFoldEvaluation evaluator = new POSTenFoldEvaluation();
			List<Integer> numPredictions = new ArrayList<Integer>();
			for (int i = 0; i < 20; i++)
				numPredictions.add(i);
			evaluator.tenFoldEvaluateAndWrite(totalList, numPredictions, pw);
		}

	}
}
