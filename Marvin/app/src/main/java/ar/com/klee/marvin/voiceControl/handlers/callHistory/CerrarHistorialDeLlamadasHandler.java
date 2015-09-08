package ar.com.klee.marvin.voiceControl.handlers.callHistory;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.CallHistoryActivity;
import ar.com.klee.marvin.activities.SMSInboxActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class CerrarHistorialDeLlamadasHandler extends CommandHandler {


    public CerrarHistorialDeLlamadasHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("cerrar historial de llamadas"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        CallHistoryActivity callHistoryActivity = context.getObject(ACTIVITY, CallHistoryActivity.class);

        getTextToSpeech().speakText("Cerrando historial de llamadas");

        getCommandHandlerManager().defineActivity(CommandHandlerManager.ACTIVITY_MAIN, getCommandHandlerManager().getMainActivity());

        callHistoryActivity.finish();
        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
