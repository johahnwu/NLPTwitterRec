package test.pos;

import io.TweetHashTagTuple;
import io.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import predictor.POSHashTagPredictor;
import test.EvaluationMethods.EvaluatorOptions;
import test.IterativeEvaluation;

public class POSIterativeEvaluation {
	public static void main(String[] args) throws IOException {
		String inputFile = "data/randomized_tweets";
		String outputFile = "output/iterative_recall_output.csv";
		String testingFile = "data/testing_set";
		EvaluatorOptions options = EvaluatorOptions.RECALL;

		int index = 0;
		while (index < args.length) {
			if (args[index].equalsIgnoreCase("--inputFile")) {
				inputFile = args[index + 1];
				index += 2;
			} else if (args[index].equalsIgnoreCase("--outputFile")) {
				outputFile = args[index + 1];
				index += 2;
			} else if (args[index].equalsIgnoreCase("--testingFile")) {
				testingFile = args[index + 1];
				index += 2;
			} else if (args[index].equalsIgnoreCase("--evalType")) {
				String evalType = args[index + 1];
				if (evalType.equalsIgnoreCase("NAIVE"))
					options = EvaluatorOptions.NAIVE;
				else if (evalType.equalsIgnoreCase("RECALL"))
					options = EvaluatorOptions.RECALL;
				else {
					System.err
							.println("Invalid evalType, requires [NAIVE, RECALL]");
				}
				index += 2;
			} else {
				index += 1;
			}
		}

		System.out.println("Loading training candidates file " + inputFile);
		List<TweetHashTagTuple> trainingCandidates = Utils
				.loadFilesIntoHashTagTupleList(new File(inputFile));
		System.out.println(String.format("Loaded %d tweets.",
				trainingCandidates.size()));

		System.out.println("Loading testing file " + testingFile);
		List<TweetHashTagTuple> testingList = Utils
				.loadFilesIntoHashTagTupleList(new File(testingFile));
		System.out.println(String.format("Loaded %d tweets.",
				testingList.size()));

		System.out.println("File to write to " + outputFile);
		try (PrintWriter pw = new PrintWriter(new File(outputFile))) {
			int numIterations = 10;
			int increment = (int) Math
					.ceil(((double) trainingCandidates.size() / numIterations));
			System.out.println("incrementSize " + increment);
			IterativeEvaluation evaluator = new IterativeEvaluation(
					numIterations, increment);
			POSHashTagPredictor predictor = new POSHashTagPredictor();
			List<Integer> numPredictions = new ArrayList<Integer>();
			for (int i = 0; i < 20; i++)
				numPredictions.add(i);
			evaluator.iterativelyEvaluate(predictor, trainingCandidates,
					testingList, numPredictions, pw, options);

			System.out.println("DONE!");
		}

	}
}
