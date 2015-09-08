package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class AnteriorRadioHandler extends CommandHandler {


    public AnteriorRadioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager){
        super(Arrays.asList("anterior radio"), textToSpeech, context, commandHandlerManager);

    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Volviendo a la radio anterior");
        context.getObject(ACTIVITY, MainMenuActivity.class).previousSet("radio");

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
