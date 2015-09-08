package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;
import java.util.Map;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class ReproducirEstacionHandler extends CommandHandler {

    public ReproducirEstacionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("reproducir estación {estacion}"), textToSpeech, context, commandHandlerManager);
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
