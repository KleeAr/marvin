package ar.com.klee.marvin.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.StringTokenizer;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.musicPlayer.MusicService;
import ar.com.klee.marvin.voiceControl.STTService;


public class MainMenuActivity extends ActionBarActivity {

    private Intent voiceControlServiceIntent;
    private Intent musicServiceIntent;
    private MusicService musicService;
    private BroadcastReceiver voiceControlReceiver;
    private BroadcastReceiver musicReceiver;

    private boolean mIsBound;

    private Button bt_play;
    private Button bt_pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        bt_play = (Button) findViewById(R.id.bt_play);
        bt_pause = (Button) findViewById(R.id.bt_pause);

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

                String s = intent.getStringExtra(STTService.COPA_MESSAGE);

                StringTokenizer st = new StringTokenizer(s);

                s = st.nextToken();

                if(s.equals("PB")){

                    //escuchando.setProgress(Integer.parseInt(st.nextToken()));

                }
                else if(s.equals("TV")){

                    String toPost = "";

                    while(st.hasMoreTokens())
                        toPost += st.nextToken() + " ";

                    //speechToText.setText(toPost);

                }

            }
        };
    }

    public void initializeMusicService(){

        musicServiceIntent = new Intent(this,MusicService.class);
        startService(musicServiceIntent);

        musicReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                String notification = intent.getStringExtra(MusicService.COPA_MESSAGE);

                StringTokenizer st = new StringTokenizer(notification);

                notification = st.nextToken();

                if(notification.equals("PB")){

                    //escuchando.setProgress(Integer.parseInt(st.nextToken()));

                }

            }
        };

    }

/********** METODOS PARA OBTENER LA INSTANCIA DEL SERVICIO DE MUSICA ***********/

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            musicService = ((MusicService.MusicBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            musicService = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this,
                MusicService.class), mConnection, Context.BIND_AUTO_CREATE);
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

        if(bt_play.
        musicService.startPlaying();
        else{
            musicService.pause();
            bt_play.setVisibility(View.VISIBLE);
            bt_pause.setVisibility(View.INVISIBLE);
        }
    }

    public void nextSong(View view){

        musicService.nextSong();

    }

    public void previousSong(View view){

        musicService.previousSong();

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

        stopService(voiceControlServiceIntent);
        stopService(musicServiceIntent);

        finish();

    }

}
