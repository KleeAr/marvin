package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;
import java.util.Locale;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.fragments.MainMenuFragment;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class CerrarSesionHandler extends CommandHandler {

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    public CerrarSesionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("cerrar sesión", textToSpeech, context, commandHandlerManager);
    }

    @Override
    public CommandHandlerContext drive(CommandHandlerContext currentContext) {

        getTextToSpeech().speakText("Cerrando sesión");

        MainMenuActivity.mapFragment.finishTrip();

        MainMenuFragment.getInstance().stopThread();

        ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).stopServices();

        currentContext.put(STEP, 0);
        return currentContext;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
