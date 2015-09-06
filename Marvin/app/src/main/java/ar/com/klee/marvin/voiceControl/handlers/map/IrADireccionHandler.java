package ar.com.klee.marvin.voiceControl.handlers.map;

import android.content.Context;

import java.util.Map;

import ar.com.klee.marvin.activities.MapActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class IrADireccionHandler extends CommandHandler {

    public IrADireccionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("ir a {direccion}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String command = context.getString(COMMAND);
        Map<String, String> values = getExpressionMatcher(command).getValuesFromExpression(command);
        String address = values.get("direccion");

        getCommandHandlerManager().getTextToSpeech().speakText("Activando navegación hacia " + address);

        ((MapActivity)getCommandHandlerManager().getActivity()).navigate(address);

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
