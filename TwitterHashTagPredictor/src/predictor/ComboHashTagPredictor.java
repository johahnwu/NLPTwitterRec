package predictor;

import htfidh.HTFIDHHashTagPredictor;
import io.TweetHashTagTuple;

import java.io.IOException;
import java.util.List;

public class ComboHashTagPredictor implements HashTagPredictor {

	private POSHashTagPredictor posHTPredictor;
	private HTFIDHHashTagPredictor htfidhHTPredictor;

	public ComboHashTagPredictor() throws IOException {
		posHTPredictor = new POSHashTagPredictor();
		htfidhHTPredictor = new HTFIDHHashTagPredictor();
	}

	public ComboHashTagPredictor(POSHashTagPredictor posHashTagPredictor,
			HTFIDHHashTagPredictor htfidhHashTagPredictor) {
		posHTPredictor = posHashTagPredictor;
		htfidhHTPredictor = htfidhHashTagPredictor;
	}

	@Override
	public boolean trainModel(List<TweetHashTagTuple> trainingList) {
		posHTPredictor.trainModel(trainingList);
		htfidhHTPredictor.trainModel(trainingList);
		return true;
	}

	@Override
	public List<HashTagPrediction> predictTopKHashTagsForTweet(String tweet,
			int k) {
		List<HashTagPrediction> posHTPredictions = posHTPredictor
				.predictTopKHashTagsForTweet(tweet, -1);
		List<HashTagPrediction> htfidhHTPredictions = htfidhHTPredictor
				.predictTopKHashTagsForTweet(tweet, -1);

		return null;
	}
}
