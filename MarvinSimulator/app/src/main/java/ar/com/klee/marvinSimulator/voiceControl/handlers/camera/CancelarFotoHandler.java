package ar.com.klee.marvinSimulator.voiceControl.handlers.camera;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.CameraActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class CancelarFotoHandler extends CommandHandler {

    public CancelarFotoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("cancelar foto","volver","refrescar"),textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(getCommandHandlerManager().getIsPhotoTaken()) {
            getTextToSpeech().speakText("Cancelando foto");
            context.getObject(ACTIVITY, CameraActivity.class).cancel();
            getCommandHandlerManager().setIsPhotoTaken(false);
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
