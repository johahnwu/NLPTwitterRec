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
		int iterationNumber = 0;
		while (iterationNumber < numIterations) {
			if (trainingCandidates.isEmpty()) {
				System.out.println("Training Candidates Empty, Iteration"
						+ iterationNumber);
			}
			int endIndex = Math.min(trainIncrementSize,
					trainingCandidates.size());
			List<TweetHashTagTuple> tweetsToAdd = trainingCandidates.subList(0,
					endIndex);
			trainingCandidates.removeAll(tweetsToAdd);
			currentTrainingList.addAll(tweetsToAdd);
		}
	}

}
