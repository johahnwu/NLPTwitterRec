package test;

import htfidh.HTFIDHHashTagPredictor;
import htfidh.ModelHelp;

public class TestApproach2 {

	public static void main(String[] args) {
		HTFIDHHashTagPredictor predictor = new HTFIDHHashTagPredictor();
		// TODO set paths
		predictor.setDeliminator("###");
		predictor.train();
		// ModelHelp.printTHFM(predictor.thfm);
		// ModelHelp.printHFM(predictor.hfm);
		ModelHelp.printIDF(predictor.idf);
	}
}