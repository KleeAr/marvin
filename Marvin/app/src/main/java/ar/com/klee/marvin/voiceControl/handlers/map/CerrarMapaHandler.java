package ar.com.klee.marvin.voiceControl.handlers.map;

import android.content.Context;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.activities.MapActivity;
import ar.com.klee.marvin.gps.MapFragment;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class CerrarMapaHandler extends CommandHandler {


    public CerrarMapaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("cerrar mapa", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        MapActivity mapActivity = context.getObject(ACTIVITY, MapActivity.class);

        getTextToSpeech().speakText("Cerrando mapa");

        getCommandHandlerManager().defineActivity(CommandHandlerManager.ACTIVITY_MAIN, getCommandHandlerManager().getMainActivity());

        if(MapFragment.isInstanceInitialized())
            MapFragment.getInstance().setSearch(false);

        mapActivity.finish();
        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
