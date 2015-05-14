package predictor;

import io.TweetHashTagTuple;
import io.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pos.predictor.POSPredictionModel;
import pos.predictor.POSPredictionTrainer;
import pos.tagger.TaggedWord;
import pos.tagger.TwitterPOSTagger;

public class POSHashTagPredictor implements HashTagPredictor {
	public static final double UNIGRAM_INT = 0.3;
	public static final double BIGRAM_INT = 0.7;

	private TwitterPOSTagger posTagger;
	private POSPredictionModel predictionModel;
	private POSPredictionTrainer modelTrainer;

	public POSHashTagPredictor() throws IOException {
		this(TwitterPOSTagger.PENN_MODEL, null);
	}

	public POSHashTagPredictor(String modelPOSFile,
			String predictionPOSModelFile) throws IOException {
		if (modelPOSFile == null)
			posTagger = new TwitterPOSTagger();
		else
			posTagger = new TwitterPOSTagger(modelPOSFile);
		if (predictionPOSModelFile == null)
			predictionModel = null;
		else
			predictionModel = new POSPredictionModel(predictionPOSModelFile);
		modelTrainer = new POSPredictionTrainer(posTagger);
	}

	public POSHashTagPredictor(TwitterPOSTagger tagger, POSPredictionModel model) {
		posTagger = tagger;
		predictionModel = model;
		modelTrainer = new POSPredictionTrainer(tagger);
	}

	@Override
	public boolean trainModel(List<TweetHashTagTuple> trainingList) {
		predictionModel = modelTrainer.trainModel(trainingList);
		// for (String s : predictionModel.getModel().keySet()) {
		// System.out.println(s + ":" + predictionModel.getModel().get(s));
		// }
		return true;
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
	@Override
	public List<HashTagPrediction> predictTopKHashTagsForTweet(String tweet,
			int k) {
		List<HashTagPrediction> hashTagPredictions = new ArrayList<HashTagPrediction>();
		Map<String, Double> posProbabilities = predictionModel.getModel();

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
			previousPOS = unigramPOS;
			double interpolatedProbs = unigramProb * UNIGRAM_INT + bigramProb
					* BIGRAM_INT;
			currentPrediction.confidence = interpolatedProbs;
			hashTagPredictions.add(currentPrediction);
		}

		// account for duplicate hash tags
		Map<String, Integer> seenHashTags = new HashMap<String, Integer>();
		Map<String, Integer> counts = new HashMap<String, Integer>();
		List<HashTagPrediction> finalPredictions = new ArrayList<HashTagPrediction>();
		PredictorUtils.addInConfidences(finalPredictions, hashTagPredictions,
				seenHashTags, counts);
		PredictorUtils.takeCounthRoots(finalPredictions, counts);
		PredictorUtils.normalize(finalPredictions);

		// sort from highest prob to lowest prob
		Collections.sort(finalPredictions, Collections.reverseOrder());
		// truncate the list to the max size
		int outputSize = Math.min(k, finalPredictions.size());
		if (k < 0)
			outputSize = finalPredictions.size();

		return finalPredictions.subList(0, outputSize);
	}
}
