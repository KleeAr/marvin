package ar.com.klee.marvin.call;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import ar.com.klee.marvin.activities.DefaultIncomingCallActivity;
import ar.com.klee.marvin.activities.IncomingCallActivity;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;
import ar.com.klee.marvin.voiceControl.handlers.AgendarContactoHandler;
import ar.com.klee.marvin.voiceControl.handlers.ResponderLlamadaHandler;

/*
Servicio que se activa cuando se produce un cambio en el estado del telefono
Puede ser por una llamada entrante, o una realizada por la aplicacion
 */
public class CallReceiver extends AbstractCallReceiver {

    protected void onIncomingCallStarted(final Context ctx, final String number, Date start) {

        STTService.getInstance().setIsListening(true);
        commandHandlerManager.setCurrentCommandHandler(new ResponderLlamadaHandler(commandHandlerManager.getTextToSpeech(), commandHandlerManager.getContext(), commandHandlerManager));
        commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "responder llamada")));
        startIncomingCallActivity(ctx, number, start, DefaultIncomingCallActivity.class);
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        if(!CommandHandlerManager.isInstanceInitialized()){
            return;
        }

        Log.d("CALL", "Incoming ended");

        commandHandlerManager = CommandHandlerManager.getInstance();
        long diffInMs = end.getTime() - timeCall;
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        //hay que agregar a la pregunta de si no es contacto la duracion de los segundos. Valor a definir
        if (diffInSec > LONG_CALL && !Contact.isContact(ctx, savedNumber)) {

            STTService.getInstance().setIsListening(true);
            STTService.getInstance().stopListening();
            if (commandHandlerManager.getCurrentActivity() == CommandHandlerManager.ACTIVITY_MAIN)
                ((MainMenuActivity) commandHandlerManager.getMainActivity()).setButtonsDisabled();
            commandHandlerManager.setCurrentCommandHandler(new AgendarContactoHandler(commandHandlerManager.getTextToSpeech(), commandHandlerManager.getContext(), commandHandlerManager));
            commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "agendar contacto " + savedNumber)));

        }else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    commandHandlerManager.getTextToSpeech().speakText("Llamada finalizada");
                }
            }, 1000);
        }
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        if(!CommandHandlerManager.isInstanceInitialized()){
            return;
        }

        commandHandlerManager = CommandHandlerManager.getInstance();
        long diffInMs = end.getTime() - timeCall;
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

        Log.d("CALL", "Outgoing ended");

        if(diffInSec > BUSY) {
            //hay que agregar a la pregunta de si no es contacto la duracion de los segundos. Valor a definir
            if (diffInSec > LONG_CALL && !Contact.isContact(ctx, savedNumber)) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        STTService.getInstance().setIsListening(true);
                        STTService.getInstance().stopListening();
                        if (commandHandlerManager.getCurrentActivity() == CommandHandlerManager.ACTIVITY_MAIN)
                            ((MainMenuActivity) commandHandlerManager.getMainActivity()).setButtonsDisabled();
                        commandHandlerManager.setCurrentCommandHandler(new AgendarContactoHandler(commandHandlerManager.getTextToSpeech(), commandHandlerManager.getContext(), commandHandlerManager));
                        commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "agendar contacto " + savedNumber)));
                    }
                }, 1000);
            }
            else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        commandHandlerManager.getTextToSpeech().speakText("Llamada finalizada");
                    }
                }, 1000);
            }
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    commandHandlerManager.getTextToSpeech().speakText("Número ocupado");
                }
            }, 1000);
        }
    }

    protected void onMissedCall(Context ctx, String number, Date start) {

        Log.d("CALL", "Missed Call");

        if (DefaultIncomingCallActivity.isInstanceInitialized()) {
            if (DefaultIncomingCallActivity.getInstance().isRejected()) {
                commandHandlerManager.getTextToSpeech().speakText("Llamada rechazada");
                DefaultIncomingCallActivity.getInstance().setIsRejected(false);
            }else {
                commandHandlerManager.getTextToSpeech().speakText("Llamada perdida");
                DefaultIncomingCallActivity.getInstance().closeActivity();
            }
        }else{
            commandHandlerManager.getTextToSpeech().speakText("Llamada perdida");
        }

    }

    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No se produjeron cambios
            return;
        }

        switch (state) {

            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
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


}
