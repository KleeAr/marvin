package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.Map;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class ReproducirCancionHandler extends CommandHandler{

    public ReproducirCancionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("reproducir canción {cancion}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Map<String, String> values = getExpressionMatcher().getValuesFromExpression(context.getString(COMMAND));

        String song = values.get("cancion");

        if(!context.getObject(ACTIVITY, MainMenuActivity.class).findSong(song))
            getTextToSpeech().speakText("La canción " + song + " no fue encontrada");
        else
            getTextToSpeech().speakText("Reproducdiendo canción " + song);

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
