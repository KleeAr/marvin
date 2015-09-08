package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class EnviarSMSANumeroHandler extends CommandHandler {

    public static final String NUMERO = "numero";
    private static final String SET_NUMBER = "SET_NUMBER";
    private static final String NUMBER = "NUMBER";

    public EnviarSMSANumeroHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("enviar sms al número {numero}"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Boolean setContact = context.getBoolean(SET_NUMBER);
        if(setContact) {
            context.put(NUMBER, context.getString(COMMAND));
        }

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
        String command = commandHandlerContext.getString(COMMAND);
        commandHandlerContext.put(NUMBER, getExpressionMatcher(command).getValuesFromExpression(command).get(NUMERO));
        commandHandlerContext.put(SET_NUMBER, false);

        commandHandlerContext.getObject(ACTIVITY, MainMenuActivity.class).displaySendSMS();
    }

    //PRONUNCIA NUMERO
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        String contact = context.getString(NUMBER);

        contact = contact.replace(" ","");

        try{

            Integer.parseInt(contact);

        }catch (NumberFormatException e){

            getTextToSpeech().speakText("No se indicó un número. Reingresalo");
            context.put(SET_NUMBER, true);
            context.put(STEP, 1);
            return context;

        }

        int i = 0;
        String contactWithSpaces = "";

        while(i < contact.length()){
            contactWithSpaces += contact.charAt(i) + " ";
            i++;
        }

        getTextToSpeech().speakText("¿Querés enviar un sms al número " + contactWithSpaces + "?");
        context.getObject(ACTIVITY, MainMenuActivity.class).setNumber(contact);
        context.put(SET_NUMBER, false);
        context.put(STEP, 3);
        return context;
    }

    //CONFIRMA NUMERO
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText("¿Qué mensaje le querés mandar por sms?");
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            context.getObject(ACTIVITY,MainMenuActivity.class).cancelMessage();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿A qué número querés mandarle el sms?");
            context.getObject(ACTIVITY, MainMenuActivity.class).setNumber("");
            context.put(SET_NUMBER, true);
            context.put(STEP, 1);
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

        getTextToSpeech().speakText("¿Querés enviar por sms el mensaje " + input + "?");
        context.getObject(ACTIVITY, MainMenuActivity.class).setMessageBody(input);
        context.put(STEP, 7);
        return context;

    }

    //CONFIRMACION DE MENSAJE
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText(context.getObject(ACTIVITY,MainMenuActivity.class).sendMessage());
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
            getTextToSpeech().speakText("¿Qué mensaje querés mandar?");
            context.getObject(ACTIVITY, MainMenuActivity.class).setMessageBody("");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");
        context.put(STEP, 7);
        return context;

    }


}

