package test;

import htfidh.HFIHUOtsuka;
import htfidh.HTFIDHHashTagPredictor;

import java.io.IOException;

import predictor.ComboHashTagPredictor;
import predictor.HashTagPredictor;
import predictor.POSHashTagPredictor;
import test.EvaluationMethods.EvaluatorOptions;

/**
 * Class to parse command line args
 * 
 * @author Matthew
 *
 */
public class Options {
	public String inputFile = "data/randomize_train";
	public String outputFile = "output/iterative_naive_output.csv";
	public String testingFile = "data/randomized_test";
	public EvaluatorOptions evalOptions = EvaluatorOptions.NAIVE;
	public int numIterations = 10;
	public int incrementSize = -1;
	public HashTagPredictor predictor = null;

	public Options(String[] args) throws IOException {
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
					evalOptions = EvaluatorOptions.NAIVE;
				else if (evalType.equalsIgnoreCase("RECALL")) {
					evalOptions = EvaluatorOptions.RECALL;
				} else {
					System.err
							.println("Invalid evalType, requires [NAIVE, RECALL]");
				}
				index += 2;
			} else if (args[index].equalsIgnoreCase("--iterations")) {
				numIterations = Integer.valueOf(args[index + 1]);
				index += 2;
			} else if (args[index].equalsIgnoreCase("--incrementSize")) {
				incrementSize = Integer.valueOf(args[index + 1]);
				index += 2;
			} else if (args[index].equalsIgnoreCase("--predictor")) {
				predictor = getPredictor(args[index + 1]);
				index += 2;
			} else {
				index += 1;
			}
		}
	}

	private HashTagPredictor getPredictor(String predictorString)
			throws IOException {
		if (predictorString.equalsIgnoreCase("htfidh")) {
			return new HTFIDHHashTagPredictor();
		} else if (predictorString.equalsIgnoreCase("pos")) {
			return new POSHashTagPredictor();
		} else if (predictorString.equalsIgnoreCase("otsuka")) {
			return new HFIHUOtsuka();
		} else if (predictorString.equalsIgnoreCase("combo")) {
			return new ComboHashTagPredictor();
		} else {
			return null;
		}
	}
}
