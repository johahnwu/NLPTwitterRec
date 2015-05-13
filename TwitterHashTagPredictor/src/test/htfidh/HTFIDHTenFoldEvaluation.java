package test.htfidh;

import htfidh.HTFIDHHashTagPredictor;

import java.io.IOException;

import test.GeneralEvaluation;
import test.Options;

public class HTFIDHTenFoldEvaluation {
	public static void main(String[] args) throws IOException {
		Options options = new Options(args);
		options.predictor = new HTFIDHHashTagPredictor();
		GeneralEvaluation.tenFoldEvaluation(options);
	}
}
