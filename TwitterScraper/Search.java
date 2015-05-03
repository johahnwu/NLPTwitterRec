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
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Search {

	public static final String SEARCH_ENDPOINT = "/search/tweets";

	private Twitter twitter;

	public Search(ConfigurationBuilder cb) {
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}

	public int SearchAndWriteKResults(String queryText, int k, PrintWriter pw)
			throws TwitterException {
		try {

			Query query = new Query(queryText);
			query.setLang("en");
			QueryResult result;
			int counter = 0;
			do {
				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					Utils.sanitizeAndWriteTweet(tweet.getText(), pw);
					counter++;
					if (counter == k)
						return 0;
				}
				return 0;
			} while ((query = result.nextQuery()) != null);
		} catch (TwitterException te) {
			if (te.exceededRateLimitation()) {
				RateLimitStatus currentStatus = twitter.getRateLimitStatus(
						"search").get(SEARCH_ENDPOINT);
				return currentStatus.getSecondsUntilReset();
			} else {
				te.printStackTrace();
				System.exit(-1);
			}

		}
		return -1;

	}

	public static void main(String[] args) throws InterruptedException,
			TwitterException {
		if (args.length < 1) {
			System.out
					.println("java twitter4j.examples.search.SearchTweets [query]");
			System.exit(-1);
		}

		String oAuthFileName = "mjlauKeys.auth";

		ConfigurationBuilder cb = Utils.createConfigurationBuilder(new File(
				oAuthFileName));
		if (cb == null) {
			throw new IllegalArgumentException("Configuration File Issues");
		}

		String inputFile = "dictionary/chosen_words";
		String outputFile = "tweetSet";
		int numTweetsPerHashTag = 50;
		int index = 0;
		while (index < args.length) {
			if (args[index].equalsIgnoreCase("--inputFile")) {
				inputFile = args[index + 1];
				index += 2;
			} else if (args[index].equalsIgnoreCase("--outputFile")) {
				outputFile = args[index + 1];
				index += 2;
			} else if (args[index].equalsIgnoreCase("--tweets")) {
				numTweetsPerHashTag = Integer.valueOf(args[index + 1]);
				index += 2;
			} else {
				index += 1;
			}
		}

		Search search = new Search(cb);
		try (BufferedReader reader = new BufferedReader(new FileReader(
				inputFile));
				PrintWriter writer = new PrintWriter(new File(outputFile))) {
			String word = "";
			while ((word = reader.readLine()) != null) {
				int timeLeft = search.SearchAndWriteKResults("#" + word+" -RT",
						numTweetsPerHashTag, writer);
				if (timeLeft > 0) {
					System.out.println("sleeping for " + timeLeft + " seconds");
					Thread.sleep(timeLeft * 1000);
				}
				System.out.println("done with #" + word);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
