package io;

import java.util.ArrayList;
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
}
