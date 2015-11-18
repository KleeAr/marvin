package ar.com.klee.marvin.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.http.HttpStatusCodes;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.client.Marvin;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PasswordRecoveryActivity extends FragmentActivity {

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        token = getIntent().getData().getLastPathSegment();
        setContentView(R.layout.activity_password_recovery);
    }

    public void resetPassword(View view) {
        EditText newPassword = (EditText)findViewById(R.id.new_password);
        EditText confirmNewPassword = (EditText)findViewById(R.id.confirm_new_password);
        if(!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
            newPassword.setText("");
            confirmNewPassword.setText("");
            newPassword.setError(getString(R.string.password_must_match));
        } else {
            Marvin.users().resetPassword(token, newPassword.getText().toString(), new Callback<Void>() {
                @Override
                public void success(Void aVoid, Response response) {
                    Toast.makeText(getApplicationContext(), getString(R.string.pasword_updated), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getResponse() != null && error.getResponse().getStatus() == 409) {
                        Toast.makeText(getApplicationContext(), getString(R.string.token_expired), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.service_unavailable), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
