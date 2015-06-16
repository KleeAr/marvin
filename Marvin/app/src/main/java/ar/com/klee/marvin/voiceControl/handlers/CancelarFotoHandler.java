package ar.com.klee.marvin.voiceControl.handlers;

import android.app.Activity;
import android.content.Context;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class CancelarFotoHandler extends CommandHandler{

    public CancelarFotoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("cancelar foto",textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){
        getTextToSpeech().speakText("Cancelando foto");

        CameraActivity cameraActivity = context.get(CAMERA_ACTIVITY, CameraActivity.class);
        cameraActivity.cancel();
        context.put(STEP, 0);
        return context;
    }
}
