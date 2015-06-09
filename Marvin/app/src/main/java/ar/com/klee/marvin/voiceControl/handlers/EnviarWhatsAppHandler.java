package ar.com.klee.marvin.voiceControl.handlers;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.social.TwitterService;
import ar.com.klee.marvin.social.WhatsAppService;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarWhatsAppHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private String message;
    private boolean setMessage = false;
    private TTS textToSpeech;
    private WhatsAppService whatsAppService;

    public EnviarWhatsAppHandler(String command, TTS textToSpeech, Activity activity){

        whatsAppService =  new WhatsAppService(activity);

        expressionMatcher = new ExpressionMatcher("enviar whatsapp {mensaje}");

        this.command = command;
        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){

        Map<String, String> values = expressionMatcher.getValuesFromExpression(command);

        message = values.get("mensaje");

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

        if(input.equals("sí")) {
            sendWhatsApp(message);
            return 5;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando envío");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿Qué mensaje deseás publicar junto al mensaje?");
            setMessage = true;
            return 1;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 3;

    }

    public void sendWhatsApp(String textToPublish) {

        textToSpeech.speakText("Mensaje configurado. Seleccioná el contacto al que querés enviarlo");

        whatsAppService.sendWhatsApp(textToPublish);

    }

}

