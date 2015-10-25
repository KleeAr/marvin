package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.fragments.MainMenuFragment;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class PausarReproducciónHandler extends CommandHandler {

    public PausarReproducciónHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("pausar música","pausar canción","pausar reproducción","pausar reproductor","detener música","detener canción","detener reproducción","detener reproductor","pausa música","pausa canción","pausa reproducción","pausa reproductor"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(context.getObject(ACTIVITY, MainMenuActivity.class).getWasPlaying()) {
            getTextToSpeech().speakText("Pausando reproducción");
            context.getObject(ACTIVITY, MainMenuActivity.class).setWasPlaying(false);
            MainMenuFragment.tv_song.setText("Reproducción pausada");
            MainMenuFragment.tv_artist.setText("");
        }else
            getTextToSpeech().speakText("La reproducción ya estaba pausada");

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
