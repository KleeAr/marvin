package kleear.phoneapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class CallListener extends BroadcastReceiver {

    private ContactBean incomingCall;


    @Override
    public void onReceive(Context context, Intent intent) {

        int lastState = TelephonyManager.CALL_STATE_IDLE;

        Bundle extras = intent.getExtras();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
           //Llamada entrante
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                lastState = TelephonyManager.CALL_STATE_RINGING;

                incomingCall = new ContactBean();
                incomingCall.setPhoneNo(extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
                //Toast.makeText(context, phoneNumber, Toast.LENGTH_LONG).show();
             context.startActivity(new Intent(context, IncomingCall.class).putExtra("number", incomingCall.getPhoneNo())
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            }

            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                //Se activa el altavoz
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                am.setMode(AudioManager.MODE_IN_CALL);
                am.setSpeakerphoneOn(true);

                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Llamada entrante contestada
                }
                lastState = TelephonyManager.CALL_STATE_OFFHOOK;

            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //llamada perdida
                }
                if(lastState == TelephonyManager.CALL_STATE_OFFHOOK){
                    //llamada finalizada
                }

                    lastState = TelephonyManager.CALL_STATE_IDLE;
            }


        }
    }

}
