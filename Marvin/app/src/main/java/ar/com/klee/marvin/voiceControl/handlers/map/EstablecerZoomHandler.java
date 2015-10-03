package ar.com.klee.marvin.voiceControl.handlers.map;

import android.content.Context;
import android.media.AudioManager;

import java.util.Arrays;
import java.util.Map;

import ar.com.klee.marvin.activities.MapActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class EstablecerZoomHandler extends CommandHandler {

    public EstablecerZoomHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("establecer zoom {zoom}","setear zoom {zoom}","configurar zoom {zoom}"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String command = context.getString(COMMAND);
        Map<String, String> values = getExpressionMatcher(command).getValuesFromExpression(command);

        String zoom = values.get("zoom");
        int z;

        try{

            z = Integer.parseInt(zoom);

        }catch(NumberFormatException e){

            getTextToSpeech().speakText("Debe indicarse un número para el zoom");

            context.put(STEP, 0);
            return context;

        }

        if(z>20 || z<1){

            getTextToSpeech().speakText("El zoom indicado debe estar entre 1 y 20");

        }else {

            getTextToSpeech().speakText("El zoom cambió a: " + ((MapActivity)getCommandHandlerManager().getActivity()).setZoom(z));
        }
        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
