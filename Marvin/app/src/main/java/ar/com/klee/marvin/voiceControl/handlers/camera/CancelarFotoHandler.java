package ar.com.klee.marvin.voiceControl.handlers.camera;

import android.content.Context;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class CancelarFotoHandler extends CommandHandler {

    public CancelarFotoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("cancelar foto",textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){
        getTextToSpeech().speakText("Cancelando foto");

        CameraActivity cameraActivity = context.getObject(ACTIVITY, CameraActivity.class);
        cameraActivity.cancel();
        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
