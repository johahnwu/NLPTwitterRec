import java.io.File;
import java.util.Map;

import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class RateLimit {
	public static void main(String[] args) {
		try {
			String oAuthFileName = "mjlauKeys.auth";
			ConfigurationBuilder cb = Utils
					.createConfigurationBuilder(new File(oAuthFileName));
			Twitter twitter = new TwitterFactory(cb.build()).getInstance();
			Map<String, RateLimitStatus> rateLimitStatus = twitter
					.getRateLimitStatus();
			for (String endpoint : rateLimitStatus.keySet()) {
				RateLimitStatus status = rateLimitStatus.get(endpoint);
				System.out.println("Endpoint: " + endpoint);
				System.out.println(" Limit: " + status.getLimit());
				System.out.println(" Remaining: " + status.getRemaining());
				System.out.println(" ResetTimeInSeconds: "
						+ status.getResetTimeInSeconds());
				System.out.println(" SecondsUntilReset: "
						+ status.getSecondsUntilReset());
			}
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get rate limit status: "
					+ te.getMessage());
			System.exit(-1);
		}
	}
}
