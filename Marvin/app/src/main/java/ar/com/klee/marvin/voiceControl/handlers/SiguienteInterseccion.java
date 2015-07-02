package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class SiguienteInterseccion extends CommandHandler{

    public SiguienteInterseccion(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("siguiente intersección", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Estás en ");

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
