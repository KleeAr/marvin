package ar.com.klee.marvin.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.configuration.UserConfig;
import ar.com.klee.marvin.fragments.MisViajesFragment;
import ar.com.klee.marvin.gps.Trip;
import ar.com.klee.marvin.gps.TripMap;
import ar.com.klee.marvin.social.FacebookService;
import ar.com.klee.marvin.social.InstagramService;
import ar.com.klee.marvin.social.TwitterService;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;

public class TripActivity extends ActionBarActivity {

    private TripMap fragment;
    private Trip trip;

    private CommandHandlerManager commandHandlerManager;
    private String mapPath = "/sdcard/MARVIN/trip.png";
    private Bitmap mapBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(UserConfig.getInstance().getOrientation() == UserConfig.ORIENTATION_PORTRAIT)
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        setContentView(R.layout.activity_trip);

        addMap();

        trip = MisViajesFragment.getInstance().getChosenTrip();

        commandHandlerManager = CommandHandlerManager.getInstance();

        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_TRIP,this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                fragment.setTrip(trip);
            }
        }, 1000);

        TextView beginningAddress, beginningTime, endingAddress, endingTime, distance, velocity, time;

        beginningAddress = (TextView) findViewById(R.id.beginningAddress);
        beginningTime = (TextView) findViewById(R.id.beginningTime);
        endingAddress = (TextView) findViewById(R.id.endingAddress);
        endingTime = (TextView) findViewById(R.id.endingTime);
        distance = (TextView) findViewById(R.id.distance);
        velocity = (TextView) findViewById(R.id.velocity);
        time = (TextView) findViewById(R.id.time);

        beginningAddress.setText("Desde: " + trip.getBeginningAddress());
        endingAddress.setText("Hasta: " + trip.getEndingAddress());
        distance.setText("Distancia: " + trip.getDistance() + " kms");
        velocity.setText("Velocidad: " + trip.getAverageVelocity() + " km/h");
        time.setText("Tiempo: " + trip.getTime());

        String formattedDate = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(trip.getStartTime());
        beginningTime.setText(formattedDate);

        formattedDate = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(trip.getFinishTime());
        endingTime.setText(formattedDate);

    }

    public void onBackPressed(){
        commandHandlerManager.setNullCommand();
        STTService.getInstance().setIsListening(false);
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_TRIP_HISTORY, commandHandlerManager.getMainActivity());
        this.finish();
    }

    public void addMap(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment = new TripMap();
        transaction.add(R.id.tripMap, fragment);
        transaction.commit();
    }

    public void share(View v){
        openShareDialog();
    }

    public void openShareDialog(){
        AlertDialog.Builder builder =new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(true);
        builder.setTitle("¿Dónde querés compartir el viaje?");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final ImageView imgMarvin = new ImageView(this);
        imgMarvin.setImageResource(R.drawable.marvin);
        imgMarvin.setMaxHeight(30);
        imgMarvin.setMaxWidth(30);
        layout.addView(imgMarvin);

        builder.setView(layout);

        builder.setPositiveButton("facebook", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                shareInFacebook("");
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();

            }
        });
        builder.setNegativeButton("twitter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                shareInTwitter("");
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();

            }
        });

        builder.setNeutralButton("instagram", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                shareInInstagram("");
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();

            }
        });

        builder.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    STTService.getInstance().setIsListening(false);
                    commandHandlerManager.setNullCommand();
                    //customDialog.dismiss();
                }
                return true;
            }

        });


        builder.show();

    }

    public void shareInFacebook(String text){

        if(!text.equals("")){
            Character firstCharacter, newFirstCharacter;

            firstCharacter = text.charAt(0);
            newFirstCharacter = Character.toUpperCase(firstCharacter);
            text = text.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        }

        fragment.captureScreen();

        FacebookService facebookService = new FacebookService(this);

        facebookService.postImage(mapBitmap, text);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                File photo = new File(mapPath);
                photo.delete();
            }
        }, 3000);

    }

    public void shareInTwitter(String text){

        if(!text.equals("")){
            Character firstCharacter, newFirstCharacter;

            firstCharacter = text.charAt(0);
            newFirstCharacter = Character.toUpperCase(firstCharacter);
            text = text.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        }

        final String textToPublish = text;

        fragment.captureScreen();

        final TwitterService twitterService = TwitterService.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                twitterService.postTweet(textToPublish, new File(mapPath));
            }
        }, 1000);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                File photo = new File(mapPath);
                photo.delete();
            }
        }, 3000);

    }

    public void shareInInstagram(String text){

        if(!text.equals("")){
            Character firstCharacter, newFirstCharacter;

            firstCharacter = text.charAt(0);
            newFirstCharacter = Character.toUpperCase(firstCharacter);
            text = text.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        }

        final String textToPublish = text;

        fragment.captureScreen();

        final InstagramService instagramService = new InstagramService(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                instagramService.postImageOnInstagram("image/png", textToPublish, mapPath);
            }
        }, 1000);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                File photo = new File(mapPath);
                photo.delete();
            }
        }, 3000);

    }

    public void setMapBitmap(Bitmap bitmap){
        mapBitmap = bitmap;
    }

}
