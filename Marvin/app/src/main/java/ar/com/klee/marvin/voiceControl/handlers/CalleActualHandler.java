package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class CalleActualHandler extends CommandHandler{

    public CalleActualHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("calle actual",textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext currentContext){
        getTextToSpeech().speakText("Estás en ");

        //TODO: CODIGO PARA OBTENER CALLE ACTUAL

        currentContext.put(STEP, 0);
        return currentContext;
    }
}
