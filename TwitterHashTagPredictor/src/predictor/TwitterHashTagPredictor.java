package predictor;

import io.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import pos.predictor.POSPredictionModel;
import pos.tagger.TaggedWord;
import pos.tagger.TwitterPOSTagger;

public class TwitterHashTagPredictor {
	public static final double UNIGRAM_INT = 0.5;
	public static final double BIGRAM_INT = 0.5;

	private TwitterPOSTagger posTagger;
	private POSPredictionModel predictionModel;
	private Map<String, Double> posProbabilities;

	public TwitterHashTagPredictor() throws IOException {
		this(TwitterPOSTagger.PENN_MODEL, null);
	}

	public TwitterHashTagPredictor(String modelPOSFile,
			String predictionPOSModelFile) throws IOException {
		if (modelPOSFile == null)
			posTagger = new TwitterPOSTagger();
		else
			posTagger = new TwitterPOSTagger(modelPOSFile);
		if (predictionPOSModelFile == null)
			predictionModel = new POSPredictionModel();
		else
			predictionModel = new POSPredictionModel(predictionPOSModelFile);
		posProbabilities = predictionModel.getModel();
	}

	public TwitterHashTagPredictor(TwitterPOSTagger tagger,
			POSPredictionModel model) {
		posTagger = tagger;
		predictionModel = model;
		posProbabilities = predictionModel.getModel();
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
		String previousPOS = POSPredictionModel.START_TAG;
		for (TaggedWord tw : taggedWords) {
			HashTagPrediction currentPrediction = new HashTagPrediction();
			currentPrediction.hashtag = Utils.sanitizeWordToHashTag(tw.word);
			String unigramPOS = tw.pos;
			String bigramPOS = POSPredictionModel.concatWordsWithDelimiter(
					previousPOS, unigramPOS);
			double unigramProb = 0.0;
			if (posProbabilities.containsKey(unigramPOS)) {
				unigramProb = posProbabilities.get(unigramPOS);
			}
			double bigramProb = 0.0;
			if (posProbabilities.containsKey(bigramPOS)) {
				bigramProb = posProbabilities.get(bigramPOS);
			}
			double interpolatedProbs = unigramProb * UNIGRAM_INT + bigramProb
					* BIGRAM_INT;
			currentPrediction.confidence = interpolatedProbs;
			hashTagPredictions.add(currentPrediction);
		}

		// sort from highest prob to lowest prob
		Collections.sort(hashTagPredictions, Collections.reverseOrder());
		// truncate the list to the max size
		int outputSize = Math.min(k, hashTagPredictions.size());
		if (k < 0)
			outputSize = hashTagPredictions.size();

		return hashTagPredictions.subList(0, outputSize);
	}
}
