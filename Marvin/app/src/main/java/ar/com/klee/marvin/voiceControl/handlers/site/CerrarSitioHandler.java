package ar.com.klee.marvin.voiceControl.handlers.site;

import android.content.Context;
import android.os.Handler;

import java.io.File;
import java.util.Arrays;

import ar.com.klee.marvin.activities.SiteActivity;
import ar.com.klee.marvin.activities.TripActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class CerrarSitioHandler extends CommandHandler {

    private String mapPath = "/sdcard/MARVIN/site.png";

    public CerrarSitioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("cerrar sitio","cerrar","volver"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        SiteActivity siteActivity = context.getObject(ACTIVITY, SiteActivity.class);

        getTextToSpeech().speakText("Cerrando sitio");

        getCommandHandlerManager().defineActivity(CommandHandlerManager.ACTIVITY_PLACES, getCommandHandlerManager().getMainActivity());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    File photo = new File(mapPath);
                    photo.delete();
                } catch (Exception e) {
                }
            }
        }, 2000);

        siteActivity.finish();

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
