package ar.com.klee.social;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ar.com.klee.social.dialogs.DateTimeDialog;
import ar.com.klee.social.services.FacebookService;
import ar.com.klee.social.services.TwitterService;
import ar.com.klee.social.services.WhatsAppService;
import ar.com.klee.social.services.exceptions.WhatsAppException;


public class PublishActivity extends FragmentActivity{

    private WhatsAppService whatsAppService;
    private FacebookService facebookService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_layout);
        whatsAppService =  new WhatsAppService(this);
        facebookService = new FacebookService(this);

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

}
