package ar.com.klee.social;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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

import ar.com.klee.social.dialogs.DateTimeDialog;
import ar.com.klee.social.services.FacebookService;
import ar.com.klee.social.services.TwitterService;
import ar.com.klee.social.services.WhatsAppService;
import ar.com.klee.social.services.exceptions.WhatsAppException;


public class PublishActivity extends FragmentActivity {

    private WhatsAppService whatsAppService;
    private FacebookService facebookService;
    private Gmail gmailClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_layout);
        whatsAppService =  new WhatsAppService(this);
        facebookService = new FacebookService(this);
        final HttpTransport transport = AndroidHttp.newCompatibleTransport();
        final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), GmailScopes.all())
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString("accountName", null));
        gmailClient = new Gmail.Builder(transport, jsonFactory, credential)
                .setApplicationName("MARVIN Car assistant")
                .build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.publish_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void publishOnFacebook(View view) {
        final String textToPublish = getTextToPublish();
        if(!textToPublish.isEmpty()) {
            facebookService.publishText(textToPublish);
        }
    }

    public void sendWhatsApp(View view) {
        final String textToPublish = getTextToPublish();
        if(!textToPublish.isEmpty()) {
            try {
                whatsAppService.sendWhatsApp(textToPublish);
            } catch (WhatsAppException e) {
                Toast.makeText(getApplicationContext(),"WhatsApp not sent", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void createGoogleCalendarEvent(View view) {
        Dialog dialog = new DateTimeDialog(this);
        dialog.show();
    }

    public void postTweet(View view) {
        final String textToPublish = getTextToPublish();
        if(!textToPublish.isEmpty()) {
                TwitterService.getInstance().postTweet(textToPublish);
        }
    }

    private String getTextToPublish() {
        TextView publishText = (TextView)findViewById(R.id.publishText);
        return publishText.getText().toString();
    }

    public void sendEmail(View view) {
        final String textToPublish = getTextToPublish();
        if(!textToPublish.isEmpty()) {
            try {
                gmailClient.users().messages().send("me", createMessageWithEmail(createEmail("matias.d.salerno@gmail.com", "matias.d.salerno@gmail.com", "Test", textToPublish)));
            } catch (IOException | MessagingException e) {
                Toast.makeText(this, "the email failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
     * @throws MessagingException
     */
    public static MimeMessage createEmail(String to, String from, String subject,
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
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage email)
            throws MessagingException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        email.writeTo(bytes);
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}
