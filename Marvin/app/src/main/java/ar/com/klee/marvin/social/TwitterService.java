package ar.com.klee.marvin.social;

import android.os.AsyncTask;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.io.File;

import ar.com.klee.marvin.activities.LoginActivity;
import twitter4j.StatusUpdate;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 *
 * Service class used to interact with Twitter
 *
 * @author msalerno
 */
public class TwitterService {

    private static TwitterService instance;
    private twitter4j.Twitter twitter;

    private String text;
    private File image;

    private TwitterService() {
        twitter = TwitterFactory.getSingleton();
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

    public void postTweet(String textToTweet, File image) {

        text = textToTweet;
        this.image = image;

        twitter.setOAuthConsumer(LoginActivity.TWITTER_KEY, LoginActivity.TWITTER_SECRET);
        TwitterAuthToken authToken = Twitter.getSessionManager().getActiveSession().getAuthToken();
        twitter.setOAuthAccessToken(new AccessToken(authToken.token, authToken.secret));

        new AsyncCaller().execute();
    }

    private class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(Void... params) {

            try {
                StatusUpdate update = new StatusUpdate(text);
                update.setMedia(image);
                twitter.updateStatus(update);
            } catch (twitter4j.TwitterException e) {
                throw new RuntimeException("Tu tweet no pudo ser enviado", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

    }
}
