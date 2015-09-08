package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import java.util.Arrays;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.call.CallReceiver;
import ar.com.klee.marvin.call.Contact;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class AgendarContactoHandler extends CommandHandler {

    public static final String NUMBER = "NUMBER";
    public static final String NUMERO = "numero";
    public static final String CONTACT = "CONTACT";

    public AgendarContactoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("agendar contacto {numero}"), textToSpeech, context, commandHandlerManager);
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
        String command = commandHandlerContext.getString(COMMAND);
        commandHandlerContext.put(NUMBER, getExpressionMatcher(command).getValuesFromExpression(command).get(NUMERO));

    }

    //INICIO GENERADO POR EL INCOMING MESSAGE
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        getTextToSpeech().speakText("¿Te gustaría agendar el número con el que te comunicaste?");
        context.put(STEP, 3);
        return context;
    }

    //INDICA SI QUIERE RESPONDER
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText("¿Con qué nombre querés agendar el contacto?");
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("No se agendará el contacto");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("No se agendará el contacto");
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

        char[] characters = input.toCharArray();

        characters[0] = Character.toUpperCase(characters[0]);

        for (int i = 0; i < characters.length - 2; i++)
            if (characters[i] == ' ')
                characters[i + 1] = Character.toUpperCase(characters[i + 1]);

        input = new String(characters);

        context.put(CONTACT, input);
        getTextToSpeech().speakText("¿Querés agendar el número con el nombre " + input + "?");
        context.put(STEP, 7);
        return context;
    }

    //CONFIRMACION DE MENSAJE
    public CommandHandlerContext stepSeven(CommandHandlerContext context) {
        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText("Agendando contacto");
            Contact contact = new Contact(context.getString(CONTACT),context.getString(NUMBER),"");
            try {
                contact.addNewContact(getCommandHandlerManager().getContext(), contact);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("No se agendará el número");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Con qué nombre querés agendar el contacto?");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");
        context.put(STEP, 7);
        return context;

    }

}

