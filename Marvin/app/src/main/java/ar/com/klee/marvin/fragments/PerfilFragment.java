package ar.com.klee.marvin.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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
import android.widget.Button;
import android.widget.EditText;
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
import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class PerfilFragment extends Fragment {
    
    private MainMenuActivity activity;
    private View view;

    //variables para el manejo de contraseñas
    @Bind(R.id.password_current)  EditText passwordCurrentText;
    @Bind(R.id.password_new)  EditText passwordNewText;
    @Bind(R.id.repeat_password) EditText repeatPasswordText;
    @Bind(R.id.btn_change) Button changeButton;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();

        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_PROFILE,commandHandlerManager.getMainActivity());

        activity = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_perfil, container, false);
        ButterKnife.bind(this,view);

        changeButton.setEnabled(true);

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

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

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Twitter login result
        TwitterLoginButton loginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


    public void changePassword() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        changeButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Cambiando contraseña...");
        progressDialog.show();

        String passwordCurrent = passwordCurrentText.getText().toString();
        String passwordNew = passwordNewText.getText().toString();
        String passwordRepeat = repeatPasswordText.getText().toString();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }
    public void onSignupSuccess() {
        changeButton.setEnabled(true);
    }

    public void onSignupFailed() {
        Toast.makeText(getActivity(), "Ingreso fallido", Toast.LENGTH_LONG).show();
        changeButton.setEnabled(true);
    }
    public boolean validate() {
        boolean valid = true;

        String passwordCurrent = passwordCurrentText.getText().toString();
        String passwordNew = passwordNewText.getText().toString();
        String passwordRepeat = repeatPasswordText.getText().toString();


        if (passwordCurrent.isEmpty() || passwordCurrent.length() < 4 || passwordCurrent.length() > 10) {
            passwordCurrentText.setError("Debe tener entre 4 y 8 caracteres alfanumericos");
            valid = false;
        } else {
            passwordCurrentText.setError(null);
        }

        if (passwordNew.isEmpty() || passwordNew.length() < 4 || passwordNew.length() > 10) {
            passwordNewText.setError("Debe tener entre 4 y 8 caracteres alfanumericos");
            valid = false;
        } else {
            passwordNewText.setError(null);
        }


        if (passwordRepeat.isEmpty() || passwordRepeat.equals(passwordNew)) {
            repeatPasswordText.setError("Contraseña incorrecta");
            valid = false;
        } else {
            repeatPasswordText.setError(null);
        }

        return valid;
    }

}