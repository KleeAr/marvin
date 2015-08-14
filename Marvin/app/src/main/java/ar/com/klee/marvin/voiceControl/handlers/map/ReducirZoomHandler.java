package ar.com.klee.marvin.voiceControl.handlers.map;

import android.content.Context;
import android.media.AudioManager;

import ar.com.klee.marvin.activities.MapActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class ReducirZoomHandler extends CommandHandler {

    public ReducirZoomHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("reducir zoom", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Reduciendo zoom. El valor actual es: " + ((MapActivity)getCommandHandlerManager().getActivity()).zoomOut());

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
