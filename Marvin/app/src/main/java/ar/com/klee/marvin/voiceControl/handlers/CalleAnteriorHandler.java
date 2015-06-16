package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class CalleAnteriorHandler extends CommandHandler{

    public CalleAnteriorHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("calle anterior",textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext currentContext){
        getTextToSpeech().speakText("Estás en ");

        //TODO CODIGO PARA OBTENER CALLE ANTERIOR
        currentContext.put(STEP, 0);
        return currentContext;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
