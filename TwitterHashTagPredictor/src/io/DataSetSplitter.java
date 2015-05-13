package io;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DataSetSplitter {
	public static void main(String[] args) throws IOException {
		String inputFile = args[0];
		String trainingFile = args[1];
		String testingFile = args[2];
		InputFileReader reader = new InputFileReader(new File(inputFile));
		String line;
		List<TweetHashTagTuple> trainingList = new ArrayList<>();
		while ((line = reader.getNextLine()) != null) {
			TweetHashTagTuple tuple = Utils.convertInputToHashTagTuple(line);
			if (tuple != null)
				trainingList.add(tuple);
		}
		List<TweetHashTagTuple> testingList = Utils
				.chooseAndRemoveTenPercent(trainingList);
		try (PrintWriter trainWriter = new PrintWriter(trainingFile);
				PrintWriter testWriter = new PrintWriter(testingFile)) {
			for (TweetHashTagTuple tuple : trainingList) {
				trainWriter.println(tuple.toString());
			}
			for (TweetHashTagTuple tuple : testingList) {
				testWriter.println(tuple.toString());
			}
		}
	}
}
