package test.pos;

import java.io.IOException;

import predictor.POSHashTagPredictor;
import test.GeneralEvaluation;
import test.Options;

public class POSTenFoldEvaluation {

	public static void main(String[] args) throws IOException {
		Options options = new Options(args);
		options.predictor = new POSHashTagPredictor();
		GeneralEvaluation.tenFoldEvaluation(options);
	}
}
