package ar.com.klee.marvinSimulator.voiceControl.handlers.map;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.MapActivity;
import ar.com.klee.marvinSimulator.gps.MapFragment;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class CerrarMapaHandler extends CommandHandler {


    public CerrarMapaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("cerrar mapa","cerrar","volver al menú principal"), textToSpeech, context, commandHandlerManager);
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
