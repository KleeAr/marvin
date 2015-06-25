package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class ReproducirMusicaHandler extends CommandHandler{

    public ReproducirMusicaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("reproducir música", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(context.getObject(ACTIVITY, MainMenuActivity.class).isListEmpty()){
            getTextToSpeech().speakText("No se han encontrado canciones en el dispositivo");
        }else {
            if (!context.getObject(ACTIVITY, MainMenuActivity.class).getWasPlaying()) {
                getTextToSpeech().speakText("Reproduciendo música");
                context.getObject(ACTIVITY, MainMenuActivity.class).setWasPlaying(true);
            } else
                getTextToSpeech().speakText("La música ya estaba sonando");
        }

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
