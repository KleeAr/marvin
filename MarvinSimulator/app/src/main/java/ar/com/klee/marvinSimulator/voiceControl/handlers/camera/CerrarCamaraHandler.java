package ar.com.klee.marvinSimulator.voiceControl.handlers.camera;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.CameraActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class CerrarCamaraHandler extends CommandHandler {


    public CerrarCamaraHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("cerrar cámara","volver al menú principal","cerrar"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        CameraActivity cameraActivity = context.getObject(ACTIVITY, CameraActivity.class);

        if(getCommandHandlerManager().getIsPhotoTaken()){
            cameraActivity.cancel();
        }
        getTextToSpeech().speakText("Cerrando cámara");

        getCommandHandlerManager().defineActivity(CommandHandlerManager.ACTIVITY_MAIN, getCommandHandlerManager().getMainActivity());

        cameraActivity.finish();
        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
