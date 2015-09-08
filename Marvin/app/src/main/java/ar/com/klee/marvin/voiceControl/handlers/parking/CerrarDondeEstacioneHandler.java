package ar.com.klee.marvin.voiceControl.handlers.parking;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.TripActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class CerrarDondeEstacioneHandler extends CommandHandler {


    public CerrarDondeEstacioneHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("cerrar dónde estacioné"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Cerrando dónde estacioné");

        getCommandHandlerManager().defineActivity(CommandHandlerManager.ACTIVITY_MAIN, getCommandHandlerManager().getMainActivity());

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
