package ar.com.klee.marvin.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.speech.SpeechRecognizer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.SlidingTabLayout;
import ar.com.klee.marvin.ViewPagerAdpater;
import ar.com.klee.marvin.applications.Application;
import ar.com.klee.marvin.call.CallDriver;
import ar.com.klee.marvin.call.CallReceiver;
import ar.com.klee.marvin.data.Channel;
import ar.com.klee.marvin.data.Item;
import ar.com.klee.marvin.gps.LocationSender;
import ar.com.klee.marvin.gps.MapFragment;
import ar.com.klee.marvin.multimedia.music.MusicService;
import ar.com.klee.marvin.multimedia.video.YouTubeVideo;
import ar.com.klee.marvin.service.WeatherServiceCallback;
import ar.com.klee.marvin.service.YahooWeatherService;
import ar.com.klee.marvin.sms.SMSDriver;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;


public class MainMenuActivity extends ActionBarActivity implements DelegateTask<List<YouTubeVideo>>, WeatherServiceCallback {


    public final int CANT_APPLICATION=12; //variable en que se definen la cantidad de aplicaciones disponibles
    public final int UPDATE_WEATHER=1000000; //cantidad de milisegundos para actualizar el clima

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdpater adapter;
    CharSequence Titles[]={"Home","Aplicacion","Mapa"};
    int NumbOfTabs = 3;
    private long date;
    public static TextView cityText;
    public static TextView mainStreet;
    public static TextView speed;

    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private YahooWeatherService service;
    public static Application[] shortcutList; //lista para almacenar las aplicaciones configuradas
    public static ImageButton[] shortcutButton;
    public static MapFragment mapFragment;
    public static boolean isMapCreated = false;

    private Intent voiceControlServiceIntent;
    private Intent musicServiceIntent;
    private MusicService musicService;
    private BroadcastReceiver voiceControlReceiver;
    private BroadcastReceiver musicReceiver;
    private CommandHandlerManager commandHandlerManager;
    private LocationSender locationSender;

    private boolean mIsBound;
    private boolean wasPlaying;

    private SMSDriver smsDriver;
    private CallDriver callDriver;

    private ImageButton bt_play;
    private ImageButton bt_next;
    private ImageButton bt_previous;

    private TextView tv_song;
    private TextView tv_artist;

    private ServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommandHandlerManager.destroyInstance();
        setContentView(R.layout.activity_main_menu);

        bt_play = (ImageButton) findViewById(R.id.bt_play);
        bt_next = (ImageButton) findViewById(R.id.bt_next);
        bt_previous = (ImageButton) findViewById(R.id.bt_previous);

        tv_song = (TextView)findViewById(R.id.song);
        tv_artist = (TextView)findViewById(R.id.artist);

        SlidingTabLayout tabs;

        initializeMusicService();
        initializeSTTService();

        //Crea el mapa
        if(MapFragment.isInstanceInitialized())
            mapFragment = MapFragment.getInstance();
        else
            mapFragment = new MapFragment();

        locationSender = new LocationSender(this, mapFragment);

        ////////////////////////////////////


      //  LocationSender locationSender = new LocationSender(this);

        cityText = (TextView)findViewById(R.id.cityText);
        mainStreet = (TextView)findViewById(R.id.mainStreet);


        shortcutList = new Application[CANT_APPLICATION]; //creamos la lista para almacenar los accesos directos

        for (int i = 0; i < CANT_APPLICATION; i++)
            shortcutList[i] = new Application(null, null, null, false);//inicia los objetos

        shortcutButton = new ImageButton[CANT_APPLICATION]; //creamos la lista para almacenar los botones


