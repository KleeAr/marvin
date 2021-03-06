package ar.com.klee.marvinSimulator.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.multimedia.video.YouTubeService;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

/**
 * @author msalerno
 */
public class BuscarEnYoutubeHandler extends CommandHandler {

    YouTubeService youTubeService = new YouTubeService();

    public BuscarEnYoutubeHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("buscar en youtube {query}", "buscar video en youtube {query}"), textToSpeech, context, commandHandlerManager);
    }

    @Override
    public CommandHandlerContext drive(CommandHandlerContext currentContext) {
        String command = currentContext.getString(COMMAND);
        String query = getExpressionMatcher(command).getValuesFromExpression(command).get("query");
        getTextToSpeech().speakText("Buscando videos en youtube");
        youTubeService.searchVideos(query, currentContext.getObject(ACTIVITY, MainMenuActivity.class));
        currentContext.put(STEP, 0);
        return currentContext;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {

    }
}
