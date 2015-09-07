package ar.com.klee.marvin.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PowerManager;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ar.com.klee.marvin.DrawerMenuAdapter;
import ar.com.klee.marvin.DrawerMenuItem;
import ar.com.klee.marvin.R;
import ar.com.klee.marvin.call.CallDriver;
import ar.com.klee.marvin.configuration.UserConfig;
import ar.com.klee.marvin.data.Channel;
import ar.com.klee.marvin.data.Item;
import ar.com.klee.marvin.fragments.ConfigureAppFragment;
import ar.com.klee.marvin.fragments.DondeEstacioneFragment;
import ar.com.klee.marvin.fragments.MainMenuFragment;
import ar.com.klee.marvin.fragments.MisSitiosFragment;
import ar.com.klee.marvin.fragments.MisViajesFragment;
import ar.com.klee.marvin.fragments.PerfilFragment;
import ar.com.klee.marvin.gps.LocationSender;
import ar.com.klee.marvin.gps.MapFragment;
import ar.com.klee.marvin.multimedia.music.MusicService;
import ar.com.klee.marvin.multimedia.video.YouTubeVideo;
import ar.com.klee.marvin.service.WeatherServiceCallback;
import ar.com.klee.marvin.service.YahooWeatherService;
import ar.com.klee.marvin.sms.SMSDriver;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;


public class MainMenuActivity extends ActionBarActivity implements DelegateTask<List<YouTubeVideo>>, WeatherServiceCallback,AdapterView.OnItemClickListener {


    public final int UPDATE_WEATHER = 1000000; //cantidad de milisegundos para actualizar el clima
    public static final String TWITTER_KEY = "IsfPZw7I4i4NCZaFxM9BZX4Qi";
    public static final String TWITTER_SECRET = "aPnfZPsetWBwJ7E42RF0MMwsVL361hBu92ey1JwzkMcrNGedWE";

    public static MainMenuFragment mainMenuFragment;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mLvDrawerMenu;
    private DrawerMenuAdapter mDrawerMenuAdapter;
    private Toolbar toolbar;

    public static TextView cityText;

    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private YahooWeatherService service;

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

    private ServiceConnection mConnection;

    public static TextView weekDay;
    public static TextView dateText;

    private int actualFragmentPosition = 1;
    private Stack<Integer> previousMenus = new Stack();

    private PowerManager.WakeLock wakeLock;

    private boolean wasBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommandHandlerManager.destroyInstance();
        setContentView(R.layout.activity_main_menu);

        PowerManager powerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        wakeLock.acquire();

        initializeMusicService();
        initializeSTTService();
        if(!UserConfig.isInstanceInitialized())
            UserConfig.initializeInstance();

        UserConfig configuration = UserConfig.getInstance();

        //Crea el mainMenu
        if (MainMenuFragment.isInstanceInitialized())
            mainMenuFragment = MainMenuFragment.getInstance();
        else
            mainMenuFragment = new MainMenuFragment();

        weekDay = (TextView) findViewById(R.id.weekDayText);
        dateText = (TextView) findViewById(R.id.dateText);

        //Crea el mapa
        if (MapFragment.isInstanceInitialized())
            mapFragment = MapFragment.getInstance();
        else
            mapFragment = new MapFragment();

        locationSender = new LocationSender(this, mapFragment);

        if (SMSDriver.isInstanceInitialized()) {
            smsDriver = SMSDriver.getInstance();
        } else {
            smsDriver = SMSDriver.initializeInstance(getApplicationContext());
        }

        if (CallDriver.isInstanceInitialized()) {
            callDriver = CallDriver.getInstance();
        } else {
            callDriver = CallDriver.initializeInstance(getApplicationContext());
        }

        cityText = (TextView) findViewById(R.id.cityText);

        //definimos los tipos de letra
        Typeface fBariolRegular = Typeface.createFromAsset(getAssets(), "Bariol_Regular.otf");

        temperatureTextView = (TextView) findViewById(R.id.temperatureText);
        temperatureTextView.setTypeface(fBariolRegular);

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

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLvDrawerMenu = (ListView) findViewById(R.id.lv_drawer_menu);

        /*
        * Redondeo de la imagen de usuario del menu deslizable
        */

        //extraemos el drawable en un bitmap
        Drawable originalDrawable = getResources().getDrawable(R.drawable.icon_user);
        Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();

