package ar.com.klee.marvin.voiceControl.handlers.map;

import android.content.Context;

import java.util.Map;

import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class IrASitioHandler extends CommandHandler {

    public IrASitioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("ir a sitio {sitio}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Map<String, String> values = getExpressionMatcher().getValuesFromExpression(context.getString(COMMAND));
        String site = values.get("sitio");

        getCommandHandlerManager().getTextToSpeech().speakText("Activando navegación hacia " + site);

        //TODO: Obtener direccion

        //((MapActivity)getCommandHandlerManager().getActivity()).navigate(address);

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
