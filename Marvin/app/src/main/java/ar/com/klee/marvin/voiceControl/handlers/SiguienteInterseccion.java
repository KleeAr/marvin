package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class SiguienteInterseccion extends CommandHandler{

    public SiguienteInterseccion(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("siguiente intersección", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String nextStreet = context.getObject(ACTIVITY, MainMenuActivity.class).nextStreet();

        if(nextStreet.equals("error")){

            getTextToSpeech().speakText("Avanzá un poco para que pueda detectar tu posición y volvé a llamarme");

        }else if(nextStreet.equals("")){

            getTextToSpeech().speakText("No hay una intersección cerca o no pude detectarla");

        }else{

            getTextToSpeech().speakText("La próxima calle es " + nextStreet);

        }

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
