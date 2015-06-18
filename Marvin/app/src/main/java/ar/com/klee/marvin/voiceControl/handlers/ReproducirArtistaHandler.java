package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class ReproducirArtistaHandler extends CommandHandler{

    public static final String ARTIST = "ARTIST";
    public static final String ARTISTA = "artista";

    public ReproducirArtistaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("reproducir artista {artista}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String artist = context.getString(ARTIST);
        getTextToSpeech().speakText("Reproduciendo artista " + artist);

        //TODO: CODIGO PARA BUSCAR Y REPRODUCIR UN ARTISTA

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        commandHandlerContext.put(ARTIST, getExpressionMatcher().getValuesFromExpression(commandHandlerContext.getString(COMMAND)).get(ARTISTA));
    }
}
