package test;

import test.EvaluationMethods.EvaluatorOptions;

/**
 * Class to parse command line args
 * 
 * @author Matthew
 *
 */
public class Options {
	public String inputFile = "data/randomized_tweets";
	public String outputFile = "output/iterative_naive_output.csv";
	public String testingFile = "data/testing_set";
	public EvaluatorOptions evalOptions = EvaluatorOptions.NAIVE;
	public int numIterations = 10;
	public int incrementSize = -1;

	public Options(String[] args) {
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
			} else {

				index += 1;
			}
		}
	}
}
