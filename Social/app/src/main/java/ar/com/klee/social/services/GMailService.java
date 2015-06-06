package ar.com.klee.social.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author msalerno
 */
public class GMailService {


    private final Gmail gmailClient;
    private final Activity activity;

    public GMailService(Activity activity) {
        final HttpTransport transport = AndroidHttp.newCompatibleTransport();
        final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        // Initialize credentials and service object.
        SharedPreferences settings = activity.getPreferences(Context.MODE_PRIVATE);
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                activity, GmailScopes.all())
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString("accountName", null));
        gmailClient = new Gmail.Builder(transport, jsonFactory, credential)
                .setApplicationName("MARVIN Car assistant")
                .build();
        this.activity = activity;
    }

    public void sendEmail(String textToPublish) {
        new SendEmailTask().execute(textToPublish);
    }


    private class SendEmailTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {
            try {
                gmailClient.users().messages().send("me", createMessageWithEmail(createEmail("matias.d.salerno@gmail.com", "matias.d.salerno@gmail.com", "Test", params[0])));
            } catch (IOException | MessagingException e) {
                Toast.makeText(activity, "the email failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            return null;
        }
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to Email address of the receiver.
     * @param from Email address of the sender, the mailbox account.
     * @param subject Subject of the email.
     * @param bodyText Body text of the email.
     * @return MimeMessage to be used to send email.
     * @throws javax.mail.MessagingException
     */
    private static MimeMessage createEmail(String to, String from, String subject,
                                          String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    /**
     * Create a Message from an email
     *
     * @param email Email to be set to raw of message
     * @return Message containing base64url encoded email.
     * @throws java.io.IOException
     * @throws MessagingException
     */
    private static Message createMessageWithEmail(MimeMessage email)
            throws MessagingException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        email.writeTo(bytes);
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}
