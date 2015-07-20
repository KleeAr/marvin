package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class DesactivarReproduccionAleatoriaHandler extends CommandHandler {

    public DesactivarReproduccionAleatoriaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("desactivar reproducción aleatoria", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(context.getObject(ACTIVITY, MainMenuActivity.class).setRandom(false))
            getTextToSpeech().speakText("Reproducción aleatoria desactivada");
        else
            getTextToSpeech().speakText("La reproducción aleatoria ya estaba desactivada");

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
