package ar.com.klee.marvinSimulator.voiceControl.handlers.camera;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.CameraActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class SacarFotoHandler extends CommandHandler {

    public SacarFotoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("sacar foto","tomar foto","fotografiar","tomar fotografía","whisky"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(!getCommandHandlerManager().getIsPhotoTaken()) {
            getTextToSpeech().speakText("Sacando foto");
            context.getObject(ACTIVITY, CameraActivity.class).takePicture();
            getCommandHandlerManager().setIsPhotoTaken(true);
        }else{
            getTextToSpeech().speakText("Debés indicar qué hacer con la foto ya sacada");
        }

        context.put(STEP,0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
