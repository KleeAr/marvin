package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class ActivarHotspotHandler extends CommandHandler{


    public ActivarHotspotHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager){
        super("activar hotspot", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext currentContext){

        getTextToSpeech().speakText("activando hotspot");

        //CODIGO PARA ACTIVAR HOTSPOT

        currentContext.put(STEP, 0);
        return currentContext;
    }
}
