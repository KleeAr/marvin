package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.expressions.exceptions.ExpressionMatcherException;
import ar.com.klee.marvin.social.WhatsAppService;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarWhatsAppHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private String message;
    private boolean setMessage = false;
    private TTS textToSpeech;
    private WhatsAppService whatsAppService;

    public EnviarWhatsAppHandler(String command, TTS textToSpeech, Context context){

        super(expressionMatcher, textToSpeech, context, commandHandlerManager);
        whatsAppService =  new WhatsAppService(context);

        expressionMatcher = new ExpressionMatcher("enviar whatsapp {mensaje}");

        this.command = command;
        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){

        try {
            Map<String, String> values = expressionMatcher.getValuesFromExpression(command);
            message = values.get("mensaje");
        }catch(ExpressionMatcherException e){
            return false;
        }

        return expressionMatcher.matches(command);

    }

    public int drive(int step, String input){

        if(setMessage)
            message = input;

        switch(step){

            case 1:
                return stepOne();
            case 3:
                return stepThree(input);

        }

        return 0;

    }

    //PRONUNCIA MENSAJE
    public int stepOne(){

        textToSpeech.speakText("¿Querés enviar el mensaje " + message + " por WhatsApp?");

        setMessage = false;

        return 3;

    }

    //CONFIRMA MENSAJE
    public int stepThree(String input){

        if(input.equals("si")) {
            sendWhatsApp(message);
            return 0;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando envío");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿Qué mensaje deseás enviar?");
            setMessage = true;
            return 1;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 3;

    }

    public void sendWhatsApp(String textToPublish) {

        Character firstCharacter, newFirstCharacter;

        firstCharacter = textToPublish.charAt(0);
        newFirstCharacter = Character.toUpperCase(firstCharacter);
        textToPublish = textToPublish.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        textToSpeech.speakText("Seleccioná el contacto y presioná dos veces atrás para volver a la aplicación");

        whatsAppService.sendWhatsApp(textToPublish);

    }

}

