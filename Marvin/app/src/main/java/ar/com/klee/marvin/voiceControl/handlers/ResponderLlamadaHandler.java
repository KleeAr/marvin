package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.IncomingCallActivity;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

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
            ((IncomingCallActivity)getCommandHandlerManager().getActivity()).acceptCall();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("rechazar")) {
            ((IncomingCallActivity)getCommandHandlerManager().getActivity()).rejectCall();
            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("");

        context.put(STEP, 3);
        return context;

    }

}

