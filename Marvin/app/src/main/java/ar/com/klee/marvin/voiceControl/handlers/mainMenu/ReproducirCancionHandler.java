package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;
import java.util.Map;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class ReproducirCancionHandler extends CommandHandler {

    public ReproducirCancionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("reproducir canción {cancion}"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String command = context.getString(COMMAND);
        Map<String, String> values = getExpressionMatcher(command).getValuesFromExpression(command);

        String song = values.get("cancion");

        if(context.getObject(ACTIVITY, MainMenuActivity.class).isListEmpty()) {
            getTextToSpeech().speakText("No se han encontrado canciones en el dispositivo");
        }else{
            if (!context.getObject(ACTIVITY, MainMenuActivity.class).findSong(song))
                getTextToSpeech().speakText("La canción " + song + " no fue encontrada");
            else
                getTextToSpeech().speakText("Reproduciendo canción " + song);
        }

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
