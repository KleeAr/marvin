package ar.com.klee.marvinSimulator.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class SiguienteInterseccion extends CommandHandler {

    public SiguienteInterseccion(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("siguiente intersección","intersección siguiente","siguiente esquina","esquina siguiente"), textToSpeech, context, commandHandlerManager);
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
