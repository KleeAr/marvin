package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class CerrarCamaraHandler extends CommandHandler{


    public CerrarCamaraHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("cerrar cámara", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){
        getTextToSpeech().speakText("Cerrando cámara");

        getCommandHandlerManager().defineActivity(CommandHandlerManager.ACTIVITY_MAIN, getCommandHandlerManager().getMainActivity());

        CameraActivity cameraActivity = context.getObject(ACTIVITY, CameraActivity.class);
        cameraActivity.finish();
        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
