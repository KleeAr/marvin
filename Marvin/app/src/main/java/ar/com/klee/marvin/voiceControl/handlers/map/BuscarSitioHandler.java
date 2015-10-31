package ar.com.klee.marvin.voiceControl.handlers.map;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.activities.MapActivity;
import ar.com.klee.marvin.configuration.UserSites;
import ar.com.klee.marvin.gps.Site;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class BuscarSitioHandler extends CommandHandler {

    public BuscarSitioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("buscar sitio {sitio}"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String command = context.getString(COMMAND);
        Map<String, String> values = getExpressionMatcher(command).getValuesFromExpression(command);
        String site = values.get("sitio");

        Integer i;
        Site searchedSite = null;

        List<Site> sites = UserSites.getInstance().getSites();

        for(i=0; i<=sites.size() ; i++) {
            if(i!=sites.size()) {
                searchedSite = sites.get(i);
                if (searchedSite.getSiteName().toLowerCase().equals(site))
                    break;
            }
        }

        if(i==sites.size())
            getCommandHandlerManager().getTextToSpeech().speakText("El sitio no fue encontrado");
        else {
            getCommandHandlerManager().getTextToSpeech().speakText("Buscando el sitio");
            ((MapActivity) getCommandHandlerManager().getActivity()).search(searchedSite.getSiteAddress());
        }

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
