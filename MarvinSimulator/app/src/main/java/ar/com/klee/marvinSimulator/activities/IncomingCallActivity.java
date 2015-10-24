package ar.com.klee.marvinSimulator.activities;

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
import android.widget.Button;
import android.widget.TextView;

import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;

/**
 * Clase para administrar las llamadas entrantes
 */
public class IncomingCallActivity extends Activity {

    private Button btnAceptar;
    private Button btnRechazar;
    private TextView textView = null; //Texto para mostrar el número entrante
    private CommandHandlerManager commandHandlerManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(ar.com.klee.marvinSimulator.R.layout.activity_incoming_call);

        commandHandlerManager = CommandHandlerManager.getInstance();

        //Declaracion de los botones
        btnAceptar = (Button)findViewById(ar.com.klee.marvinSimulator.R.id.btnAceptar); //se puede cambiar por imagenes
        btnRechazar = (Button)findViewById(ar.com.klee.marvinSimulator.R.id.btnRechazar); //se puede cambiar por imagenes

        //Obtencion del parametro pasado de la Activity llamadora
        Bundle bundle = getIntent().getExtras();
        String dato=bundle.getString("number");


        //Establecer el los datos en la interfaz
        Typeface fontBold = Typeface.createFromAsset(getAssets(),"Bariol_Bold.otf");
        textView = (TextView) findViewById(ar.com.klee.marvinSimulator.R.id.toCall);
        String contact = getContactName(getApplicationContext(), dato);
        textView.setText("Llamada entrante:"+"\n"+ contact);
        textView.setTypeface(fontBold);

        try{

            Integer.parseInt(contact);

            String contactWithSpaces = "";

            int i=0;

            while(i < contact.length()){
                contactWithSpaces += contact.charAt(i) + " ";
                i++;
            }

            contact = contactWithSpaces;

        }catch (NumberFormatException e){

        }

        finally {
            commandHandlerManager.getTextToSpeech().speakText("Tenés una llamada de " + contact + ". Indicá atender o rechazar");
            commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_INCOMING_CALL, this);

            btnAceptar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "atender")));

                }
            });

            btnRechazar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "rechazar")));

                }
            });
        }

    }

    public void acceptCall(){
        Context mContext = getApplicationContext();
        Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonUp.putExtra(Intent.EXTRA_KEY_EVENT,
                new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
        mContext.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");

        //activar el altavoz
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_IN_CALL);
        am.setSpeakerphoneOn(true);
    }

    public void rejectCall(){
        Context mContext = getApplicationContext();
        Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON); buttonDown.putExtra(Intent.EXTRA_KEY_EVENT,
                new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
        mContext.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");
        finish(); //Regresa a la activity anterior
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
