package ar.com.klee.marvinSimulator.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.CallbackManager;

import ar.com.klee.marvin.client.Marvin;
import ar.com.klee.marvin.client.MarvinLoginCallback;
import ar.com.klee.marvin.client.model.LoginResponse;
import ar.com.klee.marvinSimulator.configuration.UserConfig;
import butterknife.ButterKnife;
import butterknife.Bind;

import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    // public static final String TWITTER_KEY = "IsfPZw7I4i4NCZaFxM9BZX4Qi";
    //public static final String TWITTER_SECRET = "aPnfZPsetWBwJ7E42RF0MMwsVL361hBu92ey1JwzkMcrNGedWE";

    private CallbackManager callbackManager = CallbackManager.Factory.create();

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @Bind(ar.com.klee.marvinSimulator.R.id.input_email) EditText emailText;
    @Bind(ar.com.klee.marvinSimulator.R.id.input_password) EditText passwordText;
    @Bind(ar.com.klee.marvinSimulator.R.id.btn_login) Button loginButton;
    @Bind(ar.com.klee.marvinSimulator.R.id.link_signup) TextView signupLink;
    @Bind(ar.com.klee.marvinSimulator.R.id.link_rememberPassword) TextView rememberPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initializeFacebookSdk();
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        //Fabric.with(this, new Twitter(authConfig));
        setContentView(ar.com.klee.marvinSimulator.R.layout.activity_login);
        //initializeTwitterSdk();
        ButterKnife.bind(this);
        Marvin.setMarvinHost("186.23.170.58");
        Marvin.setMarvinPort(":80");

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Wisdom Script AJ.otf");

        TextView text_app = (TextView) findViewById(ar.com.klee.marvinSimulator.R.id.textView);
        text_app.setTypeface(typeface);

        rememberPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rememberPassword();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "El GPS está desactivado. Activalo para usar las funciones de localización.", Toast.LENGTH_LONG).show();
        }

    }


    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                ar.com.klee.marvinSimulator.R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticando...");
        progressDialog.show();

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        Marvin.users().authenticate(email, password, new MarvinLoginCallback() {
            @Override
            public void onSuccess(final LoginResponse loginResponse, Response response) {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class );
                                UserConfig.setSettings(loginResponse.getSettings());
                                startActivity(intent);
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }

            @Override
            public void failure(RetrofitError error) {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                onLoginFailed();
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }
        });


    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }
*/
    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
        finish();
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Usuario y/o Contraseña fallida", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Ingresa un email valido");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            passwordText.setError("Debe tener entre 4 y 8 caracteres alfanumericos");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
    //////////////////////////////////////


    /*
    private void initializeTwitterSdk() {

        TwitterLoginButton loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                Toast.makeText(getApplicationContext(), session.getUserName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(),exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    */

    /*
    private void initializeFacebookSdk() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        showHashKey(getApplicationContext());
        LoginManager.getSettings().registerCallback(callbackManager,
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
        LoginManager.getSettings().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Facebook login result
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Twitter login result
        TwitterLoginButton loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }



    public static void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "ar.com.klee.marvin", PackageManager.GET_SIGNATURES); //Your package name here
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getSettings("SHA");
                md.update(signature.toByteArray());
                Log.v("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.v("Exception: ", e.getMessage(), e);
        }
    }

    */

    public void logIn(View v){

        Intent intent = new Intent(this, MainMenuActivity.class );
        startActivity(intent);

    }

    public void rememberPassword(){

        AlertDialog.Builder builder =new AlertDialog.Builder(this, ar.com.klee.marvinSimulator.R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(true);
        builder.setTitle("Recuperar contraseña");
        builder.setIcon(ar.com.klee.marvinSimulator.R.drawable.marvin);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText siteAddress = new EditText(this);
        siteAddress.setHint("Email");
        layout.addView(siteAddress);

        builder.setView(layout);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Marvin.users().recoverPasswordEmail(siteAddress.getText().toString(), new retrofit.Callback<Void>() {
                    @Override
                    public void success(Void aVoid, Response response) {
                        Toast.makeText(getApplicationContext(),getString(ar.com.klee.marvinSimulator.R.string.recover_pass_email_sent), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getApplicationContext(),getString(ar.com.klee.marvinSimulator.R.string.service_unavailable), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error from server: " +  error.getMessage(), error);
                    }
                });
                Toast.makeText(getApplicationContext(), "Enviando...", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                dialog.dismiss();

            }
        });


        builder.show();



    }


}