        // RECUPERAR DATOS
        // Creamos la instancia de "SharedPreferences" en MODE_PRIVATE
        SharedPreferences settings = getSharedPreferences("PREFERENCES", 0);
      /* Esta parte es para validar que se creo un acceso directo
      if(!settings.getBoolean("IsIconCreated",false)){
            addShortcut();
            getSharedPreferences("PREFERENCES", 0).edit().putBoolean("IsIconCreated", true);

        }*/
        //Se recupera la información en los arrays
        for(int i=0; i<CANT_APPLICATION;i++){
            shortcutList[i].setPackageName(settings.getString("ButtonPack" + i, ""));
            shortcutList[i].setName(settings.getString("ButtonName" + i, ""));
            shortcutList[i].setConfigured(settings.getBoolean("ButtonConfig" + i, false));
            try {
                shortcutList[i].setIcon(getPackageManager().getApplicationIcon(shortcutList[i].getPackageName()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }

        ////////////////////


        //definimos los tipos de letra
        Typeface fBariolBold = Typeface.createFromAsset(getAssets(), "Bariol_Bold.otf");
        Typeface fBariolRegular = Typeface.createFromAsset(getAssets(), "Bariol_Regular.otf");

        speed = (TextView)findViewById(R.id.speed);
        speed.setTypeface(fBariolBold);
        final TextView speedUnit = (TextView)findViewById(R.id.speedUnit);
        speedUnit.setTypeface(fBariolRegular);

        temperatureTextView = (TextView) findViewById(R.id.temperatureText);
        temperatureTextView.setTypeface(fBariolRegular);


        final TextView digitalClock = (TextView)findViewById(R.id.digitalClock);
        final TextView weekDay = (TextView)findViewById(R.id.weekDayText);
        weekDay.setTypeface(fBariolRegular);

        final TextView dateText = (TextView)findViewById(R.id.dateText);
        dateText.setTypeface(fBariolRegular);

        final TextView anteMeridiem = (TextView)findViewById(R.id.anteMeridiem);
        date = System.currentTimeMillis();

        final SimpleDateFormat formatTime1 = new SimpleDateFormat("hh:mm");
        final SimpleDateFormat formatTime2 = new SimpleDateFormat("aa");

        digitalClock.setText(formatTime1.format(date));
        digitalClock.setTypeface(fBariolBold);

        anteMeridiem.setText(formatTime2.format(date));
        anteMeridiem.setTypeface(fBariolRegular);

        final SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        weekDay.setText(sdf.format(date));//sdf.format(new Date()));


        final SimpleDateFormat formatTime3 = new SimpleDateFormat("dd 'de' MMMM");
        dateText.setText(formatTime3.format(date));

        final SimpleDateFormat dateComplete = new SimpleDateFormat("hh:mm aa");



        Thread tTime = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(999);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                date = System.currentTimeMillis();
                                digitalClock.setText(formatTime1.format(date));
                                anteMeridiem.setText(formatTime2.format(date));
                                if(dateComplete.format(date).equals("12:00 a.m.")){
                                    weekDay.setText(sdf.format(date));
                                    dateText.setText(formatTime3.format(date));

                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        tTime.start();



        weatherIconImageView = (ImageView) findViewById(R.id.weatherImage);
        service = new YahooWeatherService(this, getApplicationContext());
        service.refreshWeather(cityText.getText().toString());

        Thread tWheather = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(UPDATE_WEATHER);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                service.refreshWeather(cityText.getText().toString());
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        tWheather.start();


        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdpater(getSupportFragmentManager(),Titles,NumbOfTabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        if(SMSDriver.isInstanceInitialized()) {
            smsDriver = SMSDriver.getInstance();
        } else {
            smsDriver = SMSDriver.initializeInstance(getApplicationContext());
        }

        if(CallDriver.isInstanceInitialized()) {
            callDriver = CallDriver.getInstance();
        } else {
            callDriver = CallDriver.initializeInstance(getApplicationContext());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_principal, menu);
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

    public void onBackPressed(){
        musicService.onStop();
        stopService(voiceControlServiceIntent);
        stopService(musicServiceIntent);
        finish();
    }

    public void stopServices(){
        musicService.onStop();
        stopService(voiceControlServiceIntent);
        stopService(musicServiceIntent);
        finish();
    }

    @Override
    public void serviceSuccess(Channel channel) {

        Item item = channel.getItem();

        int resourceId = getResources().getIdentifier("drawable/icon_"+item.getCondition().getCode(), null, getPackageName());

        @SuppressWarnings("deprecation")
        Drawable weatherIconDrawble = getResources().getDrawable(resourceId);

        weatherIconImageView.setImageDrawable(weatherIconDrawble);

        temperatureTextView.setText(item.getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature());

    }

    @Override
    public void serviceFailure(Exception exception) {
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
    }


    public void initializeSTTService(){

        // Lanzamos el servicio que controla la ejecución por comandos de voz
        voiceControlServiceIntent = new Intent(this,STTService.class);
        startService(voiceControlServiceIntent);

        // Creamos un receptor de parámetros que comunique el Activity con el servicio de voz
        voiceControlReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                String notification = intent.getStringExtra(STTService.COPA_MESSAGE);

                if(notification.equals("Marvin")){

                    if(musicService.isPlaying()) {
                        wasPlaying = true;
                        musicService.pause();
                    }else
                        wasPlaying = false;

                    bt_play.setEnabled(false);
                    bt_next.setEnabled(false);
                    bt_previous.setEnabled(false);

                    // ACTIVAR PANTALLA DE MARVIN

                }else if(notification.equals("MarvinFinish")){

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if(wasPlaying) {
                                bt_play.setImageResource(R.drawable.ic_media_pause);
                                musicService.startPlaying();
                            }else{
                                bt_play.setImageResource(R.drawable.ic_media_play);
                            }
                        }
                    }, 5000);

                    bt_play.setEnabled(true);
                    bt_next.setEnabled(true);
                    bt_previous.setEnabled(true);

                    // CERRAR PANTALLA DE MARVIN

                }else if(notification.startsWith("Command ")){

                    notification = notification.replace("Command ","");

                    //MOSTRAR COMANDO

                }else if(notification.equals("Started")){
                    commandHandlerManager = CommandHandlerManager.getInstance();
                    smsDriver.initializeCommandHandlerManager();
                    callDriver.initializeCommandHandlerManager();
                    setCommandHandlerManager();
                }

            }
        };
    }

