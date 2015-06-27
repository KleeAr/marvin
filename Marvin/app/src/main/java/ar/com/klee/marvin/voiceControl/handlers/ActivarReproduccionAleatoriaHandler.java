package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class ActivarReproduccionAleatoriaHandler extends CommandHandler{

    public ActivarReproduccionAleatoriaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("activar reproducción aleatoria", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(context.getObject(ACTIVITY, MainMenuActivity.class).setRandom(true))
            getTextToSpeech().speakText("Reproducción aleatoria activada");
        else
            getTextToSpeech().speakText("La reproducción aleatoria ya estaba activada");

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
