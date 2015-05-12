package test.pos;

import io.TweetHashTagTuple;
import io.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import predictor.HashTagPredictor;
import predictor.POSHashTagPredictor;
import test.IterativeEvaluation;
import test.Options;

public class POSIterativeEvaluation {
	public static void main(String[] args) throws IOException {
		Options options = new Options(args);

		System.out.println("Loading training candidates file "
				+ options.inputFile);
		List<TweetHashTagTuple> trainingCandidates = Utils
				.loadFilesIntoHashTagTupleList(new File(options.inputFile));
		System.out.println(String.format("Loaded %d tweets.",
				trainingCandidates.size()));

		// System.out.println("Loading testing file " + testingFile);
		// List<TweetHashTagTuple> testingList = Utils
		// .loadFilesIntoHashTagTupleList(new File(testingFile));
		// System.out.println(String.format("Loaded %d tweets.",
		// testingList.size()));
		System.out.println("Choosing test candidates from training candidates");
		List<TweetHashTagTuple> testingList = Utils
				.chooseAndRemoveTenPercent(trainingCandidates);
		System.out.println(String.format(
				"Training Candidates %d, \t Testing %d",
				trainingCandidates.size(), testingList.size()));

		System.out.println("File to write to " + options.outputFile);
		try (PrintWriter pw = new PrintWriter(new File(options.outputFile))) {
			if (options.incrementSize == -1)
				options.incrementSize = (int) Math
						.ceil(((double) trainingCandidates.size() / options.numIterations));
			System.out.println("incrementSize " + options.incrementSize);
			IterativeEvaluation evaluator = new IterativeEvaluation(
					options.numIterations, options.incrementSize);
			HashTagPredictor predictor = new POSHashTagPredictor();
			List<Integer> numPredictions = new ArrayList<Integer>();
			for (int i = 0; i < 20; i++)
				numPredictions.add(i);
			evaluator.iterativelyEvaluate(predictor, trainingCandidates,
					testingList, numPredictions, pw, options.evalOptions);

			System.out.println("DONE!");
		}

	}
}
