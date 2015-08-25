package ar.com.klee.marvin.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;

import ar.com.klee.marvin.R;
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
    private Dialog currentDialog;
    private String mapPath = "/sdcard/MARVIN/trip.png";
    private Bitmap mapBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        beginningAddress.setText(trip.getBeginningAddress());
        endingAddress.setText(trip.getEndingAddress());
        distance.setText(trip.getDistance());
        velocity.setText(trip.getAverageVelocity());
        time.setText(trip.getTime());

        String formattedDate = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(trip.getStartTime());
        beginningTime.setText(formattedDate);

        formattedDate = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(trip.getFinishTime());
        endingTime.setText(formattedDate);

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
        final Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(true);
        customDialog.setContentView(R.layout.dialog_trip);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        currentDialog = customDialog;

        customDialog.findViewById(R.id.facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                shareInFacebook("");

                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();

            }
        });
        customDialog.findViewById(R.id.twitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                shareInTwitter("");

                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();

            }
        });
        customDialog.findViewById(R.id.instagram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                shareInInstagram("");

                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();

            }
        });

        customDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    STTService.getInstance().setIsListening(false);
                    commandHandlerManager.setNullCommand();
                    customDialog.dismiss();
                }
                return true;
            }

        });

        customDialog.show();

    }

    public void shareInFacebook(String text){

        fragment.captureScreen();

        FacebookService facebookService = new FacebookService(this);

        facebookService.postImage(mapBitmap);

        File photo = new File(mapPath);
        photo.delete();

        currentDialog.dismiss();

    }

    public void shareInTwitter(String text){

        fragment.captureScreen();

        TwitterService twitterService = TwitterService.getInstance();

        twitterService.postTweet(text, new File(mapPath));

        File photo = new File(mapPath);
        photo.delete();

        currentDialog.dismiss();

    }

    public void shareInInstagram(String text){

        fragment.captureScreen();

        InstagramService instagramService = new InstagramService(this);

        instagramService.postImageOnInstagram("image/jpeg", text, mapPath);

        File photo = new File(mapPath);
        photo.delete();

        currentDialog.dismiss();

    }

    public void setMapBitmap(Bitmap bitmap){
        mapBitmap = bitmap;
    }

}
