package ar.com.klee.social;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import ar.com.klee.social.dialogs.DateTimeDialog;
import ar.com.klee.social.services.TwitterService;
import ar.com.klee.social.services.WhatsAppService;
import ar.com.klee.social.services.exceptions.CalendarService;
import ar.com.klee.social.services.exceptions.WhatsAppException;


public class PublishActivity extends FragmentActivity {

    private WhatsAppService whatsAppService;
    private CalendarService calendarService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_layout);
        whatsAppService =  new WhatsAppService(this);
        calendarService = new CalendarService(this);
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
            JSONObject params = new JSONObject();
            try {
                params.put("message", textToPublish);
            } catch (JSONException e) {
                throw new RuntimeException("Error publishing", e);
            }
            GraphRequest.newPostRequest(AccessToken.getCurrentAccessToken(),
                    "me/feed", params, new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse graphResponse) {
                            Toast.makeText(getApplicationContext(), "Tu texto se publicó en facebook", Toast.LENGTH_SHORT).show();
                        }
                    }).executeAsync();
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

}
