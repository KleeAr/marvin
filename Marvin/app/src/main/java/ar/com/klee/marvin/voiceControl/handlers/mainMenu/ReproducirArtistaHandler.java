package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class ReproducirArtistaHandler extends CommandHandler {

    public static final String ARTIST = "ARTIST";
    public static final String ARTISTA = "artista";

    public ReproducirArtistaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("reproducir artista {artista}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String artist = context.getString(ARTIST);

        if(context.getObject(ACTIVITY, MainMenuActivity.class).isListEmpty()) {
            getTextToSpeech().speakText("No se han encontrado canciones en el dispositivo");
        }else {
            if (!context.getObject(ACTIVITY, MainMenuActivity.class).findArtist(artist))
                getTextToSpeech().speakText("El artista " + artist + " no fue encontrado");
            else
                getTextToSpeech().speakText("Reproduciendo artista " + artist);
        }

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        String command = commandHandlerContext.getString(COMMAND);
        commandHandlerContext.put(ARTIST, getExpressionMatcher(command).getValuesFromExpression(command).get(ARTISTA));
    }
}
