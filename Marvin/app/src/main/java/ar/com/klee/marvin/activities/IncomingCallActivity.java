package ar.com.klee.marvin.activities;

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

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.call.ContactUtils;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;

/**
 * Clase para administrar las llamadas entrantes
 */
public abstract class IncomingCallActivity extends Activity {

    private Button btnAceptar;
    private Button btnRechazar;
    private TextView textView = null; //Texto para mostrar el número entrante
    private CommandHandlerManager commandHandlerManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_incoming_call);

        commandHandlerManager = CommandHandlerManager.getInstance();

        //Declaracion de los botones
        btnAceptar = (Button)findViewById(R.id.btnAceptar); //se puede cambiar por imagenes
        btnRechazar = (Button)findViewById(R.id.btnRechazar); //se puede cambiar por imagenes

        //Obtencion del parametro pasado de la Activity llamadora
        Bundle bundle = getIntent().getExtras();
        String dato=bundle.getString("number");


        //Establecer el los datos en la interfaz
        Typeface fontBold = Typeface.createFromAsset(getAssets(),"Bariol_Bold.otf");
        textView = (TextView) findViewById(R.id.toCall);
        String contact = ContactUtils.getContactName(getApplicationContext(), dato);
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

    public abstract void acceptCall();

    public abstract void rejectCall();

}
