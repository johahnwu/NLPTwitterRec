package test.combo;

import java.io.IOException;

import predictor.ComboHashTagPredictor;
import test.GeneralEvaluation;
import test.Options;

public class ComboIterativeEvaluation {
	public static void main(String[] args) throws IOException {
		Options options = new Options(args);
		options.predictor = new ComboHashTagPredictor();
		GeneralEvaluation.iterativeEvaluation(options);
	}
}
