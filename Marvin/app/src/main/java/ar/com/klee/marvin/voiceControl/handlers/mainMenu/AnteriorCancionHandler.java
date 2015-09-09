package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class AnteriorCancionHandler extends CommandHandler {


    public AnteriorCancionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager){
        super(Arrays.asList("anterior canción","canción anterior","canción previa","reproducir anterior canción","reproducir canción anterior","volver a la anterior canción","volver a la canción anterior"), textToSpeech, context, commandHandlerManager);

    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(context.getObject(ACTIVITY, MainMenuActivity.class).isListEmpty()) {
            getTextToSpeech().speakText("No se han encontrado canciones en el dispositivo");
        }else {
            getTextToSpeech().speakText("Volviendo a la canción anterior");
            context.getObject(ACTIVITY, MainMenuActivity.class).previousSet("music");
        }

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
