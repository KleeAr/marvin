package ar.com.klee.marvinSimulator.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class LlamarANumeroHandler extends CommandHandler {

    public static final String NUMERO = "numero";
    private static final String SET_NUMBER = "SET_NUMBER";
    private static final String NUMBER = "NUMBER";

    public LlamarANumeroHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("llamar al número {numero}","llamar al {numero}"), textToSpeech, context, commandHandlerManager);
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

        }
        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        String command = commandHandlerContext.getString(COMMAND);
        commandHandlerContext.put(NUMBER, getExpressionMatcher(command).getValuesFromExpression(command).get(NUMERO));
        commandHandlerContext.put(SET_NUMBER, false);

        commandHandlerContext.getObject(ACTIVITY, MainMenuActivity.class).openCallDialog();
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

        getTextToSpeech().speakText("¿Querés llamar al número " + contactWithSpaces + "?");
        context.getObject(ACTIVITY, MainMenuActivity.class).setCallNumber(contact);
        context.put(SET_NUMBER, false);
        context.put(STEP, 3);
        return context;
    }

    //CONFIRMA NUMERO
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            context.getObject(ACTIVITY,MainMenuActivity.class).callNumber(context.getString(NUMBER));
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando llamada");
            context.getObject(ACTIVITY,MainMenuActivity.class).closeCallDialog();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿A qué número querés llamar?");
            context.getObject(ACTIVITY, MainMenuActivity.class).setCallNumber("");
            context.put(SET_NUMBER, true);
            context.put(STEP, 1);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        context.put(STEP, 3);
        return context;

    }

}

