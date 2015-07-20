package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class AnteriorCancionHandler extends CommandHandler {


    public AnteriorCancionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager){
        super("anterior canción", textToSpeech, context, commandHandlerManager);

    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(context.getObject(ACTIVITY, MainMenuActivity.class).isListEmpty()) {
            getTextToSpeech().speakText("No se han encontrado canciones en el dispositivo");
        }else {
            getTextToSpeech().speakText("Volviendo a la canción anterior");
            context.getObject(ACTIVITY, MainMenuActivity.class).previousSongSet();
        }

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
