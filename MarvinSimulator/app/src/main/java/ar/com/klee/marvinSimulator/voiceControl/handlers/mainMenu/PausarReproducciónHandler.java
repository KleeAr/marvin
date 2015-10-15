package ar.com.klee.marvinSimulator.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.fragments.MainMenuFragment;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class PausarReproducciónHandler extends CommandHandler {

    public PausarReproducciónHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("pausar música","pausar canción","pausar reproducción","pausar reproductor","detener música","detener canción","detener reproducción","detener reproductor"), textToSpeech, context, commandHandlerManager);
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
