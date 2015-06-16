package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class SMSDeEmergenciaHandler extends CommandHandler{

    public SMSDeEmergenciaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("sms de emergencia", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        getTextToSpeech().speakText("Enviando sms de emergencia");

        //CODIGO PARA LEVANTAR Y ENVIAR EL SMS
        context.put(STEP, 0);
        return context;

    }
}
