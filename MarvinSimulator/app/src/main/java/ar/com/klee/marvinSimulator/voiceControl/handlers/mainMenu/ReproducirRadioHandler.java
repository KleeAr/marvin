package ar.com.klee.marvinSimulator.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class ReproducirRadioHandler extends CommandHandler {

    public ReproducirRadioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("reproducir radio","escuchar radio"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if (!context.getObject(ACTIVITY, MainMenuActivity.class).getWasPlaying()) {
            getTextToSpeech().speakText("Reproduciendo radio");
            context.getObject(ACTIVITY, MainMenuActivity.class).setWasPlaying(true);
            if(!context.getObject(ACTIVITY, MainMenuActivity.class).getMusicService().getIsRadio())
                context.getObject(ACTIVITY, MainMenuActivity.class).radioMusic();
        } else if(!context.getObject(ACTIVITY, MainMenuActivity.class).getMusicService().getIsRadio()) {
            getTextToSpeech().speakText("Reproduciendo radio");
            context.getObject(ACTIVITY, MainMenuActivity.class).setWasPlaying(true);
            context.getObject(ACTIVITY, MainMenuActivity.class).radioMusic();
        } else
            getTextToSpeech().speakText("La radio ya estaba sonando");


        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
