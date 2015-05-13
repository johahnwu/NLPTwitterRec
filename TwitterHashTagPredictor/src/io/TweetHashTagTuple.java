package io;

import java.util.List;

public class TweetHashTagTuple {
	public static String HASHTAG_DELIMITER = " ### ";

	public String text;
	public List<String> hashTags;

	public String toString() {
		String ret = text;
		ret += HASHTAG_DELIMITER;
		for (String ht : hashTags) {
			ret += ht + " ";
		}
		return ret;
	}
}
