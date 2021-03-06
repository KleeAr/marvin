package ar.com.klee.marvin.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.client.Marvin;
import ar.com.klee.marvin.client.model.User;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_first_name) EditText nameText;
    @Bind(R.id.input_last_name) EditText lastNameText;
    @Bind(R.id.input_email) EditText emailText;
    @Bind(R.id.input_password) EditText passwordText;
    @Bind(R.id.repeat_password) EditText repeatPasswordText;
    @Bind(R.id.btn_signup) Button signupbutton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        signupbutton.setEnabled(false);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Wisdom Script AJ.otf");
        Typeface fBariolRegular = Typeface.createFromAsset(getAssets(), "Bariol_Regular.otf");

        CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox);
        TextView tv_terms = (TextView)findViewById(R.id.tv_terms);
        tv_terms.setTypeface(fBariolRegular);


        TextView text_app = (TextView) findViewById(R.id.textView);
        text_app.setTypeface(typeface);

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ConfTermsActivity.class);
                startActivity(intent);
            }
        });

         checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    signupbutton.setEnabled(true);
                }
                else {
                    signupbutton.setEnabled(false);
                }
            }
        });


    }





    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupbutton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creando la cuenta...");
        progressDialog.show();

        String name = nameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        Marvin.users().register(new User(null, name, lastName, email, password), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                onSignupSuccess();
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("SignupActivity", "Error al crear cuenta en servidor", error);
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                onSignupFailed();
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }
        });


    }


    public void onSignupSuccess() {
        signupbutton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Error al crear la cuenta", Toast.LENGTH_LONG).show();

        signupbutton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String repeatPassword = repeatPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("Debe tener al menos 3 caracteres");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Ingresa un email valido");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("Debe tener entre 4 y 8 caracteres alfanumericos");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (repeatPassword.isEmpty() || !password.equals(repeatPassword)) {
            repeatPasswordText.setError("Contraseña incorrecta");
            valid = false;
        } else {
            repeatPasswordText.setError(null);
        }

        return valid;
    }
}