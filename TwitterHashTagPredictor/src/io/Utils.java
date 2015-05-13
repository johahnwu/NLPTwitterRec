package io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import predictor.HashTagPrediction;

public class Utils {

	public static String HASHTAG_DELIMITER = " ### ";

	public static List<TweetHashTagTuple> loadFilesIntoHashTagTupleList(
			File file) throws IOException {
		InputFileReader reader = new InputFileReader(file);
		List<TweetHashTagTuple> totalList = new ArrayList<TweetHashTagTuple>();
		String tweet;
		while ((tweet = reader.getNextLine()) != null) {
			TweetHashTagTuple tuple = Utils.convertInputToHashTagTuple(tweet);
			if (tuple == null)
				continue;
			totalList.add(tuple);
		}
		return totalList;
	}

	/**
	 * converts a line of text from the format [message text] ### [hashtag]* to
	 * a data structure (TweetHashTagTuple) that holds them
	 * 
	 * @param inputText
	 * @return HashTag
	 */
	public static TweetHashTagTuple convertInputToHashTagTuple(String inputText) {
		TweetHashTagTuple tuple = new TweetHashTagTuple();
		String[] split = inputText.split(HASHTAG_DELIMITER);
		if (split.length != 2)
			return null;
		tuple.text = split[0];
		String[] hashtags = split[1].split(" ");
		List<String> hashtagList = new ArrayList<String>();
		for (String s : hashtags) {
			if (s.length() > 0)
				hashtagList.add(sanitizeWordToHashTag(s));
		}
		tuple.hashTags = hashtagList;
		return tuple;
	}

	public static String sanitizeWordToHashTag(String word) {
		return word.replaceAll("\\W+", "").toLowerCase();
	}

	/**
	 * Does the following to a tweet: All lowercase Remove non-alphanumerical
	 * characters Porter stemming
	 * 
	 * @param sentence
	 *            The tweet
	 * @return Processed tweet split up as an array
	 */
	public static String[] fixSentence(String sentence) {
		sentence = sentence.replaceAll("[^A-Za-z0-9 ]", "").toLowerCase();
		String[] wordsInSentence = sentence.split("\\s+");
		Stemmer s = new Stemmer();
		for (int i = 0; i < wordsInSentence.length; i++) {
			s.add(wordsInSentence[i].toCharArray(), wordsInSentence[i].length());
			s.stem();
			wordsInSentence[i] = s.toString();
		}
		return wordsInSentence;
	}

	/**
	 * Lowercases, removes non-alphanumerical characters and splits a string of
	 * hashtags
	 * 
	 * @param hashtags
	 *            String of hashtags
	 * @return
	 */
	public static String[] fixHashTags(String hashtags) {
		String[] hashtagArray = hashtags.split("\\s+");
		for (int i = 0; i < hashtagArray.length; i++) {
			hashtagArray[i] = Utils.sanitizeWordToHashTag(hashtagArray[i]);
		}
		return hashtagArray;
	}

	public static ArrayList<String> removeEmpty(ArrayList<String> l) {
		l.removeAll(Arrays.asList(null, ""));
		return l;
	}

	public static List<TweetHashTagTuple> chooseAndRemoveTenPercent(
			List<TweetHashTagTuple> list) {
		List<TweetHashTagTuple> clone = new ArrayList<>(list);
		Collections.shuffle(clone);
		int size = list.size() / 10;
		clone = clone.subList(0, size);
		list.removeAll(clone);
		return clone;

	}

	/**
	 * Normalizes the confidence values of a list of HashtagPrediction objects
	 * 
	 * @param l
	 *            The list of hashtagPrediction objects
	 * @return List of hashtagPrediction objects that have their confidence
	 *         values normalized
	 */
	public static List<HashTagPrediction> Normalize(List<HashTagPrediction> l) {
		double sum = 0.0;
		for (HashTagPrediction temp : l) {
			sum += temp.confidence;
		}

		ArrayList<HashTagPrediction> ret = new ArrayList<HashTagPrediction>();
		for (HashTagPrediction temp : l) {
			temp.confidence = temp.confidence / sum;
			ret.add(temp);
		}

		return ret;
	}

	public static List<HashTagPrediction> Weight(List<HashTagPrediction> l,
			double weight) {
		ArrayList<HashTagPrediction> ret = new ArrayList<HashTagPrediction>();
		for (HashTagPrediction temp : l) {
			temp.confidence = temp.confidence * weight;
			ret.add(temp);
		}
		return ret;
	}
}
