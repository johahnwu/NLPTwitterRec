package htfidh;

import io.TweetHashTagTuple;
import io.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import predictor.HashTagPrediction;

/**
 * Our implementation of HFIHU Probably should have been implemented before
 * HTFIDHashTagPredictor Changed so that the data structure calculations reflect
 * how they are described in the paper
 * 
 * @author Johahn
 *
 */
public class HFIHUOtsuka extends HTFIDHHashTagPredictor {

	public HashMap<String, Integer> hfm = new HashMap<String, Integer>();
	public int sizeOfCorpus = 0;

	public HFIHUOtsuka() {
		super();
	}

	@Override
	public ArrayList<String> predictTweet(String tweet, int topK) {
		String[] wordsInSentence = Utils.fixSentence(tweet); // tweet.split("\\s+");
		HashMap<String, Double> recHashTags = new HashMap<String, Double>();
		for (String word : wordsInSentence) {
			// what if word doesn't exist in idf?

			// loop through all hashtags co-occuring with word
			// what if word doesn't exist in thfm?
			Map<String, Integer> hashAssocWithWordAsMap = thfm.get(word);
			if (hashAssocWithWordAsMap == null)
				continue;
			int totalHashTagFreq = 0;
			for (Integer freq : hashAssocWithWordAsMap.values()) {
				totalHashTagFreq = totalHashTagFreq + freq;
			}
			for (Map.Entry<String, Integer> entry : hashAssocWithWordAsMap
					.entrySet()) {
				String hashtag = entry.getKey();
				Integer freq = entry.getValue();
				double hf = freq * 1.0 / totalHashTagFreq;
				double ihu = Math.log(thfm.size() * 1.0 / hfm.get(hashtag));
				double hashTagScore = hf * ihu;
				if (recHashTags.containsKey(hashtag)) {
					recHashTags.put(hashtag, recHashTags.get(hashtag)
							+ hashTagScore);
				} else {
					recHashTags.put(hashtag, hashTagScore);
				}
			}
		}

		return this.findTopK(recHashTags, topK);
	}

	@Override
	public List<HashTagPrediction> predictTopKHashTagsForTweet(String tweet,
			int k) {
		ArrayList<String> result = this.predictTweet(tweet, k);
		ArrayList<HashTagPrediction> ret = new ArrayList<HashTagPrediction>();
		Double confidence = 1.0;
		Double increment = 0.8 / result.size(); // magic numbers, don't worry
												// about it.
		for (String hashtag : result) {
			HashTagPrediction temp = new HashTagPrediction();
			temp.hashtag = hashtag;
			temp.confidence = confidence;
			confidence = confidence - increment;
			ret.add(temp);
		}
		return ret;
	}

	@Override
	public boolean trainModel(List<TweetHashTagTuple> trainingList) {
		hfm.clear();
		thfm.clear();
		idf.clear();

		for (TweetHashTagTuple tup : trainingList) {
			// String[] tweetAndHash = tup.text.split(tweetHashDelim);

			// Create the three datastructures.
			// TODO OOV?
			// String[] wordsInSentence = tweetAndHash[0].split("\\s+");
			String[] wordsInSentence = Utils.fixSentence(tup.text);
			List<String> hashtags = tup.hashTags;
			// add hashtags to some data structure.

			// for thfm
			Set<String> wordsAddedToIdf = new HashSet<String>();
			for (String word : wordsInSentence) {
				// System.out.println(thfm.size());
				Map<String, Integer> wordHashTagFreq;
				if (thfm.containsKey(word)) {
					wordHashTagFreq = thfm.get(word);
				} else {
					wordHashTagFreq = new HashMap<String, Integer>();
				}
				for (String ht : hashtags) {
					if (ht.trim().length() <= 0) {
						continue;
					}
					if (wordHashTagFreq.containsKey(ht)) {
						wordHashTagFreq.put(ht, wordHashTagFreq.get(ht) + 1);
					} else {
						wordHashTagFreq.put(ht, 1);
					}
				}
				thfm.put(word, wordHashTagFreq);
				// for idf
				if (!wordsAddedToIdf.contains(word)) {
					wordsAddedToIdf.add(word);
					if (idf.containsKey(word)) {
						idf.put(word, idf.get(word) + 1);
					} else {
						idf.put(word, 1);
					}
				}
			}

			// for hfm
			for (String ht : hashtags) {
				if (ht.trim().length() <= 0) {
					continue;
				}
				Integer wordsAssociatedWithHash;
				if (hfm.containsKey(ht)) {
					wordsAssociatedWithHash = hfm.get(ht)
							+ wordsInSentence.length;
				} else {
					wordsAssociatedWithHash = wordsInSentence.length;
				}
				sizeOfCorpus = sizeOfCorpus + wordsInSentence.length;
				hfm.put(ht, wordsAssociatedWithHash);
			}

		}
		return true;

	}
}
