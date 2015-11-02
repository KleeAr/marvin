package ar.com.klee.marvin.social;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

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
                        Toast.makeText(context, "Texto publicado en Facebook", Toast.LENGTH_SHORT).show();
                        if (graphResponse.getError() != null) {
                            Log.d("FacebookService", "Error publishing in facebook. " + graphResponse.getError().getErrorMessage() + ". Error code: " + graphResponse.getError().getErrorCode());
                        }
                    }
                }).executeAsync();
    }

    public void postImage(Bitmap image, String textToPublish) {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image).setCaption(textToPublish)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(context, "Imagen publicada en Facebook", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Log.e("Facebook Service", "Error while sharing your photo", e);
                Toast.makeText(context, "Debés vincular tu cuenta en el menú perfil.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
