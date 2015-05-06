package test;

import io.InputFileReader;
import io.TweetHashTagTuple;
import io.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import predictor.HashTagPrediction;
import predictor.POSHashTagPredictor;

public class HashTagCorrectnessTestHarness {
	public static void main(String[] args) throws IOException {
		String inputFile = "data/randomized_tweets";
		String outputFile = "output";
		String posPredictionModelFile = "models/normal_posmodel";
		int numPredictions = 3;

		int index = 0;
		while (index < args.length) {
			if (args[index].equalsIgnoreCase("--inputFile")) {
				inputFile = args[index + 1];
				index += 2;
			} else if (args[index].equalsIgnoreCase("--outputFile")) {
				outputFile += args[index + 1];
				index += 2;
			} else if (args[index].equalsIgnoreCase("--posPredictionModelFile")) {
				posPredictionModelFile = args[index + 1];
				index += 2;
			} else if (args[index].equalsIgnoreCase("numPredictions")) {
				numPredictions = Integer.valueOf(args[index + 1]);
				index += 2;
			} else {
				index += 1;
			}
		}

		POSHashTagPredictor predictor = new POSHashTagPredictor(null,
				posPredictionModelFile);

		InputFileReader fileReader = new InputFileReader(new File(inputFile));
		String tweet;
		double totalTweets = 0.0;
		double correctlyTaggedTweets = 0.0;
		while ((tweet = fileReader.getNextLine()) != null) {
			TweetHashTagTuple tuple = Utils.convertInputToHashTagTuple(tweet);
			if (tuple == null)
				continue;
			List<HashTagPrediction> predictedHashTags = predictor
					.predictTopKHashTagsForTweet(tuple.text, numPredictions);
			List<String> actualHashTags = tuple.hashTags;
			List<String> predictions = new ArrayList<String>();
			for (HashTagPrediction pred : predictedHashTags) {
				predictions.add(pred.hashtag);
			}

			boolean correctlyTagged = false;
			for (HashTagPrediction prediction : predictedHashTags) {
				if (actualHashTags.contains(prediction.hashtag)) {
					correctlyTaggedTweets += 1;
					correctlyTagged = true;
					break;
				}
			}
			totalTweets += 1;

			if (correctlyTagged) {
				System.out.print("SUCCESS");
			} else {
				System.out.print("FAILED");
			}
			System.out.println(": " + tuple.text);
			System.out.println("Predicted:" + predictions);
			System.out.println("Actual:" + actualHashTags);
		}
		double accuracy = correctlyTaggedTweets / totalTweets;
		System.out.println("Total Tweets:" + totalTweets);
		System.out.println("Correctly Tagged Tweets:" + correctlyTaggedTweets);
		System.out.println("Accuracy:" + accuracy);

	}
}
