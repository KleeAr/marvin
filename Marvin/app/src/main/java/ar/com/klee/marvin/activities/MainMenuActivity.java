package ar.com.klee.marvin.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.util.StringTokenizer;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.voiceControl.STTService;


public class MainMenuActivity extends ActionBarActivity {

    private Intent voiceControlService;
    private BroadcastReceiver voiceControlReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        initializeSTTService();

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
        voiceControlService = new Intent(this,STTService.class);
        startService(voiceControlService);

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
    }

    // Utilizado por el receptor de parámetros del servicio de comandos de voz
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(voiceControlReceiver);
        super.onStop();
    }

    // Sale de la aplicación deteniendo el servicio
    public void exit(View view){

        stopService(voiceControlService);

        finish();

    }

}
