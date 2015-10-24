package ar.com.klee.marvinSimulator.voiceControl.handlers.map;

import android.content.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ar.com.klee.marvinSimulator.activities.MapActivity;
import ar.com.klee.marvinSimulator.configuration.UserSites;
import ar.com.klee.marvinSimulator.gps.Site;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class IrASitioHandler extends CommandHandler {

    public IrASitioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("ir a sitio {sitio}","navegar hacia sitio {sitio}"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String command = context.getString(COMMAND);
        Map<String, String> values = getExpressionMatcher(command).getValuesFromExpression(command);
        String site = values.get("sitio");

        Integer i;
        Site searchedSite = null;

        List<Site> sites = UserSites.getInstance().getSites();

        for(i=0; i<sites.size() ; i++) {
            searchedSite = sites.get(i);
            if(searchedSite.getSiteName().toLowerCase().equals(site))
                break;
        }

        if(i==sites.size()+1)
            getCommandHandlerManager().getTextToSpeech().speakText("El sitio no fue encontrado");
        else {
            getCommandHandlerManager().getTextToSpeech().speakText("Activando navegación hacia el sitio");
            ((MapActivity)getCommandHandlerManager().getActivity()).navigate(searchedSite.getSiteAddress());
        }

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
