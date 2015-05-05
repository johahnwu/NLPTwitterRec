package io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
	public static String HASHTAG_DELIMITER = " ### ";

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

}
