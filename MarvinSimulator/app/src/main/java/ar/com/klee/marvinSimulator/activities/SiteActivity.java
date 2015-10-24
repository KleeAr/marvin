package ar.com.klee.marvinSimulator.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import ar.com.klee.marvinSimulator.gps.CardSiteAdapter;
import ar.com.klee.marvinSimulator.gps.Site;
import ar.com.klee.marvinSimulator.gps.SiteMap;
import ar.com.klee.marvinSimulator.social.FacebookService;
import ar.com.klee.marvinSimulator.social.InstagramService;
import ar.com.klee.marvinSimulator.social.TwitterService;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.STTService;

public class SiteActivity extends ActionBarActivity {

    private SiteMap fragment;
    private Site site;

    private CommandHandlerManager commandHandlerManager;
    private String mapPath = "/sdcard/MARVIN/site.png";
    private Bitmap mapBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ar.com.klee.marvinSimulator.R.layout.activity_site);

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
        tv_site = (TextView) findViewById(ar.com.klee.marvinSimulator.R.id.tv_site);
        tv_site.setText(site.getSiteName() + " - " + site.getSiteAddress());

        ImageView imageView = (ImageView)findViewById(ar.com.klee.marvinSimulator.R.id.img_thumbnail);
        File imgFile = new  File("/sdcard/MARVIN/Sitios/" + site.getSiteName() + ".png");
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }else{
            imageView.setImageResource(ar.com.klee.marvinSimulator.R.drawable.city);
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
        transaction.add(ar.com.klee.marvinSimulator.R.id.siteMap, fragment);
        transaction.commit();
    }

    public void shareSite(View v){
        openShareDialog();
    }

    public void openShareDialog(){

        final AlertDialog.Builder builderDialog =new AlertDialog.Builder(this, ar.com.klee.marvinSimulator.R.style.AppCompatAlertDialogStyle);
        builderDialog.setCancelable(true);
        builderDialog.setTitle("¿Dónde querés compartir el sitio?");
        builderDialog.setIcon(ar.com.klee.marvinSimulator.R.drawable.marvin);


        builderDialog.setPositiveButton("facebook", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                shareInFacebook("");
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                dialog.dismiss();
            }
        });
        builderDialog.setNegativeButton("twitter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                shareInTwitter("");
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                dialog.dismiss();
            }
        });

        builderDialog.setNeutralButton("instagram", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                shareInInstagram("");
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                dialog.dismiss();
            }
        });

        builderDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    STTService.getInstance().setIsListening(false);
                    commandHandlerManager.setNullCommand();
                    //dialog.dismiss();
                }
                return true;
            }

        });
        builderDialog.show();


    }

    public void shareInFacebook(String text){

        if(!text.equals("")){
            Character firstCharacter, newFirstCharacter;

            firstCharacter = text.charAt(0);
            newFirstCharacter = Character.toUpperCase(firstCharacter);
            text = text.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        }

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

    public void showImageDialog(View v){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, ar.com.klee.marvinSimulator.R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(true);
        builder.setTitle(site.getSiteName() + " - " + site.getSiteAddress());

        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(30, 10, 30, 10);

        final ImageView image1 = new ImageView(this);

        File imgFile = new  File("/sdcard/MARVIN/Sitios/" + site.getSiteName() + ".png");
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            image1.setImageBitmap(myBitmap);
        }else{
            image1.setImageResource(ar.com.klee.marvinSimulator.R.drawable.city);
        }

        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        image1.setLayoutParams(lp);
        layout.addView(image1);

        builder.setView(layout);

        builder.show();
    }

    public void setMapBitmap(Bitmap bitmap){
        mapBitmap = bitmap;
    }

}
