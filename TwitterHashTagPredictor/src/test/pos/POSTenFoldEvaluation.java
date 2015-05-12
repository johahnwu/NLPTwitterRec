package test.pos;

import io.TweetHashTagTuple;
import io.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import predictor.POSHashTagPredictor;
import test.Options;
import test.TenFoldEvaluation;

public class POSTenFoldEvaluation {

	public static void main(String[] args) throws IOException {
		Options options = new Options(args);

		System.out.println("Loading file " + options.inputFile);
		List<TweetHashTagTuple> totalList = Utils
				.loadFilesIntoHashTagTupleList(new File(options.inputFile));

		System.out.println("File to write to " + options.outputFile);
		try (PrintWriter pw = new PrintWriter(new File(options.outputFile))) {
			TenFoldEvaluation evaluator = new TenFoldEvaluation();
			POSHashTagPredictor predictor = new POSHashTagPredictor();
			List<Integer> numPredictions = new ArrayList<Integer>();
			for (int i = 0; i < 20; i++)
				numPredictions.add(i);
			evaluator.tenFoldEvaluateAndWrite(predictor, totalList,
					numPredictions, pw, options.evalOptions);
		}

	}
}
