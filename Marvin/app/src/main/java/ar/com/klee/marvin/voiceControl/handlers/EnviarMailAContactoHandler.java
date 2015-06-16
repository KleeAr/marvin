package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarMailAContactoHandler extends CommandHandler{

    public static final String SET_CONTACT = "SET_CONTACT";

    public EnviarMailAContactoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("enviar mail a {contacto}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(!context.containsKey(SET_CONTACT)) {
            context.put(SET_CONTACT, false);
        }
        Boolean setContact = context.get(SET_CONTACT, Boolean.class);

        if(setContact) {
            context.put("CONTACT", context.get(INPUT, String.class));
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
    public int stepOne(CommandHandlerContext context){

        textToSpeech.speakText("¿Querés enviar un mail al contacto " + contact + "?");

        setContact = false;

        return 3;

    }

    //CONFIRMA CONTACTO
    public int stepThree(CommandHandlerContext input){

        if(input.equals("si")) {
            textToSpeech.speakText("¿Qué mensaje le querés mandar por mail?");
            return 5;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando envío");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿A qué contacto querés mandarle el mail?");
            setContact = true;
            return 1;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 3;

    }

    //INGRESO MENSAJE
    public int stepFive(CommandHandlerContext input){

        textToSpeech.speakText("¿Querés enviar por mail el mensaje " + input + "?");

        return 7;

    }

    //CONFIRMACION DE MENSAJE
    public int stepSeven(CommandHandlerContext input){

        if(input.equals("si")) {
            textToSpeech.speakText("Enviando mail");
            return 0;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando envío");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿Qué mensaje querés mandar?");
            return 5;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 7;

    }


}

