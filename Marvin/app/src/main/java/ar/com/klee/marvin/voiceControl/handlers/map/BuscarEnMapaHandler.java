package ar.com.klee.marvin.voiceControl.handlers.map;

import android.content.Context;

import java.util.Map;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.activities.MapActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class BuscarEnMapaHandler extends CommandHandler {

    public BuscarEnMapaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("buscar en mapa {direccion}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Map<String, String> values = getExpressionMatcher().getValuesFromExpression(context.getString(COMMAND));
        String address = values.get("direccion");

        getCommandHandlerManager().getTextToSpeech().speakText("Buscando la dirección " + address);

        ((MapActivity)getCommandHandlerManager().getActivity()).search(address);

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
