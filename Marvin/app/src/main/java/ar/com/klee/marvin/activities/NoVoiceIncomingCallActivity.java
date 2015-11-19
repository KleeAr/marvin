package ar.com.klee.marvin.activities;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Method;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.bluetooth.BluetoothConstants;
import ar.com.klee.marvin.bluetooth.BluetoothHandler;
import ar.com.klee.marvin.bluetooth.BluetoothService;

public class NoVoiceIncomingCallActivity extends FragmentActivity {

    static private NoVoiceIncomingCallActivity instance;
    static private Boolean onCall = false;

    private BluetoothHandler handler = new BluetoothHandler(this) {
        @Override
        protected void onMessageRead(String readMessage) {
            if(BluetoothConstants.ACCEPT_CALL.equals(readMessage)) {
                acceptCall();
            } else if (BluetoothConstants.REJECT_CALL.equals(readMessage)) {
                rejectCall();
            }
        }

        @Override
        protected void onMessageWrote(String writeMessage) {

        }
    };

    public static Boolean getOnCall() {
        return onCall;
    }

    public static void setOnCall(Boolean onCall) {
        NoVoiceIncomingCallActivity.onCall = onCall;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
        BluetoothService.getInstance().setmHandler(handler);
        Button btnAceptar = (Button) findViewById(R.id.btnAceptar);
        Button btnRechazar = (Button) findViewById(R.id.btnRechazar);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptCall();
            }
        });

        btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rejectCall();
            }
        });

        instance = this;
    }

    public void acceptCall(){
        onAcceptCall();
        onCall = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
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
        onRejectCall();
        onCall = false;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
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
            Log.e("NoVoiceCallActivity", "FATAL ERROR: could not connect to telephony subsystem");
            Log.e("NoVoiceCallActivity", "Exception object: " + e);
        }
    }

    public static NoVoiceIncomingCallActivity getInstance() {
        return instance;
    }

    public void hangUp() {
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d("NoVoiceIncomingCallActivity","PhoneStateReceiver **" + ex.toString());
        }
        onCall = false;
    }
}