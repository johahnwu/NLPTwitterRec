package test;

import io.TweetHashTagTuple;
import io.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import predictor.POSHashTagPredictor;

public class POSTenFoldEvaluation {

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
		List<TweetHashTagTuple> totalList = Utils
				.loadFilesIntoHashTagTupleList(new File(inputFile));

		System.out.println("File to write to " + outputFile);
		try (PrintWriter pw = new PrintWriter(new File(outputFile))) {
			TenFoldEvaluation evaluator = new TenFoldEvaluation();
			POSHashTagPredictor predictor = new POSHashTagPredictor();
			List<Integer> numPredictions = new ArrayList<Integer>();
			for (int i = 0; i < 20; i++)
				numPredictions.add(i);
			evaluator.tenFoldEvaluateAndWrite(predictor, totalList,
					numPredictions, pw);
		}

	}
}
