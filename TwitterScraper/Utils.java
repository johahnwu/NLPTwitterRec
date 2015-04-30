import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import twitter4j.conf.ConfigurationBuilder;

public class Utils {
	public static ConfigurationBuilder createConfigurationBuilder(File oAuthFile) {
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

	public static final String HASHTAG_DELIMITER = "###";

	public static void sanitizeAndWriteTweet(String tweet, PrintWriter pw) {
		List<String> hashtags = new ArrayList<String>();
		String[] splitWords = tweet.split("\\s+");
		for (String word : splitWords) {
			word = word.toLowerCase();
			if (word.contains("#")) {
				String[] words = word.split("#");
				if (words[0].length() > 0)
					pw.print(words[0] + " ");
				for (int i = 1; i < words.length; i++) {
					word = words[i];
					pw.print(word + " ");
					hashtags.add(word);
				}
			} else {
				pw.print(word + " ");
			}
		}
		pw.write(HASHTAG_DELIMITER + " ");
		for (String ht : hashtags) {
			pw.print(ht + " ");
		}
		pw.println();
	}
}
