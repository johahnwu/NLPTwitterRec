package htfidh;

import io.Utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HTFIDHHashTagPredictorMain {

	private String trainingTweetPath = "poemTags.txt";
	private String testingTweetPath = "poemTest.txt";
	private String tweetHashDelim = "###";
	private int defaultTopK = 10;
	private boolean useDefaultTopK = false;
	private int numTweets;

	public HashMap<String, HashSet<String>> hfm = new HashMap<String, HashSet<String>>();
	public HashMap<String, HashMap<String, Integer>> thfm = new HashMap<String, HashMap<String, Integer>>();
	public HashMap<String, Integer> idf = new HashMap<String, Integer>();
	public List<String> trainingTweets;
	public List<String> testingTweets;

	public HTFIDHHashTagPredictorMain() {
		trainingTweets = null;
		testingTweets = null;
		numTweets = 0;
	}

	public void setTrainingPath(String path) {
		trainingTweetPath = path;
	}

	public void setTestingPath(String path) {
		testingTweetPath = path;
	}

	public void setDeliminator(String delim) {
		tweetHashDelim = delim;
	}

	public void train() {
		try {
			trainingTweets = Files.readAllLines(Paths.get(trainingTweetPath),
					Charset.defaultCharset());
		} catch (IOException e) {
			System.out.println(e);
		}
		numTweets = trainingTweets.size();
		for (String line : trainingTweets) {
			String[] tweetAndHash = line.split(tweetHashDelim);
			if (tweetAndHash.length == 2) {
				// Create the three datastructures.

				// String[] wordsInSentence = tweetAndHash[0].split("\\s+");
				String[] wordsInSentence = Utils.fixSentence(tweetAndHash[0]);
				String[] hashtags = Utils.fixHashTags(tweetAndHash[1]);
				// add hashtags to some data structure.

				// for thfm
				HashSet<String> wordsAddedToIdf = new HashSet<String>();
				for (String word : wordsInSentence) {
					// System.out.println(thfm.size());
					HashMap<String, Integer> wordHashTagFreq;
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
							wordHashTagFreq
									.put(ht, wordHashTagFreq.get(ht) + 1);
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
					HashSet<String> wordsAssociatedWithHash;
					if (hfm.containsKey(ht)) {
						wordsAssociatedWithHash = hfm.get(ht);
					} else {
						wordsAssociatedWithHash = new HashSet<String>();
					}
					for (String word : wordsInSentence) {
						wordsAssociatedWithHash.add(word);
					}
					hfm.put(ht, wordsAssociatedWithHash);
				}
			}
		}
	}

	public void test(int topK) {
		// Predicting
		try {
			testingTweets = Files.readAllLines(Paths.get(testingTweetPath),
					Charset.defaultCharset());

		} catch (IOException e) {
			System.out.println(e);
		}
		int correct = 0;
		int incorrect = 0;
		for (String line : testingTweets) {
			String[] tweetAndHash = line.split(tweetHashDelim);
			if (tweetAndHash.length == 2) {
				String tweet = tweetAndHash[0];
				ArrayList<String> hashtags = new ArrayList<String>(
						Arrays.asList(Utils.fixHashTags(tweetAndHash[1])));
				ArrayList<String> predictedHashTags = predictTweet(tweet, topK);
				hashtags = Utils.removeEmpty(hashtags);
				if (EvaluateHelp.getSinglePrecisionApproach2(predictedHashTags,
						hashtags)) {
					correct++;
				} else {
					incorrect++;
				}

			}
		}
		System.out.println(EvaluateHelp.precision(correct, incorrect));

	}

	public ArrayList<String> predictTweet(String tweet, int topK) {
		String[] wordsInSentence = Utils.fixSentence(tweet); // tweet.split("\\s+");
		HashMap<String, Double> recHashTags = new HashMap<String, Double>();
		for (String word : wordsInSentence) {
			// what if word doesn't exist in idf?
			double idfVal = Math.log(numTweets * 1.0 / idf.get(word));
			// loop through all hashtags co-occuring with word
			// what if word doesn't exist in thfm?
			HashMap<String, Integer> hashAssocWithWordAsMap = thfm.get(word);
			int totalHashTagFreq = 0;
			for (Integer freq : hashAssocWithWordAsMap.values()) {
				totalHashTagFreq = totalHashTagFreq + freq;
			}
			for (Map.Entry<String, Integer> entry : hashAssocWithWordAsMap
					.entrySet()) {
				String hashtag = entry.getKey();
				Integer freq = entry.getValue();
				double hf = freq * 1.0 / totalHashTagFreq;
				double ihu = Math.log(thfm.size() * 1.0
						/ hfm.get(hashtag).size());
				double hashTagScore = idfVal * hf * ihu;
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

	public ArrayList<String> findTopK(HashMap<String, Double> result, int topK) {
		ValueComparator comp = new ValueComparator(result);
		TreeMap<String, Double> resultTreeMap = new TreeMap<String, Double>(
				comp);
		resultTreeMap.putAll(result);
		ArrayList<String> returnTopK = new ArrayList<String>();
		int useThisTopK = useDefaultTopK ? defaultTopK : topK;
		int count = 0;
		for (Map.Entry<String, Double> entry : resultTreeMap.entrySet()) {

			returnTopK.add(entry.getKey());
			count++;
			if (count >= useThisTopK) {
				break;
			}
		}
		return returnTopK;
	}

	class ValueComparator implements Comparator<String> {

		Map<String, Double> base;

		public ValueComparator(Map<String, Double> base) {
			this.base = base;
		}

		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			}
		}
	}

}