        if (originalBitmap.getWidth() > originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        } else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getWidth());


        //Añadimos cabecera general

        View header = getLayoutInflater().inflate(R.layout.drawer_header_menu_item, null);
        mLvDrawerMenu.addHeaderView(header);

        ImageView imageView = (ImageView) findViewById(R.id.im_perfil);
        imageView.setImageDrawable(roundedDrawable);

        ShapeDrawable sd = new ShapeDrawable(new OvalShape());
        sd.setIntrinsicHeight(100);
        sd.setIntrinsicWidth(100);
        sd.getPaint().setColor(Color.parseColor("#ffffff"));

        imageView.setBackground(sd);

        List<DrawerMenuItem> menuItems = generateDrawerMenuItems();
        mDrawerMenuAdapter = new DrawerMenuAdapter(getApplicationContext(), menuItems);
        mLvDrawerMenu.setAdapter(mDrawerMenuAdapter);

        mLvDrawerMenu.setOnItemClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View drawerView) {

                // getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
            }


            public void onDrawerOpened(View drawerView) {

                // getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    public int getActualFragmentPosition() {
        return actualFragmentPosition;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mDrawerLayout.isDrawerOpen(mLvDrawerMenu)) {
                    mDrawerLayout.closeDrawer(mLvDrawerMenu);
                }
                else {
                    mDrawerLayout.openDrawer(mLvDrawerMenu);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(actualFragmentPosition == position) {
            mDrawerLayout.closeDrawer(mLvDrawerMenu);
            mLvDrawerMenu.invalidateViews();
            return;
        }

        previousMenus.push(actualFragmentPosition);

        actualFragmentPosition = position;

        switch (position) {
            case 0:
                setFragment(0, PerfilFragment.class);
                break;
            case 1:
                setFragment(1, MainMenuFragment.class);
                break;
            case 2:
                Toast.makeText(getApplicationContext(), "posicion " + position, Toast.LENGTH_SHORT).show();
                //setFragment(2, ComandosDeVoz.class);
                break;
            case 4:
                setFragment(4, MisViajesFragment.class);
                break;
            case 5:
                setFragment(5, MisSitiosFragment.class);
                break;
            case 6:
                setFragment(5, DondeEstacioneFragment.class);
                break;
            case 8:
                setFragment(8, ConfigureAppFragment.class);
                break;
            case 9:
                Toast.makeText(getApplicationContext(), "posicion 9" + position, Toast.LENGTH_SHORT).show();
                break;
            case 10:
                MainMenuActivity.mapFragment.finishTrip();
                MainMenuFragment.getInstance().stopThread();
                stopServices();
                finish();
                break;
            case 11:
                mDrawerLayout.closeDrawer(mLvDrawerMenu);
                mLvDrawerMenu.invalidateViews();
                break;
        }

        // Setting it as the Toolbar for the activity
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {

        if(!previousMenus.empty()){

            int position = previousMenus.pop();

            actualFragmentPosition = position;

            switch (position) {
                case 0:
                    setFragment(0, PerfilFragment.class);
                    break;
                case 1:
                    setFragment(1, MainMenuFragment.class);
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "posicion " + position, Toast.LENGTH_SHORT).show();
                    //setFragment(2, ComandosDeVoz.class);
                    break;
                case 4:
                    setFragment(4, MisViajesFragment.class);
                    break;
                case 5:
                    setFragment(5, MisSitiosFragment.class);
                    break;
                case 6:
                    Toast.makeText(getApplicationContext(), "posicion 6" + position, Toast.LENGTH_SHORT).show();
                    //setFragment(5, DondeEstacioneFragment.class);
                    break;
                case 8:
                    setFragment(8, ConfigureAppFragment.class);
                    break;
                case 9:
                    Toast.makeText(getApplicationContext(), "posicion 9" + position, Toast.LENGTH_SHORT).show();
                    break;
                case 10:
                    MainMenuActivity.mapFragment.finishTrip();
                    MainMenuFragment.getInstance().stopThread();
                    stopServices();
                    finish();
                    break;
                case 11:
                    mDrawerLayout.closeDrawer(mLvDrawerMenu);
                    mLvDrawerMenu.invalidateViews();
                    break;
            }

            setSupportActionBar(toolbar);

            return;
        }

        if(wasBackPressed)
            return;

        wasBackPressed = true;

        MainMenuFragment.getInstance().setItem(2);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mapFragment.finishTrip();

                if(MainMenuFragment.isInstanceInitialized())
                    MainMenuFragment.getInstance().stopThread();
                musicService.onStop();
                stopService(voiceControlServiceIntent);
                stopService(musicServiceIntent);
                finish();
            }
        }, 1000);

    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(mDrawerToggle != null)
            mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void setFragment(int position, Class<? extends Fragment> fragmentClass) {
        try {

            Fragment fragment;

            fragment = fragmentClass.newInstance();

            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment, fragmentClass.getSimpleName());
            fragmentTransaction.commit();

            mLvDrawerMenu.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mLvDrawerMenu);
            mLvDrawerMenu.invalidateViews();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<DrawerMenuItem> generateDrawerMenuItems() {
        String[] itemsText = getResources().getStringArray(R.array.nav_drawer_items);
        TypedArray itemsIcon = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        List<DrawerMenuItem> result = new ArrayList<DrawerMenuItem>();
        for (int i = 0; i < itemsText.length; i++) {
            switch (i) {
                case 2:
                    DrawerMenuItem item = new DrawerMenuItem();
                    item.setText("Viajes");
                    result.add(item);
                    break;
                case 5:
                    DrawerMenuItem item2 = new DrawerMenuItem();
                    item2.setText("Opciones");
                    result.add(item2);
                    break;
            }

            DrawerMenuItem item = new DrawerMenuItem();
            item.setText(itemsText[i]);
            item.setIcon(itemsIcon.getResourceId(i, -1));
            result.add(item);
        }


        return result;
    }

    public void stopServices() {
        wakeLock.release();
        musicService.onStop();
        stopService(voiceControlServiceIntent);
        stopService(musicServiceIntent);
        finish();
    }

    @Override
    public void serviceSuccess(Channel channel) {

        Item item = channel.getItem();

        int resourceId = getResources().getIdentifier("drawable/icon_" + item.getCondition().getCode(), null, getPackageName());

        @SuppressWarnings("deprecation")
        Drawable weatherIconDrawble = getResources().getDrawable(resourceId);

        weatherIconImageView.setImageDrawable(weatherIconDrawble);

        temperatureTextView.setText(item.getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature());

    }

    @Override
    public void serviceFailure(Exception exception) {
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
    }


    public void initializeSTTService() {

        // Lanzamos el servicio que controla la ejecución por comandos de voz
        voiceControlServiceIntent = new Intent(this, STTService.class);
        startService(voiceControlServiceIntent);

        // Creamos un receptor de parámetros que comunique el Activity con el servicio de voz
        voiceControlReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                String notification = intent.getStringExtra(STTService.COPA_MESSAGE);

                if (notification.equals("Marvin")) {

                    if (musicService.isPlaying()) {
                        wasPlaying = true;
                        musicService.pause();
                    } else
                        wasPlaying = false;

                    MainMenuFragment.bt_play.setEnabled(false);
                    MainMenuFragment.bt_next.setEnabled(false);
                    MainMenuFragment.bt_previous.setEnabled(false);

                    // ACTIVAR PANTALLA DE MARVIN

                } else if (notification.equals("MarvinFinish")) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (wasPlaying) {
                                MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_pause);
                                musicService.startPlaying();
                            } else {
                                MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_play);
                            }
                        }
                    }, 5000);

                    MainMenuFragment.bt_play.setEnabled(true);
                    MainMenuFragment.bt_next.setEnabled(true);
                    MainMenuFragment.bt_previous.setEnabled(true);

                    // CERRAR PANTALLA DE MARVIN

                } else if (notification.startsWith("Command ")) {

                    notification = notification.replace("Command ", "");

                    //MOSTRAR COMANDO

                } else if (notification.equals("Started")) {
                    commandHandlerManager = CommandHandlerManager.getInstance();
                    smsDriver.initializeCommandHandlerManager();
                    callDriver.initializeCommandHandlerManager();
                    setCommandHandlerManager();
                    initializeFragmentManager();
                }

            }
        };
    }

    public void initializeFragmentManager(){
        setFragment(1, MainMenuFragment.class);
    }

    public void initializeMusicService() {

        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                musicService = ((MusicService.MusicBinder) service).getService();
            }

            public void onServiceDisconnected(ComponentName className) {
                musicService = null;
            }
        };

        doBindService();

        musicReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                String notification = intent.getStringExtra(MusicService.COPA_MESSAGE);

                if (notification.startsWith("SONG_TITLE ")) {

                    notification = notification.replace("SONG_TITLE ", "");
                    Log.d("TITLE", notification);
                    MainMenuFragment.tv_song.setText(notification);


                } else if (notification.startsWith("SONG_ARTIST ")) {

                    notification = notification.replace("SONG_ARTIST ", "");
                    Log.d("ARTIST", notification);
                    MainMenuFragment.tv_artist.setText(notification);

                }
            }
        };
    }

    /**
     * ******* METODOS PARA OBTENER LA INSTANCIA DEL SERVICIO DE MUSICA **********
     */

    void doBindService() {
        musicServiceIntent = new Intent(this, MusicService.class);
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

    /**
     * ***********************************************************************
     */

    public void radioMusic(View view){

        if(musicService.getIsRadio()){
            MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_pause);
            MainMenuFragment.bt_radioMusic.setImageResource(R.mipmap.ic_audiotrack_white_48dp);
            musicService.setIsRadio(false);
        }else{
            MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_play);
            MainMenuFragment.bt_radioMusic.setImageResource(R.mipmap.ic_radio_white_48dp);
            musicService.setIsRadio(true);
        }

    }

    public void radioMusic(){

        MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_pause);
        if(musicService.getIsRadio()){
            MainMenuFragment.bt_radioMusic.setImageResource(R.mipmap.ic_audiotrack_white_48dp);
            musicService.setIsRadio(false);
        }else{
            MainMenuFragment.bt_radioMusic.setImageResource(R.mipmap.ic_radio_white_48dp);
            musicService.setIsRadio(true);
        }

    }

    public void startPauseMusic(View view) {

        if(!musicService.getIsRadio()) {
            if (musicService.isListEmpty()) {
                Toast.makeText(this, "No se encontraron canciones en el dispositivo", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (!musicService.isPlaying()) {
            musicService.startPlaying();
            MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_pause);
        } else {
            musicService.pause();
            MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_play);
            MainMenuFragment.tv_song.setText("Reproducción Pausada");
            MainMenuFragment.tv_artist.setText("");
        }

    }

    public void next(View view) {

        if(!musicService.getIsRadio()) {
            if (musicService.isListEmpty()) {
                Toast.makeText(this, "No se encontraron canciones en el dispositivo", Toast.LENGTH_LONG).show();
                return;
            }

            if (!musicService.isPlaying()) {
                MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_pause);
            }

            musicService.nextSong();

        }else{

            MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_play);
            musicService.nextRadio();

        }

    }

    public void nextSet(String type) {

        if(type.equals("music")) {
            if (!musicService.isPlaying()) {
                MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_pause);
            }

            MainMenuFragment.bt_radioMusic.setImageResource(R.mipmap.ic_audiotrack_white_48dp);
            musicService.setIsRadio(false);

            wasPlaying = true;

            musicService.nextSongSet();

        }else{

            MainMenuFragment.bt_radioMusic.setImageResource(R.mipmap.ic_radio_white_48dp);
            musicService.setIsRadio(true);

            wasPlaying = true;

            MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_pause);
            musicService.nextRadioSet();

        }

    }

    public void previous(View view) {

        if(!musicService.getIsRadio()) {

            if (musicService.isListEmpty()) {
                Toast.makeText(this, "No se encontraron canciones en el dispositivo", Toast.LENGTH_LONG).show();
                return;
            }

            if (!musicService.isPlaying()) {
                MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_pause);
            }

            musicService.previousSong();

        }else{

            MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_play);
            musicService.previousRadio();
        }

    }

    public void previousSet(String type) {

        if(type.equals("music")) {

            if (!musicService.isPlaying()) {
                MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_pause);
            }

            MainMenuFragment.bt_radioMusic.setImageResource(R.mipmap.ic_audiotrack_white_48dp);
            musicService.setIsRadio(false);

            wasPlaying = true;

            musicService.previousSongSet();

        }else{

            MainMenuFragment.bt_radioMusic.setImageResource(R.mipmap.ic_radio_white_48dp);
            musicService.setIsRadio(true);

            wasPlaying = true;

            MainMenuFragment.bt_play.setImageResource(R.drawable.ic_media_pause);
            musicService.previousRadioSet();

        }

    }

    public boolean findArtist(String artist) {

        wasPlaying = true;
        MainMenuFragment.bt_radioMusic.setImageResource(R.mipmap.ic_audiotrack_white_48dp);

        if(musicService.findArtist(artist)){
            musicService.setIsRadio(false);
            return true;
        }

        return false;


    }

    public boolean findSong(String song) {

        wasPlaying = true;
        MainMenuFragment.bt_radioMusic.setImageResource(R.mipmap.ic_audiotrack_white_48dp);

        if(musicService.findTitle(song)){
            musicService.setIsRadio(false);
            return true;
        }

        return false;

    }

    public boolean setRandom(boolean random) {

        return musicService.setRandom(random);

    }

    public boolean isListEmpty() {

        return musicService.isListEmpty();

    }

    public boolean findRadioName(String name){

        wasPlaying = true;
        MainMenuFragment.bt_radioMusic.setImageResource(R.mipmap.ic_radio_white_48dp);

        if(musicService.findRadioName(name)){
            musicService.setIsRadio(true);
            return true;
        }

        return false;

    }

    public boolean findRadioFrequence(String frequence){

        wasPlaying = true;
        MainMenuFragment.bt_radioMusic.setImageResource(R.mipmap.ic_radio_white_48dp);

        if(musicService.findRadioFrequence(frequence)){
            musicService.setIsRadio(true);
            return true;
        }

        return false;

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


    public MusicService getMusicService() {

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

    public boolean getWasPlaying() {

        return wasPlaying;

    }

    public void setWasPlaying(boolean was) {

        wasPlaying = was;

    }

    public void setCommandHandlerManager() {
        commandHandlerManager.defineMainActivity(this);
    }

    /**
     * ***********************************
     * *********LOCATION METHODS************
     * ***********************************
     */

    public String getAddress() {

        return locationSender.getAddress();

    }

    public String getTown() {

        return locationSender.getTown();

    }

    public String nextStreet() {

        return locationSender.nextStreet();

    }

    public String previousStreet() {

        return locationSender.previousStreet();

    }

    /**
     * ***********************************
     * ************SMS METHODS**************
     * ***********************************
     */


    public void displaySendSMS() {

        smsDriver.displaySendSMS();

    }

    public void setNumber(String number) {

        smsDriver.setNumber(number);

    }

    public void setMessageBody(String message) {

        smsDriver.setMessageBody(message);

    }

    public String sendMessage() {

        return smsDriver.sendMessage();

    }

    public String sendEmergencyMessage() {

        return smsDriver.sendEmergencyMessage();

    }

    public void cancelMessage() {

        smsDriver.cancelMessage();

    }

    public void displayRespondSMS() {

        smsDriver.cancelMessage();
        smsDriver.displayRespuesta();

    }

    public void setAnswer(String message) {

        smsDriver.setAnswer(message);

    }

    public String respondMessage() {

        return smsDriver.respondMessage();

    }


    /**
     * ***********************************
     * ************CALL METHODS**************
     * ***********************************
     */


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        STTService.getInstance().stopListening();

        if (requestCode == CallDriver.REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            callDriver.setUriContact(data.getData());
            callDriver.retrieveContactNumber();

        }
    }

    public void openCallDialog() {
        callDriver.outgoingCallDialog();
    }

    public void setCallNumber(String number) {
        callDriver.setPhone(number);
    }

    public void closeCallDialog() {
        callDriver.closeDialog();
    }

    public void callNumber(String number) {
        callDriver.callNumber(number);
    }


    /**
     * ***********************************
     * ************TTS METHOD***************
     * ***********************************
     */

    public void activate(final SpeechRecognizer mSpeechRecognizer, final Intent mSpeechRecognizerIntent) {
        runOnUiThread(new Runnable() {

            public void run() {

                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                STTService.getInstance().setState(true);

            }
        });
    }

    /**
     * ***********************************
     * ************MAPS METHODS**************
     * ***********************************
     */

    public FragmentManager getManager() {
        return getSupportFragmentManager();
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


    public void setButtonsEnabled() {

        MainMenuFragment.bt_play.setEnabled(true);
        MainMenuFragment.bt_next.setEnabled(true);
        MainMenuFragment.bt_previous.setEnabled(true);

    }

    public void setButtonsDisabled() {

        MainMenuFragment.bt_play.setEnabled(false);
        MainMenuFragment.bt_next.setEnabled(false);
        MainMenuFragment.bt_previous.setEnabled(false);

    }

}

