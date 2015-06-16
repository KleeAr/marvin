package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class ReproducirArtistaHandler extends CommandHandler{

    public ReproducirArtistaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("reproducir artista {artista}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Map<String, String> values = getExpressionMatcher().getValuesFromExpression(context.get(COMMAND, String.class));

        String artist = values.get("cancion");

        getTextToSpeech().speakText("Reproduciendo artista "+artist);

        //TODO: CODIGO PARA BUSCAR Y REPRODUCIR UN ARTISTA

        context.put(STEP, 0);
        return context;
    }
}
