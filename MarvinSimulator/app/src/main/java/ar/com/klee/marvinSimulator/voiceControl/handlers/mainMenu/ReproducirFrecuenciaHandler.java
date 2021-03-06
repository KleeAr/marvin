package ar.com.klee.marvinSimulator.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;
import java.util.Map;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class ReproducirFrecuenciaHandler extends CommandHandler {

    public ReproducirFrecuenciaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("reproducir frecuencia {frecuencia}","reproducir frecuencia de radio {frecuencia}","escuchar frecuencia {frecuencia}","escuchar frecuencia de radio {frecuencia}"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String command = context.getString(COMMAND);
        Map<String, String> values = getExpressionMatcher(command).getValuesFromExpression(command);

        String frequence = values.get("frecuencia");

        frequence.replaceAll(" punto ",".");

        if(!frequence.startsWith("fm") && !frequence.startsWith("am")){
            getTextToSpeech().speakText("Debe indicar si la frecuencia es A M o FM");
        }else {

            frequence.replaceAll("cero","0");
            frequence.replaceAll("uno","1");
            frequence.replaceAll("dos","2");
            frequence.replaceAll("tres","3");
            frequence.replaceAll("cuatro","4");
            frequence.replaceAll("cinco","5");
            frequence.replaceAll("seis","6");
            frequence.replaceAll("siete","7");
            frequence.replaceAll("ocho","8");
            frequence.replaceAll("nueve","9");

            if (!context.getObject(ACTIVITY, MainMenuActivity.class).findRadioFrequence(frequence))
                getTextToSpeech().speakText("La frecuencia " + frequence + " no fue encontrada");
            else
                getTextToSpeech().speakText("Reproduciendo frecuencia " + frequence);
        }

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
