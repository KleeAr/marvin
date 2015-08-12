package ar.com.klee.marvin.voiceControl.handlers.tripHistory;

import android.content.Context;

import ar.com.klee.marvin.activities.MapActivity;
import ar.com.klee.marvin.activities.TripHistoryActivity;
import ar.com.klee.marvin.gps.MapFragment;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class CerrarHistorialDeViajesHandler extends CommandHandler {


    public CerrarHistorialDeViajesHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("cerrar historial de viajes", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        TripHistoryActivity tripHistoryActivity = context.getObject(ACTIVITY, TripHistoryActivity.class);

        getTextToSpeech().speakText("Cerrando historial de viajes");

        getCommandHandlerManager().defineActivity(CommandHandlerManager.ACTIVITY_MAIN, getCommandHandlerManager().getMainActivity());

        tripHistoryActivity.finish();
        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
