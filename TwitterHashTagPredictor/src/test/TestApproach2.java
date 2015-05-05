package test;

import htfidh.HTFIDHHashTagPredictorMain;
import htfidh.ModelHelp;

import java.util.ArrayList;

public class TestApproach2 {

	public static void main(String[] args) {
		HTFIDHHashTagPredictorMain predictor = new HTFIDHHashTagPredictorMain();
		// TODO set paths
		predictor.setDeliminator("###");
		predictor.train();
		// ModelHelp.printTHFM(predictor.thfm);
		ModelHelp.printHFM(predictor.hfm);
		// ModelHelp.printIDF(predictor.idf);
		String testTweet = "i hell cannot pee";
		ArrayList<String> predicted = predictor.predictTweet(testTweet, 1);
		// System.out.println(predicted);
		predictor.test(2);
		// System.out.println(Utils.fixHashTags(testTweet)[2]);

	}
}