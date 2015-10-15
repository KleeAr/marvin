package ar.com.klee.marvinSimulator.activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ar.com.klee.marvin.client.Marvin;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PasswordRecoveryActivity extends FragmentActivity {

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = getIntent().getData().getLastPathSegment();
        setContentView(ar.com.klee.marvinSimulator.R.layout.activity_password_recovery);
    }

    public void resetPassword(View view) {
        EditText newPassword = (EditText)findViewById(ar.com.klee.marvinSimulator.R.id.new_password);
        EditText confirmNewPassword = (EditText)findViewById(ar.com.klee.marvinSimulator.R.id.confirm_new_password);
        if(!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
            newPassword.setText("");
            confirmNewPassword.setText("");
            newPassword.setError(getString(ar.com.klee.marvinSimulator.R.string.password_must_match));
        } else {
            Marvin.users().resetPassword(token, newPassword.getText().toString(), new Callback<Void>() {
                @Override
                public void success(Void aVoid, Response response) {
                    Toast.makeText(getApplicationContext(), getString(ar.com.klee.marvinSimulator.R.string.pasword_updated), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getResponse().getStatus() == 409) {
                        Toast.makeText(getApplicationContext(), getString(ar.com.klee.marvinSimulator.R.string.token_expired), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(ar.com.klee.marvinSimulator.R.string.service_unavailable), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
