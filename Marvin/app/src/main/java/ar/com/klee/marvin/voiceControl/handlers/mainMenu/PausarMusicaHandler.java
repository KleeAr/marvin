package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class PausarMusicaHandler extends CommandHandler {

    public PausarMusicaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("pausar música","pausar canción","pausar reproducción","pausar reproductor","detener música","detener canción","detener reproducción","detener reproductor"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(context.getObject(ACTIVITY, MainMenuActivity.class).getWasPlaying()) {
            getTextToSpeech().speakText("Pausando música");
            context.getObject(ACTIVITY, MainMenuActivity.class).setWasPlaying(false);
        }else
            getTextToSpeech().speakText("La música ya estaba pausada");

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
