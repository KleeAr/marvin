package ar.com.klee.marvin.social;

import android.content.Context;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Service class to interact with Facebook
 *
 * @author msalerno
 */
public class FacebookService {

    private Context context;

    public FacebookService(Context context) {
        this.context = context;
    }

    public void publishText(String textToPublish) {
        JSONObject params = new JSONObject();
        try {
            params.put("message", textToPublish);
        } catch (JSONException e) {
            throw new FacebookException("Error publishing", e);
        }
        GraphRequest.newPostRequest(AccessToken.getCurrentAccessToken(),
                "me/feed", params, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Toast.makeText(context, "Tu texto se publicó en facebook", Toast.LENGTH_SHORT).show();
                    }
                }).executeAsync();
    }
}
