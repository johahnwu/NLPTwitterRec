package test.otsuka;

import htfidh.HFIHUOtsuka;

import java.io.IOException;

import test.GeneralEvaluation;
import test.Options;

public class OtsukaTenFoldEvaluation {
	public static void main(String[] args) throws IOException {
		Options options = new Options(args);
		options.predictor = new HFIHUOtsuka();
		GeneralEvaluation.tenFoldEvaluation(options);

	}
}
