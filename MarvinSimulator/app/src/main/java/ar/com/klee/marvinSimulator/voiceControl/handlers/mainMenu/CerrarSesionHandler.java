package ar.com.klee.marvinSimulator.voiceControl.handlers.mainMenu;

import android.content.Context;
import android.os.Handler;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.expressions.ExpressionMatcher;
import ar.com.klee.marvinSimulator.fragments.MainMenuFragment;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class CerrarSesionHandler extends CommandHandler {

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    public CerrarSesionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("cerrar sesión","salir"), textToSpeech, context, commandHandlerManager);
    }

    @Override
    public CommandHandlerContext drive(final CommandHandlerContext currentContext) {

        getTextToSpeech().speakText("Cerrando sesión");

        MainMenuFragment.getInstance().setItem(2);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(MainMenuFragment.isInstanceInitialized())
                    MainMenuFragment.getInstance().stopThread();
                MainMenuActivity.mapFragment.finishTrip();
                MainMenuActivity.locationSender.stopLocationSender();
                ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).stopServices();
            }
        }, 1000);

        currentContext.put(STEP, 0);
        return currentContext;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
