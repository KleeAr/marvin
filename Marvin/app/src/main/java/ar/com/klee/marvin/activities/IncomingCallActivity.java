package ar.com.klee.marvin.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Method;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.call.ContactUtils;
import ar.com.klee.marvin.configuration.UserConfig;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

/**
 * Clase para administrar las llamadas entrantes
 */
public class IncomingCallActivity extends Activity {

    private Button btnAceptar;
    private Button btnRechazar;
    private TextView textView = null; //Texto para mostrar el número entrante
    protected CommandHandlerManager commandHandlerManager;
    protected Activity previousActivity;
    protected int previousActivityId;

    public static IncomingCallActivity instance;

    protected ViewGroup mTopView;
    protected WindowManager wm;

    protected boolean isRejected = false;

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
        String contact = ContactUtils.getContactName(getApplicationContext(), dato);
        textView.setText("Llamada entrante:" + "\n" + contact);
        textView.setTypeface(fontBold);

        contact = ContactUtils.getContactSpeechName(contact);

        commandHandlerManager.getTextToSpeech().speakText("Tenés una llamada de " + contact + ". Indicá atender o rechazar");
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_INCOMING_CALL, this);

        btnAceptar.setClickable(true);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                STTService.getInstance().stopListening();
                Log.i(getTag(), "Answering call. CommandHandlerManager = " + commandHandlerManager.toString());
                CommandHandlerContext newContext = commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "atender");
                Log.i(getTag(), "Context created: " + newContext.toString() );
                commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(newContext));
            }
        });

        btnRechazar.setClickable(true);
        btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommandHandlerContext context = commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "rechazar");
                commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(context));
            }
        });

        getWindow().setAttributes(params);
        wm.addView(mTopView, params);



    }

    protected String getTag() {
        return "DefaultIncomingCallActivity";
    }

    public void acceptCall(){
        onAcceptCall();
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

    protected void onAcceptCall() {
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
        isRejected = true;
        onRejectCall();
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

    protected void onRejectCall() {
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
