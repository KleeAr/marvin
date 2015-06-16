package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class DesactivarHotspotHandler extends CommandHandler{

    public DesactivarHotspotHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("desactivar hotspot", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("desactivando hotspot");

        //CODIGO PARA DESACTIVAR HOTSPOT

        context.put(STEP, 0);
        return context;
    }
}
