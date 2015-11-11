package ar.com.klee.marvin.call;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import ar.com.klee.marvin.activities.IncomingCallActivity;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;
import ar.com.klee.marvin.voiceControl.handlers.AgendarContactoHandler;
import ar.com.klee.marvin.voiceControl.handlers.ResponderLlamadaHandler;

/**
 * @author msalerno
 */
public class DefaultCallMode extends CallMode{

    protected DefaultCallMode() {
        super(IncomingCallActivity.class);
    }

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        STTService.getInstance().setIsListening(true);
        final CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();
        commandHandlerManager.setCurrentCommandHandler(new ResponderLlamadaHandler(commandHandlerManager.getTextToSpeech(), commandHandlerManager.getContext(), commandHandlerManager));
        commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "responder llamada")));
    }

    @Override
    public void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d("CALL", "Incoming ended");
        final CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();
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

    @Override
    public void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        long diffInMs = end.getTime() - timeCall;
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        final CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();
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
                    commandHandlerManager.getTextToSpeech().speakText("Llamada finalizada");
                }
            }, 1000);
        }
    }

    @Override
    public void onMissedCall(Context ctx, String number, Date start) {
        Log.d("CALL", "Missed Call");
        final CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();
        if (IncomingCallActivity.isInstanceInitialized()) {
            if (IncomingCallActivity.getInstance().isRejected()) {
                commandHandlerManager.getTextToSpeech().speakText("Llamada rechazada");
                IncomingCallActivity.getInstance().setIsRejected(false);
            }else {
                commandHandlerManager.getTextToSpeech().speakText("Llamada perdida");
                IncomingCallActivity.getInstance().closeActivity();
            }
        }else{
            commandHandlerManager.getTextToSpeech().speakText("Llamada perdida");
        }
    }

    @Override
    public boolean isDefault() {
        return true;
    }
}
