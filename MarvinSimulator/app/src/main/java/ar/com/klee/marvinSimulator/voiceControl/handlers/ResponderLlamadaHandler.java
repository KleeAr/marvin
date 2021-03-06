package ar.com.klee.marvinSimulator.voiceControl.handlers;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.IncomingCallActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;

public class ResponderLlamadaHandler extends CommandHandler{

    public ResponderLlamadaHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("responder llamada"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Integer step = context.getInteger(STEP);
        switch(step){

            case 1:
                return stepOne(context);
            case 3:
                return stepThree(context);

        }
        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {

    }

    //INICIO GENERADO POR EL INCOMING CALL
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        context.put(STEP, 3);
        return context;
    }

    //INDICA SI QUIERE RESPONDER
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("atender")) {
            context.getObject(ACTIVITY,IncomingCallActivity.class).acceptCall();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("rechazar")) {
            getTextToSpeech().speakText("Rechazando llamada");
            context.getObject(ACTIVITY,IncomingCallActivity.class).rejectCall();
            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar atender o rechazar");

        context.put(STEP, 3);
        return context;

    }

}

