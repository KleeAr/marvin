package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class ReproducirArtistaHandler extends CommandHandler{

    public static final String ARTIST = "ARTIST";
    public static final String ARTISTA = "artista";

    public ReproducirArtistaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("reproducir artista {artista}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String artist = context.get(ARTIST, String.class);
        getTextToSpeech().speakText("Reproduciendo artista " + artist);

        //TODO: CODIGO PARA BUSCAR Y REPRODUCIR UN ARTISTA

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        commandHandlerContext.put(ARTIST, getExpressionMatcher().getValuesFromExpression(commandHandlerContext.get(COMMAND, String.class)).get(ARTISTA));
    }
}
