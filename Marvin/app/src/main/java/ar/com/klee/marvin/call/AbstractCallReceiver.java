package ar.com.klee.marvin.call;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;

import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;

/**
 * @author msalerno
 */
public abstract class AbstractCallReceiver extends BroadcastReceiver {

    protected static boolean isIncoming;//variable para controlar si la llamada es recibida por el dispositivo
    protected static int lastState = TelephonyManager.CALL_STATE_IDLE;
    protected static Date callStartTime;

    protected String savedNumber;
    protected long timeCall; //calculo los segundos hablados en la llamada

    protected final int BUSY = 10;
    protected final int LONG_CALL = 0;

    protected CommandHandlerManager commandHandlerManager;

    public void onReceive(Context context, Intent intent) {
        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        int state = 0;
        if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            state = TelephonyManager.CALL_STATE_IDLE;
        } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            state = TelephonyManager.CALL_STATE_OFFHOOK;
        } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            state = TelephonyManager.CALL_STATE_RINGING;
        }

        onCallStateChanged(context, state, number);
    }


    protected abstract void onIncomingCallStarted(final Context ctx, final String number, Date start);

    protected abstract void onIncomingCallEnded(Context ctx, String number, Date start, Date end);

    protected abstract void onOutgoingCallEnded(Context ctx, String number, Date start, Date end);

    protected void onMissedCall(Context ctx, String number, Date start) {
    }

    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No se produjeron cambios
            return;
        }

        if(!STTService.isInstanceInitialized())
            return;

        switch (state) {

            case TelephonyManager.CALL_STATE_RINGING:
                Log.d("CALL", "State - Ringing");
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.d("CALL","State - Offhook");
                //Transicion de ringing->offhook
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                am.setMode(AudioManager.MODE_IN_CALL);
                am.setSpeakerphoneOn(true);//se activa el altavoz al iniciar la llamada o al recibirla

                //Si se quiere poner el telefono en silencio deberia ser implemntada por voz
                //am.setMicrophoneMute(true); //pone el microfono en mute

                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    savedNumber = CallDriver.getInstance().getLastOutgoingCallNumber();
                    callStartTime = new Date();
                    timeCall = callStartTime.getTime();
                    //llamada realizada desde el dispositivo
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                Log.d("CALL","State - Idle");
                AudioManager am2 = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                am2.setMode(AudioManager.MODE_NORMAL);
                am2.setSpeakerphoneOn(false);
                //Telefono en sin uso- esto puede ser porque termino una llamada o no. Depende del estado previo.
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Sono pero no contesto - llamada perdida
                    onMissedCall(context, number, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }

                break;
        }
        lastState = state;
    }

    protected void startIncomingCallActivity(final Context ctx, final String number, Date start, final Class<? extends Activity> activity) {
        //Hay que crear un nuevo hilo y esperar unos instantes para superponer el activity
        Thread thread = new Thread() {
            private int sleepTime = 200;

            @Override
            public void run() {
                super.run();
                try {
                    int wait_Time = 0;

                    while (wait_Time < sleepTime) {
                        sleep(200);
                        wait_Time += 100;
                    }
                } catch (Exception e) {

                    e.printStackTrace();

                } finally {
                }
                Intent incomingIntent = new Intent(ctx, activity);
                incomingIntent.putExtra("number",number);
                incomingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(incomingIntent);

            }
        };
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.run();

        //lanza el activity para aceptar o rechazar la llamada

        timeCall = start.getTime();
    }
}
