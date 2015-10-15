package ar.com.klee.marvinSimulator.voiceControl.handlers.map;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.MapActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class UbicacionActualHandler extends CommandHandler {

    public UbicacionActualHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("ubicación actual","localización actual"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Mostrando ubicación actual");

        ((MapActivity)getCommandHandlerManager().getActivity()).currentLocation();

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
