package test;

import htfidh.HTFIDHHashTagPredictorMain;
import io.TweetHashTagTuple;
import io.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import predictor.HashTagPrediction;

public class TestApproach2 {

	public static void main(String[] args) {
		HTFIDHHashTagPredictorMain predictor = new HTFIDHHashTagPredictorMain();
		String testPath = "poemTags.txt";
		File f = new File(testPath);
		List<TweetHashTagTuple> thtt = null;
		try {
			thtt = Utils.loadFilesIntoHashTagTupleList(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}

		predictor.trainModel(thtt);
		// TODO set paths
		// predictor.setDeliminator("###");
		// predictor.train();
		// ModelHelp.printTHFM(predictor.thfm);
		// ModelHelp.printHFM(predictor.hfm);
		// ModelHelp.printIDF(predictor.idf);

		String testTweet = "i hell burnt pee";
		List<HashTagPrediction> predicted = predictor
				.predictTopKHashTagsForTweet(testTweet, 2);
		System.out.println(predicted);
		// predictor.test(2);
		// System.out.println(Utils.fixHashTags(testTweet)[2]);

	}
}