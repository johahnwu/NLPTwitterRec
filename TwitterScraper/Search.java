/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Modified Matthew Lau 2015
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Search {

	public static final String HASHTAG_DELIMITER = "###";

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out
					.println("java twitter4j.examples.search.SearchTweets [query]");
			System.exit(-1);
		}

		String oAuthFileName = "mjlauKeys.auth";

		ConfigurationBuilder cb = createConfigurationBuilder(new File(
				oAuthFileName));
		if (cb == null) {
			throw new IllegalArgumentException("Configuration File Issues");
		}

		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		try {
			Query query = new Query(args[0]);
			query.setLang("en");
			QueryResult result;
			do {
				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				System.out.println(tweets.size());
				for (Status tweet : tweets) {
					System.out.println("@" + tweet.getUser().getScreenName()
							+ " - " + tweet.getText());
				}
			} while ((query = result.nextQuery()) != null);
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		}
	}

	private static void sanitizeAndWriteTweet(String tweet, PrintWriter pw) {
		List<String> hashtags = new ArrayList<String>();
		String[] splitWords = tweet.split(" ");
		for (String word : splitWords) {
			if (word.startsWith("#")) {
				word = word.substring(1);
				hashtags.add(word);
			}
			pw.print(word + " ");
		}
		pw.write(HASHTAG_DELIMITER);
		for (String ht : hashtags) {
			pw.print(ht + " ");
		}
		pw.println();
	}

	private static ConfigurationBuilder createConfigurationBuilder(
			File oAuthFile) {
		try (BufferedReader fileReader = new BufferedReader(new FileReader(
				oAuthFile))) {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true).setOAuthConsumerKey(fileReader.readLine())
					.setOAuthConsumerSecret(fileReader.readLine())
					.setOAuthAccessToken(fileReader.readLine())
					.setOAuthAccessTokenSecret(fileReader.readLine());
			return cb;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Configuration File not found.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err
					.println("Configuration file formatted badly, should be \n"
							+ "oAuthConsumerKey \noAuthConsumerSecret \noAuthAccessToken \noAuthAccessTokenSecret");
			e.printStackTrace();
		}
		return null;
	}
}