    public void initializeMusicService(){

        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                musicService = ((MusicService.MusicBinder)service).getService();
            }

            public void onServiceDisconnected(ComponentName className) {
                musicService = null;
            }
        };

        doBindService();

        musicReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                String notification = intent.getStringExtra(MusicService.COPA_MESSAGE);

                if(notification.startsWith("SONG_TITLE ")){

                    notification = notification.replace("SONG_TITLE ","");
                    Log.d("TITLE",notification);
                    tv_song.setText(notification);


                }else if(notification.startsWith("SONG_ARTIST ")) {

                    notification = notification.replace("SONG_ARTIST ","");
                    Log.d("ARTIST", notification);
                    tv_artist.setText(notification);

                }
            }
        };
    }

    /********** METODOS PARA OBTENER LA INSTANCIA DEL SERVICIO DE MUSICA ***********/

    void doBindService() {
        musicServiceIntent = new Intent(this,MusicService.class);
        startService(musicServiceIntent);
        bindService(musicServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

 /***************************************************************************/

    public void startPauseMusic(View view){

        if(musicService.isListEmpty()){
            Toast.makeText(this, "No se encontraron canciones en el dispositivo", Toast.LENGTH_LONG).show();
            return;
        }

        if(!musicService.isPlaying()) {
            musicService.startPlaying();
            bt_play.setImageResource(R.drawable.ic_media_pause);

        }else{
            musicService.pause();
            bt_play.setImageResource(R.drawable.ic_media_play);
        }
    }

    public void nextSong(View view){

        if(musicService.isListEmpty()){
            Toast.makeText(this, "No se encontraron canciones en el dispositivo", Toast.LENGTH_LONG).show();
            return;
        }

        if(!musicService.isPlaying()) {
            bt_play.setImageResource(R.drawable.ic_media_pause);
        }

        musicService.nextSong();

    }

    public void nextSongSet(){

        if(!musicService.isPlaying()) {
            bt_play.setImageResource(R.drawable.ic_media_pause);
        }

        wasPlaying = true;

        musicService.nextSongSet();

    }
    
    public void previousSong(View view){

        if(musicService.isListEmpty()){
            Toast.makeText(this, "No se encontraron canciones en el dispositivo", Toast.LENGTH_LONG).show();
            return;
        }

        if(!musicService.isPlaying()) {
            bt_play.setImageResource(R.drawable.ic_media_pause);
        }

        musicService.previousSong();

    }

    public void previousSongSet(){

        if(!musicService.isPlaying()) {
            bt_play.setImageResource(R.drawable.ic_media_pause);
        }

        wasPlaying = true;

        musicService.previousSongSet();

    }

    public boolean findArtist(String artist){

        wasPlaying = true;

        return musicService.findArtist(artist);

    }

    public boolean findSong(String song){

        wasPlaying = true;

        return musicService.findTitle(song);

    }

    public boolean setRandom(boolean random){

        return musicService.setRandom(random);

    }

    public boolean isListEmpty(){

        return musicService.isListEmpty();

    }

    // Utilizado por el receptor de parámetros del servicio de comandos de voz
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((voiceControlReceiver),
                new IntentFilter(STTService.COPA_RESULT)
        );
        LocalBroadcastManager.getInstance(this).registerReceiver(musicReceiver,
                new IntentFilter(STTService.COPA_RESULT)
        );
    }

    // Utilizado por el receptor de parámetros del servicio de comandos de voz
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(voiceControlReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(musicReceiver);
        super.onStop();
    }


    public MusicService getMusicService(){

        return musicService;

    }

    @Override
    public void executeCallback(List<YouTubeVideo> result) {
        Intent intent = new Intent(this, SearchResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("videos", new ArrayList<Parcelable>(result));
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public boolean getWasPlaying(){

        return wasPlaying;

    }

    public void setWasPlaying(boolean was){

        wasPlaying = was;

    }

    public void setCommandHandlerManager(){
        commandHandlerManager.defineMainActivity(this);
    }

/**************************************
**********LOCATION METHODS************
*************************************/

    public String getAddress(){

        return locationSender.getAddress();

    }

    public String getTown(){

        return locationSender.getTown();

    }

    public String nextStreet(){

        return locationSender.nextStreet();

    }

    public String previousStreet(){

        return locationSender.previousStreet();

    }

/**************************************
*************SMS METHODS**************
*************************************/


    public void displaySendSMS(){

        smsDriver.displaySendSMS();

    }

    public void setNumber(String number){

        smsDriver.setNumber(number);

    }

    public void setMessageBody(String message){

        smsDriver.setMessageBody(message);

    }

    public String sendMessage(){

        return smsDriver.sendMessage();

    }

    public void cancelMessage(){

        smsDriver.cancelMessage();

    }

    public void displayRespondSMS(){

        smsDriver.cancelMessage();
        smsDriver.displayRespuesta();

    }

    public void setAnswer(String message){

        smsDriver.setAnswer(message);

    }

    public String respondMessage(){

        return smsDriver.respondMessage();

    }

    public void setButtonsEnabled(){

        bt_play.setEnabled(true);
        bt_next.setEnabled(true);
        bt_previous.setEnabled(true);

    }

    public void setButtonsDisabled(){

        bt_play.setEnabled(false);
        bt_next.setEnabled(false);
        bt_previous.setEnabled(false);

    }


/**************************************
*************CALL METHODS**************
*************************************/


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        STTService.getInstance().stopListening();

        if (requestCode == CallDriver.REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            callDriver.setUriContact(data.getData());
            callDriver.retrieveContactNumber();

        }
    }

    public void openCallDialog(){
        callDriver.outgoingCallDialog();
    }

    public void setCallNumber(String number){
        callDriver.setPhone(number);
    }

    public void closeCallDialog(){
        callDriver.closeDialog();
    }

    public void callNumber(String number){
        callDriver.callNumber(number);
    }


/**************************************
*************TTS METHOD***************
*************************************/

    public void activate(final SpeechRecognizer mSpeechRecognizer, final Intent mSpeechRecognizerIntent){
        runOnUiThread(new Runnable() {

            public void run() {

                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                STTService.getInstance().setState(true);

            }
        });
    }


         /*
    private void addShortcut() {

        //on Home screen
        Intent shortcutIntent = new Intent(getApplicationContext(),SplashActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "marvin");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource.fromContext(getApplicationContext(),R.mipmap.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }
*/

/**************************************
 *************MAPS METHODS**************
 *************************************/

    public FragmentManager getManager(){
        return getSupportFragmentManager();
    }

}
