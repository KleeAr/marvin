package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class SiguienteCancionHandler extends CommandHandler{


    public SiguienteCancionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("siguiente canción", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Reproduciendo");

        //CODIGO PARA AVANZAR A LA CANCIÓN SIGUIENTE
        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
