package ar.com.klee.marvinSimulator.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class AnteriorInterseccionHandler extends CommandHandler {

    public AnteriorInterseccionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("anterior intersección","anterior esquina","intersección anterior","esquina anterior"),textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext currentContext){

        String previousStreet = currentContext.getObject(ACTIVITY, MainMenuActivity.class).previousStreet();

        if(previousStreet.equals("error")){

            getTextToSpeech().speakText("Avanzá un poco para que pueda detectar tu posición y volvé a llamarme");

        }else if(previousStreet.equals("")){

            getTextToSpeech().speakText("No hay una intersección cercana detrás o no pude detectarla");

        }else{

            getTextToSpeech().speakText("La anterior calle es " + previousStreet);

        }

        currentContext.put(STEP, 0);
        return currentContext;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
