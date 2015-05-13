package predictor;

import htfidh.HTFIDHHashTagPredictor;
import io.TweetHashTagTuple;
import io.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ComboHashTagPredictor implements HashTagPredictor {

	private static int DEFAULT_K = 30;

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
		double posWeight = 0.4;
		double htfWeight = 0.6;
		List<HashTagPrediction> posHTPredictions = posHTPredictor
				.predictTopKHashTagsForTweet(tweet, -1);
		List<HashTagPrediction> htfidhHTPredictions = htfidhHTPredictor
				.predictTopKHashTagsForTweet(tweet, -1);

		posHTPredictions = Utils.Normalize(posHTPredictions);
		// htf is already normalized

		// add them up
		List<HashTagPrediction> htfWeightedList = Utils.Weight(
				htfidhHTPredictions, htfWeight);
		for (HashTagPrediction posObject : posHTPredictions) {
			int indx = htfWeightedList.indexOf(posObject.hashtag);
			if (indx >= 0) {
				HashTagPrediction htfObject = htfWeightedList.get(indx);
				htfObject.confidence = htfObject.confidence
						+ posObject.confidence * posWeight;
			} else {
				posObject.confidence = posObject.confidence * posWeight;
				htfWeightedList.add(posObject);
			}
		}

		// sort by highest confidence first
		Collections.sort(htfWeightedList, Collections.reverseOrder());
		// get the top k results
		int maxIndex = Math.min(htfWeightedList.size(), DEFAULT_K);
		if (k >= 0) {
			maxIndex = Math.min(htfWeightedList.size(), k);
		}

		return htfWeightedList.subList(0, maxIndex);
	}
}
