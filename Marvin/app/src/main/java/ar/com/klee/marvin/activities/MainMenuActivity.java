package ar.com.klee.marvin.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.call.CallDriver;
import ar.com.klee.marvin.call.CallReceiver;
import ar.com.klee.marvin.gps.LocationSender;
import ar.com.klee.marvin.multimedia.music.MusicService;
import ar.com.klee.marvin.multimedia.video.YouTubeVideo;
import ar.com.klee.marvin.sms.SMSDriver;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;


public class MainMenuActivity extends ActionBarActivity implements DelegateTask<List<YouTubeVideo>> {

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
    private CallReceiver callReceiver;

    private Button bt_play;
    private Button bt_pause;
    private Button bt_next;
    private Button bt_previous;

    private TextView tv_song;
    private TextView tv_artist;

    private ServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommandHandlerManager.destroyInstance();
        setContentView(R.layout.activity_main_menu);

        bt_play = (Button) findViewById(R.id.bt_play);
        bt_pause = (Button) findViewById(R.id.bt_pause);
        bt_next = (Button) findViewById(R.id.bt_next);
        bt_previous = (Button) findViewById(R.id.bt_previous);

        bt_play.setVisibility(View.VISIBLE);
        bt_pause.setVisibility(View.INVISIBLE);

        tv_song = (TextView)findViewById(R.id.song);
        tv_artist = (TextView)findViewById(R.id.artist);

        initializeMusicService();
        initializeSTTService();

        locationSender = new LocationSender(this);

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

        if(CallReceiver.isInstanceInitialized()) {
            callReceiver = CallReceiver.getInstance();
        } else {
            callReceiver = CallReceiver.initializeInstance(getApplicationContext());
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
                    bt_pause.setEnabled(false);
                    bt_next.setEnabled(false);
                    bt_previous.setEnabled(false);

                    // ACTIVAR PANTALLA DE MARVIN

                }else if(notification.equals("MarvinFinish")){

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if(wasPlaying) {
                                bt_play.setVisibility(View.INVISIBLE);
                                bt_pause.setVisibility(View.VISIBLE);
                                musicService.startPlaying();
                            }else{
                                bt_play.setVisibility(View.VISIBLE);
                                bt_pause.setVisibility(View.INVISIBLE);
                            }
                        }
                    }, 5000);

                    bt_play.setEnabled(true);
                    bt_pause.setEnabled(true);
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
                    callReceiver.initializeCommandHandlerManager();
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
            bt_play.setVisibility(View.INVISIBLE);
            bt_pause.setVisibility(View.VISIBLE);
        }else{
            musicService.pause();
            bt_play.setVisibility(View.VISIBLE);
            bt_pause.setVisibility(View.INVISIBLE);
        }
    }

    public void nextSong(View view){

        if(musicService.isListEmpty()){
            Toast.makeText(this, "No se encontraron canciones en el dispositivo", Toast.LENGTH_LONG).show();
            return;
        }

        if(!musicService.isPlaying()) {
            bt_play.setVisibility(View.INVISIBLE);
            bt_pause.setVisibility(View.VISIBLE);
        }

        musicService.nextSong();

    }

    public void nextSongSet(){

        if(!musicService.isPlaying()) {
            bt_play.setVisibility(View.INVISIBLE);
            bt_pause.setVisibility(View.VISIBLE);
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
            bt_play.setVisibility(View.INVISIBLE);
            bt_pause.setVisibility(View.VISIBLE);
        }

        musicService.previousSong();

    }

    public void previousSongSet(){

        if(!musicService.isPlaying()) {
            bt_play.setVisibility(View.INVISIBLE);
            bt_pause.setVisibility(View.VISIBLE);
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

    public void openCamera(View v){

        Intent intent = new Intent(this, CameraActivity.class );
        startActivity(intent);

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

    // Sale de la aplicación deteniendo el servicio
    public void exit(View view){

        musicService.onStop();
        stopService(voiceControlServiceIntent);
        stopService(musicServiceIntent);

        finish();

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
        bt_pause.setEnabled(true);
        bt_next.setEnabled(true);
        bt_previous.setEnabled(true);

    }

    public void setButtonsDisabled(){

        bt_play.setEnabled(false);
        bt_pause.setEnabled(false);
        bt_next.setEnabled(false);
        bt_previous.setEnabled(false);

    }


/**************************************
*************CALL METHODS**************
*************************************/


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

}
