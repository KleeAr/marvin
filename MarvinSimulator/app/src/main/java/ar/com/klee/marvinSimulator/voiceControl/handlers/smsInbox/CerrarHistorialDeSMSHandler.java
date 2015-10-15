package ar.com.klee.marvinSimulator.voiceControl.handlers.smsInbox;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.SMSInboxActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class CerrarHistorialDeSMSHandler extends CommandHandler {


    public CerrarHistorialDeSMSHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("cerrar historial de sms","cerrar historial","cerrar","volver al menú principal"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        SMSInboxActivity smsInboxActivity = context.getObject(ACTIVITY, SMSInboxActivity.class);

        getTextToSpeech().speakText("Cerrando historial de sms");

        getCommandHandlerManager().defineActivity(CommandHandlerManager.ACTIVITY_MAIN, getCommandHandlerManager().getMainActivity());

        smsInboxActivity.finish();
        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
