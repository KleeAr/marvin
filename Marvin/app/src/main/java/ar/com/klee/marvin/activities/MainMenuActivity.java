package ar.com.klee.marvin.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.multimedia.music.MusicService;
import ar.com.klee.marvin.voiceControl.Helper;
import ar.com.klee.marvin.voiceControl.STTService;


public class MainMenuActivity extends ActionBarActivity {

    private Intent voiceControlServiceIntent;
    private Intent musicServiceIntent;
    private MusicService musicService;
    private BroadcastReceiver voiceControlReceiver;
    private BroadcastReceiver musicReceiver;

    private boolean mIsBound;
    private boolean wasPlaying;

    private Button bt_play;
    private Button bt_pause;
    private Button bt_next;
    private Button bt_previous;

    private ServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        bt_play = (Button) findViewById(R.id.bt_play);
        bt_pause = (Button) findViewById(R.id.bt_pause);
        bt_next = (Button) findViewById(R.id.bt_next);
        bt_previous = (Button) findViewById(R.id.bt_previous);

        bt_play.setVisibility(View.VISIBLE);
        bt_pause.setVisibility(View.INVISIBLE);

        initializeSTTService();
        initializeMusicService();

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

                Log.d("NOT",notification);

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

                }else if(notification.startsWith("SONG_ARTIST ")) {

                    notification = notification.replace("SONG_ARTIST ","");
                    Log.d("ARTIST", notification);

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

    public boolean getWasPlaying(){

        return wasPlaying;

    }

    public void setWasPlaying(boolean was){

        wasPlaying = was;

    }

    public void setCommandHandlerManager(){
        Helper.commandHandlerManager.defineMainActivity(this);
    }

}
