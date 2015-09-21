package ar.com.klee.marvin.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import ar.com.klee.marvin.gps.CardSiteAdapter;
import ar.com.klee.marvin.R;
import ar.com.klee.marvin.gps.Site;
import ar.com.klee.marvin.gps.SiteMap;
import ar.com.klee.marvin.social.FacebookService;
import ar.com.klee.marvin.social.InstagramService;
import ar.com.klee.marvin.social.TwitterService;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;

public class SiteActivity extends ActionBarActivity {

    private SiteMap fragment;
    private Site site;

    private CommandHandlerManager commandHandlerManager;
    private Dialog currentDialog;
    private String mapPath = "/sdcard/MARVIN/site.png";
    private Bitmap mapBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);

        addMap();

        site = CardSiteAdapter.getInstance().getChosenSite();

        commandHandlerManager = CommandHandlerManager.getInstance();

        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_SITE,this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                fragment.setSite(site);
            }
        }, 1000);

        TextView tv_site;
        tv_site = (TextView) findViewById(R.id.tv_site);
        tv_site.setText(site.getSiteName() + " - " + site.getSiteAddress());

        ImageView imageView = (ImageView)findViewById(R.id.img_thumbnail);
        File imgFile = new  File("/sdcard/MARVIN/Sitios/" + site.getSiteName() + ".png");
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }else{
            imageView.setImageResource(R.drawable.city);
        }

    }

    public void onBackPressed(){
        commandHandlerManager.setNullCommand();
        STTService.getInstance().setIsListening(false);
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_PLACES, commandHandlerManager.getMainActivity());
        this.finish();
    }

    public void addMap(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment = new SiteMap();
        transaction.add(R.id.siteMap, fragment);
        transaction.commit();
    }

    public void share(View v){
        openShareDialog();
    }

    public void openShareDialog(){
        final Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(true);
        customDialog.setContentView(R.layout.dialog_site);
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

        if(!text.equals("")){
            Character firstCharacter, newFirstCharacter;

            firstCharacter = text.charAt(0);
            newFirstCharacter = Character.toUpperCase(firstCharacter);
            text = text.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        }

        currentDialog.dismiss();

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

        currentDialog.dismiss();

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

        currentDialog.dismiss();

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
