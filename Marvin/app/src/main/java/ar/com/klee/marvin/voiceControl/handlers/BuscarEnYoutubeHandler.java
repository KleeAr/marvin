package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.multimedia.video.YouTubeService;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

/**
 * @author msalerno
 */
public class BuscarEnYoutubeHandler extends CommandHandler{

    YouTubeService youTubeService = new YouTubeService();

    public BuscarEnYoutubeHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("buscar en youtube {query}", textToSpeech, context, commandHandlerManager);
    }

    @Override
    public CommandHandlerContext drive(CommandHandlerContext currentContext) {
        String query = getExpressionMatcher().getValuesFromExpression(currentContext.getString(COMMAND)).get("query");
        getTextToSpeech().speakText("Buscando videos en youtube");
        youTubeService.searchVideos(query, currentContext.getObject(ACTIVITY, MainMenuActivity.class));
        currentContext.put(STEP, 0);
        return currentContext;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {

    }
}
