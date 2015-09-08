package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class ReproducirRadioHandler extends CommandHandler {

    public ReproducirRadioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("reproducir radio"), textToSpeech, context, commandHandlerManager);
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
