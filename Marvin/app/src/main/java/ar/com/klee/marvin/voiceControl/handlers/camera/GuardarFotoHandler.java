package ar.com.klee.marvin.voiceControl.handlers.camera;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class GuardarFotoHandler extends CommandHandler {

    public GuardarFotoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("guardar foto"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(getCommandHandlerManager().getIsPhotoTaken()) {
            getTextToSpeech().speakText("Guardando foto");
            context.getObject(ACTIVITY, CameraActivity.class).save();
        }else{
            getTextToSpeech().speakText("Debés sacar una foto antes");
        }

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
