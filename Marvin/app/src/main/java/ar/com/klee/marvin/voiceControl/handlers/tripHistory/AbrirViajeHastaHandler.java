package ar.com.klee.marvin.voiceControl.handlers.tripHistory;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class AbrirViajeHastaHandler extends CommandHandler {

    public AbrirViajeHastaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("abrir último viaje"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        boolean result = ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).checkTripExistence("last");

        if(result) {
            getTextToSpeech().speakText("Abriendo el último viaje");
            ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).openTrip("last");
        }else
            getTextToSpeech().speakText("No hay viajes guardados");

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
    }
}
