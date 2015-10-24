package ar.com.klee.marvin.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;

import java.lang.reflect.Method;
import android.os.Handler;

import ar.com.klee.marvin.voiceControl.STTService;

/**
 * @author msalerno
 */
public class DefaultIncomingCallActivity extends IncomingCallActivity {

    public static DefaultIncomingCallActivity instance;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        instance = this;
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

    public static DefaultIncomingCallActivity getInstance(){
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

}
