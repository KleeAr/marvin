package ar.com.klee.marvin.voiceControl.handlers.trip;

import android.content.Context;
import android.os.Handler;

import java.io.File;
import java.util.Arrays;

import ar.com.klee.marvin.activities.CallHistoryActivity;
import ar.com.klee.marvin.activities.TripActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class CerrarViajeHandler extends CommandHandler {

    private String mapPath = "/sdcard/MARVIN/trip.png";

    public CerrarViajeHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("cerrar viaje","cerrar","volver"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        TripActivity tripActivity = context.getObject(ACTIVITY, TripActivity.class);

        getTextToSpeech().speakText("Cerrando viaje");

        getCommandHandlerManager().defineActivity(CommandHandlerManager.ACTIVITY_TRIP_HISTORY, getCommandHandlerManager().getMainActivity());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    File photo = new File(mapPath);
                    photo.delete();
                } catch (Exception e) {
                }
            }
        }, 2000);

        tripActivity.finish();

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
