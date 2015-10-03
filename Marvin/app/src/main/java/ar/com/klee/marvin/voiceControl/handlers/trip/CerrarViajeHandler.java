package ar.com.klee.marvin.voiceControl.handlers.trip;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.CallHistoryActivity;
import ar.com.klee.marvin.activities.TripActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class CerrarViajeHandler extends CommandHandler {


    public CerrarViajeHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("cerrar viaje","cerrar","volver"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        TripActivity tripActivity = context.getObject(ACTIVITY, TripActivity.class);

        getTextToSpeech().speakText("Cerrando viaje");

        getCommandHandlerManager().defineActivity(CommandHandlerManager.ACTIVITY_TRIP_HISTORY, getCommandHandlerManager().getMainActivity());

        tripActivity.finish();

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
