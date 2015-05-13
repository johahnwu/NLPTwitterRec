package test;

import io.TweetHashTagTuple;
import io.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import predictor.HashTagPredictor;

public class GeneralEvaluation {
	public static void iterativeEvaluation(Options options) throws IOException {
		System.out.println("Loading training candidates file "
				+ options.inputFile);
		List<TweetHashTagTuple> trainingCandidates = Utils
				.loadFilesIntoHashTagTupleList(new File(options.inputFile));
		System.out.println(String.format("Loaded %d tweets.",
				trainingCandidates.size()));

		System.out.println("Loading testing file " + options.testingFile);
		List<TweetHashTagTuple> testingList = Utils
				.loadFilesIntoHashTagTupleList(new File(options.testingFile));
		System.out.println(String.format("Loaded %d tweets.",
				testingList.size()));
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
			HashTagPredictor predictor = options.predictor;
			List<Integer> numPredictions = new ArrayList<Integer>();
			for (int i = 0; i < 20; i++)
				numPredictions.add(i);
			evaluator.iterativelyEvaluate(predictor, trainingCandidates,
					testingList, numPredictions, pw, options.evalOptions);

			System.out.println("DONE!");
		}
	}

	public static void tenFoldEvaluation(Options options) throws IOException {
		System.out.println("Loading file " + options.inputFile);
		List<TweetHashTagTuple> totalList = Utils
				.loadFilesIntoHashTagTupleList(new File(options.inputFile));

		System.out.println("File to write to " + options.outputFile);
		try (PrintWriter pw = new PrintWriter(new File(options.outputFile))) {
			TenFoldEvaluation evaluator = new TenFoldEvaluation();
			HashTagPredictor predictor = options.predictor;
			List<Integer> numPredictions = new ArrayList<Integer>();
			for (int i = 0; i < 20; i++)
				numPredictions.add(i);
			evaluator.tenFoldEvaluateAndWrite(predictor, totalList,
					numPredictions, pw, options.evalOptions);
		}
	}
}
