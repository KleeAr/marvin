package ar.com.klee.marvin.voiceControl.handlers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class ResponderSMSHandler extends CommandHandler {

    public ResponderSMSHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("responder sms"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Integer step = context.getInteger(STEP);
        switch(step){

            case 1:
                return stepOne(context);
            case 3:
                return stepThree(context);
            case 5:
                return stepFive(context);
            case 7:
                return stepSeven(context);

        }
        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {

    }

    //INICIO GENERADO POR EL INCOMING MESSAGE
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        context.put(STEP, 3);
        return context;
    }

    //INDICA SI QUIERE RESPONDER
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText("¿Qué le querés responder por sms?");
            context.getObject(ACTIVITY,MainMenuActivity.class).displayRespondSMS();
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando respuesta");
            context.getObject(ACTIVITY,MainMenuActivity.class).cancelMessage();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("Cancelando respuesta");
            context.getObject(ACTIVITY,MainMenuActivity.class).cancelMessage();
            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 3);
        return context;

    }

    //INGRESO MENSAJE
    public CommandHandlerContext stepFive(CommandHandlerContext context){
        String input = context.getString(COMMAND);

        Character firstCharacter, newFirstCharacter;
        firstCharacter = input.charAt(0);
        newFirstCharacter = Character.toUpperCase(firstCharacter);
        input = input.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        context.getObject(ACTIVITY, MainMenuActivity.class).setAnswer(input);
        getTextToSpeech().speakText("¿Querés responder el mensaje " + input + "?");
        context.put(STEP, 7);
        return context;
    }

    //CONFIRMACION DE MENSAJE
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText(context.getObject(ACTIVITY,MainMenuActivity.class).respondMessage());
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            context.getObject(ACTIVITY,MainMenuActivity.class).cancelMessage();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje querés responder?");
            context.getObject(ACTIVITY, MainMenuActivity.class).setAnswer("");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");
        context.put(STEP, 7);
        return context;

    }

}

