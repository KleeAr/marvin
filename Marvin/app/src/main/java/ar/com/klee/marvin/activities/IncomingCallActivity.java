package ar.com.klee.marvin.activities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.call.ContactUtils;
import ar.com.klee.marvin.configuration.UserConfig;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;

/**
 * Clase para administrar las llamadas entrantes
 */
public abstract class IncomingCallActivity extends Activity {

    protected Button btnAceptar;
    protected Button btnRechazar;
    protected TextView textView = null; //Texto para mostrar el número entrante
    protected CommandHandlerManager commandHandlerManager;
    protected Activity previousActivity;
    protected int previousActivityId;

    protected ViewGroup mTopView;
    protected WindowManager wm;

    protected boolean isRejected = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(CommandHandlerManager.isInstanceInitialized())
            commandHandlerManager = CommandHandlerManager.getInstance();
        else
            finish();

        try {
            previousActivity = commandHandlerManager.getActivity();
            previousActivityId = commandHandlerManager.getCurrentActivity();
        }catch(Exception e){
            e.printStackTrace();
        }

        if(UserConfig.getInstance().getOrientation() == UserConfig.ORIENTATION_PORTRAIT)
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        //setContentView(R.layout.activity_incoming_call);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        mTopView = (ViewGroup) this.getLayoutInflater().inflate(R.layout.activity_incoming_call, null);

        //Declaracion de los botones
        btnAceptar = (Button)findViewById(R.id.btnAceptar); //se puede cambiar por imagenes
        btnRechazar = (Button)findViewById(R.id.btnRechazar); //se puede cambiar por imagenes

        //Obtencion del parametro pasado de la Activity llamadora
        Bundle bundle = getIntent().getExtras();
        String dato=bundle.getString("number");


        //Establecer el los datos en la interfaz
        Typeface fontBold = Typeface.createFromAsset(getAssets(), "Bariol_Bold.otf");
        textView = (TextView) mTopView.findViewById(R.id.toCall);
        String contact = ContactUtils.getContactName(getApplicationContext(), dato);
        textView.setText("Llamada entrante:" + "\n" + contact);
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

            btnAceptar.setClickable(true);
            btnAceptar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    STTService.getInstance().stopListening();
                    commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "atender")));

                }
            });

            btnRechazar.setClickable(true);
            btnRechazar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "rechazar")));

                }
            });

            getWindow().setAttributes(params);
            wm.addView(mTopView, params);

        }

    }

    public abstract void acceptCall();

    public abstract void rejectCall();


    public boolean isRejected() {
        return isRejected;
    }

    public void setIsRejected(boolean isRejected) {
        this.isRejected = isRejected;
    }
}
