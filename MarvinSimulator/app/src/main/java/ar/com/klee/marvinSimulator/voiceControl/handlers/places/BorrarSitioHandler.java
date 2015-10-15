package ar.com.klee.marvinSimulator.voiceControl.handlers.places;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class BorrarSitioHandler extends CommandHandler {

    public static final String SITIO = "sitio";
    public static final String SITE = "SITE";

    public BorrarSitioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("borrar sitio {sitio}","borrar lugar {sitio}","borrar {sitio}"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String site = context.getString(SITE);

        boolean result = ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).checkSiteExistence(site);

        if(result) {
            getTextToSpeech().speakText("Eliminando el sitio " + site);
            ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).deleteSite(site);
        }else
            getTextToSpeech().speakText("El sitio " + site + " no existe");

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        String command = commandHandlerContext.getString(COMMAND);
        commandHandlerContext.put(SITE, getExpressionMatcher(command).getValuesFromExpression(command).get(SITIO));
    }
}
