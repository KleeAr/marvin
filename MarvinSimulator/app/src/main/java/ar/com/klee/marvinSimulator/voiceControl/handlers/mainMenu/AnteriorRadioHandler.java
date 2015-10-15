package ar.com.klee.marvinSimulator.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class AnteriorRadioHandler extends CommandHandler {


    public AnteriorRadioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager){
        super(Arrays.asList("anterior radio","anterior estación","estación anterior","radio anterior"), textToSpeech, context, commandHandlerManager);

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
