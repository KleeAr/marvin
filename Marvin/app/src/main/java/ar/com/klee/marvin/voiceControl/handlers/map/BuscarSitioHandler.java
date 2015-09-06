package ar.com.klee.marvin.voiceControl.handlers.map;

import android.content.Context;

import java.util.Map;

import ar.com.klee.marvin.activities.MapActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class BuscarSitioHandler extends CommandHandler {

    public BuscarSitioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("buscar sitio {sitio}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String command = context.getString(COMMAND);
        Map<String, String> values = getExpressionMatcher(command).getValuesFromExpression(command);
        String site = values.get("sitio");

        getCommandHandlerManager().getTextToSpeech().speakText("Buscando el sitio " + site);

        //TODO: Obtener direccion

        //((MapActivity)getCommandHandlerManager().getActivity()).search(address);

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
