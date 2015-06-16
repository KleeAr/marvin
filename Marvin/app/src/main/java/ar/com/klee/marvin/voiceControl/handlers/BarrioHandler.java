package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class BarrioHandler extends CommandHandler{

    public BarrioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("barrio", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Estás en ");

        //CODIGO PARA OBTENER BARRIO
        context.put(STEP, 0);
        return context;

    }
}
