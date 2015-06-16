package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class ReproducirMusicaHandler extends CommandHandler{

    public ReproducirMusicaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("reproducir música", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Reproduciendo música");

        //CODIGO PARA REPRUDUCIR MUSICA
        context.put(STEP, 0);
        return context;

    }
}
