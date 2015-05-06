package test;

import io.TweetHashTagTuple;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import predictor.HashTagPredictor;
import test.EvaluationMethods.EvaluatorOptions;

public class IterativeEvaluation extends Evaluator {

	public int numIterations;
	public int trainIncrementSize;

	public IterativeEvaluation(int numIterations, int trainIncrementSize) {
		this.numIterations = numIterations;
		this.trainIncrementSize = trainIncrementSize;
	}

	public void iterativelyEvaluate(HashTagPredictor predictor,
			List<TweetHashTagTuple> trainingCandidates,
			List<TweetHashTagTuple> testingList,
			List<Integer> numPredictionsList, PrintWriter pw,
			EvaluatorOptions option) {

		if (numIterations <= 0 || trainIncrementSize <= 0) {
			throw new IllegalArgumentException(
					"Set numIterations and trainIncrementSize to positive");
		}

		Collections.shuffle(trainingCandidates);

		List<TweetHashTagTuple> currentTrainingList = new ArrayList<TweetHashTagTuple>();
		List<Integer> trainingListSize = new ArrayList<Integer>();
		List<List<Double>> accuracyResults = new ArrayList<>();
		for (int i = 0; i < numPredictionsList.size(); i++) {
			accuracyResults.add(new ArrayList<Double>());
		}

		int iterationNumber = 0;
		while (iterationNumber < numIterations) {
			System.out.println("Starting iteration " + iterationNumber);
			if (trainingCandidates.isEmpty()) {
				System.out.println("Training Candidates Empty, Iteration "
						+ iterationNumber);
				break;
			}
			int movedCounter = 0;
			while (movedCounter < trainIncrementSize
					&& !trainingCandidates.isEmpty()) {
				currentTrainingList.add(trainingCandidates.remove(0));
				movedCounter += 1;
			}
			trainingListSize.add(currentTrainingList.size());

			predictor.trainModel(currentTrainingList);

			List<Double> accuracyEvaluations = testPredictor(testingList,
					predictor, numPredictionsList, option);

			assert (accuracyEvaluations.size() == numPredictionsList.size());

			for (int i = 0; i < accuracyEvaluations.size(); i++) {
				accuracyResults.get(i).add(accuracyEvaluations.get(i));
			}
			iterationNumber += 1;
		}

		System.out.println("printing to output");
		// pw.print("k\t");
		// for (int i = 0; i < trainingListSize.size(); i++) {
		// pw.print(padTo8(trainingListSize.get(i).toString()));
		// }
		// pw.println();
		// for (int i = 0; i < accuracyResults.size(); i++) {
		// pw.print(numPredictionsList.get(i) + "\t");
		// for (double d : accuracyResults.get(i)) {
		// pw.print(padTo8(String.format("%.4f", d)));
		// }
		// pw.println();
		// }

		pw.print(padTo8("k"));
		for (int i = 0; i < numPredictionsList.size(); i++) {
			pw.print(padTo8(numPredictionsList.get(i).toString()));
		}
		pw.println();
		for (int i = 0; i < trainingListSize.size(); i++) {
			pw.print(padTo8(trainingListSize.get(i).toString()));

			for (int j = 0; j < numPredictionsList.size(); j++) {
				double d = accuracyResults.get(j).get(i);
				pw.print(padTo8(String.format("%.4f", d)));
			}
			pw.println();
		}
	}

	private String padTo8(String s) {
		return String.format("%8s,", s);
	}

}
