package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class ReproducirCancionHandler extends CommandHandler{

    public ReproducirCancionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("reproducir canción {cancion}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Map<String, String> values = getExpressionMatcher().getValuesFromExpression(context.get(COMMAND, String.class));

        String song = values.get("cancion");

        getTextToSpeech().speakText("Reproduciendo canción " + song);

        //CODIGO PARA BUSCAR Y REPRODUCIR UNA CANCION
        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
