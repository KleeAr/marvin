package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class AnteriorInterseccionHandler extends CommandHandler{

    public AnteriorInterseccionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("anterior intersección",textToSpeech, context, commandHandlerManager);
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
