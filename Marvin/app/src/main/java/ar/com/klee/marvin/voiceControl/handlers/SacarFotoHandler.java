package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class SacarFotoHandler extends CommandHandler{

    public SacarFotoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("sacar foto", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Sacando foto");

        context.getObject(ACTIVITY, CameraActivity.class).takePicture();

        context.put(STEP,0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
