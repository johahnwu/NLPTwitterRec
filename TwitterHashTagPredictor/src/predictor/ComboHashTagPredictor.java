package predictor;

import htfidh.HTFIDHHashTagPredictor;
import io.TweetHashTagTuple;
import io.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		double posWeight = 0.5;
		double htfWeight = 0.5;
		// System.out.println(tweet);
		List<HashTagPrediction> posHTPredictions = posHTPredictor
				.predictTopKHashTagsForTweet(tweet, -1);
		List<HashTagPrediction> htfidhHTPredictions = htfidhHTPredictor
				.predictTopKHashTagsForTweet(tweet, -1);
		// Utils.printList("pos", posHTPredictions);
		// Utils.printList("htfidh", htfidhHTPredictions);

		// add them up
		List<HashTagPrediction> htfWeightedList = Utils.Weight(
				htfidhHTPredictions, htfWeight);
		List<HashTagPrediction> posWeightedList = Utils.Weight(
				posHTPredictions, posWeight);

		Map<String, HashTagPrediction> setOfPredictions = new HashMap<>();
		PredictorUtils.maxConfidenceForEachHTP(setOfPredictions,
				htfWeightedList);
		PredictorUtils.maxConfidenceForEachHTP(setOfPredictions,
				posWeightedList);

		List<HashTagPrediction> finalPredictions = new ArrayList<>(
				setOfPredictions.values());
		PredictorUtils.normalize(finalPredictions);

		// sort by highest confidence first
		Collections.sort(finalPredictions, Collections.reverseOrder());
		// get the top k results
		int maxIndex = Math.min(finalPredictions.size(), DEFAULT_K);
		if (k >= 0) {
			maxIndex = Math.min(finalPredictions.size(), k);
		}

		return finalPredictions.subList(0, maxIndex);
	}

}
