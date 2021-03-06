package ar.com.klee.marvinSimulator.voiceControl.handlers.site;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.SiteActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class CerrarSitioHandler extends CommandHandler {


    public CerrarSitioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("cerrar sitio","cerrar","volver"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        SiteActivity siteActivity = context.getObject(ACTIVITY, SiteActivity.class);

        getTextToSpeech().speakText("Cerrando sitio");

        getCommandHandlerManager().defineActivity(CommandHandlerManager.ACTIVITY_PLACES, getCommandHandlerManager().getMainActivity());

        siteActivity.finish();

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
