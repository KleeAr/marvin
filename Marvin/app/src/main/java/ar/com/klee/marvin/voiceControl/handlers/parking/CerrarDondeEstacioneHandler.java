package ar.com.klee.marvin.voiceControl.handlers.parking;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.activities.TripActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class CerrarDondeEstacioneHandler extends CommandHandler {


    public CerrarDondeEstacioneHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("cerrar dónde estacioné","cerrar","volver al menú principal"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Cerrando dónde estacioné");

        int previousMenu = ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).previousMenus.pop();

        ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).setFragment(previousMenu);

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
