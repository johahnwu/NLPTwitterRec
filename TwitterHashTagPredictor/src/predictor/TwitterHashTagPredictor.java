package predictor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pos.predictor.POSPredictionModel;
import pos.tagger.TaggedWord;
import pos.tagger.TwitterPOSTagger;

public class TwitterHashTagPredictor {
	private TwitterPOSTagger posTagger;
	private POSPredictionModel predictionModel;

	public TwitterHashTagPredictor() throws IOException {
		this(TwitterPOSTagger.PENN_MODEL);
	}

	public TwitterHashTagPredictor(String modelPOSFile) throws IOException {
		posTagger = new TwitterPOSTagger(modelPOSFile);
		predictionModel = new POSPredictionModel();
	}

	/**
	 * 
	 * @param tweet
	 *            - the Tweet to summarize
	 * @param k
	 *            - the number of HashTaags to recommend, up to k. -1 means get
	 *            all
	 * @return the k best predicted HashTags
	 */
	public List<HashTagPrediction> predictTopKHashTagsForTweet(String tweet,
			int k) {
		List<HashTagPrediction> hashTagPredictions = new ArrayList<HashTagPrediction>();

		List<TaggedWord> taggedWords = posTagger.tagSentence(tweet);
		for (TaggedWord tw : taggedWords) {
			HashTagPrediction currentPrediction = new HashTagPrediction();
			currentPrediction.hashtag = tw.word;
			currentPrediction.confidence = predictionModel
					.calculateConfidence(tw);
		}

		Collections.sort(hashTagPredictions, Collections.reverseOrder());
		// truncate the list to the max size
		int outputSize = Math.min(k, hashTagPredictions.size());
		if (k >= 0)
			outputSize = hashTagPredictions.size();

		return hashTagPredictions.subList(0, outputSize);
	}
}
