package kleear.phoneapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Clase para administrar las llamadas entrantes
 */
public class IncomingCall extends Activity {
    private Button btnAceptar;
    private Button btnRechazar;
    TextView textView = null; //Texto para mostrar el número entrante

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_incoming_call);

        //Declaracion de los botones
        btnAceptar = (Button)findViewById(R.id.btnAceptar); //se puede cambiar por imagenes
        btnRechazar = (Button)findViewById(R.id.btnRechazar); //se puede cambiar por imagenes

        //Obtencion del parametro pasado de la Activity llamadora
        Bundle bundle = getIntent().getExtras();
        String dato=bundle.getString("number");


        //Establecer el los datos en la interfaz
        Typeface fontBold = Typeface.createFromAsset(getAssets(),"Bariol_Bold.otf");
        textView = (TextView) findViewById(R.id.toCall);
        textView.setText("Llamada entrante:"+"\n"+ getContactName(getApplicationContext(), dato));
        textView.setTypeface(fontBold);



        btnAceptar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Acciones
                Context mContext = getApplicationContext();
                Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
                buttonUp.putExtra(Intent.EXTRA_KEY_EVENT,
                        new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                mContext.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");

                //activar el altavoz
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.setMode(AudioManager.MODE_IN_CALL);
                am.setSpeakerphoneOn(true);

                finish(); //Regresa a la activity anterior

            }
        });

        btnRechazar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //Acciones
                Context mContext = getApplicationContext();
                Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON); buttonDown.putExtra(Intent.EXTRA_KEY_EVENT,
                        new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
                mContext.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");
                finish(); //Regresa a la activity anterior


            }
        });


    }

    /*
    Funcion que recibe como parametro un numero de telefono
    Retorna el nombre del contacto si esta registrado, sino devuelve el numero de telefono
     */
    public String getContactName(Context context, final String phoneNumber){
        Uri uri;
        String[] projection;

        if (Build.VERSION.SDK_INT >= 5)
        {
            uri = Uri.parse("content://com.android.contacts/phone_lookup");
            projection = new String[] { "display_name" };
        }
        else
        {
            uri = Uri.parse("content://contacts/phones/filter");
            projection = new String[] { "name" };
        }

        uri = Uri.withAppendedPath(uri, Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        String contactName = phoneNumber;

        if (cursor.moveToFirst())
        {
            contactName = cursor.getString(0);

        }

        cursor.close();

        return contactName;
    }



}


