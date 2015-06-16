package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarMailAContactoHandler extends CommandHandler{

    public EnviarMailAContactoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("enviar mail a {contacto}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(!context.containsKey(SET_CONTACT)) {
            context.put(SET_CONTACT, false);
        }
        Boolean setContact = context.get(SET_CONTACT, Boolean.class);

        if(setContact) {
            context.put(CONTACT, context.get(INPUT, String.class));
        }

        Integer step = context.get(STEP, Integer.class);
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

    //PRONUNCIA CONTACTO
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        String contact = context.get(CONTACT, String.class);
        getTextToSpeech().speakText("¿Querés enviar un mail al contacto " + contact + "?");

        context.put(SET_CONTACT, false);

        context.put(STEP, 3);
        return context;

    }

    //CONFIRMA CONTACTO
    public CommandHandlerContext stepThree(CommandHandlerContext context){
        String input = context.get(INPUT, String.class);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Qué mensaje le querés mandar por mail?");
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿A qué contacto querés mandarle el mail?");
            context.put(SET_CONTACT, true);
            context.put(STEP, 1);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");
        context.put(STEP, 3);
        return context;

    }

    //INGRESO MENSAJE
    public CommandHandlerContext stepFive(CommandHandlerContext context){
        String input = context.get(INPUT, String.class);
        getTextToSpeech().speakText("¿Querés enviar por mail el mensaje " + input + "?");

        context.put(STEP, 7);
        return context;

    }

    //CONFIRMACION DE MENSAJE
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.get(INPUT, String.class);
        if(input.equals("si")) {
            getTextToSpeech().speakText("Enviando mail");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje querés mandar?");
            context.put(STEP, 5);
            return context;
        }
        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");
        context.put(STEP, 7);
        return context;
    }
}

