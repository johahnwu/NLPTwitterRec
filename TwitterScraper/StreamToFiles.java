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
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public final class StreamToFiles {
	public static final long NUM_TWEETS = 1000000;
	public static int fileNumber = 1;
	public static String outFile;

	public static void main(String[] args) throws TwitterException,
			FileNotFoundException {
		String oAuthFileName = "mjlauKeys.auth";
		outFile = args[0];

		ConfigurationBuilder cb = Utils.createConfigurationBuilder(new File(
				oAuthFileName));
		if (cb == null) {
			throw new IllegalArgumentException("Configuration File Issues");
		}

		TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
				.getInstance();

		StatusListener listener = new StatusListener() {
			long counter = 0;
			PrintWriter outputWriter = new PrintWriter(new File(outFile));

			@Override
			public void onStatus(Status status) {
				// System.out.println("@" + status.getUser().getScreenName()
				// + " - " + status.getText());
				if (status.getText().contains("#")) {
					Utils.sanitizeAndWriteTweet(status.getText(), outputWriter);
					counter++;
					if (counter % 1000 == 0) {
						System.out.println("done with " + counter);
					}
					if (counter >= NUM_TWEETS) {
						twitterStream.cleanUp();
						twitterStream.shutdown();
						outputWriter.close();
					}

				}
			}

			@Override
			public void onDeletionNotice(
					StatusDeletionNotice statusDeletionNotice) {
				// System.out.println("Got a status deletion notice id:"
				// + statusDeletionNotice.getStatusId());
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				// System.out.println("Got track limitation notice:"
				// + numberOfLimitedStatuses);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				// System.out.println("Got scrub_geo event userId:" + userId
				// + " upToStatusId:" + upToStatusId);
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				// System.out.println("Got stall warning:" + warning);
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};
		twitterStream.addListener(listener);
		twitterStream.sample("en");
	}

	private static PrintWriter getNextPrintWriter()
			throws FileNotFoundException {
		PrintWriter newWriter = new PrintWriter(new File(outFile + fileNumber));
		fileNumber++;
		return newWriter;
	}
}
