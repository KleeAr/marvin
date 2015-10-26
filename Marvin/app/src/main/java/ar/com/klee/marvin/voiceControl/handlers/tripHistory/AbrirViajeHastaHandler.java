package ar.com.klee.marvin.voiceControl.handlers.tripHistory;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class AbrirViajeHastaHandler extends CommandHandler {

    public static final String DIRECCION = "direccion";
    public static final String ADDRESS = "ADDRESS";


    public AbrirViajeHastaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("abrir viaje hasta {direccion}"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String address = context.getString(ADDRESS);

        boolean result = ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).checkTripExistence("finish - " + address);

        if(result) {
            getTextToSpeech().speakText("Abriendo viaje haste " + address);
            ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).openTrip("finish - " + address);
        }else
            getTextToSpeech().speakText("No se encontraron viajes haste la dirección " + address);

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        String command = commandHandlerContext.getString(COMMAND);
        commandHandlerContext.put(ADDRESS, getExpressionMatcher(command).getValuesFromExpression(command).get(DIRECCION));
    }
}
