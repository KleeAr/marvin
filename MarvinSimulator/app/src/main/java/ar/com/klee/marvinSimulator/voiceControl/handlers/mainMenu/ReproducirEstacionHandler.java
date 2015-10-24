package ar.com.klee.marvinSimulator.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;
import java.util.Map;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class ReproducirEstacionHandler extends CommandHandler {

    public ReproducirEstacionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("reproducir estación {estacion}","reproducir estación de radio {estacion}","reproducir radio {estacion}","escuchar estación {estacion}","escuchar estación de radio {estacion}","escuchar radio {estacion}"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String command = context.getString(COMMAND);
        Map<String, String> values = getExpressionMatcher(command).getValuesFromExpression(command);

        String station = values.get("estacion");

        station.replaceAll("cien","100");
        station.replaceAll("cuarenta","40");

        if (!context.getObject(ACTIVITY, MainMenuActivity.class).findRadioName(station))
            getTextToSpeech().speakText("La estación " + station + " no fue encontrada");
        else
            getTextToSpeech().speakText("Reproduciendo estación " + station);


        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
