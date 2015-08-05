package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class DetenerReproduccionHandler extends CommandHandler {

    public DetenerReproduccionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("detener reproducción", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Deteniendo reproducción");

        //CODIGO PARA DETENER MUSICA

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}