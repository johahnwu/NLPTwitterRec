package predictor;

import io.TweetHashTagTuple;

import java.util.List;

public interface HashTagPredictor {
	/**
	 * Get the top k predicted hash tags for the given tweet
	 * 
	 * @param tweet
	 *            Tweet to predict for
	 * @param k
	 *            Number of predictions
	 * @return Top K predicted hash tags
	 */
	List<HashTagPrediction> predictTopKHashTagsForTweet(String tweet, int k);

	/**
	 * Train the Model, so that predictTopKHashTags can be called correctly
	 * 
	 * @param trainingList
	 * @return
	 */
	boolean trainModel(List<TweetHashTagTuple> trainingList);
}
