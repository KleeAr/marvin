package ar.com.klee.marvin.voiceControl.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.social.WhatsAppService;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarMailAContactoHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private String contact;
    private String message;
    private String subject = "";
    private boolean setContact = false;
    private TTS textToSpeech;
    private Context context;

    public EnviarMailAContactoHandler(String command, TTS textToSpeech, Context context){

        expressionMatcher = new ExpressionMatcher("enviar mail a {contacto}");

        this.command = command;
        this.textToSpeech = textToSpeech;

        this.context = context;

    }

    public boolean validateCommand(){

        Map<String, String> values = expressionMatcher.getValuesFromExpression(command);

        contact = values.get("contacto");

        return expressionMatcher.matches(command);

    }

    public int drive(int step, String input){

        if(setContact)
            contact = input;

        switch(step){

            case 1:
                return stepOne();
            case 3:
                return stepThree(input);
            case 5:
                return stepFive(input);
            case 7:
                return stepSeven(input);
            case 9:
                return stepNine(input);
            case 11:
                return stepEleven(input);
            case 13:
                return stepThirteen(input);

        }

        return 0;

    }

    //PRONUNCIA CONTACTO
    public int stepOne(){

        //OBTENER MAIL DEL CONTACTO

        //CONTACTO INEXISTENTE - CONTACTO SIN MAIL

        textToSpeech.speakText("¿Querés enviar un mail al contacto " + contact + "?");

        setContact = false;

        return 3;

    }

    //CONFIRMA CONTACTO
    public int stepThree(String input){

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
    public int stepFive(String input){

        textToSpeech.speakText("¿Querés enviar por mail el mensaje " + input + "?");

        message = input;

        return 7;

    }

    //CONFIRMACION DE MENSAJE
    public int stepSeven(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("¿Deseás agregar un asunto?");

            return 9;
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

    //INDICA SI QUIERE AGREGAR ASUNTO
    public int stepNine(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("¿Qué asunto deseás agregar?");
            return 11;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando envío");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("Enviando mail");
            sendMail();
            return 0;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 9;

    }

    //INDICA ASUNTO
    public int stepEleven(String input){

        textToSpeech.speakText("¿Querés enviar el asunto " + input + "?");

        subject = input;

        return 13;

    }

    //CONFIRMA ASUNTO
    public int stepThirteen(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("Enviando mail");
            sendMail();
            return 0;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando envío");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿Qué asunto deseás agregar?");
            return 11;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 13;

    }


    public void sendMail(){

        message = message + "\n\n\n" + "Mensaje enviado a través de MARVIN";
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, contact);
        //emailIntent.putExtra(Intent.EXTRA_CC, cc);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(Intent.createChooser(emailIntent, "Seleccionar cuenta de Email:"));

    }


}

