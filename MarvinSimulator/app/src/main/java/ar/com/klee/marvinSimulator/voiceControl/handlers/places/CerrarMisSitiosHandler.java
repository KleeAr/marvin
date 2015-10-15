package ar.com.klee.marvinSimulator.voiceControl.handlers.places;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class CerrarMisSitiosHandler extends CommandHandler {


    public CerrarMisSitiosHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("cerrar mis sitios","cerrar","volver al menú principal"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Cerrando mis sitios");

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
