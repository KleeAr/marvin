package ar.com.klee.marvin.call;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import ar.com.klee.marvin.activities.DefaultIncomingCallActivity;
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

    private static boolean wasRegistered = false;

    public static void setWasRegistered(boolean wasRegistered) {
        CallReceiver.wasRegistered = wasRegistered;
    }

    public static boolean wasRegistered() {
        return wasRegistered;
    }

    protected void onIncomingCallStarted(final Context ctx, final String number, Date start) {

        if(!CommandHandlerManager.isInstanceInitialized()){
            return;
        }
        commandHandlerManager = CommandHandlerManager.getInstance();
        STTService.getInstance().setIsListening(true);
        commandHandlerManager.setCurrentCommandHandler(new ResponderLlamadaHandler(commandHandlerManager.getTextToSpeech(), commandHandlerManager.getContext(), commandHandlerManager));
        commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "responder llamada")));
        startIncomingCallActivity(ctx, number, start, DefaultIncomingCallActivity.class);
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        if(!CommandHandlerManager.isInstanceInitialized()){
            return;
        }

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
            commandHandlerManager.getTextToSpeech().speakText("Llamada finalizada");
        }
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        if(!CommandHandlerManager.isInstanceInitialized()){
            return;
        }

        commandHandlerManager = CommandHandlerManager.getInstance();
        long diffInMs = end.getTime() - timeCall;
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

        if(diffInSec > BUSY) {
            //hay que agregar a la pregunta de si no es contacto la duracion de los segundos. Valor a definir
            if (diffInSec > LONG_CALL && !Contact.isContact(ctx, savedNumber)) {

                STTService.getInstance().setIsListening(true);
                STTService.getInstance().stopListening();
                if (commandHandlerManager.getCurrentActivity() == CommandHandlerManager.ACTIVITY_MAIN)
                    ((MainMenuActivity) commandHandlerManager.getMainActivity()).setButtonsDisabled();
                commandHandlerManager.setCurrentCommandHandler(new AgendarContactoHandler(commandHandlerManager.getTextToSpeech(), commandHandlerManager.getContext(), commandHandlerManager));
                commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "agendar contacto " + savedNumber)));
            }
            else
                commandHandlerManager.getTextToSpeech().speakText("Llamada finalizada");
        }else {
            commandHandlerManager.getTextToSpeech().speakText("Número ocupado");
        }
    }

    protected void onMissedCall(Context ctx, String number, Date start) {
    }

}
