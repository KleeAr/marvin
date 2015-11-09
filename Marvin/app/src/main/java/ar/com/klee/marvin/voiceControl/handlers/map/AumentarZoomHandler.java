package ar.com.klee.marvin.voiceControl.handlers.map;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.MapActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class AumentarZoomHandler extends CommandHandler {

    public AumentarZoomHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("aumentar zoom","incrementar zoom","acercar zoom","acercar","aumentar sum","incrementar sum"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Aumentando zoom. El valor actual es: " + ((MapActivity)getCommandHandlerManager().getActivity()).zoomIn());

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
