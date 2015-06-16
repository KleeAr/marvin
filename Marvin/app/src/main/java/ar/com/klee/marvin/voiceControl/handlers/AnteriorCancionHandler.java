package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class AnteriorCancionHandler extends CommandHandler{


    public AnteriorCancionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager){
        super("anterior canción", textToSpeech, context, commandHandlerManager);

    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Reproduciendo");

        //CODIGO PARA VOLVER A LA CANCIÓN ANTERIOR
        context.put(STEP, 0);
        return context;

    }
}
