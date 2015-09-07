package ar.com.klee.marvin.voiceControl.handlers.places;

import android.content.Context;

import ar.com.klee.marvin.activities.TripActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class CerrarMisSitiosHandler extends CommandHandler {


    public CerrarMisSitiosHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("cerrar mis sitios", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Cerrando mis sitios");

        getCommandHandlerManager().defineActivity(CommandHandlerManager.ACTIVITY_MAIN, getCommandHandlerManager().getMainActivity());

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
