package ar.com.klee.marvin.social;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

/**
 *
 * Service class used to interact with Twitter
 *
 * @author msalerno
 */
public class TwitterService {


    private static TwitterService instance;

    private TwitterService() {

    }

    public static TwitterService getInstance() {
        if (instance == null) {
            instance = new TwitterService();
        }
        return instance;
    }

    public void postTweet(String textToTweet) {
        Twitter.getApiClient().getStatusesService().update(textToTweet, null, null, null, null, null, null, null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> tweetResult) {
                tweetResult.data.getId();
            }

            @Override
            public void failure(TwitterException e) {
                throw new RuntimeException("Tu tweet no pudo ser enviado", e);
            }
        });
    }
}
