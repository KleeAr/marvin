package ar.com.klee.marvin.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import java.security.MessageDigest;
import java.util.Arrays;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import io.fabric.sdk.android.Fabric;

public class PerfilFragment extends Fragment {

    private CallbackManager callbackManager = CallbackManager.Factory.create();
    
    private MainMenuActivity activity;
    private View view;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();

        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_PROFILE,commandHandlerManager.getMainActivity());

        activity = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();

        initializeFacebookSdk();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(activity.TWITTER_KEY, activity.TWITTER_SECRET);
        Fabric.with(activity, new Twitter(authConfig));

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_perfil, container, false);

        initializeTwitterSdk();

        //extraemos el drawable en un bitmap
        Drawable originalDrawable = getResources().getDrawable(R.drawable.icon_user);
        Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();

        if (originalBitmap.getWidth() > originalBitmap.getHeight()){
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        }else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getWidth());

        ImageView imageView = (ImageView) view.findViewById(R.id.im_perfil);
        imageView.setImageDrawable(roundedDrawable);

        ShapeDrawable sd = new ShapeDrawable(new OvalShape());
        sd.setIntrinsicHeight(100);
        sd.setIntrinsicWidth(100);
        sd.getPaint().setColor(Color.parseColor("#ffffff"));

        imageView.setBackground(sd);

        return view;
    }

    private void initializeTwitterSdk() {

        TwitterLoginButton loginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                Toast.makeText(activity.getApplicationContext(), session.getUserName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(activity.getApplicationContext(),exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeFacebookSdk() {
        FacebookSdk.sdkInitialize(activity.getApplicationContext());
        showHashKey(activity.getApplicationContext());
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Facebook login result
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Twitter login result
        TwitterLoginButton loginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    public static void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "ar.com.klee.marvin", PackageManager.GET_SIGNATURES); //Your package name here
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.v("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.v("Exception: ", e.getMessage(), e);
        }
    }


}