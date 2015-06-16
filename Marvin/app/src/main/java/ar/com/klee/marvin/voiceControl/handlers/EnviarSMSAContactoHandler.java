package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarSMSAContactoHandler extends CommandHandler{

    public EnviarSMSAContactoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("enviar sms a {contacto}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

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
        getTextToSpeech().speakText("¿Querés enviar un sms al contacto " + contact + "?");
        context.put(SET_CONTACT, false);
        context.put(STEP, 3);
        return context;
    }

    //CONFIRMA CONTACTO
    public int stepThree(CommandHandlerContext input){

        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Qué mensaje le querés mandar por sms?");
            return 5;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            return 0;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿A qué contacto querés mandarle el sms?");
            setContact = true;
            return 1;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        return 3;

    }

    //INGRESO MENSAJE
    public int stepFive(CommandHandlerContext input){

        getTextToSpeech().speakText("¿Querés enviar por sms el mensaje " + input + "?");

        return 7;

    }

    //CONFIRMACION DE MENSAJE
    public int stepSeven(CommandHandlerContext input){

        if(input.equals("si")) {
            getTextToSpeech().speakText("Enviando sms");
            return 0;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            return 0;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje querés mandar?");
            return 5;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        return 7;

    }


}

