package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class GuardarFotoHandler extends CommandHandler{

    public GuardarFotoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("guardar foto", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Guardando foto");

        context.getObject(ACTIVITY, CameraActivity.class).save();

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
