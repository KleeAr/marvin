package ar.com.klee.marvin.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Method;

import ar.com.klee.marvin.Command;
import ar.com.klee.marvin.R;
import ar.com.klee.marvin.configuration.UserConfig;
import ar.com.klee.marvin.fragments.MainMenuFragment;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;

/**
 * Clase para administrar las llamadas entrantes
 */
public class IncomingCallActivity extends Activity {

    private Button btnAceptar;
    private Button btnRechazar;
    private TextView textView = null; //Texto para mostrar el número entrante
    private CommandHandlerManager commandHandlerManager;
    private Activity previousActivity;
    private int previousActivityId;

    public static IncomingCallActivity instance;

    private ViewGroup mTopView;
    private WindowManager wm;

    private boolean isRejected = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

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
        btnAceptar = (Button)mTopView.findViewById(R.id.btnAceptar); //se puede cambiar por imagenes
        btnRechazar = (Button)mTopView.findViewById(R.id.btnRechazar); //se puede cambiar por imagenes

        //Obtencion del parametro pasado de la Activity llamadora
        Bundle bundle = getIntent().getExtras();
        String dato=bundle.getString("number");

        //Establecer el los datos en la interfaz
        Typeface fontBold = Typeface.createFromAsset(getAssets(), "Bariol_Bold.otf");
        textView = (TextView) mTopView.findViewById(R.id.toCall);
        String contact = getContactName(getApplicationContext(), dato);
        textView.setText("Llamada entrante:" + "\n" + contact);
        textView.setTypeface(fontBold);

        try{

            Integer.parseInt(contact);

            String contactWithSpaces = "";

            int i=0;

            while(i < contact.length()){
                if(i%2 == 1)
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
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                wm.removeView(mTopView);
                instance = null;
                commandHandlerManager.setNullCommand();
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.defineActivity(previousActivityId,previousActivity);
                Log.d("CALL", "accept");
                finish();
            }
        }, 1000);

    }

    public void rejectCall(){

        isRejected = true;

        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("IncomingCallActivity", "FATAL ERROR: could not connect to telephony subsystem");
            Log.e("IncomingCallActivity", "Exception object: " + e);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                instance = null;
                commandHandlerManager.setNullCommand();
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.defineActivity(previousActivityId, previousActivity);
                wm.removeView(mTopView);
                Log.d("CALL", "reject");
                finish(); //Regresa a la activity anterior
            }
        }, 1000);

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

    public static IncomingCallActivity getInstance(){
        return instance;
    }

    public static  boolean isInstanceInitialized(){
        if(instance != null)
            return true;
        else
            return false;
    }

    public void closeActivity(){
        commandHandlerManager.defineActivity(previousActivityId,previousActivity);
        commandHandlerManager.setNullCommand();
        STTService.getInstance().setIsListening(false);
        wm.removeView(mTopView);
        isRejected = false;
        Log.d("CALL", "close");
        finish();
    }

    public boolean isRejected() {
        return isRejected;
    }

    public void setIsRejected(boolean isRejected) {
        this.isRejected = isRejected;
    }
}
