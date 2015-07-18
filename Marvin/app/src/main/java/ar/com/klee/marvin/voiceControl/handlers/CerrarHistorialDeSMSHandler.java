package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.activities.SMSInboxActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class CerrarHistorialDeSMSHandler extends CommandHandler{


    public CerrarHistorialDeSMSHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("cerrar historial de sms", textToSpeech, context, commandHandlerManager);
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
