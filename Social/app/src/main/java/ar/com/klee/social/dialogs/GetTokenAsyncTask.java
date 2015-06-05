package ar.com.klee.social.dialogs;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.services.gmail.GmailScopes;

import java.io.IOException;

/**
 * @author msalerno
 */
public class GetTokenAsyncTask extends AsyncTask<Object, Void, Void> {

    private Context context;

    public GetTokenAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Object... params) {
        try {
            GoogleAuthUtil.getToken(context, "matias.d.salerno@gmail.com", GmailScopes.GMAIL_COMPOSE);
        } catch (IOException | GoogleAuthException e) {
            Toast.makeText(context, "Login failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.v("ERROR", "Google login failed: " + e.getMessage(), e);
        }
        return null;
    }
}
